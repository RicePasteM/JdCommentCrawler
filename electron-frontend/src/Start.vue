<template>
    <div id="appOuter">
        <div class="starter" v-show="!progRunning">
            <h2 style="margin-bottom:40px;">京东商品评论词云统计</h2>
            <div class="group">
                <input type="text" required v-model="serverIP">
                <span class="highlight"></span>
                <span class="bar"></span>
                <label>服务器地址</label>
            </div>
            <div class="group">
                <input type="text" required v-model="serverPort">
                <span class="highlight"></span>
                <span class="bar"></span>
                <label>服务器端口</label>
            </div>
            <div class="group">
                <input type="text" required v-model="productId">
                <span class="highlight"></span>
                <span class="bar"></span>
                <label>商品ID</label>
            </div>
            <n-input-number v-model:value="pageCount" clearable placeholder="爬取页数"
                style="width: 300px; margin-bottom:30px" />
            <n-button type="success" size="large" @click="runProg">
                开始
            </n-button>
        </div>
        <vue-word-cloud style="
                height: 100vh;
                width: 100vw;
                position:absolute;
                z-index:10;
                background-color: white;
            " v-show="showCloud" class="wordCloud" :words="shownResults" :showTooltip="true"
            :color="([, weight]) => weight > 10 ? 'DeepPink' : weight > 5 ? 'RoyalBlue' : 'Indigo'" font-family="Roboto" />

        <div class="mask"></div>
        <div class="output">
            {{ terminalOutput }}
        </div>
    </div>
</template>

<script>
import { h, ref, defineComponent, inject } from "vue";
import { useRouter } from "vue-router";
const { ipcRenderer } = require("electron");
const { dialog } = require("@electron/remote");
import VueWordCloud from 'vuewordcloud';
import stopwords from './utils/stopwords.js';
import { useMessage } from 'naive-ui'

export default {
    components: { VueWordCloud },
    setup() {
        const router = useRouter();
        const terminalOutput = ref("");
        const showCloud = ref(false);
        const shownResults = ref([]);
        const progRunning = ref(false);
        const message = useMessage()

        const serverIP = ref("192.168.79.129");
        const serverPort = ref("43151");
        const productId = ref("");
        const pageCount = ref(10);

        function getRandomColor() {
            var letters = "0123456789ABCDEF";
            var color = "#";

            for (var i = 0; i < 6; i++) {
                color += letters[Math.floor(Math.random() * 16)];
            }

            return color;
        }

        let totalPopTexts = 0;
        const maxPopTexts = 100;


        function createRandomText(text) {
            // 创建一个新的文本标签元素
            var newText = document.createElement("div");
            let outer = document.getElementById("appOuter");
            // 设置文本内容
            newText.textContent = text;

            // 设置样式
            newText.style.position = "absolute";
            newText.style.left = Math.random() * window.innerWidth + "px";
            newText.style.top = Math.random() * window.innerHeight + "px";
            newText.style.fontSize = Math.random() * 40 + 10 + "px";
            newText.style.color = getRandomColor();

            // 添加文本标签到页面
            outer.appendChild(newText);

            // 动画效果
            var fadeEffect = setInterval(function () {
                if (!newText.style.opacity) {
                    newText.style.opacity = 1;
                }
                newText.style.opacity -= 0.02;
                newText.style.fontSize = parseFloat(newText.style.fontSize) + 0.5 + "px";
                if (newText.style.opacity <= 0) {
                    clearInterval(fadeEffect);
                    // 移除文本标签
                    outer.removeChild(newText);
                    totalPopTexts--;
                }
            }, 10);

            // 一秒钟后消失
            setTimeout(function () {
                clearInterval(fadeEffect);
                // 移除文本标签
                outer.removeChild(newText);
            }, 1000);
        }

        let popWords = [];

        setInterval(renderWords, 5);

        function renderWords() {
            let item = popWords.pop();
            if (item != null) {
                if (!showCloud.value) {
                    if (totalPopTexts < maxPopTexts) {
                        createRandomText(item);
                        totalPopTexts++;
                    }

                }
            }

        }



        function runProg() {
            progRunning.value = true;
            var net = require('net');
            let results = [];
            // 连接到服务器，提交工作需求
            var client = net.connect({ host: serverIP.value, port: Number(serverPort.value) }, function () {
                console.log('连接到服务器！');
                client.write("#StartProgram" + productId.value + "#" + pageCount.value + "\n")
            });
            // 使用缓冲区接收大量响应数据，并进行对应的处理。
            let cache = "";
            client.on('data', function (data) {
                let result = data.toString();
                let unhandledMessages = [];
                cache = cache + result;
                if (cache.indexOf("|") != -1) {
                    unhandledMessages = cache.split("|");
                    cache = unhandledMessages[unhandledMessages.length - 1];
                }

                let sentence;

                for (let item of unhandledMessages) {
                    sentence = item;
                    if (item != "") {
                        try {
                            let parsedResult = JSON.parse(item);
                            // console.log(parsedResult);
                            if (parsedResult.type == "result") {
                                let tuple = parsedResult.data.split(" ");
                                tuple[1] = Number(tuple[1]);
                                results.push(tuple);
                            } else if (parsedResult.type == "word") {
                                popWords.push(parsedResult.data);
                            } else if (parsedResult.type == "end") {
                                console.log("end");
                                message.loading(
                                    "任务已完成，正在渲染词云..."
                                );
                                results.sort((a, b) => {
                                    return (a[1] < b[1]);
                                })
                                results = results.filter((item) => {
                                    return (stopwords.indexOf(item[0]) == -1);
                                })
                                results = results.slice(0, 500);
                                console.log(results);

                                setTimeout(() => {
                                    showCloud.value = true;
                                    shownResults.value = results;
                                }, 1000);
                            } else {
                                terminalOutput.value += parsedResult.data + "\n";
                            }
                        } catch (e) {
                            // console.log(sentence);
                            console.log(e);
                        }
                    }
                }
            });
            client.on('end', function () {
                console.log('断开与服务器的连接');
            });
        }

        return {
            terminalOutput, showCloud, shownResults, runProg, serverIP, serverPort, productId, pageCount, progRunning
        }
    }
}
</script>

<style scoped lang="scss">
#appOuter {
    height: 100vh;
    width: 100vw;
    position: relative;
    // background-color:#f9fbfc;
    color: #2e2f31;
    overflow: hidden;

    display: flex;

    .mask {
        position: absolute;
        height: 100vh;
        width: 100vw;
        background-image: linear-gradient(to top, #ffffff00, #ffffffff);

    }

    .output {
        white-space: pre-line; // 合并空白符序列，但保留换行符
        font-size: 30px;
        height: 100vh;
        width: 100vw;
        overflow: hidden;
        display: flex;
        align-items: flex-end;
    }

    .starter {
        position: absolute;
        height: 100vh;
        width: 100vw;
        z-index: 20;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;

        .group {
            position: relative;
            margin-bottom: 45px;
        }

        input {
            font-size: 18px;
            padding: 10px 10px 10px 5px;
            display: block;
            width: 300px;
            border: none;
            border-bottom: 1px solid #757575;
        }

        input:focus {
            outline: none;
        }

        /* LABEL ======================================= */
        label {
            color: #999;
            font-size: 18px;
            font-weight: normal;
            position: absolute;
            pointer-events: none;
            left: 5px;
            top: 10px;
            transition: 0.2s ease all;
            -moz-transition: 0.2s ease all;
            -webkit-transition: 0.2s ease all;
        }

        /* active state */
        input:focus~label,
        input:valid~label {
            top: -20px;
            font-size: 14px;
            color: #52ae5a;
        }

        /* BOTTOM BARS ================================= */
        .bar {
            position: relative;
            display: block;
            width: 300px;
        }

        .bar:before,
        .bar:after {
            content: '';
            height: 2px;
            width: 0;
            bottom: 1px;
            position: absolute;
            background: #52ae66;
            transition: 0.2s ease all;
            -moz-transition: 0.2s ease all;
            -webkit-transition: 0.2s ease all;
        }

        .bar:before {
            left: 50%;
        }

        .bar:after {
            right: 50%;
        }

        /* active state */
        input:focus~.bar:before,
        input:focus~.bar:after {
            width: 50%;
        }

        /* HIGHLIGHTER ================================== */
        .highlight {
            position: absolute;
            height: 60%;
            width: 100px;
            top: 25%;
            left: 0;
            pointer-events: none;
            opacity: 0.5;
        }

        /* active state */
        input:focus~.highlight {
            -webkit-animation: inputHighlighter 0.3s ease;
            -moz-animation: inputHighlighter 0.3s ease;
            animation: inputHighlighter 0.3s ease;
        }

        /* ANIMATIONS ================ */
        @-webkit-keyframes inputHighlighter {
            from {
                background: #52ae75;
            }

            to {
                width: 0;
                background: transparent;
            }
        }

        @-moz-keyframes inputHighlighter {
            from {
                background: #52ae71;
            }

            to {
                width: 0;
                background: transparent;
            }
        }

        @keyframes inputHighlighter {
            from {
                background: #52ae66;
            }

            to {
                width: 0;
                background: transparent;
            }
        }
    }

}
</style>
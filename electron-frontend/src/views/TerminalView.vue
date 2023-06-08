<template>
    <div class="outer">
        <n-scrollbar style="height:100%">
            <div class="title  bold">
                控制台
            </div>
            <div class="content">
                <div class="terminalArea">
                    <n-scrollbar>
                        {{terminalContent}}
                    </n-scrollbar>
                </div>
                <n-input-group>
                    <n-select :style="{ width: '18%' }" :options="selectOptions" v-model:value="commandMode" />
                    <n-input placeholder="输入消息" v-model:value="terminalInput" :style="{ width: '100%' }" 
                        @keyup="handleKeyUp"/>
                    <n-button type="primary" @click="sendCommand">
                        发送
                    </n-button>
                </n-input-group>
            </div>
        </n-scrollbar>
    </div>
</template>

<script>
import { h, ref, defineComponent, inject } from "vue";
import { useRouter } from "vue-router";
const { ipcRenderer } = require("electron");

export default {
    setup() {
        // const router = useRouter();
        // const socket = inject("socket");

        // let terminalContent = ref("");
        // let terminalInput = ref("");

        // let selectOptions = ref([
        //     {
        //         label:"命令",
        //         value:"command"
        //     },
        //     {
        //         label:"全服广播",
        //         value:"broadcast"
        //     },
        //     {
        //         label:"私聊",
        //         value:"private"
        //     }
        // ])

        // let commandMode = ref("command")

        // socket.onmessage = function (event) {
        //     // console.log(event.data);
        //     terminalContent.value = terminalContent.value + event.data;
        // }

        // function sendCommand(){
        //     if(terminalInput.value == "") return;
        //     let command = "";
        //     if(commandMode.value == "command"){
        //         command = (terminalInput.value + "\n").toString("utf-8");
        //     } else if(commandMode.value == "broadcast"){
        //         command = `tellraw @a {"text": "${terminalInput.value}", "color": "blue"}\n`.toString("utf-8");
        //     }
        //     socket.send(command);
        //     terminalInput.value = "";
        // }

        // function handleKeyUp(e) {
        //     if(e.key == "Enter"){
        //         sendCommand()
        //     }
        // }

        // return {
        //     terminalContent, terminalInput, sendCommand, handleKeyUp,  selectOptions, commandMode
        // }
    }
}
</script>

<style scoped lang="scss">
.outer {
    height: 100%;
    width: 100%;
    position:relative;
    // background-color:#f9fbfc;
    color: #2e2f31;
    padding: 50px 10px 0 15px;
    .title{
        font-size: 28px;
        user-select: none;
    }
    .content{
        margin:10px 0;
        position:relative;
        width:96%;
        .terminalArea{
            height:calc(100vh - 210px) !important;
            border:#dedede solid 1px;
            border-radius: 4px;
            margin-bottom:5px;
            white-space:pre-wrap;
        }
    }
}
</style>
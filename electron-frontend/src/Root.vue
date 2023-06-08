<template>
    <n-message-provider>
        <div id="windowTop">
            <div class="btn clickable" @click="refresh">
                <n-icon size="22">
                    <ArrowBack />
                </n-icon>
            </div>
            <div class="left" @mousedown="windowTopMousedown" @dblclick="windowMaximize"></div>
            <div class="right">
                <div class="btn clickable" @click="windowMinimize">
                    <n-icon size="14">
                        <WindowMinimizeRegular />
                    </n-icon>
                </div>
                <!-- <div class="btn clickable" @click="windowMaximize">
                <n-icon size="14">
                    <WindowMaximizeRegular />
                </n-icon>
            </div> -->
                <div class="btn clickable close" @click="windowClose">
                    <n-icon size="22">
                        <Close />
                    </n-icon>
                </div>
            </div>
        </div>
        <router-view></router-view>
    </n-message-provider>
</template>

<script>
import { Close, ArrowBack } from "@vicons/ionicons5";
import { WindowMinimizeRegular, WindowMaximizeRegular } from "@vicons/fa";
const { ipcRenderer } = require("electron");
export default {
    components: {
        Close, WindowMinimizeRegular, WindowMaximizeRegular, ArrowBack,
    },
    setup() {
        // 窗口拖动功能
        let windowControl = {
            isKeyDown: false,
            dinatesX: 0,
            dinatesY: 0,
            sizeX: 0,
            sizeY: 0,
            moved: false
        }

        document.onmousemove = (ev) => {
            if (windowControl.isKeyDown) {
                const x = Math.floor(ev.screenX - windowControl.dinatesX);
                const y = Math.floor(ev.screenY - windowControl.dinatesY);
                windowControl.moved = true;
                //给主进程传入坐标
                ipcRenderer.send('move-application', {
                    appX: x,
                    appY: y
                })
            }
        };

        document.onmouseup = (ev) => {
            if (windowControl.isKeyDown) {
                windowControl.isKeyDown = false;
                if (windowControl.moved) {
                    windowControl.moved = false;
                    ipcRenderer.send('end-move-application');
                }
            }
        };

        function windowTopMousedown(e) {
            windowControl.isKeyDown = true;
            windowControl.dinatesX = e.x;
            windowControl.dinatesY = e.y;
            ipcRenderer.send('start-move-application');
        }

        function windowMaximize() {
            ipcRenderer.send('window-maximize');
        }

        function windowMinimize() {
            ipcRenderer.send('window-minimize');
        }

        function windowClose() {
            ipcRenderer.send('window-close');
        }

        function refresh() {
            window.location.reload();
        }

        return {
            windowTopMousedown, windowMinimize, windowMaximize, windowClose,
            Close, WindowMinimizeRegular, WindowMaximizeRegular, ArrowBack, refresh
        }
    }
}
</script>

<style lang="scss">
#windowTop {
    user-select: none;
    // background-color: #f9fbfc;
    color: #2e2f31;
    display: flex;
    justify-content: space-between;
    width: 100vw;
    position: absolute;
    z-index: 10086;
    height: 40px;

    .left {
        height: 100%;
        width: calc(100%);
        display: flex;
        align-items: center;
        padding-left: 10px;
        white-space: nowrap;
        font-size: 30px;
    }

    .right {
        display: flex;
    }

    .btn {
        width: 50px;
        height: 40px;
        display: flex;
        justify-content: center;
        align-items: center;
        transition: all .3s;
        flex-shrink: 0;
        color: #2e2f31;
        background-color: #ffffff88;
    }

    .btn.clickable {
        color: #2e2f31;
    }

    .btn.clickable:hover {
        background-color: #626262;
        color: #f9fbfc;
    }

    .btn.clickable:active {
        background-color: #0a0d10;
        transition: none;
    }

    .btn.clickable.close:hover {
        background-color: rgb(215, 21, 38);
    }

    .btn.clickable.close:active {
        background-color: rgb(212, 88, 98);
        transition: none;
    }
}

@font-face {
    font-family: "阿里巴巴普惠体";
    src: url('./static/阿里巴巴普惠体.ttf'),
}

* {
    font-family: "阿里巴巴普惠体";
}

*.bold {
    font-weight: bold;
}
</style>
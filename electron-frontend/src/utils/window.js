const electron = require('electron')
import appWindows from '../background.js';

//管理主进程监听

let moveFixedSize = undefined;
let resetCount = 0;
electron.ipcMain.on('start-move-application',(event,res) => {
    moveFixedSize = appWindows.mainWindow.getSize();
    // console.log("开始拖动",moveFixedSize);
})

electron.ipcMain.on('end-move-application',(event,res) => {
    // console.log("停止拖动",appWindows.mainWindow.getSize());
    resetCount = 0;
    let resetSizeInterval = setInterval(()=>{
        //由于electron的某些bug，导致必须要反复确认窗口大小是否恢复拖动前的状态，目前只能这样做，不知道有没有更好的方案。
        appWindows.mainWindow.setSize(moveFixedSize[0],moveFixedSize[1]);
        let curSize = appWindows.mainWindow.getSize();
        if((curSize[0] == moveFixedSize[0] && curSize[1] == moveFixedSize[1]) || resetCount >= 10){
            clearInterval(resetSizeInterval);
        }
        resetCount ++;
        // console.log("重置大小",resetCount,appWindows.mainWindow.getSize());
    }, 10);
})

electron.ipcMain.on('move-application',(event,res) => {
    appWindows.mainWindow && appWindows.mainWindow.setPosition(res.appX,res.appY);
    appWindows.mainWindow.setSize(moveFixedSize[0],moveFixedSize[1]);
})

import execAnimate from './animate.js'
electron.ipcMain.on('resize-application',(event,res) => {
    let beginSize = appWindows.mainWindow.getSize();
    let finalSize = [res.sizeX,res.sizeY]
    let animateExector = new execAnimate({
        setFunc: function(value){
            let process = value/100;
            let curSize = [
                beginSize[0] + process * (finalSize[0] - beginSize[0]),
                beginSize[1] + process * (finalSize[1] - beginSize[1])
            ]
            appWindows.mainWindow.setSize(Math.floor(curSize[0]),Math.floor(curSize[1]));
        },
        sV:1,
        eV:100,
        aimSpeed:res.speed
    })
    animateExector.start();
    // appWindows.mainWindow.setSize(res.sizeX,res.sizeY);
})

electron.ipcMain.on('resize-application-non-animation',(event,res) => {
    appWindows.mainWindow.setSize(res.sizeX,res.sizeY);
})

electron.ipcMain.on('window-close',(event,res) => {
    appWindows.mainWindow && appWindows.mainWindow.destroy();
})

electron.ipcMain.on('window-minimize',(event,res) => {
    appWindows.mainWindow && appWindows.mainWindow.minimize();
})

electron.ipcMain.on('window-maximize',(event,res) => {
    if(appWindows.mainWindow.isMaximized()){
        appWindows.mainWindow && appWindows.mainWindow.restore();
    } else {
        appWindows.mainWindow && appWindows.mainWindow.maximize();
    }
    
})
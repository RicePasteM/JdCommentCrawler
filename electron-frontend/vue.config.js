const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  pluginOptions: {
    electronBuilder: {
      // 在vue中能引入electron模块
      nodeIntegration: true,
    }
  }

})

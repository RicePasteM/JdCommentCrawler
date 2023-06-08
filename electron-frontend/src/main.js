import { createApp } from 'vue'
import App from './Root.vue'
import router from './router'
import store from './store'
import naive from 'naive-ui'

import Vue from 'vue';
 

createApp(App).use(store).use(router).use(naive).mount('#app')

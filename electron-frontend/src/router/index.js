import { createRouter, createWebHashHistory } from 'vue-router'
import Start from "../Start.vue"

const routes = [
  {
    route:"/",
    component:Start
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router

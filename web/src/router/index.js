import { createRouter, createWebHistory } from 'vue-router'
import ChatIndexView from '../views/chat/ChatIndexView'
import NotFoundView from '../views/error/NotFoundView'
import LogInView from '../views/user/account/LogInView'
import ChatAudioView from '../views/chat/audio/ChatAudioView'
import ChatVideoView from '../views/chat/video/ChatVideoView'
import RegisterView from '../views/user/account/RegisterView'
import BotIndexView from '../views/user/bot/BotIndexView'
import PkIndexView from '../views/pk/PkIndexView'
import store from '../store/index'


const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/"
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: "/user/bot/",
    name: "bot_index",
    component: BotIndexView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: "/chat/",
    name: "chat_index",
    component: ChatIndexView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: "/chat/video/",
    name: "chat_video",
    component: ChatVideoView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: "/chat/audio/",
    name: "chat_audio",
    component: ChatAudioView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: "/login/",
    name: "login_index",
    component: LogInView,
    meta: {
      requestAuth: false,
    },
  },
  {
    path: "/register/",
    name: "register_index",
    component: RegisterView,
    meta: {
      requestAuth: false,
    },
  },
  {
    path: "/404/",
    name: "404",
    component: NotFoundView,
    meta: {
      requestAuth: false,
    },
  },
  {
    path: "/:catchAll(.*)",
    redirect: "/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to,from,next)=>{
    if(to.meta.requestAuth&&!store.state.user.is_login)
      next({name:"user_account_token"});
    else
      next();
})

export default router

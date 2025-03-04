<template>
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
    <div class="container">
        <router-link class="navbar-brand" :to="{name: 'home'}">Draw Chess</router-link>
        <div class="collapse navbar-collapse" id="navbarText">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
              <!-- <li class="nav-item">
              <router-link class="nav-link" active-class="active" aria-current="page" :to="{name: 'chat_index'}">聊天</router-link>
              </li> -->
              <li class="nav-item">
                <router-link class="nav-link" active-class="active" aria-current="page" :to="{name: 'chat_video'}">PK</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" active-class="active" aria-current="page" :to="{name: 'chat_audio'}">Bot</router-link>
              </li>
          </ul>
          <ul class="navbar-nav" v-if="$store.state.user.is_login">
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                {{ $store.state.user.username }}
              </a>
              <ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#">个人主页</a></li>
                <li>
                  <router-link class="dropdown-item" :to="{name: 'bot_index'}">我的Bot</router-link>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" @click="logout">退出登录</a></li>
              </ul>
            </li>
          </ul>
          <ul class="navbar-nav" v-else>
            <li class="nav-item">
              <router-link class="nav-link" :to="{name: 'login_index'}" role="button">
                登录/注册
              </router-link>
            </li>
          </ul>
        </div>
    </div>
    </nav>
</template>

<script>
import { useStore } from 'vuex'
export default {
  setup() {
    const store = useStore()
    const logout = () => {
      store.dispatch("logout")
    }
    return {
      logout
    }
  }
}
</script>

<style scoped>
</style>
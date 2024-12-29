<template>
  <NavBar></NavBar>
  <router-view></router-view>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
import { useStore } from 'vuex'
import router from './router/index'


export default {
  components: {
    NavBar
  },
  created() {
    let store = useStore()
    store.dispatch("getinfo", {
        success() {
            router.push({ name: "home" })
        },
        error() {
        }
    })
  }
}
</script>

<style>
</style>

<template>
    <ContentField v-if="show_content">
        <div class="log-body">
            <div class="row justify-content-md-center log-ui">
                <div class="col-3">
                    <ul class="nav nav-tabs nav-justified">
                        <li class="nav-item">
                            <router-link class="nav-link" active-class="active" aria-current="page" :to="{name: 'login_index'}">登录</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link class="nav-link" active-class="active" aria-current="page" :to="{name: 'register_index'}">注册</router-link>
                        </li>
                    </ul>
                </div>
                <span class="border border-top-0"></span>
                <div class="col-3 log-ui2">
                    <form @submit.prevent="login">
                        <div class="form-floating mb-3">
                            <input v-model="username" class="form-control" id="floatingInput" placeholder="输入用户名">
                            <label for="floatingInput">用户名</label>
                            </div>
                            <div class="form-floating">
                            <input v-model="password" type="password" class="form-control" id="floatingPassword" placeholder="输入密码">
                            <label for="floatingPassword">密码</label>
                        </div>
                        <div class="error-message">{{ error_message }}</div>
                        <button type="submit" class="btn btn-primary">登录</button>
                    </form>
                </div>
            </div>
        </div>
    </ContentField>
</template>

<script setup>
import ContentField from '../../../components/ContentField.vue'
import { useStore } from 'vuex'
import { ref } from 'vue'
import router from '../../../router/index'

let username = ref('')
let password = ref('')
let error_message = ref('')
let store = useStore()

const login = () => {
    error_message.value = ""
    store.dispatch("login", {
        username: username.value,
        password: password.value,
        success() {
            store.dispatch("getinfo", {
                success() {
                    router.push({ name: "home" })
                }
            })
        },
        error() {
            error_message.value = "用户名或密码错误！"
        }
    })
}

let show_content = ref(false)
const jwt_token = localStorage.getItem("jwt_token")
if (jwt_token) {
    store.commit("updateToken", jwt_token)
    store.dispatch("getinfo", {
        success() {
            router.push({ name : "home" })
        },
        error() {
            show_content = true
        }
    })
}
else {
    show_content = true
}

</script>
<style scoped>
button {
    width: 100%;
    margin-top: 10px;
}
.log-body {
    margin: 0 auto;
}
.error-message {
    margin: 5px;
    color: red;
}
.log-ui {
    margin-top: 10px;
}
.log-ui2 {
    margin-top: 10px;
}
</style>
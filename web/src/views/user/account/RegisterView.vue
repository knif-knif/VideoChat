<template>
    <ContentField>
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
                    <form @submit.prevent="register">
                        <div class="form-floating mb-3">
                            <input v-model="username" class="form-control" id="floatingInput" placeholder="输入用户名">
                            <label for="floatingInput">用户名</label>
                        </div>
                        <div class="form-floating mb-3">
                            <input v-model="password" type="password" class="form-control" id="floatingPassword" placeholder="输入密码">
                            <label for="floatingPassword">密码</label>
                        </div>
                        <div class="form-floating">
                            <input v-model="password_confirm" type="password" class="form-control" id="floatingPasswordConfirm" placeholder="确认密码">
                            <label for="floatingPasswordConfirm">确认密码</label>
                        </div>
                        <div class="error-message">{{ error_message }}</div>
                        <button type="submit" class="btn btn-primary">注册</button>
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
let password_confirm = ref('')
let error_message = ref('')
let store = useStore()

const register = () => {
    error_message.value = ""
    if (password.value != password_confirm.value) {
        error_message.value = "两次密码不一致！"
        return;
    }
    store.dispatch("register", {
        username: username.value,
        password: password.value,
        success(resp) {
            if (resp.error_message === "success") {
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
                        error_message.value = "网络错误！" + "无法登录！"
                    }
                })
            }
            else error_message.value = resp.error_message
        },
        error(resp) {
            error_message.value = "网络错误！" 
            console.log(resp)
        }
    })
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
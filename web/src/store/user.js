import $ from 'jquery'

const uri = "http://127.0.0.1:3000/"

export default {

    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
    },
    getters: {},
    mutations: {
        updateUser(state, user) {
            state.id = user.id
            state.username = user.username
            state.photo = user.photo
            state.is_login = user.is_login
            state.token = localStorage.getItem("jwt_token")
        },
        updateToken(state, token) {
            state.token = token
        },
        logout(state) {
            state.id = ""
            state.username = ""
            state.photo = ""
            state.token = ""
            state.is_login = false
        },
    },
    actions: {
        login(context, data) {
            $.ajax({
                url: uri + "user/account/token/",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        localStorage.setItem("jwt_token", resp.token)
                        context.commit("updateToken", resp.token)
                        data.success(resp)
                    }
                    else {
                        data.error(resp)
                    }
                },
                error(resp) {
                    data.error(resp)
                }
            })
        },
        getinfo(context, data) {
            $.ajax({
                url: uri + "user/account/info/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("jwt_token")
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        context.commit("updateUser", {
                            ...resp,
                            is_login: true,
                        })
                        data.success(resp)
                    }
                    else {
                        context.commit("logout")
                        data.error(resp)
                    }
                },
                error(resp) {
                    data.error(resp)
                },
                async: false
            })
        },
        logout(context) {
            localStorage.removeItem("jwt_token")
            context.commit("logout")
        },
        register(context, req_data) {
            $.ajax({
                url: uri + "user/account/register/",
                type: "post",
                data: {
                    username: req_data.username,
                    password: req_data.password,
                },
                success(resp) {
                    console.log(resp)
                    req_data.success(resp)
                },
                error(resp) {
                    req_data.error(resp)
                }
            })
        }
    }
}
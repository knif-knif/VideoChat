export default {
    state: {
        socket: null,
        user_name: "",
        user_photo: "",
        status: "chatting",
    },
    getters: {},
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket
        },
        updateUser(state, user) {
            state.user_name = user.username
            state.user_photo = user.phtot
        },
        updateStatus(state, status) {
            state.status = status
        }

    },
    actions: {},
    modules: {}
}
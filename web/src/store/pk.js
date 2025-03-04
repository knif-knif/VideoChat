export default {
    state: {
        status: "matching",
        socket: null,
        opponent_username: "",
        opponent_photo: "",
        gamemap: null,
        a_id: 0,
        b_id: 0,
        gameObject: null,
        winner: "none",
    },
    getters: {
        
    },
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket

        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username
            state.opponent_photo = opponent.photo
        },
        updateStatus(state, status) {
            state.status = status
        },
        updateGame(state, game) {
            state.gamemap = game.map
            state.a_id = game.a_id
            state.b_id = game.b_id
        },
        updateGameObject(state, gameObject) {
            state.gameObject = gameObject
        },
        updateWinner(state, winner) {
            state.winner = winner
        }
    },
    actions: {

    },
    modules: {

    }
}
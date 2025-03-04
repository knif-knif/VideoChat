<template>
    <div class="result-board">
        <div class="result-board-text" v-if="$store.state.pk.winner == 'ALL'">
            Draw
        </div>
        <div class="result-board-text" v-if="($store.state.pk.winner === 'A' && $store.state.pk.a_id == $store.state.user.id) || 
        ($store.state.pk.winner === 'B' && $store.state.pk.b_id == $store.state.user.id)">
            Win
        </div>
        <div class="result-board-text" v-else>
            Lose
        </div>
        <div class="result-board-btn">
            <button @click="restart" type="button" class="btn btn-warning btn-lg">
                再来一把
            </button>
        </div>
    </div>
    

</template>

<script>
import { useStore } from "vuex"

export default {
    setup() {
        const store = useStore()
        const restart = () => {
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })
            store.commit("updateStatus", "matching")
            store.commit("updateWinner", "none")
            console.log(store.state.pk.winner)
        }
        return {
            restart
        }
    }
}

</script>

<style scoped>
div.result-board {
    height: 30vh;
    width: 30vw;
    background-color: rgba(50, 50, 50, 0.5);
    position: absolute;
    top: 30vh;
    left: 35vw;
}

div.result-board-text {
    text-align: center;
    color: white;
    font-size: 50px;
    font-style: italic;
    font-weight: 600;
    padding-top: 5vh;
}

div.result-board-btn {
    padding-top: 7vh;
    text-align: center;
}

</style>
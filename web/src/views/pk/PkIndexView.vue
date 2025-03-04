<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'"/>
    <MatchGround v-if="$store.state.pk.status === 'matching'"/>
    <ResultBoard v-if="$store.state.pk.winner !== 'none'"/>
</template>

<script>
import PlayGround from "@/components/PlayGround.vue";
import MatchGround from "@/components/MatchGround.vue";
import ResultBoard from "@/components/ResultBoard.vue";
import { onMounted, onUnmounted } from "vue";
import { useStore } from "vuex";

export default {
    components: {
        PlayGround,
        MatchGround,
        ResultBoard
    },
    setup() {
        const store = useStore()
        const socketUrl = `ws://127.0.0.1:3000/ws/${store.state.user.token}/`
        let socket = null
        onMounted(() => {
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })
            socket = new WebSocket(socketUrl)
            
            socket.onopen = () => {
                store.commit("updateSocket", socket)
            }

            socket.onmessage = msg => {
                const data = JSON.parse(msg.data)
                if (data.event === "start-matching") {
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    })
                    setTimeout(()=> {
                        store.commit("updateStatus", "playing")
                    }, 200)
                    store.commit("updateGame", data.game)
                    
                } else if (data.event === "move") {
                    const game = store.state.pk.gameObject
                    const [ cp0, cp1 ] = game.cps 
                    const opt = JSON.parse(data.operate)
                    const nxtp = data.next
                    if (game.nxp === 0) {
                        cp0.push_chess(opt.op, opt.x, opt.y, opt.nx, opt.ny)
                    }
                    else {
                        cp1.push_chess(opt.op, opt.x, opt.y, opt.nx, opt.ny)
                    }
                    
                    if (nxtp == store.state.user.id) game.nxp = game.cid
                    else game.nxp = 1 - game.cid
                    
                } else if (data.event === "result") {
                    const game = store.state.pk.gameObject
                    const [ cp0, cp1 ] = game.cps
                    if (data.winner === "A" || data.winner === "ALL") cp0.status = "end"
                    if (data.winner === "B" || data.winner === "ALL") cp1.status = "end"
                    store.commit("updateWinner", data.winner)
                }
            }

            socket.onclose = () => {
                console.log("close!")
            }
        })
        
        onUnmounted(() => {
            socket.close()
            store.commit("updateStatus", "matching")
        })
    }
}


</script>

<style scoped>

</style>
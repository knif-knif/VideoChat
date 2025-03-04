<template>
     <ChatGround>
          <div class="chat-history">
               <div class="form-floating">
                    <textarea v-model="history_msg" class="form-control" placeholder="Leave a comment here" id="chathistoryarea" style="height: 600px" disabled></textarea>
                    <label for="chathistoryarea">历史消息</label>
               </div>
          </div>
          <div class="chat-input">
               <div class="form-floating">
                    <textarea v-model="msg" class="form-control" placeholder="输入消息" id="chatinputarea" style="height: 100px"></textarea>
               </div>
               <div class="row justify-content-end send-btn">
                    <button @click="send_msg" type="submit" class="btn btn-outline-primary">发送</button>
               </div>
          </div>
     </ChatGround>
</template>

<script setup>
import ChatGround from '../../components/ChatGround.vue'
import { onMounted, onUnmounted, ref } from 'vue'
import { useStore } from "vuex"

const store = useStore()
const socketUrl = `ws://localhost:3000/ws/${store.state.user.token}/`
let socket = null
let msg = ref('')
let history_msg= ref('')
let send_msg = () => {
     socket.send(JSON.stringify({
          msg: msg.value,
          userId: store.state.user.id
     }))
     history_msg.value += msg.value
     msg.value = ""
}
onMounted(() => {
     socket = new WebSocket(socketUrl)
     socket.onopen = () => {
          console.log("connected!")
     }
     socket.onmessage = msg => {
          const data = JSON.parse(msg.data)
          console.log(data)
     }
     socket.onclose = () => {
          console.log("disconnected!")
     }
})
onUnmounted(() => {
     socket.close()
})

</script>

<style scoped>
.chat-input {
     margin-top: 10px;
}
button {
     margin-top: 10px;
     width: 10%;
}
.send-btn {
     margin: 20px;
}
</style>
<template>
    <div class='container'>
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width: 100%;">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-header">
                        <span style="font-size: 130%;"> 我的Bot </span>
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                            创建Bot
                        </button>

                        <!-- Modal -->
                        <div class="modal fade" id="add-bot-btn" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
                        <div class="modal-dialog modal-xl">
                            <div class="modal-content">
                            <div class="modal-header">
                                <h1 class="modal-title fs-5" id="staticBackdropLabel">创建Bot</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <div class="mb-3">
                                  <label for="add-bot-title" class="form-label"> 名称 </label>
                                  <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                </div>
                                <div class="mb-3">
                                  <label for="add-bot-description" class="form-label"> 简介 </label>
                                  <textarea v-model="botadd.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                </div>
                                <div class="mb-3">
                                  <label for="add-bot-code" class="form-label"> 代码 </label>
                                  <VAceEditor v-model:value="botadd.content" @init="editorInit" lang="c_cpp"
                                              :theme="aceConfig.theme" style="height: 300px"
                                              :options="aceConfig.options" class="ace-editor" />
                                </div>
                            </div>
                            <div class="modal-footer">
                              <div class="error-message"> {{ botadd.error_message }} </div>
                                <button @click="add_bot" type="button" class="btn btn-primary">创建</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                            </div>
                            </div>
                        </div>
                        </div>

                    </div>
                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th> 名称 </th>
                                    <th> 创建时间 </th>
                                    <th> 操作 </th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td> {{ bot.title }} </td>
                                    <td> {{ bot.createtime }} </td>
                                    <td>
                                        <button type="button" class="btn btn-outline-secondary" style="margin-right: 10px;"  data-bs-toggle="modal" :data-bs-target="'#update-bot-modal-' + bot.id"> 修改 </button>

                                        <!-- Modal -->
                                        <div class="modal fade" :id="'update-bot-modal-' + bot.id" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
                                          <div class="modal-dialog modal-xl">
                                            <div class="modal-content">
                                              <div class="modal-header">
                                                <h1 class="modal-title fs-5" id="staticBackdropLabel">修改Bot</h1>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                              </div>
                                              <div class="modal-body">
                                                <div class="mb-3">
                                                  <label for="add-bot-title" class="form-label"> 名称 </label>
                                                  <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                                </div>
                                                <div class="mb-3">
                                                  <label for="add-bot-description" class="form-label"> 简介 </label>
                                                  <textarea v-model="bot.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                                </div>
                                                <div class="mb-3">
                                                  <label for="add-bot-code" class="form-label"> 代码 </label>
                                                  <VAceEditor v-model:value="bot.content"
                                                              @init="editorInit" lang="c_cpp"
                                                              :theme="aceConfig.theme" style="height: 300px"
                                                              :options="aceConfig.options" class="ace-editor" />
                                                </div>
                                              </div>
                                              <div class="modal-footer">
                                                <div class="error-message"> {{ botadd.error_message }} </div>
                                                <button @click="update_bot(bot)" type="button" class="btn btn-primary">提交</button>
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                              </div>
                                            </div>
                                          </div>
                                        </div>

                                        <button @click="remove_bot(bot)" type="button" class="btn btn-outline-danger"> 删除 </button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, reactive } from 'vue'
import $ from 'jquery'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from "vue3-ace-editor"

import "ace-builds/webpack-resolver";
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';

export default {
    components: {
      VAceEditor,
    },
    setup() {
      const aceConfig = reactive({
        theme: 'chrome', //主题
        arr: [
          /*所有主题*/
          "ambiance",
          "chaos",
          "chrome",
          "clouds",
          "clouds_midnight",
          "cobalt",
          "crimson_editor",
          "dawn",
          "dracula",
          "dreamweaver",
          "eclipse",
          "github",
          "gob",
          "gruvbox",
          "idle_fingers",
          "iplastic",
          "katzenmilch",
          "kr_theme",
          "kuroir",
          "merbivore",
          "merbivore_soft",
          "monokai",
          "mono_industrial",
          "pastel_on_dark",
          "solarized_dark",
          "solarized_light",
          "sqlserver",
          "terminal",
          "textmate",
          "tomorrow",
          "tomorrow_night",
          "tomorrow_night_blue",
          "tomorrow_night_bright",
          "tomorrow_night_eighties",
          "twilight",
          "vibrant_ink",
          "xcode",
        ],
        readOnly: false, //是否只读
        options: {
          enableBasicAutocompletion: true,
          enableSnippets: true,
          enableLiveAutocompletion: true,
          tabSize: 2,
          showPrintMargin: false,
          fontSize: 16
        }
      });
        const store = useStore()
        let bots = ref([])
        const botadd = reactive({
          title: '',
          description: '',
          content : '',
          error_message: '',
        })
        const refresh_bots = ()=> {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/getlist/",
                type: "GET",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp
                }
            })
        }
        refresh_bots()

        const add_bot = () => {
          botadd.error_message = ""
          $.ajax({
              url: "http://127.0.0.1:3000/user/bot/add/",
              type: "POST",
              data: {
                  title: botadd.title,
                  description: botadd.description,
                  content: botadd.content,
              },
              headers: {
                  Authorization: "Bearer " + store.state.user.token,
              },
              success(resp) {
                  if (resp.error_message == "success") {
                      botadd.error_message = ""
                      botadd.content = ""
                      botadd.title = ""
                      botadd.description = ""
                      Modal.getInstance("#add-bot-btn").hide()
                      refresh_bots()
                  }
                  else botadd.error_message = resp.error_message
              }
          })
        }

        const remove_bot = (bot) => {
          $.ajax({
            url: "http://127.0.0.1:3000/user/bot/remove/",
            type: "POST",
            data: {
              bot_id: bot.id,
            },
            headers: {
              Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
              if (resp.error_message == "success") {
                refresh_bots()
              }
            }
          })
        }

      const update_bot = (bot) => {
          console.log('update bot')
        botadd.error_message = ""
        $.ajax({
          url: "http://127.0.0.1:3000/user/bot/update/",
          type: "POST",
          data: {
            bot_id: bot.id,
            title: bot.title,
            description: bot.description,
            content: bot.content,
          },
          headers: {
            Authorization: "Bearer " + store.state.user.token,
          },
          success(resp) {
            if (resp.error_message == "success") {
              console.log(bot)
              botadd.error_message = ""
              Modal.getInstance("#update-bot-modal-" + bot.id).hide()
              refresh_bots()
            }
            else botadd.error_message = resp.error_message
          }
        })
      }

        return {
          bots,
          botadd,
          aceConfig,
          add_bot,
          remove_bot,
          update_bot
        }
    }
}

</script>

<style scoped>
div.error-message{
  color: red;
}
</style>
import { createStore } from 'vuex'
import ModuleUser from './user'
import ModuleChat from './chat'
import ModulePk from './pk'
import ModuleRecord from './record'
import ModuleUri from './uri'

export default createStore({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user: ModuleUser,
    chat: ModuleChat,
    pk: ModulePk,
    record: ModuleRecord,
    uri: ModuleUri,
  }
})

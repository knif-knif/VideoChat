<template>
    <ContentField>
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                   <th> 蓝方 </th>
                   <th> 红方 </th>
                   <th> 对战结果 </th>
                   <th> 对战时间 </th>
                   <th> 操作 </th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="record in records" :key="record.record.id">
                    <td>
                        <img :src="record.a_photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username"> {{ record.a_username }} </span>
                    </td>
                    <td>
                        <img :src="record.b_photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username"> {{ record.b_username }} </span>
                    </td>
                    
                    <td>
                        {{ record.result }}
                    </td>
                    <td>
                        {{ record.record.createtime.replace(/T/g, ' ').slice(0, 19) }}
                    </td>
                    <td>
                        <button @click="open_record_content(record.record.id)" type="button" class="btn btn-secondary">查看录像</button>
                    </td>
                </tr>
            </tbody>
        </table>
        <nav aria-label="Page navigation example">
            <ul class="pagination" style="float: right;">
                <li class="page-item" @click="click_page(-2)">
                    <a class="page-link" href="#" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <li :class="'page-item ' + page.is_active" v-for="page in pages" :key="page.number" @click="click_page(page.number)">
                    <a class="page-link" href="#"> {{ page.number }} </a>
                </li>
                <li class="page-item" @click="click_page(-1)">
                    <a class="page-link" href="#" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </ContentField>
</template>

<script>
import ContentField from '../../components/ContentField.vue'
import { useStore } from 'vuex'
import $ from 'jquery'
import { ref } from 'vue'
import router from '../../router/index'

export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore()
        let current_page = 1
        let records = ref([])
        let total_records = 0
        let pages = ref([])

        const click_page = page => {
            let max_pages = parseInt(Math.ceil(total_records / 10))
            if (page == -2) page = 1
            if (page == -1) page = max_pages
            if (page >= 1 && page <= max_pages) {
                pull_page(page)
            }
        }

        const update_pages = () => {
            let max_pages = parseInt(Math.ceil(total_records / 10))
            let new_pages = []
            for (let i = current_page - 2; i <= current_page + 2; ++i) {
                if (i >= 1 && i <= max_pages) {
                    new_pages.push({
                        number: i,
                        is_active: i === current_page ? "active" : ""
                    })
                }
            }
            pages.value = new_pages
        }

        const pull_page = page => {
            current_page = page
            $.ajax({
                url: store.state.uri.api_uri + "record/getlist/",
                data: {
                    page,
                },
                type: "GET",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    records.value = resp.records
                    total_records = resp.records_count
                    update_pages()
                },
                error(resp) {
                    console.log(resp)
                }
            })
        }
        
        pull_page(current_page)

        const getG = ()=> {
            let g = []
            for (let i = 0; i < 8; ++i) {
                let line = []
                for (let j = 0; j < 8; ++j) {
                    line.push(0)
                }
                g.push(line)
            }
            g[0][0] = g[0][8 - 1] = g[8 - 1][0] = g[8 - 1][8 - 1] = 1;
            for (let i = 1; i < 8 - 1; ++i) {
                g[0][i] = g[8 - 1][i] = 4;
            }
            for (let i = 1; i < 8 - 1; ++i) {
                g[i][0] = g[i][8 - 1] = 5;
            }
            return g
        }

        const open_record_content = recordId => {
            for (const record of records.value) {
                if (record.record.id === recordId) {
                    store.commit("updateIsRecord", true)
                    store.commit("updateGame", {
                        map: getG(),
                        a_id: record.record.aid,
                        b_id: record.record.bid,
                    })
                    store.commit("updateSteps", {
                        a_steps: JSON.parse(record.record.asteps),
                        b_steps: JSON.parse(record.record.bsteps),
                    })
                    store.commit("updateRecordWinner", record.record.winner)
                    router.push({
                        name: "record_content",
                        params: {
                            recordId,
                            pages
                        }
                    })
                    break;
                }
            }
        }

        return {
            records,
            open_record_content,
            click_page,
            pages,
        }
    }
}

</script>

<style scoped>
img.record-user-photo {
    width: 4vh;
    border-radius: 50%;
}
</style>
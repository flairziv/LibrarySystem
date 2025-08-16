<template>
    <div>
        <ul class="nav">
            <li>
                <Logo />
            </li>
            <li v-if="!item.isHidden" :style="{
                fontSize: selectedIndex === index ? '18px' : '14px',
                color: selectedIndex === index ? '#1c1c1c' : 'rgb(102 102 102)'
            }" class="funItem" v-for="(item, index) in menus" :key="index" @click="menuClick(`${item.path}`, index)">
                <span>
                    <el-icon v-if="item.icon">
                        <component :is="item.icon" />
                    </el-icon>
                    <span>&nbsp; {{ item.name }}</span>
                </span>
            </li>
            <li>
                <el-row>
                    <el-col :span="18">
                        <input class="search-input" placeholder="搜索..." @keyup.enter="search" v-model="filterText" />
                    </el-col>
                    <el-col :span="6">
                        <span @click="search"
                            style="background-color: #000;color: #f1f1f1;border-radius: 5px;padding: 5px 10px;width: 100%;box-sizing: border-box;">
                            搜索
                        </span>
                    </el-col>
                </el-row>
            </li>
            <li style="position: absolute;right: 330px;">
                <span @click="healthDataRecord" style="margin:14px 10px;">
                    <el-icon><EditPen /></el-icon>
                    指标记录
                </span>
            </li>
            <li style="position: absolute;right: 300px;">
                <el-badge style="margin-left: 5px;font-size: 16px;" v-if="noReadMsg !== 0" :value="noReadMsg">
                    <span class="message-span" @click="messageCenter">
                        <el-icon><Bell /></el-icon>
                    </span>
                </el-badge>
                <span style="margin-left: 5px;font-size: 16px;" v-else class="message-span" @click="messageCenter">
                    <el-icon><Bell /></el-icon>
                </span>
            </li>
            <li>
                <span class="user-block">
                    <el-dropdown class="user-dropdown">
                        <span class="el-dropdown-link" style="display: flex; align-items: center;">
                            <el-avatar :size="35" :src="userInfo.url" style="margin-top: 0;"></el-avatar>
                            <span class="userName" style="margin-left: 5px;font-size: 16px;">{{ userInfo.name }}</span>
                            <el-icon style="margin-left: 5px;"><ArrowDown /></el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item @click="userCenterPanel">
                                    <el-icon><User /></el-icon>个人中心
                                </el-dropdown-item>
                                <el-dropdown-item @click="resetPwd">
                                    <el-icon><WarningFilled /></el-icon>修改密码
                                </el-dropdown-item>
                                <el-dropdown-item @click="loginOut">
                                    <el-icon><Back /></el-icon>退出登录
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </span>
            </li>
        </ul>
    </div>
</template>
<script>
import { ref, onMounted, defineProps, defineEmits, getCurrentInstance } from 'vue'
import { 
    EditPen, 
    Bell, 
    ArrowDown, 
    User, 
    WarningFilled, 
    Back,
    // 添加用户菜单可能用到的图标
    CollectionTag,
    Notebook,
    House,
    Reading,
    View,
    Connection,
    Message,
    DataAnalysis,
    Folder
} from '@element-plus/icons-vue'
import { clearToken } from "@/utils/storage.js"
import Logo from '@/components/Logo.vue'

export default {
    name: "UserMenu",
    components: {
        Logo,
        EditPen,
        Bell,
        ArrowDown,
        User,
        WarningFilled,
        Back,
        // 添加用户菜单可能用到的图标组件
        CollectionTag,
        Notebook,
        House,
        Reading,
        View,
        Connection,
        Message,
        DataAnalysis,
        Folder
    },
    props: {
        // 路由菜单数据
        menus: {
            type: Array,
            required: true
        },
        // 用户信息数据
        userInfo: {
            type: Object,
            required: true
        }
    },
    emits: ['eventListener'],
    setup(props, { emit }) {
        const { proxy } = getCurrentInstance()
        
        const selectedIndex = ref(0)
        const messagePath = ref('/message')
        const loginPath = ref('/login')
        const sysName = ref('健康有道')
        const defaultPath = ref('/news-record')
        const filterText = ref('')
        const noReadMsg = ref(0)

        // 搜索关键词，返回父组件处理
        const search = () => {
            // 如果当前是搜索页面了，更新关键词即可
            if (proxy.$route.path === '/search-detail') {
                sessionStorage.setItem('keyWord', filterText.value)
                return
            }
            // 将关键词存起来
            sessionStorage.setItem('keyWord', filterText.value)
            emit('eventListener', 'search-detail')
        }

        // 个人中心，传回父组件处理
        const userCenterPanel = () => {
            emit('eventListener', 'center')
        }

        // 重置密码，传回父组件处理
        const resetPwd = () => {
            emit('eventListener', 'resetPwd')
        }

        // 退出登录，传回父组件处理
        const loginOut = () => {
            emit('eventListener', 'loginOut')
        }

        // 记录饮食，传回父组件处理
        const dietRecord = () => {
            emit('eventListener', 'dietRecord')
        }

        // 记录个人健康指标，传回父组件处理
        const healthDataRecord = () => {
            emit('eventListener', 'healthDataRecord')
        }

        const loadMsgCount = async () => {
            const userInfo = sessionStorage.getItem('userInfo')
            const userInfoEntity = JSON.parse(userInfo)
            const messageQueryDto = { userId: userInfoEntity.id, isRead: false }
            const response = await proxy.$axios.post(`/message/query`, messageQueryDto)
            const { data } = response
            if (data.code === 200) {
                noReadMsg.value = data.data.length
            }
        }

        // 不是存量路由，则跳转
        const pathToDo = (path) => {
            if (proxy.$route.path !== path) {
                proxy.$router.push(path)
            }
        }

        //路由跳转
        const menuClick = (path, index) => {
            selectedIndex.value = index
            pathToDo(path)
        }

        // 消息中心
        const messageCenter = () => {
            selectedIndex.value = null
            pathToDo(messagePath.value)
        }

        onMounted(() => {
            pathToDo(defaultPath.value)
            loadMsgCount()
        })

        return {
            selectedIndex,
            messagePath,
            loginPath,
            sysName,
            defaultPath,
            filterText,
            noReadMsg,
            search,
            userCenterPanel,
            resetPwd,
            loginOut,
            dietRecord,
            healthDataRecord,
            loadMsgCount,
            pathToDo,
            menuClick,
            messageCenter
        }
    }
}
</script>
<style scoped lang="scss">
.nav {
    padding: 12px 160px;
    height: 70px;
    line-height: 70px;
    list-style: none;
    border-bottom: 1px solid #f1f1f1;
    margin: 0;

    li {
        float: left;
        height: 70px;
        line-height: 70px;
        font-weight: 400;
        padding: 0 20px;
        user-select: none;
        color: rgb(102, 102, 102);
        font-size: 14px;
        transition: all 0.5s;

        i {
            color: rgb(102, 102, 102);
        }

        .message-span {
            padding: 5px;
            border-radius: 5px;

            i {
                font-size: 16px;
            }
        }

        .message-span:hover {
            background-color: rgb(240, 240, 240);
        }

        .search-input {
            outline: none;
            width: 100%;
            font-size: 14px;
            height: 35px;
            line-height: 35px;
            font-size: 16px;
            padding: 2px 30px;
            border-radius: 5px;
            transition: all 0.5s;
            border: 1px solid rgb(76, 77, 11);
            border-radius: 5px;
        }

        .search-button {
            background-color: #000;
            font-size: 16px;
            cursor: pointer;
            height: 30px;
            line-height: 30px;
        }

        .serch-input:focus {
            border: 1px solid rgb(188, 229, 247);
        }

    }
}

.user-block {
    position: absolute;
    right: 200px;

    .userName {
        display: inline-block;
        vertical-align: middle;
        font-size: 14px;
        cursor: pointer;
        user-select: none;
    }
}

.info-block {
    position: fixed;
    right: 10px;
    float: right;
    display: flex;
    align-items: center;
    flex-wrap: wrap;

    .search {
        display: flex;
        align-items: center;
        flex-wrap: wrap;

        span {
            font-size: 12px;
            padding: 0 8px;
        }

    }

    i {
        padding: 6px;
        border-radius: 3px;
        font-size: 20px;
    }

    i:hover {
        background-color: rgb(230, 230, 230);
    }

    .user-name {
        padding: 0 10px;
        color: #252933;
        font-weight: 400;
        margin: 0 10px;
        font-size: 14px;
    }

    .login-out {
        margin: 0 20px;
        width: 20px;
        height: 20px;
        padding: 6px;
        background-color: none !important;
        border-radius: 3px;
    }

    .login-out:hover {
        background-color: rgb(230, 230, 230);
    }
}
</style>

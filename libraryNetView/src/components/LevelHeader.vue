<template>
    <div class="header-container">
        <div class="header-left">
            <button class="collapse-button" @click="operation">
                <el-icon v-if="showFlag"><Expand /></el-icon>
                <el-icon v-else><Fold /></el-icon>
            </button>
            <h1 class="page-title">{{ tag || '仪表盘' }}</h1>
        </div>
        
        <div class="header-right">
            <!-- 下拉菜单组件，点击用户头像区域显示"个人资料"和"退出登录"选项 -->
            <el-dropdown trigger="click">
                 <!-- 触发器：点击显示下拉菜单 -->
                <div class="user-profile">
                    <el-avatar :size="32" :src="userInfo.url"></el-avatar>
                    <span class="username">{{ userInfo.name }}</span>
                    <el-icon><ArrowDown /></el-icon>
                </div>
                <!-- 下拉内容：菜单项 -->
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="userCenterPanel">
                            <el-icon><User /></el-icon>个人资料
                        </el-dropdown-item>
                        <el-dropdown-item @click="loginOut">
                            <el-icon><SwitchButton /></el-icon>退出登录
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue'
import { 
    Expand, 
    Fold, 
    ArrowDown, 
    User, 
    SwitchButton 
} from '@element-plus/icons-vue'

export default {
    name: "LevelHeader",
    components: {
        Expand,
        Fold,
        ArrowDown,
        User,
        SwitchButton
    },
    props: {
        tag: {
            type: String,
            required: true,
            default: ''
        },
        userInfo: {
            type: Object,
            required: true,
            default: () => ({})
        }
    },
    emits: ['eventListener', 'selectOperation'],
    setup(props, { emit }) {
        const showFlag = ref(sessionStorage.getItem('flag') === 'true')

        const userCenterPanel = () => {
            emit('eventListener', 'center')
        }

        const loginOut = () => {
            emit('eventListener', 'loginOut')
        }

        const operation = () => {
            showFlag.value = !showFlag.value
            sessionStorage.setItem('flag', showFlag.value)
            emit('selectOperation', showFlag.value)
        }

        return {
            showFlag,
            userCenterPanel,
            loginOut,
            operation
        }
    }
}
</script>

<style scoped lang="scss">
.header-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    height: 100%;
    width: 100%;
    box-sizing: border-box;
}

.header-left {
    display: flex;
    align-items: center;
}

.collapse-button {
    width: 42px;
    height: 42px;
    border-radius: 12px;
    background-color: #f5f7fa;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.25s ease;
    margin-right: 18px;
    
    &:hover {
        background-color: #ecf5ff;
        transform: scale(1.05);
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
    }
    
    i {
        font-size: 20px;
        color: #409EFF;
    }
}

.page-title {
    font-family: '未来圆SC','PingFang SC', 'Microsoft YaHei', sans-serif;
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin: 0;
}

.header-right {
    display: flex;
    align-items: center;
}

.user-profile {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 12px;
    transition: all 0.25s ease;
    
    &:hover {
        background-color: #f5f7fa;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
    }
    
    .username {
        margin: 0 10px;
        font-size: 15px;
        color: #303133;
        font-weight: 500;
    }
    
    i {
        font-size: 14px;
        color: #909399;
    }
}

.el-dropdown-menu {
    padding: 8px 0;
    border-radius: 8px;
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
    
    .el-dropdown-item {
        font-size: 14px;
        padding: 10px 20px;
        
        i {
            margin-right: 10px;
            font-size: 16px;
            color: #409EFF;
        }
    }
}
</style>

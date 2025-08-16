<template>
    <div class="menu-container" :class="{'collapsed-menu': flag}">
        <div class="nav-wrapper">
        <UserNavigation 
            :routes="userRoutes" 
            :current-route-name="nowRoute.name" 
            :user-info="userInfo" 
            :is-collapsed="flag"
            :user-center-path="selfPath"
            @route-click="routerClick"
            @toggle-collapse="toggleSidebar"
            @logout="loginOut"
        />
        </div>
        <div class="main-wrapper" :class="{'main-wrapper-expanded': flag}">
            <div class="main">
            <div class="page-header">
                <div class="header-title">
                    <el-icon v-if="nowRoute.icon">
                        <component :is="nowRoute.icon" />
                    </el-icon>
                    <span>{{ nowRoute.name || '首页' }}</span>
                </div>
                <div class="header-actions">
                    <template v-if="nowRoute.path === '/main'">
                        <el-tooltip content="添加留言" placement="bottom" effect="light">
                            <div class="action-btn" @click="handleAction('add')">
                                <el-icon>
                                    <EditPen />
                                </el-icon>
                            </div>
                        </el-tooltip>
                    </template>
                </div>
            </div>
            <div class="content-section">
                <router-view ref="currentView"></router-view>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { ref, reactive, onMounted, watch, getCurrentInstance, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from "@/utils/request.js";
import router from "@/router/index";
import { clearToken } from "@/utils/storage"
import UserNavigation from '@/components/UserNavigation.vue';
import { EditPen } from '@element-plus/icons-vue'

export default {
    name: "UserHome",
    components: {
        UserNavigation,
        EditPen
    },
    setup() {
        const { proxy } = getCurrentInstance()
        const vueRouter = useRouter()
        const route = useRoute()
        
        // 响应式数据
        const selfPath = reactive({ name: '个人中心', path: '/mySelf' })
        const userRoutes = ref([
            { path: "/main", name: '留言', icon: 'ChatDotSquare', show: true },
            { path: "/bookOperation", name: '借阅', icon: 'Reading', show: true },
            { path: "/bookOrderHistory", name: '历史', icon: 'Timer', show: true },
            { path: "/bookSave", name: '收藏', icon: 'Collection', show: true },
            { path: "/bookRssHistory", name: '订阅', icon: 'Connection', show: true },
            { path: "/rssNotification", name: '通知', icon: 'Message', show: true },
            { path: "/userOperationLog", name: '日志', icon: 'Notebook', show: true },
            { path: "/notice", name: '公告', icon: 'Bell', show: true }
        ])
        const userInfo = reactive({
            id: null,
            url: '',
            name: '',
            role: null,
            email: ''
        })
        const flag = ref(false)
        const nowRoute = reactive({
            name: '',
            path: '',
            icon: null
        })
        const currentView = ref(null)

        // 方法
        const handleAction = (action) => {
            if (nowRoute.path === '/main' && action === 'add') {
                if (currentView.value && currentView.value.postWord) {
                    currentView.value.postWord();
                }
            }
        }

        const toggleSidebar = () => {
            flag.value = !flag.value;
            sessionStorage.setItem('flag', flag.value);
        }

        const routerClick = (route) => {
            handleRouteSelect(route);
        }

        const loginOut = async () => {
            const confirmed = await proxy.$swalConfirm({
                title: '退出登录？',
                text: '退出后需重新登录',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: '确认',
                cancelButtonText: '取消',
                confirmButtonColor: '#ff5722',
                cancelButtonColor: '#909399'
            });
            if (confirmed) {
                proxy.$swal.fire({
                    title: '退出登录成功',
                    text: '1s 后返回登录页面',
                    icon: 'success',
                    showConfirmButton: false,
                    timer: 1000,
                });
                setTimeout(() => {
                    clearToken();
                    vueRouter.push("/login");
                }, 1000)
            }
        }

        const menuOperationHistory = () => {
            flag.value = sessionStorage.getItem('flag') === 'true';
        }

        const handleRouteSelect = (routeItem) => {
            if (nowRoute.name !== routeItem.name) {
                Object.assign(nowRoute, routeItem);
                vueRouter.push(routeItem.path);
            }
        }

        const tokenCheckLoad = async () => {
            try {
                const res = await request.get('user/auth');
                if (res.data.code === 400) {
                    proxy.$message.error(res.data.msg);
                    vueRouter.push('/login');
                    return;
                }
                const { id, userAvatar: url, userName: name, userRole: role, userEmail: email } = res.data.data;
                Object.assign(userInfo, { id, url, name, role, email });
                sessionStorage.setItem('userInfo', JSON.stringify(userInfo));
                sessionStorage.setItem('userId', userInfo.id);
                
                // 注释掉旧的路由API调用，使用静态配置
                // const rolePath = role === 1 ? '/admin' : '/user';
                // const targetMenu = router.options.routes.find(route => route.path === rolePath);
                // if (targetMenu) {
                //     userRoutes.value = targetMenu.children;
                // }
            } catch (error) {
                console.error('获取用户认证信息时发生错误:', error);
                proxy.$message.error('认证信息加载失败，请重试！');
            }
        }

        const updateCurrentRoute = () => {
            const currentPath = route.path;
            const matchedRoute = userRoutes.value.find(routeItem => routeItem.path === currentPath);
            if (matchedRoute) {
                Object.assign(nowRoute, matchedRoute);
                return true;
            }
            return false;
        }

        // 监听路由变化
        watch(() => route.path, () => {
            updateCurrentRoute();
        })

        // 生命周期
        onMounted(() => {
            tokenCheckLoad();
            menuOperationHistory();
            
            if (!updateCurrentRoute() && userRoutes.value.length > 0) {
                routerClick(userRoutes.value[0]);
            }
        })

        return {
            selfPath,
            userRoutes,
            userInfo,
            flag,
            nowRoute,
            currentView,
            handleAction,
            toggleSidebar,
            routerClick,
            loginOut,
            menuOperationHistory,
            handleRouteSelect,
            tokenCheckLoad,
            updateCurrentRoute
        }
    }
};
</script>
<style scoped lang="scss">
.menu-container {
    display: flex;
    height: 100vh;
    width: 100%;
    overflow: hidden;
    position: relative;
    background-color: #FFFFFF;
    
    &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 228px; /* 与导航栏宽度相近的位置 */
        height: 100%;
        width: 2px;
        background: linear-gradient(to bottom, 
            rgba(255, 123, 0, 0.03), 
            rgba(255, 123, 0, 0.1) 50%, 
            rgba(255, 123, 0, 0.03)
        );
        z-index: 999;
        pointer-events: none;
        transition: left 0.25s cubic-bezier(0.22, 1, 0.36, 1);
        opacity: 0.8;
    }
    
    &.collapsed-menu::after {
        left: 118px; /* 当菜单收起时的位置 */
    }
}

.nav-wrapper {
    position: relative;
    z-index: 1000;
    height: 100%;
    margin-right: 2px;
}

// 用更优雅的方式为UserNavigation组件添加样式
:deep(.menu-side) {
    // 移除覆盖背景的样式，让组件使用自己的渐变背景
    // background: rgba(255, 255, 255, 0.9) !important;
    backdrop-filter: blur(10px);
    box-shadow: 0 0 15px rgba(0, 0, 0, 0.05);
    border-radius: 0 20px 20px 0;
    border: none;
    height: 100vh;
}

.main-wrapper {
    flex-grow: 1;
    height: 100vh;
    position: relative;
    transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
    overflow: hidden;
    background: #FFFFFF;
    margin-left: 3px;
}

.main-wrapper-expanded {
    margin-left: 0;
}

.main {
    position: relative;
    height: 100%;
    width: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    box-sizing: border-box;
    padding: 0;
    background-image: linear-gradient(to top, #fad0c4 0%, #fad0c4 1%, #ffd1ff 100%);
    background-attachment: fixed;
    border-radius: 16px 0 0 16px;
    box-shadow: -6px 0 15px rgba(0, 0, 0, 0.05);
    border-left: 1px solid rgba(255, 123, 0, 0.15);
    
    &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: 
            radial-gradient(circle at 20% 30%, rgba(84, 182, 229, 0.04) 0%, rgba(84, 182, 229, 0) 40%),
            radial-gradient(circle at 80% 60%, rgba(255, 160, 100, 0.03) 0%, rgba(255, 160, 100, 0) 40%);
        pointer-events: none;
        z-index: 0;
    }
}
    
    .content-section {
        flex-grow: 1;
        padding: 20px;
        box-sizing: border-box;
        overflow-y: auto;
        width: 100%;
    position: relative;
    z-index: 1;
        
        &::-webkit-scrollbar {
            width: 4px;
        }
  
        &::-webkit-scrollbar-track {
            background: transparent;
        }
  
        &::-webkit-scrollbar-thumb {
        background-color: rgba(255, 123, 0, 0.12);
            border-radius: 10px;
        
        &:hover {
            background-color: rgba(255, 123, 0, 0.18);
        }
    }
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 20px 15px 20px;
    background-color: transparent;
    position: relative;
    z-index: 1;
    height: 60px;
    box-sizing: border-box;
    
    .header-title {
        display: flex;
        align-items: center;
        font-size: 18px;
        font-weight: 600;
        color: #333;
        
        i {
            margin-right: 10px;
            font-size: 20px;
            color: #ff5722;
        }
    }
    
    .header-actions {
        display: flex;
        gap: 10px;
        
        .action-btn {
            width: 36px;
            height: 36px;
            border-radius: 10px;
            background-color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            transition: all 0.3s ease;
            
            i {
                font-size: 16px;
                color: #606266;
            }
            
            &:hover {
                background-color: rgba(255, 87, 34, 0.08);
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                
                i {
                    color: #ff5722;
                }
            }
        }
    }
}
</style>

<template>
    <div class="admin-layout">
        <!-- 左侧导航 -->
        <div class="sidebar" :class="{'sidebar-collapsed': flag}">
            <div class="logo-container">
                <Logo name="书海智控台" :flag="flag" :bag="colorLogo" />
            </div>
            <div class="menu-wrapper">
                <!-- @select监听子组件handleSelect方法中emit('select', activeIndex.value)传递过来的参数 -->
                <AdminMenu :flag="flag" :routes="adminRoutes" :bag="bagMenu" @select="handleRouteSelect" />
            </div>
        </div>

        <!-- 右侧内容区 -->
        <div class="main-container" :class="{'main-expanded': flag}">
            <div class="header">
                <LevelHeader 
                    @eventListener="eventListener" 
                    @selectOperation="selectOperation" 
                    :tag="tag"
                    :userInfo="userInfo" 
                />
            </div>
            <div class="content">
                <router-view></router-view>
            </div>
        </div>

        <!-- 个人中心弹窗 -->
        <el-dialog 
            v-model="dialogOperaion" 
            width="380px" 
            custom-class="user-center-dialog"
            :show-close="true">
            <div class="dialog-content-wrapper">
            <div class="user-center-header">
                <h3>个人中心</h3>
            </div>
            <div class="user-center-body">
                <div class="form-item">
                    <label class="form-label">
                        <el-icon><UserFilled /></el-icon>
                        <span>头像</span>
                    </label>
                    <div class="avatar-wrapper">
                        <el-upload 
                            class="avatar-uploader" 
                            action="/api/library-system-api/v1.0/file/upload"
                            :show-file-list="false" 
                            :on-success="handleAvatarSuccess">
                            <img v-if="userInfo.url" :src="userInfo.url" class="user-avatar">
                            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                        </el-upload>
                    </div>
                </div>
                <div class="form-item">
                    <label class="form-label">
                        <el-icon><User /></el-icon>
                        <span>用户名</span>
                    </label>
                    <el-input v-model="userInfo.name" placeholder="请输入用户名"></el-input>
                </div>
                <div class="form-item">
                    <label class="form-label">
                        <el-icon><Message /></el-icon>
                        <span>邮箱</span>
                    </label>
                    <el-input v-model="userInfo.email" placeholder="请输入邮箱"></el-input>
                </div>
            </div>
                <div class="user-center-footer">
                <el-button @click="dialogOperaion = false">取 消</el-button>
                <el-button type="primary" @click="updateUserInfo">确认修改</el-button>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { ref, reactive, onMounted, watch, getCurrentInstance } from 'vue'
import request from "@/utils/request.js"
import { clearToken } from "@/utils/storage"
import AdminMenu from '@/components/VerticalMenu.vue'
import Logo from '@/components/Logo.vue'
import LevelHeader from '@/components/LevelHeader.vue'
import { UserFilled, Plus, User, Message } from '@element-plus/icons-vue'

export default {
    name: "AdminHome",
    components: {
        Logo,
        LevelHeader,
        AdminMenu,
        UserFilled,
        Plus,
        User,
        Message
    },
    setup() {
        const { proxy } = getCurrentInstance()
        
        // 响应式数据
        const adminRoutes = ref([])
        const userInfo = reactive({
            id: null,
            url: '',
            name: '',
            role: null,
            email: ''
        })
        const flag = ref(false)
        const tag = ref('仪表盘')
        const colorLogo = ref('#333')
        const bagMenu = ref('transparent')
        const dialogOperaion = ref(false)

        // 方法定义
        // 更新页面标题
        const updatePageTitle = () => {
            const currentPath = proxy.$route.path
            const currentRoute = adminRoutes.value.find(route => route.path === currentPath)
            if (currentRoute) {
                tag.value = currentRoute.name
            }
        }
        
        // 加载侧边栏状态
        const loadSidebarState = () => {
            flag.value = sessionStorage.getItem('flag') === 'true'
        }
        
        // 处理头像上传成功
        const handleAvatarSuccess = (res) => {
            if (res.code !== 200) {
                proxy.$message.error('头像上传失败')
                return
            }
            
            proxy.$message.success('头像上传成功')
            userInfo.url = res.data
        }
        
        // 更新用户信息
        const updateUserInfo = async () => {
            try {
                const userUpdateDTO = {
                    userAvatar: userInfo.url,
                    userName: userInfo.name,
                    userEmail: userInfo.email
                }
                
                const response = await proxy.$axios.put('/user/update', userUpdateDTO)
                const { data } = response
                
                if (data.code === 200) {
                    dialogOperaion.value = false
                    tokenCheckLoad()
                    
                    proxy.$swal.fire({
                        title: '修改成功',
                        text: data.msg,
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1000
                    })
                }
            } catch (error) {
                dialogOperaion.value = false
                
                proxy.$swal.fire({
                    title: '修改失败',
                    text: error.message || '发生未知错误',
                    icon: 'error',
                    showConfirmButton: false,
                    timer: 2000
                })
                
                console.error('修改个人信息异常:', error)
            }
        }
        
        // 处理头部事件
        const eventListener = (event) => {
            if (event === 'center') {
                dialogOperaion.value = true
            } else if (event === 'loginOut') {
                loginOut()
            }
        }
        
        // 退出登录
        const loginOut = async () => {
            const confirmed = await proxy.$swalConfirm({
                title: '确认退出登录?',
                text: '退出后需重新登录',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: '确认',
                cancelButtonText: '取消',
                confirmButtonColor: '#ff5722',
                cancelButtonColor: '#909399',
                iconColor: '#ff9800',
                customClass: {
                    popup: 'modern-swal-popup',
                    header: 'modern-swal-header',
                    title: 'modern-swal-title',
                    content: 'modern-swal-content',
                    confirmButton: 'modern-swal-confirm-button',
                    cancelButton: 'modern-swal-cancel-button',
                    actions: 'modern-swal-actions'
                },
                buttonsStyling: true,
                showClass: {
                    popup: 'animate__animated animate__fadeInUp animate__faster'
                },
                hideClass: {
                    popup: 'animate__animated animate__fadeOutDown animate__faster'
                }
            })
            
            if (confirmed) {
                proxy.$swal.fire({
                    title: '退出成功',
                    text: '正在返回登录页面...',
                    icon: 'success',
                    showConfirmButton: false,
                    timer: 1000,
                    iconColor: '#67C23A',
                    customClass: {
                        popup: 'modern-swal-popup',
                        title: 'modern-swal-title',
                        content: 'modern-swal-content'
                    }
                })
                
                setTimeout(() => {
                    clearToken()
                    proxy.$router.push("/login")
                }, 1000)
            }
        }
        
        // 切换侧边栏
        const selectOperation = (flagValue) => {
            flag.value = flagValue
            sessionStorage.setItem('flag', flagValue)
        }
        
        // 选择菜单项
        // 这里的path参数内容是子组件传递的emit('select', activeIndex.value)
        const handleRouteSelect = (path) => {
            if (proxy.$route.path === path) return
            
            const route = adminRoutes.value.find(item => item.path === path)
            if (route) {
                tag.value = route.name
                proxy.$router.push(path)
            }
        }
        
        // 验证token并加载用户信息
        const tokenCheckLoad = async () => {
            try {
                const res = await request.get('user/auth')
                
                // 处理错误
                if (res.data.code === 400) {
                    proxy.$message.error(res.data.msg)
                    proxy.$router.push('/login')
                    return
                }
                
                // 设置用户信息
                const { id, userAvatar: url, userName: name, userRole: role, userEmail: email } = res.data.data
                // 保持响应式 批量更新响应式对象 userInfo 的属性值，等价于userInfo.id = id、userInfo.url = url、userInfo.name = name、userInfo.role = role、userInfo.email = email
                Object.assign(userInfo, { id, url, name, role, email })
                
                // 验证用户角色
                if (role !== 1) {
                    proxy.$message.warning('您没有管理员权限，正在跳转...')
                    proxy.$router.push('/user')
                }
            } catch (error) {
                console.error('获取用户信息失败:', error)
                proxy.$message.error('认证失败，请重新登录')
                proxy.$router.push('/login')
            }
        }

        // 路由监听
        watch(() => proxy.$route, () => {
            updatePageTitle()
        })

        // 生命周期
        onMounted(() => {
            // 直接使用路由配置 - 避免 Vue Router 4 的复杂性
            const adminRoutes_static = [
                { path: "/adminLayout", name: '数据总览', icon: 'DataAnalysis' },
                { path: "/userManage", name: '用户管理', icon: 'User' },
                { path: "/bookManage", name: '书籍管理', icon: 'Reading' },
                { path: "/bookOrderHistoryManage", name: '借阅记录', icon: 'View' },
                { path: "/bookRssHistoryManage", name: '图书订阅', icon: 'Connection' },
                { path: "/rssNotificationManage", name: '订阅通知', icon: 'Message' },
                { path: "/bookshelfManage", name: '书架管理', icon: 'Folder' },
                { path: "/categoryManage", name: '类别管理', icon: 'CollectionTag' },
                { path: "/readerProposalManage", name: '读者建议', icon: 'EditPen' },
                { path: "/noticeManage", name: '公告管理', icon: 'Bell' },
                { path: "/userOperationLogManage", name: '行为日志', icon: 'Notebook' }
            ]
            adminRoutes.value = adminRoutes_static
            console.log('设置admin路由:', adminRoutes.value)
            
            // 初始化数据
            tokenCheckLoad()
            loadSidebarState()
            
            // 初始化标题
            updatePageTitle()
        })

        return {
            adminRoutes,
            userInfo,
            flag,
            tag,
            colorLogo,
            bagMenu,
            dialogOperaion,
            updatePageTitle,
            loadSidebarState,
            handleAvatarSuccess,
            updateUserInfo,
            eventListener,
            loginOut,
            selectOperation,
            handleRouteSelect,
            tokenCheckLoad
        }
    }
}
</script>

<style scoped lang="scss">
// 主布局
.admin-layout {
    display: flex;
    height: 100vh;
    background: linear-gradient(135deg, #e8f0ff 0%, #d6e9ff 50%, #e8e0ff 100%);
    background-attachment: fixed;
    overflow: hidden;
    position: relative;
    
    &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: 
            radial-gradient(circle at 15% 20%, rgba(255, 130, 180, 0.15) 0%, rgba(255, 130, 180, 0) 35%),
            radial-gradient(circle at 85% 70%, rgba(100, 180, 255, 0.2) 0%, rgba(100, 180, 255, 0) 35%);
        pointer-events: none;
    }
}

// 侧边栏
.sidebar {
    width: 240px;
    background-color: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    display: flex;  /* 使用 flex 布局，便于内部纵向排列 */
    flex-direction: column;  /* 子元素垂直排列（从上到下） */
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);  /* 过渡动画，all：监听所有可过渡的 CSS 属性变化 0.3s：动画持续时间 0.3 秒 开始时加速（0.4 控制）中间匀速 结束时减速（0.2, 1 控制）*/
    z-index: 10;  /* 提高层级，确保位于主要内容之上 */
    flex-shrink: 0;  /* 在 flex 容器中不允许收缩，保持设定宽度 */
    border-radius: 0 16px 16px 0;  /* 右上与右下圆角，从左上角开始顺时针转动 */
    overflow: hidden;  /* 隐藏溢出内容，配合圆角避免内容溢出显示 */
    margin-right: 0px;  /* 右侧外边距为0 */
    position: relative;  /* 相对定位以便内部绝对定位元素参照该容器 */
}

.sidebar-collapsed {
    width: 90px;
}

.logo-container {
    height: 70px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 1px solid rgba(0, 0, 0, 0.04);
    padding: 0 16px;
}

.menu-wrapper {
    flex: 1;
    overflow-y: auto;
    padding: 8px 0;
    
    &::-webkit-scrollbar {
        width: 4px;
    }
    
    &::-webkit-scrollbar-track {
        background: transparent;
    }
    
    &::-webkit-scrollbar-thumb {
        background-color: rgba(0, 0, 0, 0.1);
        border-radius: 2px;
        
        &:hover {
            background-color: rgba(0, 0, 0, 0.2);
        }
    }
}

// 主内容区
.main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: 16px 20px 16px 32px;
    background-color: transparent;
    transition: all 0.3s ease;
    margin-left: -12px;
}

.main-expanded {
    padding-left: 24px;
}

.header {
    height: 70px;
    background-color: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    margin-bottom: 16px;
    box-shadow: 0 4px 15px rgba(125, 150, 255, 0.1);
    display: flex;
    align-items: center;
}

.content {
    flex: 1;
    background-color: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    padding: 24px;
    overflow-y: auto;
    box-shadow: 0 4px 15px rgba(125, 150, 255, 0.1);
    
    &::-webkit-scrollbar {
        width: 6px;
    }
    
    &::-webkit-scrollbar-track {
        background: rgba(240, 242, 245, 0.5);
        border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb {
        background-color: rgba(0, 0, 0, 0.1);
        border-radius: 3px;
        
        &:hover {
            background-color: rgba(0, 0, 0, 0.2);
        }
    }
}

// 用户中心弹窗
.user-center-dialog {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(125, 150, 255, 0.2);

    :deep(.el-dialog__body) {
        padding: 0;
    }

    :deep(.el-dialog__header) {
        padding: 0;
    }

    :deep(.el-dialog__headerbtn) {
        z-index: 10;
    }
    
    :deep(.el-dialog__wrapper) {
        backdrop-filter: blur(4px);
    }
}

.dialog-content-wrapper {
    display: flex;
    flex-direction: column;
    overflow: hidden;
    max-height: 80vh;
}

.user-center-header {
    background: linear-gradient(135deg, #e0eaff, #f2e6ff);
    padding: 24px;
    position: relative;
    
    h3 {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        position: relative;
        padding-left: 14px;
        
        &:before {
            content: '';
            position: absolute;
            left: 0;
            top: 50%;
            transform: translateY(-50%);
            width: 4px;
            height: 18px;
            background: linear-gradient(to bottom, #409EFF, #a18cd1);
            border-radius: 4px;
        }
    }
}

.user-center-body {
    padding: 24px;
    background-color: white;
}

.user-center-footer {
    padding: 16px 24px;
    background-color: rgba(245, 247, 250, 0.8);
    text-align: right;
    border-top: 1px solid rgba(0, 0, 0, 0.03);
    
    .el-button {
        padding: 10px 22px;
        font-size: 14px;
        border-radius: 8px;
    }
}

.form-item {
    margin-bottom: 20px;
    
    &:last-child {
        margin-bottom: 0;
    }
}

.form-label {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    
    i {
        font-size: 16px;
        color: #409EFF;
        margin-right: 8px;
    }
    
    span {
        font-size: 14px;
        color: #606266;
        font-weight: 500;
    }
}

.avatar-wrapper {
    display: flex;
    justify-content: center;
    margin: 16px 0;
}

.user-avatar {
    width: 90px;
    height: 90px;
    border-radius: 50%;
    object-fit: cover;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border: 3px solid #fff;
}

.avatar-uploader-icon {
    font-size: 26px;
    width: 90px;
    height: 90px;
    line-height: 90px;
    text-align: center;
    border: 2px dashed #d9d9d9;
    border-radius: 50%;
    color: #8c939d;
    
    &:hover {
        border-color: #409EFF;
        color: #409EFF;
    }
}
</style>

<style>
/* SweetAlert2 自定义样式 */
.modern-swal-popup {
    border-radius: 16px !important;
    padding: 24px !important;
    background-color: rgba(255, 255, 255, 0.95) !important;
    backdrop-filter: blur(10px);
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15) !important;
    border: 1px solid rgba(255, 255, 255, 0.2) !important;
}

.modern-swal-header {
    padding-bottom: 16px !important;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05) !important;
}

.modern-swal-title {
    font-size: 1.4rem !important;
    font-weight: 600 !important;
    color: #333 !important;
    margin-bottom: 8px !important;
    font-family: '未来圆SC','PingFang SC', 'Microsoft YaHei', sans-serif !important;
}

.modern-swal-content {
    font-size: 1rem !important;
    color: #606266 !important;
    margin-top: 16px !important;
    margin-bottom: 24px !important;
}

.modern-swal-actions {
    margin-top: 24px !important;
    display: flex !important;
    justify-content: flex-end !important;
    gap: 12px !important;
}

.modern-swal-confirm-button, 
.modern-swal-cancel-button {
    border-radius: 40px !important;
    padding: 10px 24px !important;
    font-size: 14px !important;
    font-weight: 500 !important;
    letter-spacing: 0.5px !important;
    text-transform: none !important;
    transition: all 0.3s ease !important;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

.modern-swal-confirm-button {
    background: linear-gradient(135deg, #ff7043, #ff5722) !important;
}

.modern-swal-confirm-button:hover {
    transform: translateY(-2px) !important;
    box-shadow: 0 6px 16px rgba(255, 87, 34, 0.3) !important;
}

.modern-swal-cancel-button {
    background-color: #f0f2f5 !important;
    color: #606266 !important;
}

.modern-swal-cancel-button:hover {
    background-color: #e4e7ed !important;
    transform: translateY(-2px) !important;
    }

/* 为动画效果添加的样式 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translate3d(0, 30px, 0);
  }
  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}

@keyframes fadeOutDown {
  from {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
  to {
    opacity: 0;
    transform: translate3d(0, 30px, 0);
  }
}

.animate__animated {
  animation-duration: 300ms;
  animation-fill-mode: both;
}

.animate__fadeInUp {
  animation-name: fadeInUp;
}

.animate__fadeOutDown {
  animation-name: fadeOutDown;
}

.animate__faster {
  animation-duration: 200ms;
}
</style>

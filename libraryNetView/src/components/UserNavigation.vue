<template>
  <div class="menu-side" :class="{ 'menu-side-narrow': isCollapsed }">
    <!-- 顶部Logo区域 -->
    <div class="logo-container">
      <img src="/Logo2.png" class="logo-image">
      <div class="logo-text">悦读空间</div>
      <div class="collapse-btn" @click="toggleCollapse">
        <el-icon>
          <component :is="isCollapsed ? 'Expand' : 'Fold'" />
        </el-icon>
      </div>
    </div>
    
    <!-- 导航菜单区域 -->
    <div class="navigation-container">
      <div class="nav-group">
        <div v-for="(routeItem, index) in (routes || [])" 
          :key="index"
          class="item-route" 
          :class="{'active-route': currentRouteName === routeItem.name}"
          @click="handleRouteClick(routeItem)">
          <div class="active-indicator" v-if="currentRouteName === routeItem.name"></div>
          <div class="item-content">
            <el-icon v-if="routeItem.icon" class="nav-icon">
              <component :is="routeItem.icon" />
            </el-icon>
            <span class="route-text">{{ routeItem.name }}</span>
          </div>
          <el-tooltip 
            v-if="isCollapsed" 
            :content="routeItem.name" 
            placement="right" 
            :open-delay="300">
            <span class="tooltip-trigger"></span>
          </el-tooltip>
        </div>
      </div>
      
      <!-- 底部用户信息区域 -->
      <div class="user-section">
        <div class="divider"></div>
        
        <!-- 用户信息下拉菜单 -->
        <el-dropdown @command="handleCommand" placement="top-start" trigger="click">
          <div class="item-route user-info-item">
            <div class="item-content">
              <img :src="userInfo.url || '/assets/default-avatar.png'" class="user-avatar">
              <div class="user-details" v-if="!isCollapsed">
                <span class="username">{{ userInfo.name || '用户' }}</span>
                <span class="user-role">{{ userInfo.role === 1 ? '管理员' : '读者' }}</span>
              </div>
              <el-icon class="dropdown-icon" v-if="!isCollapsed">
                <ArrowUp />
              </el-icon>
            </div>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon><Setting /></el-icon>
                设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script>
import { defineProps, defineEmits } from 'vue'
import { 
  Expand, 
  Fold, 
  ArrowUp, 
  User, 
  Setting, 
  SwitchButton,
  House,
  Reading,
  View,
  Connection,
  Message,
  Star,
  Bell,
  Notebook,
  ChatDotSquare,
  Timer,
  Collection
} from '@element-plus/icons-vue'

export default {
  name: 'UserNavigation',
  components: {
    Expand,
    Fold,
    ArrowUp,
    User,
    Setting,
    SwitchButton,
    House,
    Reading,
    View,
    Connection,
    Message,
    Star,
    Bell,
    Notebook,
    ChatDotSquare,
    Timer,
    Collection
  },
  props: {
    routes: {
      type: Array,
      default: () => []
    },
    currentRouteName: {
      type: String,
      default: ''
    },
    userInfo: {
      type: Object,
      default: () => ({})
    },
    isCollapsed: Boolean,
    userCenterPath: {
      type: Object,
      default: () => ({ name: '个人中心', path: '/mySelf' })
    }
  },
  emits: ['route-click', 'toggle-collapse', 'logout'],
  setup(props, { emit }) {
    const handleRouteClick = (route) => {
      emit('route-click', route)
    }

    const goToUserCenter = () => {
      emit('route-click', props.userCenterPath)
    }

    const toggleCollapse = () => {
      emit('toggle-collapse')
    }

    const handleLogout = () => {
      emit('logout')
    }

    const handleCommand = (command) => {
      switch (command) {
        case 'profile':
          goToUserCenter()
          break
        case 'settings':
          // 可以添加设置页面的路由
          emit('route-click', { name: '设置', path: '/settings' })
          break
        case 'logout':
          handleLogout()
          break
      }
    }

    return {
      handleRouteClick,
      goToUserCenter,
      toggleCollapse,
      handleLogout,
      handleCommand
    }
  }
}
</script>

<style scoped lang="scss">
.menu-side {
  width: 230px !important;
  min-width: 120px !important;
  height: 100vh;
  box-sizing: border-box;
  transition: width 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  border-right: 1px solid rgba(0, 0, 0, 0.05);
  background-image: linear-gradient(45deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%);
  backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
  border-radius: 0 20px 20px 0;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
  z-index: 1000;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
}

.logo-container {
  display: flex;
  align-items: center;
  padding: 20px;
  padding-left: 30px;
  margin-bottom: 10px;
  position: relative;
  transition: padding 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  background-color: transparent;
  
  .logo-image {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;
    object-fit: cover;
    
    &:hover {
      transform: scale(1.05);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
    }
  }
  
  .logo-text {
    margin-left: 12px;
    font-size: 20px;
    color: white;
    font-weight: 600;
    letter-spacing: 1px;
    flex: 1;
    white-space: nowrap;
    overflow: hidden;
    font-family: '抖音美好体', '微软雅黑', sans-serif;
  }
  
  .collapse-btn {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 32px;
    height: 32px;
    border-radius: 8px;
    background-color: rgba(255, 199, 199, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background-color: rgba(255, 199, 199, 0.6);
      transform: translateY(-50%) scale(1.05);
    }
    
    i {
      font-size: 18px;
      color: white;
    }
  }
}

.navigation-container {
  display: flex;
  flex-direction: column;
  padding: 10px 14px;
  overflow-y: auto;
  flex: 1;
  justify-content: space-between;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: rgba(255, 123, 0, 0.15);
    border-radius: 10px;
  }
}

.nav-group {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.item-route {
  position: relative;
  padding: 12px 14px;
  margin: 4px 0;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  height: 48px;
  box-sizing: border-box;
  width: 100%;
  text-align: center;
  display: flex;
  justify-content: center;
  border: none;
  
  .item-content {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    position: relative;
    z-index: 2;
    width: 100%;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 100%;
    height: 100%;
    background-color: rgba(255, 226, 226, 0.4);
    border-radius: 15px;
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.5);
    transition: transform 0.3s ease, opacity 0.3s ease;
    z-index: 1;
  }
  
  &:hover::after {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
  
  &:hover {
    i, .route-text, .nav-icon {
      color: #61c0bf;
    }
  }
  
  i, .nav-icon {
    font-size: 20px;
    color: white;
    transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
    display: inline-block;
    text-align: center;
    text-shadow: none;
  }

  .route-text {
    font-size: 16px;
    color: white;
    font-weight: 600;
    transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
    flex: 0 1 auto;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    text-align: center;
    text-shadow: none;
  }
  
  .tooltip-trigger {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 3;
  }
  
  .active-indicator {
    position: absolute;
    left: 0;
    top: 0;
    height: 100%;
    width: 4px;
    background-color: #FF7B00;
    border-radius: 0 4px 4px 0;
    transform: translateX(-4px);
    transition: transform 0.25s cubic-bezier(0.22, 1, 0.36, 1);
    z-index: 2;
  }
}

.active-route {
  background-color: #bbded6;
  box-shadow: 0 2px 8px rgba(187, 222, 214, 0.5);
  border: none;
  
  .active-indicator {
    transform: translateX(0);
    background-color: #61c0bf;
  }
  
  i, .route-text, .nav-icon {
    color: #f38181;
    font-weight: 600;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  }
}

.user-section {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 20px;
}

.divider {
  height: 1px;
  background-color: rgba(255, 226, 226, 0.6);
  margin: 10px 0;
  width: 100%;
}

.user-info-item {
  background-color: rgba(187, 222, 214, 0.4);
  height: auto !important;
  padding-top: 8px !important;
  padding-bottom: 8px !important;
  
  .item-content {
    width: 100%;
    align-items: center;
    gap: 10px;
    transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
    justify-content: space-between;
  }
  
  .dropdown-icon {
    color: white;
    font-size: 14px;
    margin-left: auto;
  }
  
  .user-avatar {
    width: 36px;
    height: 36px;
    border-radius: 12px;
    object-fit: cover;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 2px solid #fff;
  }

  .user-details {
    display: flex;
    flex-direction: column;
    
    .username {
      font-weight: 600;
      font-size: 14px;
      color: white;
      line-height: 1.2;
      margin-bottom: 2px;
    }
    
    .user-role {
      font-size: 12px;
      color: white;
    }
  }
}

.logout-item {
  i {
    color: white;
  }
  
  .route-text {
    color: white;
  }
  
  &:hover::after {
    background-color: rgba(252, 227, 138, 0.4);
  }
  
  &:hover {
    i, .route-text {
      color: #61c0bf;
    }
  }
}

.menu-side-narrow {
  width: 120px !important;

  .logo-container {
    justify-content: center;
    padding: 20px 0;
    position: relative;
    
    .logo-text {
      width: 0;
      margin-left: 0;
      opacity: 0;
      visibility: hidden;
      position: absolute;
      transition: opacity 0.2s cubic-bezier(0.22, 1, 0.36, 1), margin 0.25s cubic-bezier(0.22, 1, 0.36, 1), visibility 0.2s;
    }
    
    .logo-image {
      margin: 0;
    }
    
    .collapse-btn {
      position: absolute;
      right: 0;
      top: 50%;
      transform: translateY(-50%);
    }
  }

  .item-route {
    padding: 12px 0;
    
    i {
      margin: 0 auto;
      font-size: 20px;
      transition: margin 0.25s cubic-bezier(0.22, 1, 0.36, 1);
    }
    
    .route-text {
      width: 0;
      margin-left: 0;
      opacity: 0;
      visibility: hidden;
      position: absolute;
      transition: opacity 0.2s cubic-bezier(0.22, 1, 0.36, 1), width 0.25s cubic-bezier(0.22, 1, 0.36, 1), visibility 0.2s;
    }
  }
  
  .user-info-item {
    .user-details {
      width: 0;
      margin-left: 0;
      opacity: 0;
      visibility: hidden;
      position: absolute;
      transition: opacity 0.2s cubic-bezier(0.22, 1, 0.36, 1), visibility 0.2s;
    }
    
    .user-avatar {
      width: 30px;
      height: 30px;
      border-radius: 10px;
      margin: 0 auto;
    }
  }
}

.nav-tooltip {
  padding: 8px 12px;
  border-radius: 8px;
  background-color: rgba(48, 49, 51, 0.9);
  border: none;
}
</style> 

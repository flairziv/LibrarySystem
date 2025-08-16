<template>
    <div class="reset-password-container">
        <!-- 原始密码 -->
        <div class="form-group">
            <label class="form-label">
                <span class="required">*</span>原始密码
            </label>
            <div class="input-wrapper">
                <el-icon><Lock /></el-icon>
                <input 
                    class="form-input" 
                    type="password" 
                    v-model="oldPwd" 
                    placeholder="请输入原始密码" 
                />
            </div>
        </div>
        
        <!-- 新密码 -->
        <div class="form-group">
            <label class="form-label">
                <span class="required">*</span>新密码
            </label>
            <div class="input-wrapper">
                <el-icon><Key /></el-icon>
                <input 
                    class="form-input" 
                    type="password" 
                    v-model="newPwd" 
                    placeholder="请输入新密码" 
                />
            </div>
        </div>
        
        <!-- 确认密码 -->
        <div class="form-group">
            <label class="form-label">
                <span class="required">*</span>确认密码
            </label>
            <div class="input-wrapper">
                <el-icon><Check /></el-icon>
                <input 
                    class="form-input" 
                    type="password" 
                    v-model="againPwd" 
                    placeholder="请再次输入新密码" 
                />
            </div>
        </div>
        
        <div class="form-actions">
            <button class="submit-button" @click="postInfo">
                <el-icon><RefreshRight /></el-icon>
                <span>立即修改</span>
            </button>
        </div>
    </div>
</template>
<script>
import { ref, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { clearToken } from "@/utils/storage"
import { Lock, Key, Check, RefreshRight } from '@element-plus/icons-vue'

export default {
    name: "ResetPwd",
    components: {
        Lock,
        Key,
        Check,
        RefreshRight
    },
    setup() {
        const { proxy } = getCurrentInstance()
        const router = useRouter()
        
        // 响应式数据
        const oldPwd = ref('')
        const newPwd = ref('')
        const againPwd = ref('')

        // 方法
        // 提交密码信息
        const postInfo = async () => {
            const userUpdatePwdDTO = {
                oldPwd: proxy.$md5(proxy.$md5(oldPwd.value)),
                newPwd: proxy.$md5(proxy.$md5(newPwd.value)),
                againPwd: proxy.$md5(proxy.$md5(againPwd.value))
            }
            const { data } = await proxy.$axios.put('/user/updatePwd', userUpdatePwdDTO);
            proxy.$notify({
                duration: 1000,
                title: '修改密码',
                message: data.code === 200 ? '修改成功，请重新登录' : data.msg,
                type: data.code === 200 ? 'success' : 'error'
            });
            if (data.code === 200) {
                clearToken();
                router.push('/');
            }
        }

        return {
            oldPwd,
            newPwd,
            againPwd,
            postInfo
        }
    }
};
</script>
<style scoped lang="scss">
.reset-password-container {
    width: 100%;
    max-width: 450px;
    margin: 0 auto;
    padding: 20px 0;
}

.form-group {
    margin-bottom: 25px;
}

.form-label {
    display: block;
    margin-bottom: 8px;
    font-size: 14px;
    font-weight: 500;
    color: #606266;
    
    .required {
        color: #f56c6c;
        margin-right: 4px;
    }
}

.input-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    background-color: #f5f7fa;
    border-radius: 8px;
    transition: all 0.3s ease;
    border: 1px solid #e4e7ed;
    overflow: hidden;
    
    &:hover {
        border-color: #c0c4cc;
    }
    
    &:focus-within {
        border-color: #ff5722;
        background-color: #fff;
        box-shadow: 0 0 0 2px rgba(255, 87, 34, 0.1);
    }
    
    i {
        padding: 0 12px;
        font-size: 16px;
        color: #909399;
    }
}

.form-input {
    flex: 1;
    border: none;
    outline: none;
    background: transparent;
    height: 40px;
    padding: 0 12px 0 0;
    font-size: 14px;
    color: #606266;
    width: 100%;
    
    &::placeholder {
        color: #c0c4cc;
    }
}

.form-actions {
    margin-top: 30px;
    text-align: center;
}

.submit-button {
    background: linear-gradient(45deg, #ff9b44, #fc6076);
    color: white;
    border: none;
    padding: 8px 20px;
    font-size: 14px;
    border-radius: 30px;
    cursor: pointer;
    box-shadow: 0 3px 10px rgba(252, 96, 118, 0.2);
    transition: all 0.3s ease;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    
    i {
        font-size: 14px;
    }
    
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(252, 96, 118, 0.3);
        background: linear-gradient(45deg, #ff9b44, #ff5722);
    }
    
    &:active {
        transform: translateY(0);
    }
}
</style>

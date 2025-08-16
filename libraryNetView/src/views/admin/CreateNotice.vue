<template>
    <el-row class="common-container">
        <el-row class="action-bar">
            <div class="btn-group">
                <el-button class="cancel-btn" size="small" @click="returnToDo">
                    <el-icon><Close /></el-icon> 取消操作
                </el-button>
                <el-button class="confirm-btn" type="primary" size="small" @click="saveOperation">
                    <el-icon><Check /></el-icon> {{ isUpdate ? '确认修改' : '确认新增' }}
                </el-button>
            </div>
        </el-row>
        <el-row style="margin: 10px 16px;">
            <el-row class="form-item">
                <span class="title-label"><el-icon><EditPen /></el-icon> 公告标题</span>
                <el-input class="title-input" v-model="data.name" placeholder="请输入公告标题" :maxlength="50" show-word-limit></el-input>
            </el-row>
            <el-row class="form-item">
                <span class="title-label"><el-icon><Document /></el-icon> 公告内容</span>
                <div class="editor-container">
                    <Editor v-model="data.content"></Editor>
                </div>
            </el-row>
        </el-row>
    </el-row>
</template>

<script>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { Close, Check, EditPen, Document } from '@element-plus/icons-vue'
import Editor from "@/components/Editor"

export default {
    components: { 
        Editor,
        Close,
        Check, 
        EditPen,
        Document
    },
    setup() {
        const { proxy } = getCurrentInstance()
        
        // 响应式数据
        const data = reactive({})
        const saveApi = ref('/notice/save')
        const updateApi = ref('/notice/update')
        const isUpdate = ref(false)

        // 方法定义
        const saveOperation = () => {
            if (isUpdate.value) {
                update()
            } else {
                save()
            }
        }

        const loadOperation = () => {
            const operation = sessionStorage.getItem('noticeOperation')
            console.log(operation)
            if (operation === 'update') {
                const notice = sessionStorage.getItem('noticeInfo')
                Object.assign(data, JSON.parse(notice))
                isUpdate.value = true
            }
        }
        const update = () => {
            proxy.$axios.put(updateApi.value, data).then(response => {
                if (response.data.code === 200) {
                    proxy.$swal.fire({
                        title: '公告修改',
                        text: '修改成功',
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1000,
                    })
                    // 返回上一级
                    proxy.$router.go(-1)
                }
            })
        }

        const save = () => {
            proxy.$axios.post(saveApi.value, data).then(response => {
                if (response.data.code === 200) {
                    proxy.$swal.fire({
                        title: '公告新增',
                        text: '新增成功',
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1000,
                    })
                    // 返回上一级
                    proxy.$router.go(-1)
                }
            })
        }

        const returnToDo = () => {
            proxy.$router.go(-1)
        }

        // 生命周期
        onMounted(() => {
            loadOperation()
        })

        return {
            data,
            saveApi,
            updateApi,
            isUpdate,
            saveOperation,
            loadOperation,
            update,
            save,
            returnToDo
        }
    }
}
</script>

<style scoped lang="scss">
.common-container {
    background-color: #FFFFFF;
    padding: 20px 0;
    border-radius: 16px;
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    box-sizing: border-box;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.action-bar {
    padding: 10px 16px;
    display: flex;
    justify-content: flex-end;
    border-bottom: 1px solid #f0f0f0;
    margin-bottom: 15px;
}

.btn-group {
    display: flex;
    gap: 10px;
}

.cancel-btn {
    color: #606266;
    background-color: #f5f7fa;
    border-color: #dcdfe6;
    border-radius: 20px;
    padding: 8px 15px;
    font-size: 13px;
    transition: all 0.3s;
    
    &:hover {
        color: #409EFF;
        border-color: #c6e2ff;
        background-color: #ecf5ff;
    }
    
    &:focus {
        outline: none;
    }
}

.confirm-btn {
    color: white;
    background-color: #409EFF;
    border-radius: 20px;
    padding: 8px 15px;
    font-size: 13px;
    border: none;
    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.2);
    transition: all 0.3s;
    
    &:hover {
        background-color: #66b1ff;
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
    }
    
    &:focus {
        outline: none;
    }
}

.form-item {
    margin-bottom: 20px;
}

.title-label {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    font-size: 14px;
    color: #606266;
    font-weight: 500;
    
    .el-icon {
        margin-right: 5px;
        color: #409EFF;
    }
}

.title-input {
    width: 100%;
    font-size: 14px;
}

.editor-container {
    margin-top: 10px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    overflow: hidden;
}
</style>

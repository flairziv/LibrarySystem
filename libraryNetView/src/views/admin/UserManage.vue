<template>
    <el-row class="common-container">
        <el-row class="filter-row">
            <el-select style="width: 120px; margin-right: 10px;" @change="fetchFreshData" size="small"
                    v-model="userQueryDto.isLogin" placeholder="登录状态">
                    <el-option v-for="item in loginStatuList" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                </el-select>
            <el-select style="width: 120px; margin-right: 10px;" @change="fetchFreshData" size="small"
                    v-model="userQueryDto.isWord" placeholder="禁言状态">
                    <el-option v-for="item in wordStatuList" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                </el-select>
            <el-date-picker style="width: 240px; margin-right: 10px;" @change="fetchFreshData" size="small"
                    v-model="searchTime" type="daterange" range-separator="-" start-placeholder="注册开始"
                    end-placeholder="注册结束">
                </el-date-picker>
            <el-input size="small" style="width: 180px; margin-right: 5px;" v-model="userQueryDto.userName" placeholder="用户名" clearable
                    @clear="handleFilterClear">
            </el-input>
            <el-button size="small" @click="handleFilter">
                <el-icon>
                    <Search />
                </el-icon>
            </el-button>
            <span class="new-user-btn" @click="add()">
                    新增用户
                </span>
        </el-row>
        <el-row class="table-container">
            <el-table :data="tableData" class="custom-table">
                <el-table-column prop="userAvatar" width="68" label="头像">
                    <template #default="scope">
                        <el-avatar :size="25" :src="scope.row.userAvatar"></el-avatar>
                    </template>
                </el-table-column>
                <el-table-column prop="userName" label="名称"></el-table-column>
                <el-table-column prop="userAccount" width="128" label="账号"></el-table-column>
                <el-table-column prop="userEmail" width="168" label="邮箱"></el-table-column>
                <el-table-column prop="userRole" width="68" label="角色">
                    <template #default="scope">
                        <span>{{ scope.row.userRole === 1 ? '管理员' : '用户' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="isLogin" width="108" label="封号">
                    <template #default="scope">
                        <el-icon v-if="scope.row.isLogin" style="margin-right: 5px;color: #E6A23C;">
                            <Warning />
                        </el-icon>
                        <el-icon v-else style="margin-right: 5px;color: #67C23A;">
                            <SuccessFilled />
                        </el-icon>
                        <el-tooltip v-if="scope.row.isLogin" class="item" effect="dark"
                            content="账号一经封号，不可登录系统。经由管理员解禁后，方可登录" placement="bottom-end">
                            <span style="text-decoration: underline;text-decoration-style: dashed;">已封号</span>
                        </el-tooltip>
                        <span v-else>正常</span>
                    </template>
                </el-table-column>
                <el-table-column prop="isWord" width="108" label="禁言">
                    <template #default="scope">
                        <el-icon v-if="scope.row.isWord" style="margin-right: 5px;color: #E6A23C;"><Warning /></el-icon>
                        <el-icon v-else style="margin-right: 5px;color: #67C23A;"><SuccessFilled /></el-icon>
                        <el-tooltip v-if="scope.row.isWord" class="item" effect="dark"
                            content="账号一经禁言，不可评论互动。经由管理员解禁后，方可评论" placement="bottom-end">
                            <span style="text-decoration: underline;text-decoration-style: dashed;">已禁言</span>
                        </el-tooltip>
                        <span v-else>正常</span>
                    </template>
                </el-table-column>
                <el-table-column :sortable="true" prop="createTime" width="168" label="注册于"></el-table-column>
                <el-table-column label="操作" width="200" align="center">
                    <template #default="scope">
                        <div class="action-buttons">
                            <el-tag type="warning" size="small" @click="handleStatus(scope.row)" class="action-tag status-tag">
                                账号状态
                            </el-tag>
                            <el-tag type="primary" size="small" @click="handleEdit(scope.row)" class="action-tag edit-tag">
                                编辑
                            </el-tag>
                            <el-tag type="danger" size="small" @click="handleDelete(scope.row)" class="action-tag delete-tag">
                                删除
                            </el-tag>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin: 20px 0;float: right;" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20]"
                :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="totalItems"></el-pagination>
        </el-row>
        <!-- 操作面板 -->
        <el-dialog v-model="dialogUserOperaion" :show-close="false" width="460px" custom-class="user-dialog" top="20vh" append-to-body>
            <div class="dialog-wrapper">
                <div class="dialog-header">
                    <h3 class="dialog-title">用户信息</h3>
                </div>
                <div class="dialog-content">
                    <div class="user-form">
                        <div class="avatar-section">
                            <p class="form-label"><el-icon><User /></el-icon> 用户头像</p>
                            <el-upload 
                                class="avatar-uploader"
                                :action="uploadUrl" 
                                :show-file-list="false"
                                :on-success="handleAvatarSuccess">
                                <div class="avatar-container">
                                    <img v-if="userAvatar" :src="userAvatar" class="dialog-avatar">
                                    <div v-else class="avatar-placeholder">
                                        <el-icon><Plus /></el-icon>
                                    </div>
                                    <div class="avatar-hover">
                                        <el-icon><Camera /></el-icon>
                                        <span>更换头像</span>
                                    </div>
                                </div>
                            </el-upload>
                        </div>
                        <div class="form-section">
                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label"><el-icon><UserFilled /></el-icon> 用户名</label>
                                    <el-input v-model="data.userName" placeholder="请输入用户名" size="medium"></el-input>
                                </div>
                                <div class="form-group">
                                    <label class="form-label"><el-icon><Medal /></el-icon> 账号</label>
                                    <el-input v-model="data.userAccount" placeholder="请输入账号" size="medium"></el-input>
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label"><el-icon><Message /></el-icon> 邮箱</label>
                                    <el-input v-model="data.userEmail" placeholder="请输入邮箱" size="medium"></el-input>
                                </div>
                                <div class="form-group">
                                    <label class="form-label"><el-icon><Lock /></el-icon> 密码</label>
                                    <el-input v-model="userPwd" type="password" placeholder="请输入密码" size="medium" show-password></el-input>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="dialog-footer">
                    <el-button class="cancel-btn" size="small" @click="cannel()">取消操作</el-button>
                    <el-button v-if="!isOperation" class="confirm-btn" type="primary" size="small" @click="addOperation()">确定新增</el-button>
                    <el-button v-else class="confirm-btn" type="primary" size="small" @click="updateOperation()">确定修改</el-button>
                </div>
            </div>
        </el-dialog>
        <el-dialog v-model="dialogStatusOperation" :show-close="false" width="360px" custom-class="status-dialog" top="20vh" append-to-body>
            <div class="dialog-wrapper">
                <div class="dialog-header">
                    <h3 class="dialog-title">账号状态设置</h3>
                </div>
                <div class="dialog-content">
                    <div class="switch-item">
                        <div class="switch-label">
                            <el-icon><Lock /></el-icon>
                            <span>封号状态</span>
                        </div>
                        <el-switch v-model="data.isLogin" active-text="封号" inactive-text="正常状态"></el-switch>
                    </div>
                    <div class="switch-item">
                        <div class="switch-label">
                            <el-icon><TurnOff /></el-icon>
                            <span>禁言状态</span>
                        </div>
                        <el-switch v-model="data.isWord" active-text="禁言" inactive-text="正常状态"></el-switch>
                    </div>
                    <div class="switch-item">
                        <div class="switch-label">
                            <el-icon><User /></el-icon>
                            <span>用户角色</span>
                        </div>
                        <el-switch v-model="isAdmin" active-text="管理员" inactive-text="普通用户"></el-switch>
                    </div>
                </div>
                <div class="dialog-footer">
                    <el-button class="cancel-btn" size="small" @click="cannel()">取消操作</el-button>
                    <el-button class="confirm-btn" type="primary" size="small" @click="comfirmStatus()">确认设置</el-button>
                </div>
            </div>
        </el-dialog>
    </el-row>
</template>

<script>
import { ref, reactive, onMounted, getCurrentInstance, nextTick } from 'vue'
import { getUploadUrl } from "@/utils/urlHelper.js"
import { Search, Warning, SuccessFilled, User, Plus, Camera, UserFilled, Medal, Message, Lock, TurnOff } from '@element-plus/icons-vue'

export default {
    components: {
        Search,
        Warning,
        SuccessFilled,
        User,
        Plus,
        Camera,
        UserFilled,
        Medal,
        Message,
        Lock,
        TurnOff
    },
    setup() {
        const { proxy } = getCurrentInstance()
        
        // 响应式数据
        const uploadUrl = ref(getUploadUrl())
        const userPwd = ref('')
        const userAvatar = ref('')
        const data = ref({})
        const filterText = ref('')
        const isAdmin = ref(false) // 是否是管理员标志，初始值为false
        const currentPage = ref(1)
        const pageSize = ref(10)
        const totalItems = ref(0)
        const dialogStatusOperation = ref(false)
        const dialogUserOperaion = ref(false) // 开关
        const isOperation = ref(false) // 开关-标识新增或修改
        const tableData = ref([])
        const searchTime = ref([])
        const selectedRows = ref([])
        const status = ref(null)
        const userQueryDto = reactive({}) // 搜索条件
        const loginStatuList = ref([
            { value: null, label: '全部' }, 
            { value: 0, label: '正常' }, 
            { value: 1, label: '封号' }
        ])
        const wordStatuList = ref([
            { value: null, label: '全部' }, 
            { value: 0, label: '正常' }, 
            { value: 1, label: '禁言' }
        ])
        const rolesList = ref([
            { value: null, label: '全部' }, 
            { value: 2, label: '用户' }, 
            { value: 1, label: '管理员' }
        ])

        const cannel = () => {
            data.value = {}
            userAvatar.value = ''
            userPwd.value = ''
            dialogUserOperaion.value = false
            dialogStatusOperation.value = false
            isOperation.value = false
            // 清理多余的遮罩层
            nextTick(() => {
                const modals = document.querySelectorAll('.v-modal')
                if (modals.length > 1) {
                    for (let i = 1; i < modals.length; i++) {
                        modals[i].parentNode.removeChild(modals[i])
                    }
                }
                // 移除body上的dialog样式
                document.body.classList.remove('el-popup-parent--hidden')
            })
        }

        const comfirmStatus = () => {
            const userUpdateDto = {
                id: data.value.id,
                isLogin: data.value.isLogin,
                userRole: isAdmin.value ? 1 : 2,
                isWord: data.value.isWord
            }
            proxy.$axios.put(`/user/backUpdate`, userUpdateDto).then(res => {
                if (res.data.code === 200) {
                    proxy.$notify({
                        duration: 1500,
                        title: '信息修改',
                        message: '修改成功',
                        type: 'success'
                    })
                    cannel()
                    fetchFreshData()
                }
            }).catch(error => {
                console.log("修改状态异常：" + error)
            })
        }

        // 修改用户状态
        const handleStatus = (userData) => {
            // 设置用户角色
            isAdmin.value = userData.userRole === 1
            dialogStatusOperation.value = true
            data.value = userData
        }

        // 头像上传回调函数
        const handleAvatarSuccess = (res, file) => {
            proxy.$notify({
                duration: 1500,
                title: '头像上传',
                message: res.code === 200 ? '上传成功' : '上传失败',
                type: res.code === 200 ? 'success' : 'error'
            })
            // 上传成功则更新用户头像
            if (res.code === 200) {
                userAvatar.value = res.data
            }
        }

        // 批量删除数据
        const batchDelete = async () => {
            if (!selectedRows.value.length) {
                proxy.$message(`未选中任何数据`)
                return
            }
            const confirmed = await proxy.$swalConfirm({
                title: '删除用户数据',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: '确认删除',
                cancelButtonText: '取消',
                confirmButtonColor: '#f56c6c',
                cancelButtonColor: '#909399',
                customClass: {
                    popup: 'custom-delete-popup',
                    confirmButton: 'custom-delete-confirm-button',
                    cancelButton: 'custom-delete-cancel-button',
                    title: 'custom-delete-title',
                    content: 'custom-delete-content',
                    icon: 'custom-delete-icon'
                },
                buttonsStyling: true,
                iconColor: '#f56c6c',
                backdrop: `rgba(0,0,0,0.4)`,
                heightAuto: false,
                padding: '2em'
            })
            
            if (confirmed) {
                try {
                    let ids = selectedRows.value.map(entity => entity.id)
                    const response = await proxy.$axios.post(`/user/batchDelete`, ids)
                    if (response.data.code === 200) {
                        proxy.$notify({
                            duration: 1000,
                            title: '信息删除',
                            message: '删除成功',
                            type: 'success'
                        })
                        fetchFreshData()
                        return
                    }
                } catch (error) {
                    proxy.$message.error("用户信息删除异常：", error)
                    console.error(`用户信息删除异常：`, error)
                }
            }
        }

        // 修改信息
        const updateOperation = async () => {
            if (userPwd.value !== '') {
                const pwd = proxy.$md5(proxy.$md5(userPwd.value))
                data.value.userPwd = pwd
            } else {
                data.value.userPwd = null
            }
            data.value.userAvatar = userAvatar.value
            try {
                const response = await proxy.$axios.put('/user/backUpdate', data.value)
                if (response.data.code === 200) {
                    proxy.$notify({
                        duration: 1000,
                        title: '信息修改',
                        message: '修改成功',
                        type: 'success'
                    })
                    cannel()
                    fetchFreshData()
                }
            } catch (error) {
                console.error('修改出错:', error)
            }
        }

        // 信息新增
        const addOperation = async () => {
            //  密码处理
            if (userPwd.value !== '') {
                data.value.userPwd = proxy.$md5(proxy.$md5(userPwd.value))
            } else {
                data.value.userPwd = null
            }
            data.value.userAvatar = userAvatar.value
            try {
                const response = await proxy.$axios.post('/user/insert', data.value)
                if (response.data.code === 200) {
                    proxy.$notify({
                        duration: 1000,
                        title: '信息新增',
                        message: '新增成功',
                        type: 'success'
                    })
                    cannel()
                    fetchFreshData()
                } else {
                    proxy.$notify({
                        duration: 1000,
                        title: '信息新增',
                        message: response.data.msg,
                        type: 'info'
                    })
                }
            } catch (error) {
                console.error('信息新增出错:', error)
                proxy.$message.error('提交失败，请稍后再试！')
            }
        }

        const fetchFreshData = async () => {
            try {
                tableData.value = []
                let startTime = null
                let endTime = null
                if (searchTime.value != null && searchTime.value.length === 2) {
                    const [startDate, endDate] = await Promise.all(searchTime.value.map(date => date.toISOString()))
                    startTime = `${startDate.split('T')[0]}T00:00:00`
                    endTime = `${endDate.split('T')[0]}T23:59:59`
                }
                // 请求参数
                const params = {
                    current: currentPage.value,
                    size: pageSize.value,
                    key: filterText.value,
                    startTime: startTime,
                    endTime: endTime,
                    ...userQueryDto
                }
                const response = await proxy.$axios.post('/user/query', params)
                const { data: responseData } = response
                tableData.value = responseData.data
                totalItems.value = responseData.total
            } catch (error) {
                proxy.$message.error("查询用户信息异常:", error)
                console.error('查询用户信息异常:', error)
            }
        }

        const add = () => {
            dialogUserOperaion.value = true
        }

        const handleFilter = () => {
            currentPage.value = 1
            fetchFreshData()
        }

        const handleFilterClear = () => {
            filterText.value = ''
            handleFilter()
        }

        const handleSizeChange = (val) => {
            pageSize.value = val
            currentPage.value = 1
            fetchFreshData()
        }

        const handleCurrentChange = (val) => {
            currentPage.value = val
            fetchFreshData()
        }

        const handleEdit = (row) => {
            dialogUserOperaion.value = true
            isOperation.value = true
            row.userPwd = null
            userAvatar.value = row.userAvatar
            data.value = { ...row }
        }

        const handleDelete = (row) => {
            selectedRows.value.push(row)
            batchDelete()
        }

        // 生命周期
        onMounted(() => {
            fetchFreshData()
        })

        return {
            uploadUrl,
            userPwd,
            userAvatar,
            data,
            filterText,
            isAdmin,
            currentPage,
            pageSize,
            totalItems,
            dialogStatusOperation,
            dialogUserOperaion,
            isOperation,
            tableData,
            searchTime,
            selectedRows,
            status,
            userQueryDto,
            loginStatuList,
            wordStatuList,
            rolesList,
            cannel,
            comfirmStatus,
            handleStatus,
            handleAvatarSuccess,
            batchDelete,
            updateOperation,
            addOperation,
            fetchFreshData,
            add,
            handleFilter,
            handleFilterClear,
            handleSizeChange,
            handleCurrentChange,
            handleEdit,
            handleDelete
        }
    }
}
</script>
<style scoped lang="scss">
.common-container {
    padding: 10px;
    border-radius: 16px;
    background-color: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(10px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.filter-row {
    display: flex;
    align-items: center;
    padding: 10px 16px;
    margin-bottom: 5px;
}

.table-container {
    margin: 10px 16px;
    border-top: none;
}

.new-user-btn {
    padding: 7px 20px;
    background-color: #409EFF;
    color: white;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s;
    margin-left: auto;
    font-size: 14px;
    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
    
    &:hover {
        background-color: #66b1ff;
        transform: translateY(-1px);
    }
}

/* 按钮统一样式 */
.text-button {
    margin-right: 10px;
    cursor: pointer;
    font-size: 13px;
    display: inline-block;
    height: 20px;
    line-height: 20px;
    
    &:hover {
        text-decoration: underline;
    }
    
    &.status-btn {
        color: #E6A23C;
        
        &:hover {
            color: #f0b556;
        }
    }
    
    &.edit-btn {
        color: #409EFF;
        
        &:hover {
            color: #66b1ff;
        }
    }
    
    &.delete-btn {
        color: #F56C6C;
        
        &:hover {
            color: #ff8c8c;
        }
    }
}

/* 表格样式 */
.custom-table {
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 20px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
    border: none;

    :deep(.el-table__header-wrapper) {
        th {
        background-color: #f5f7fa;
        color: #606266;
        font-weight: 600;
        padding: 12px 0;
        }
    }

    :deep(.el-table__body-wrapper) {
        .el-table__row {
        transition: all 0.3s;
        
        &:hover {
            background-color: #f0f9ff !important;
        }
        
        td {
            padding: 10px 0;
            vertical-align: middle;
            height: 40px;
            line-height: 20px;
            border-bottom: 1px solid #ebeef5;
            }
        }
    }

    :deep(.el-table__empty-block) {
        min-height: 60px;
    }

    :deep(.el-table__header), 
    :deep(.el-table__body) {
        border: none;
    }
    
    &::before, 
    &::after,
    :deep(.el-table--border::after), 
    :deep(.el-table--group::after) {
        display: none;
    }
}

/* 分页样式美化 */
:deep(.el-pagination) {
    .el-pagination__total {
        font-weight: 500;
    }
    
    .el-pagination__sizes {
        .el-input .el-input__inner {
        border-radius: 4px;
        transition: all 0.3s;
        
        &:hover, &:focus {
            border-color: #409EFF;
            }
        }
    }
    
    .el-pager li {
        border-radius: 4px;
        transition: all 0.3s;
        
        &:hover {
            color: #409EFF;
        }
        
        &.active {
            background-color: #409EFF;
            color: #fff;
        }
    }
    
    .btn-prev, .btn-next {
        border-radius: 4px;
        
        &:hover {
            color: #409EFF;
        }
    }
}

/* 用户信息对话框样式 */
.user-dialog, .status-dialog {
    :deep(.el-dialog) {
        border-radius: 16px;
        overflow: hidden;
        background-color: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(10px);
        box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
        margin-top: 0 !important;
        position: relative;
        z-index: 3000 !important;
    }

    :deep(.v-modal) {
        z-index: 2999 !important;
    }
}

.dialog-wrapper {
    display: flex;
    flex-direction: column;
    overflow: hidden;
    max-height: 80vh;
}

.dialog-header {
    background: linear-gradient(135deg, #e0eaff, #f2e6ff);
    padding: 16px 20px;
    position: relative;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.dialog-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin: 0;
    position: relative;
    padding-left: 12px;
    
    &:before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 18px;
        background: linear-gradient(to bottom, #409EFF, #a18cd1);
        border-radius: 2px;
    }
}

.dialog-content {
    padding: 20px;
    background-color: white;
    overflow-y: auto;
}

.dialog-footer {
    padding: 16px 20px;
    background-color: rgba(245, 247, 250, 0.8);
    text-align: right;
    border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.user-form {
    display: flex;
    flex-direction: column;
}

.avatar-section {
    width: 100%;
    margin-bottom: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.form-section {
    width: 100%;
}

.form-label {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    font-weight: 500;
    color: #606266;
    
    i {
        margin-right: 5px;
        color: #409EFF;
    }
}

.form-row {
    display: flex;
    gap: 15px;
    margin-bottom: 15px;
}

.form-group {
    flex: 1;
}

.avatar-container {
    position: relative;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    overflow: hidden;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
        
        .avatar-hover {
            opacity: 1;
        }
    }
}

.dialog-avatar {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.avatar-placeholder {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2px dashed #d9d9d9;
    border-radius: 50%;
    background-color: #fafafa;
    
    i {
        font-size: 30px;
        color: #c0c4cc;
    }
}

.avatar-hover {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: rgba(0, 0, 0, 0.5);
    border-radius: 50%;
    opacity: 0;
    transition: opacity 0.3s;
    
    i {
        color: white;
        font-size: 20px;
        margin-bottom: 4px;
    }
    
    span {
        color: white;
        font-size: 12px;
    }
}

/* 按钮样式 */
.cancel-btn, .confirm-btn {
    padding: 8px 20px;
    border-radius: 20px;
    cursor: pointer;
    margin-left: 10px;
    font-size: 14px;
    transition: all 0.3s;
}

.cancel-btn {
    background-color: #f5f7fa;
    color: #606266;
    border: 1px solid #dcdfe6;

    &:hover {
        color: #409EFF;
        border-color: #c6e2ff;
        background-color: #ecf5ff;
        transform: translateY(-1px);
    }
}

.confirm-btn {
    background: linear-gradient(to right, #409EFF, #64b0ff);
    color: white;
    border: none;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);

    &:hover {
        background: linear-gradient(to right, #48a6ff, #70baff);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
    }
}

/* 状态对话框样式 */
.status-dialog {
    :deep(.el-switch__label) {
        color: #606266;
        
        &.is-active {
            color: #409EFF;
        }
    }
}

.switch-item {
    margin-bottom: 20px;
    display: flex;
    flex-direction: column;
    
    &:last-child {
        margin-bottom: 0;
    }
}

.switch-label {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    font-weight: 500;
    
    i {
        font-size: 16px;
        margin-right: 8px;
        color: #409EFF;
    }
    
    span {
        color: #606266;
    }
}

/* 标签样式 */
.action-tag {
    cursor: pointer;
    transition: all 0.2s;
    margin: 0 4px;
    
    &:hover {
        opacity: 0.8;
        transform: scale(1.05);
    }
    
    &.status-tag {
        background-color: #fdf6ec;
        color: #E6A23C;
        border-color: #faecd8;
    }
    
    &.edit-tag {
        background-color: #ecf5ff;
        color: #409EFF;
        border-color: #d9ecff;
    }
    
    &.delete-tag {
        background-color: #fef0f0;
        color: #F56C6C;
        border-color: #fde2e2;
    }
}

.action-buttons {
    display: flex;
    justify-content: center;
    flex-wrap: nowrap;
}
</style>

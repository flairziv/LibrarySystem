<template>
    <el-row class="common-container">
        <el-row style="padding: 10px 16px;">
            <el-row>
                <span class="top-bar">楼层</span>
                <el-input size="small" style="width: 188px;margin-right: 10px;" v-model="bookshelfQueryDto.floor"
                    placeholder="楼层" clearable @clear="handleFilterClear">
                </el-input>
                <el-button size="small" class="customer"
                    style="background-color: #3a8ee6;color: #ffffff;border: none;font-weight: 500;box-shadow: 0 2px 6px rgba(58, 142, 230, 0.3);" type="primary"
                    @click="handleFilter">立即查询</el-button>
                <el-button size="small"
                    style="background-color: #67c23a;color: #ffffff;border: none;font-weight: 500;box-shadow: 0 2px 6px rgba(103, 194, 58, 0.3);" class="customer"
                    type="info" @click="add()">新增书架</el-button>
            </el-row>
        </el-row>
        <el-row style="margin: 10px 16px;">
            <el-table :data="tableData" class="custom-table">
                <el-table-column prop="floor" label="书架所在楼层"></el-table-column>
                <el-table-column prop="area" width="188" label="书架所在区域"></el-table-column>
                <el-table-column prop="frame" width="148" label="书架名"></el-table-column>
                <el-table-column label="操作" width="160" align="center">
                    <template #default="scope">
                        <el-tag type="primary" size="small" @click.native="handleEdit(scope.row)" class="action-tag edit-tag" style="min-width: 42px; margin-right: 8px;">
                            编辑
                        </el-tag>
                        <el-tag type="danger" size="small" @click.native="handleDelete(scope.row)" class="action-tag delete-tag" style="min-width: 42px;">
                            删除
                        </el-tag>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin: 20px 0;float: right;" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[5, 7]"
                :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="totalItems"></el-pagination>
        </el-row>
        <el-dialog v-model="dialogOperation" width="25%" custom-class="bookshelf-dialog">
            <div class="dialog-header">
                <h3 class="dialog-title">{{ !isOperation ? '新增书架' : '修改书架' }}</h3>
            </div>
            <div class="dialog-content">
                <div class="form-group">
                    <label class="form-label">
                        <el-icon><OfficeBuilding /></el-icon> 
                        书架所在楼层
                    </label>
                    <el-input class="custom-input" v-model="data.floor" placeholder="请输入楼层"></el-input>
                </div>
                <div class="form-group">
                    <label class="form-label">
                        <el-icon><Location /></el-icon> 
                        书架所在区域
                    </label>
                    <el-input class="custom-input" v-model="data.area" placeholder="请输入区域"></el-input>
                </div>
                <div class="form-group">
                    <label class="form-label">
                        <el-icon><Collection /></el-icon> 
                        书架名
                    </label>
                    <el-input class="custom-input" v-model="data.frame" placeholder="请输入书架名称"></el-input>
                </div>
            </div>
            <div class="dialog-footer">
                <el-button class="cancel-btn" size="small" @click="dialogOperation = false">取消</el-button>
                <el-button class="confirm-btn" size="small" v-if="!isOperation" type="primary" @click="addOperation">新增</el-button>
                <el-button class="confirm-btn" size="small" v-else type="primary" @click="updateOperation">修改</el-button>
            </div>
        </el-dialog>
    </el-row>
</template>

<script>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'

export default {
    setup() {
        const { proxy } = getCurrentInstance()
        
        // 响应式数据
        const data = reactive({})
        const currentPage = ref(1)
        const cover = ref(null)
        const pageSize = ref(7)
        const totalItems = ref(0)
        const dialogOperation = ref(false) // 开关
        const isOperation = ref(false) // 开关-标识新增或修改
        const tableData = ref([])
        const selectedRows = ref([])
        const bookshelfQueryDto = reactive({}) // 搜索条件
        const options = ref([])

        // 方法定义
        // 多选框选中
        const handleSelectionChange = (selection) => {
            selectedRows.value = selection
        }

        // 批量删除数据
        const batchDelete = async () => {
            if (!selectedRows.value.length) {
                proxy.$message(`未选中任何数据`)
                return
            }
            const confirmed = await proxy.$swalConfirm({
                title: '删除书架数据',
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
                    const response = await proxy.$axios.post(`bookshelf/batchDelete`, ids)
                    if (response.data.code === 200) {
                        proxy.$swal.fire({
                            title: '删除成功',
                            text: response.data.msg,
                            icon: 'success',
                            showConfirmButton: false,
                            timer: 2000,
                        })
                        fetchFreshData()
                        return
                    }
                } catch (e) {
                    proxy.$swal.fire({
                        title: '错误提示',
                        text: e,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: 2000,
                    })
                    console.error(`书架信息删除异常：`, e)
                }
            }
        }

        const resetQueryCondition = () => {
            Object.keys(bookshelfQueryDto).forEach(key => {
                delete bookshelfQueryDto[key]
            })
            fetchFreshData()
        }

        // 修改信息
        const updateOperation = async () => {
            try {
                data.cover = cover.value
                const response = await proxy.$axios.put('/bookshelf/update', data)
                proxy.$swal.fire({
                    title: '书架信息修改',
                    text: response.data.msg,
                    icon: response.data.code === 200 ? 'success' : 'error',
                    showConfirmButton: false,
                    timer: 1000,
                })
                if (response.data.code === 200) {
                    closeDialog()
                    fetchFreshData()
                    clearFormData()
                }
            } catch (error) {
                console.error('提交表单时出错:', error)
                proxy.$message.error('提交失败，请稍后再试！')
            }
        }

        // 信息新增
        const addOperation = async () => {
            try {
                data.cover = cover.value
                const response = await proxy.$axios.post('/bookshelf/save', data)
                proxy.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg)
                if (response.data.code === 200) {
                    closeDialog()
                    fetchFreshData()
                    clearFormData()
                }
            } catch (error) {
                console.error('提交表单时出错:', error)
                proxy.$message.error('提交失败，请稍后再试！')
            }
        }

        const closeDialog = () => {
            dialogOperation.value = false
        }

        const clearFormData = () => {
            Object.keys(data).forEach(key => {
                delete data[key]
            })
            cover.value = null
        }

        const fetchFreshData = async () => {
            try {
                tableData.value = []
                let startTime = null
                let endTime = null
                // 请求参数
                const params = {
                    current: currentPage.value,
                    size: pageSize.value,
                    startTime: startTime,
                    endTime: endTime,
                    ...bookshelfQueryDto
                }
                const response = await proxy.$axios.post('/bookshelf/query', params)
                const { data: responseData } = response
                tableData.value = responseData.data
                totalItems.value = responseData.total
            } catch (error) {
                console.error('查询书架信息异常:', error)
            }
        }

        const add = () => {
            dialogOperation.value = true
        }

        const handleFilter = () => {
            currentPage.value = 1
            fetchFreshData()
        }

        const handleFilterClear = () => {
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

        const messagePush = (row) => {
            Object.assign(data, { ...row })
        }

        const handleEdit = (row) => {
            dialogOperation.value = true
            isOperation.value = true
            Object.assign(data, { ...row })
            cover.value = row.cover
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
            data,
            currentPage,
            cover,
            pageSize,
            totalItems,
            dialogOperation,
            isOperation,
            tableData,
            selectedRows,
            bookshelfQueryDto,
            options,
            handleSelectionChange,
            batchDelete,
            resetQueryCondition,
            updateOperation,
            addOperation,
            closeDialog,
            clearFormData,
            fetchFreshData,
            add,
            handleFilter,
            handleFilterClear,
            handleSizeChange,
            handleCurrentChange,
            messagePush,
            handleEdit,
            handleDelete
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
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.03);
}

.list-cover {
    width: 50px;
    height: 70px;
    border-radius: 5px;
}

.top-bar {
    color: #606266;
    font-size: 14px;
    margin-right: 8px;
    line-height: 32px;
    font-weight: 500;
}

.edit-btn {
    color: #409EFF;
}

.delete-btn {
    color: #F56C6C;
}

/* 日期选择器美化样式 */
.custom-date-picker {
    :deep(.el-input__inner) {
        border-radius: 4px;
        border-color: #dcdfe6;
        transition: all 0.2s;
        
        &:hover {
            border-color: #c0c4cc;
        }
        
        &:focus {
            border-color: #409EFF;
            box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
        }
    }

    :deep(.el-range-separator) {
        color: #606266;
        padding: 0 5px;
        font-weight: 500;
        display: inline-flex;
        align-items: center;
    }

    :deep(.el-range-input) {
        font-size: 13px;
        color: #606266;
    }

    :deep(.el-range__icon) {
        color: #c0c4cc;
    }

    :deep(.el-range__close-icon) {
        color: #c0c4cc;
        font-size: 14px;
        
        &:hover {
            color: #909399;
        }
    }
}

/* 对话框样式 */
.bookshelf-dialog {
    border-radius: 8px;
    overflow: hidden;
    
    :deep(.el-dialog__header) {
        display: none;
    }
    
    :deep(.el-dialog__body) {
        padding: 0;
    }
    
    :deep(.el-dialog__footer) {
        display: none;
    }
}

.dialog-header {
    background-color: #f9f9f9;
    padding: 16px 20px;
    border-bottom: 1px solid #ebeef5;
}

.dialog-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0;
    padding: 0;
    position: relative;
    display: inline-block;
    
    &:after {
        content: '';
        position: absolute;
        bottom: -6px;
        left: 0;
        width: 30px;
        height: 2px;
        background-color: #409EFF;
    }
}

.dialog-content {
    padding: 20px;
}

.dialog-footer {
    padding: 10px 20px 20px;
    text-align: right;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.form-group {
    margin-bottom: 20px;
}

.form-label {
    font-size: 14px;
    color: #606266;
    margin-bottom: 8px;
    font-weight: 500;
    display: block;
    
    i {
        margin-right: 5px;
        color: #409EFF;
    }
}

.custom-input {
    :deep(.el-input__inner) {
        border-radius: 4px;
        transition: all 0.3s;
        height: 36px;
        
        &:hover, &:focus {
            border-color: #409EFF;
        }
    }
}

.cancel-btn, .confirm-btn {
    padding: 8px 20px;
    font-size: 13px;
    border-radius: 4px;
    transition: all 0.3s;
    
    &:focus {
        outline: none;
    }
}

.cancel-btn {
    background-color: #f5f7fa;
    color: #606266;
    border: 1px solid #dcdfe6;
    
    &:hover {
        color: #409EFF;
        border-color: #c6e2ff;
        background-color: #ecf5ff;
    }
}

.confirm-btn {
    background-color: #409EFF;
    color: white;
    border: none;
    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.2);
    
    &:hover {
        background-color: #66b1ff;
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
    }
}

/* 添加表格样式 */
.custom-table {
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 20px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

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
            }
        }
    }
}

/* 添加文本按钮样式 */
.text-button {
    margin-right: 8px;
    cursor: pointer;
    font-size: 13px;
    transition: all 0.3s;
    display: inline-block;
    height: 20px;
    line-height: 20px;
    
    &:hover {
        text-decoration: underline;
        
        &.edit-btn {
            color: #66b1ff;
        }
        
        &.delete-btn {
            color: #ff8c8c;
        }
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

.action-tag {
    cursor: pointer;
    transition: all 0.2s;
    padding: 0 8px;
    line-height: 22px;
    color: #fff;
    display: inline-block;
    
    &:hover {
        opacity: 0.8;
        transform: scale(1.05);
    }
}

.edit-tag {
    background-color: #409EFF;
}

.delete-tag {
    background-color: #F56C6C;
}
</style>

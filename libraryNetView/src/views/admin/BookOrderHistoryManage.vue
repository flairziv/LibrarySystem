<template>
    <el-row class="common-container">
        <el-row class="filter-row">
            <span class="top-bar">借阅时间</span>
            <el-date-picker size="small" style="width: 240px;" v-model="searchTime" type="daterange"
                value-format="yyyy-MM-dd"
                range-separator="-" start-placeholder="起始日期" end-placeholder="结束日期">
            </el-date-picker>
            <el-button size="small" class="query-btn no-icon" type="primary"
                @click="handleFilter">立即查询</el-button>
        </el-row>
        <el-row class="table-container">
            <el-table :data="tableData" class="custom-table">
                <el-table-column prop="bookName" label="书籍名"></el-table-column>
                <el-table-column prop="userName" width="98" label="订阅者"></el-table-column>
                <el-table-column prop="deadlineNum" width="88" label="馆藏数"></el-table-column>
                <el-table-column prop="isReturn" width="130" label="是否归还">
                    <template #default="scope">
                        <el-tag :type="scope.row.isReturn ? 'success' : 'warning'" size="small">
                            {{ scope.row.isReturn ? '已归还' : '未归还' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="returnTime" width="168" label="归还时间"></el-table-column>
                <el-table-column prop="createTime" width="168" label="借阅时间"></el-table-column>
                <el-table-column label="操作" width="100" align="center">
                    <template #default="scope">
                        <div class="action-buttons">
                            <el-tag type="danger" size="small" @click.native="handleDelete(scope.row)" class="action-tag delete-tag">
                                删除
                            </el-tag>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin: 20px 0;float: right;" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[5, 7]"
                :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="totalItems"></el-pagination>
        </el-row>
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
        const tableData = ref([])
        const selectedRows = ref([])
        const bookOrderHistoryQueryDto = reactive({}) // 搜索条件
        const options = ref([])
        const bookshelfOptions = ref([])
        const searchTime = ref([])

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
                title: '删除借阅记录数据',
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
                    const response = await proxy.$axios.post(`bookOrderHistory/batchDelete`, ids)
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
                    console.error(`借阅历史删除异常：`, e)
                }
            }
        }

        const resetQueryCondition = () => {
            searchTime.value = []
            Object.keys(bookOrderHistoryQueryDto).forEach(key => {
                delete bookOrderHistoryQueryDto[key]
            })
            fetchFreshData()
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
                if (searchTime.value != null && searchTime.value.length === 2) {
                    startTime = `${searchTime.value[0]}T00:00:00`
                    endTime = `${searchTime.value[1]}T23:59:59`
                }
                // 请求参数
                const params = {
                    current: currentPage.value,
                    size: pageSize.value,
                    startTime: startTime,
                    endTime: endTime,
                    ...bookOrderHistoryQueryDto
                }
                const response = await proxy.$axios.post('bookOrderHistory/query', params)
                const { data: responseData } = response
                tableData.value = responseData.data
                totalItems.value = responseData.total
            } catch (error) {
                console.error('查询借阅记录信息异常:', error)
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
            selectedRows.value = []
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
            tableData,
            selectedRows,
            bookOrderHistoryQueryDto,
            options,
            bookshelfOptions,
            searchTime,
            handleSelectionChange,
            batchDelete,
            resetQueryCondition,
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
    box-sizing: border-box;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.03);
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

.top-bar {
    color: #606266;
    font-size: 14px;
    margin-right: 8px;
    line-height: 32px;
    font-weight: 500;
}

.query-btn {
    margin-left: 10px;
    background-color: #3a8ee6;
    color: #ffffff;
    border: none;
    font-weight: 500;
    box-shadow: 0 2px 6px rgba(58, 142, 230, 0.3);
}

/* 添加表格样式 */
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

.action-buttons {
    display: flex;
    justify-content: center;
    flex-wrap: nowrap;
}

.action-tag {
    cursor: pointer;
    transition: all 0.2s;
    margin: 0 4px;
    
    &:hover {
        opacity: 0.8;
        transform: scale(1.05);
    }
    
    &.delete-tag {
        background-color: #fef0f0;
        color: #F56C6C;
        border-color: #fde2e2;
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

/* 隐藏按钮图标 */
.no-icon {
    :deep(i) {
        display: none !important;
    }
}
</style>

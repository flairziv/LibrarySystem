<template>
    <el-row class="common-container">
        <el-row style="padding: 10px 16px;">
            <el-row>
                <span class="top-bar">订阅时间</span>
                <el-date-picker size="small" style="width: 240px;" v-model="searchTime" type="daterange"
                    value-format="yyyy-MM-dd"
                    range-separator="-" start-placeholder="起始日期" end-placeholder="结束日期">
                </el-date-picker>
                <el-button size="small" class="customer"
                    style="margin-left: 10px;background-color: rgb(235, 237, 245);color: white;border: none;" type="primary"
                    @click="handleFilter">立即查询</el-button>
            </el-row>
        </el-row>
        <el-row style="margin: 10px 16px;">
            <el-table :data="tableData" class="custom-table">
                <el-table-column prop="bookName" label="图书"></el-table-column>
                <el-table-column prop="num" width="68" label="馆藏数"></el-table-column>
                <el-table-column prop="author" width="118" label="作者"></el-table-column>
                <el-table-column prop="floor" width="148" label="馆藏区">
                    <template #default="scope">
                        <span>{{ scope.row.floor + "-" + scope.row.area + "-" + scope.row.frame }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="userName" width="148" label="订阅者"></el-table-column>
                <el-table-column prop="createTime" width="168" label="订阅时间"></el-table-column>
                <el-table-column label="操作" width="90" align="center">
                    <template #default="scope">
                        <el-tag type="danger" size="small" @click.native="handleDelete(scope.row)" class="delete-tag">
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
        const bookRssHistoryQueryDto = reactive({}) // 搜索条件
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
                title: '删除图书订阅数据',
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
                    const response = await proxy.$axios.post(`bookRssHistory/batchDelete`, ids)
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
                    console.error(`订阅记录删除异常：`, e)
                }
            }
        }

        const resetQueryCondition = () => {
            searchTime.value = []
            Object.keys(bookRssHistoryQueryDto).forEach(key => {
                delete bookRssHistoryQueryDto[key]
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
                    const [startDate, endDate] = await Promise.all(searchTime.value.map(date => date.toISOString()))
                    startTime = `${startDate.split('T')[0]}T00:00:00`
                    endTime = `${endDate.split('T')[0]}T23:59:59`
                }
                // 请求参数
                const params = {
                    current: currentPage.value,
                    size: pageSize.value,
                    startTime: startTime,
                    endTime: endTime,
                    ...bookRssHistoryQueryDto
                }
                const response = await proxy.$axios.post('bookRssHistory/query', params)
                const { data: responseData } = response
                tableData.value = responseData.data
                totalItems.value = responseData.total
            } catch (error) {
                console.error('查询图书订阅信息异常:', error)
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
            tableData,
            selectedRows,
            bookRssHistoryQueryDto,
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
    max-width: 1200px;
    margin: 0 auto;
    box-sizing: border-box;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.03);
}

.top-bar {
    color: #606266;
    font-size: 14px;
    margin-right: 8px;
    line-height: 32px;
    font-weight: 500;
}

.delete-tag {
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover {
        opacity: 0.8;
        transform: scale(1.05);
    }
}

.delete-btn {
    color: #F56C6C;
    font-size: 13px;
    cursor: pointer;
    display: inline-block;
    height: 20px;
    line-height: 20px;
    
    &:hover {
        color: #ff8c8c;
    }
}

.text-button {
    color: #409EFF;
    margin-right: 8px;
    cursor: pointer;
    font-size: 14px;
    
    &:hover {
        color: #66b1ff;
        text-decoration: underline;
    }
}

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

.list-cover {
    width: 50px;
    height: 70px;
    border-radius: 5px;
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
</style>

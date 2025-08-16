<template>
    <el-row class="common-container">
        <el-row style="padding: 10px 16px;">
            <el-row class="search-bar-container">
                <span class="top-bar">日期范围</span>
                <el-date-picker size="small" style="width: 240px;" @change="fetchFreshData" v-model="searchTime"
                    type="daterange" value-format="yyyy-MM-dd" range-separator="-" 
                    start-placeholder="起始日期" end-placeholder="结束日期">
                </el-date-picker>
                <el-input size="small" style="width: 166px;margin-left: 5px; margin-right: 5px;" v-model="userOperationLogQueryDto.userId"
                    placeholder="用户ID" clearable @clear="handleFilterClear">
                </el-input>
                <el-button size="small" @click="handleFilter">
                    <el-icon><Search /></el-icon>
                </el-button>
                <el-button size="small" class="customer"
                    style="margin-left: 5px;background-color: #3a8ee6;color: #ffffff;border: none;font-weight: 500;box-shadow: 0 2px 6px rgba(58, 142, 230, 0.3);" 
                    @click="resetQueryCondition">重置</el-button>
            </el-row>
        </el-row>
        <el-row style="margin: 10px 16px;">
            <el-table :data="tableData" class="custom-table" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="50"></el-table-column>
                <el-table-column prop="content" label="行为" min-width="200"></el-table-column>
                <el-table-column prop="userName" width="130" label="操作者"></el-table-column>
                <el-table-column prop="createTime" width="160" label="发生时间"></el-table-column>
                <el-table-column label="操作" width="80" align="center">
                    <template #default="scope">
                        <el-tag type="danger" size="small" @click="handleDelete(scope.row)" class="delete-tag">
                            删除
                        </el-tag>
                    </template>
                </el-table-column>
            </el-table>
            
            <div class="operation-bar" v-if="selectedRows.length > 0">
                <el-button size="small" type="danger" @click="batchDelete">批量删除 ({{selectedRows.length}})</el-button>
            </div>
            
            <el-pagination style="margin: 20px 0;float: right;" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[20, 50]"
                :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="totalItems"></el-pagination>
        </el-row>
    </el-row>
</template>

<script>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { Search } from '@element-plus/icons-vue'

export default {
    components: {
        Search
    },
    setup() {
        const { proxy } = getCurrentInstance()
        
        // 响应式数据
        const data = ref({})
        const currentPage = ref(1)
        const cover = ref(null)
        const pageSize = ref(20)
        const totalItems = ref(0)
        const tableData = ref([])
        const selectedRows = ref([])
        const userOperationLogQueryDto = reactive({}) // 搜索条件
        const options = ref([])
        const bookshelfOptions = ref([])
        const searchTime = ref([])

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
                title: '删除行为日志数据',
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
                    const response = await proxy.$axios.post(`userOperationLog/batchDelete`, ids)
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
                    console.error(`日志信息删除异常：`, e)
                }
            }
        }

        const resetQueryCondition = () => {
            searchTime.value = []
            Object.keys(userOperationLogQueryDto).forEach(key => delete userOperationLogQueryDto[key])
            fetchFreshData()
        }

        const closeDialog = () => {
            // dialogOperation 在这个组件中没有定义，可能是遗留代码
        }

        const clearFormData = () => {
            data.value = {}
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
                    ...userOperationLogQueryDto
                }
                const response = await proxy.$axios.post('userOperationLog/query', params)
                const { data: responseData } = response
                tableData.value = responseData.data
                totalItems.value = responseData.total
            } catch (error) {
                console.error('查询行为日志信息异常:', error)
            }
        }

        const add = () => {
            // dialogOperation 在这个组件中没有定义，可能是遗留代码
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
            data.value = { ...row }
        }

        const handleEdit = (row) => {
            // dialogOperation 在这个组件中没有定义，可能是遗留代码
            data.value = { ...row }
            cover.value = row.cover
        }

        const handleDelete = (row) => {
            selectedRows.value = [row]
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
            userOperationLogQueryDto,
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

.search-bar-container {
    display: flex;
    align-items: center;
}

.top-bar {
    color: #606266;
    font-size: 14px;
    margin-right: 8px;
    line-height: 1;
    font-weight: 500;
}

.operation-bar {
    margin: 15px 0;
}

.text-button {
    cursor: pointer;
    font-size: 13px;
    
    &:hover {
        text-decoration: underline;
    }
}

.delete-tag {
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover {
        opacity: 0.8;
        transform: scale(1.05);
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

.customer {
    font-size: 12px;
}
</style>

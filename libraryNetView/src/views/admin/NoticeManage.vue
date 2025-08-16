<template>
    <el-row class="common-container">
        <el-row style="padding: 10px 16px;">
            <el-row>
                <span class="top-bar">公告标题</span>
                <el-input size="small" style="width: 188px;" v-model="noticeQueryDto.name" placeholder="输入处" clearable
                    @clear="handleFilterClear">
                </el-input>
                <span class="top-bar">发布时间</span>
                <el-date-picker size="small" style="width: 240px;" v-model="searchTime"
                    type="daterange" value-format="yyyy-MM-dd" range-separator="-" 
                    start-placeholder="起始日期" end-placeholder="结束日期">
                </el-date-picker>
                <el-button size="small" class="customer"
                    style="margin-left: 10px;background-color: #3a8ee6;color: #ffffff;border: none;font-weight: 500;box-shadow: 0 2px 6px rgba(58, 142, 230, 0.3);" type="primary"
                    @click="handleFilter">立即查询</el-button>
                <el-button size="small"
                    style="background-color: #67c23a;color: #ffffff;border: none;font-weight: 500;box-shadow: 0 2px 6px rgba(103, 194, 58, 0.3);" class="customer"
                    type="info" @click="addNotice">新增公告</el-button>
            </el-row>
        </el-row>
        <el-row style="margin: 10px 16px;">
            <el-table :data="tableData" class="custom-table">
                <el-table-column prop="name"  label="公告"></el-table-column>
                <el-table-column prop="createTime" width="188" label="发布时间"></el-table-column>
                <el-table-column label="操作" width="160" align="center">
                    <template #default="scope">
                        <el-tag type="primary" size="small" @click="handleEdit(scope.row)" class="action-tag edit-tag" style="min-width: 42px; margin-right: 8px;">
                            修改
                        </el-tag>
                        <el-tag type="danger" size="small" @click="handleDelete(scope.row)" class="action-tag delete-tag" style="min-width: 42px;">
                            删除
                        </el-tag>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin: 20px 0;float: right;" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[8, 20]"
                :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="totalItems"></el-pagination>
        </el-row>
    </el-row>
</template>

<script>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'

export default {
    setup() {
        const { proxy } = getCurrentInstance()
        const router = useRouter()
        
        // 响应式数据
        const data = ref({})
        const filterText = ref('')
        const currentPage = ref(1)
        const pageSize = ref(8)
        const totalItems = ref(0)
        const tableData = ref([])
        const searchTime = ref([])
        const selectedRows = ref([])
        const noticeQueryDto = reactive({})

        // 公告新增
        const addNotice = () => {
            sessionStorage.setItem('noticeOperation', 'save')
            router.push('/createNotice')
        }

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
                title: '删除公告数据',
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
                    const response = await proxy.$axios.post(`notice/batchDelete`, ids)
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
                    console.error(`公告信息删除异常：`, e)
                }
            }
        }

        const resetQueryCondition = () => {
            Object.keys(noticeQueryDto).forEach(key => delete noticeQueryDto[key])
            searchTime.value = []
            fetchFreshData()
        }

        const clearFormData = () => {
            data.value = {}
        }

        const fetchFreshData = async () => {
            try {
                tableData.value = []
                let startTime = null
                let endTime = null
                if (searchTime.value != null && searchTime.value.length === 2) {
                    // 处理日期，支持字符串和Date对象
                    const [startDate, endDate] = searchTime.value.map(date => {
                        if (typeof date === 'string') {
                            return date
                        }
                        return date instanceof Date ? date.toISOString().split('T')[0] : date
                    })
                    startTime = `${startDate}T00:00:00`
                    endTime = `${endDate}T23:59:59`
                }
                // 请求参数
                const params = {
                    current: currentPage.value,
                    size: pageSize.value,
                    startTime: startTime,
                    endTime: endTime,
                    ...noticeQueryDto
                }
                const response = await proxy.$axios.post('notice/query', params)
                const { data: responseData } = response
                tableData.value = responseData.data
                totalItems.value = responseData.total
            } catch (error) {
                console.error('查询公告信息异常:', error)
            }
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
            sessionStorage.setItem('noticeInfo', JSON.stringify(row))
            sessionStorage.setItem('noticeOperation', 'update')
            router.push('/createNotice')
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
            filterText,
            currentPage,
            pageSize,
            totalItems,
            tableData,
            searchTime,
            selectedRows,
            noticeQueryDto,
            addNotice,
            handleSelectionChange,
            batchDelete,
            resetQueryCondition,
            clearFormData,
            fetchFreshData,
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
            }
        }
    }
}

// 分页样式美化
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

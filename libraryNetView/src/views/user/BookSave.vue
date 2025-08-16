<template>
    <div class="book-save-view-container">
        <div class="word-search" v-if="tableData.length !== 0">
            <div class="item">
                <el-icon><Search /></el-icon>
                <input type="text" placeholder="书籍ID" v-model="bookSaveQueryDto.bookId">
                <span class="search-text" @click="fetchFreshData">搜索</span>
            </div>
        </div>
        <el-row v-if="tableData.length === 0">
            <el-empty description="暂无收藏书籍"></el-empty>
        </el-row>
        <div v-else class="book-save-container">
            <div class="book-save-table">
                <div class="save-book header">
                    <div class="title">书籍名</div>
                    <div class="title">作者</div>
                    <div class="title">馆藏数</div>
                    <div class="title">书籍所在</div>
                    <div class="title">功能操作</div>
                </div>
                <div class="save-book" v-for="(save, index) in tableData" :key="index">
                    <div>
                        {{ save.bookName }}
                    </div>
                    <div>
                        {{ save.author }}
                    </div>
                    <div>
                        {{ save.num }}
                    </div>
                    <div>
                        {{ parseLocation(save) }}
                    </div>
                    <div class="action-buttons">
                        <button class="action-btn borrow" @click="handleEdit(save)">
                            <el-icon><Reading /></el-icon> 借书
                        </button>
                        <button class="action-btn cancel" @click="handleDelete(save)">
                            <el-icon><StarFilled /></el-icon> 取消收藏
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <div class="pager" v-if="tableData.length !== 0">
            <div>
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    v-model:current-page="current" :page-size="size" layout="total, prev, pager, next"
                    :total="totalCount">
                </el-pagination>
            </div>
        </div>
        <el-dialog :show-close="false" v-model="dialogOperation" width="400px" custom-class="borrow-dialog">
            <div class="dialog-header">
                <h3 class="dialog-title">借阅信息</h3>
            </div>
            <div class="dialog-content">
                <div class="form-group">
                    <label class="form-label"><el-icon><Goods /></el-icon> 借书数量</label>
                    <el-input-number style="width: 100%;" size="small" v-model="data.deadlineNum" :min="1" :max="10"
                        label="数量" controls-position="right"></el-input-number>
                </div>
                <div class="form-group">
                    <label class="form-label"><el-icon><Calendar /></el-icon> 归还日期</label>
                    <el-date-picker style="width: 100%;" size="small" v-model="data.returnTime" type="date"
                        placeholder="选择归还日期" value-format="yyyy-MM-dd">
                    </el-date-picker>
                </div>
                <div class="borrow-notice">
                    <el-icon><InfoFilled /></el-icon>
                    <span>请在选定日期前归还图书</span>
                </div>
            </div>
            <div class="dialog-footer">
                <el-button class="cancel-btn" size="small" @click="cannel()">取消操作</el-button>
                <el-button class="confirm-btn" type="primary" size="small" @click="addOperation">确定借书</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { Search, Reading, StarFilled, Goods, Calendar, InfoFilled } from '@element-plus/icons-vue'

export default {
    components: {
        Search,
        Reading, 
        StarFilled,
        Goods,
        Calendar,
        InfoFilled
    },
    setup() {
        const { proxy } = getCurrentInstance()
        
        // 响应式数据
        const data = reactive({})
        const current = ref(1)
        const cover = ref(null)
        const size = ref(10)
        const totalCount = ref(0)
        const dialogOperation = ref(false) // 开关
        const isOperation = ref(false) // 开关-标识新增或修改
        const tableData = ref([])
        const selectedRows = ref([])
        const bookSaveQueryDto = reactive({}) // 搜索条件
        const options = ref([])
        const bookshelfOptions = ref([])
        const bookOrderHistorys = ref([])
        const searchTime = ref([])

        // 方法
        const cannel = () => {
            Object.assign(data, {});
            dialogOperation.value = false;
            isOperation.value = false;
        }

        // 借书操作
        const addOperation = async () => {
            try {
                if (data.deadlineNum === undefined || data.returnTime === undefined) {
                    proxy.$notify({
                        duration: 1000,
                        title: '操作反馈',
                        message: '请填入信息',
                        type: 'info'
                    });
                    return;
                }
                const response = await proxy.$axios.post('/bookOrderHistory/save', data);
                if (response.data.code === 200) {
                    fetchFreshData();
                    Object.assign(data, {});
                    dialogOperation.value = false;
                    proxy.$notify({
                        title: '借书结果',
                        message: '借书成功，请及时归还',
                        type: 'success'
                    });
                } else {
                    proxy.$notify({
                        title: '借书结果',
                        message: response.data.msg,
                        type: 'error'
                    });
                }
            } catch (error) {
                proxy.$notify({
                    title: '借书结果',
                    message: error,
                    type: 'error'
                });
            }
        }

        const parseLocation = (data) => {
            return data.floor + '-' + data.area + '-' + data.frame;
        }

        const bookshelfConfig = (item) => {
            return item.floor + "-" + item.area + "-" + item.frame;
        }

        const fetchBookshelf = () => {
            proxy.$axios.post('bookshelf/query', {}).then(res => {
                if (res.data.code === 200) {
                    bookshelfOptions.value = res.data.data;
                }
            })
        }

        const fetchCategory = () => {
            proxy.$axios.post('category/query', {}).then(res => {
                if (res.data.code === 200) {
                    options.value = res.data.data;
                }
            })
        }

        const resetQueryCondition = () => {
            Object.assign(bookSaveQueryDto, {});
            fetchFreshData();
        }

        // 查询用户的预约记录
        const getBookOrderHistory = () => {
            proxy.$axios.post('bookOrderHistory/queryUser', {}).then(res => {
                if (res.data.code === 200) {
                    bookOrderHistorys.value = res.data.data;
                }
            })
        }

        // 信息新增
        const returnOperation = async () => {
            try {
                // 发之前，组装数据
                const bookOrderHsitory = {
                    id: data.id,
                    isReturn: true
                }
                const response = await proxy.$axios.put('/bookSave/update', bookOrderHsitory);
                if (response.data.code === 200) {
                    fetchFreshData();
                    Object.assign(data, {});
                    dialogOperation.value = false;
                    proxy.$notify({
                        title: '还书结果',
                        message: '还书成功',
                        type: 'success'
                    });
                } else {
                    proxy.$notify({
                        title: '还书结果',
                        message: response.data.msg,
                        type: 'error'
                    });
                }
            } catch (error) {
                proxy.$notify({
                    title: '还书结果',
                    message: error,
                    type: 'error'
                });
            }
        }

        const closeDialog = () => {
            dialogOperation.value = false;
        }

        const clearFormData = () => {
            Object.assign(data, {});
            cover.value = null;
        }

        const fetchFreshData = async () => {
            try {
                tableData.value = [];
                // 请求参数
                const params = {
                    current: current.value,
                    size: size.value,
                    ...bookSaveQueryDto
                };
                const response = await proxy.$axios.post('/bookSave/queryUser', params);
                const { data: responseData } = response;
                tableData.value = responseData.data;
                totalCount.value = responseData.total;
            } catch (error) {
                console.error('借阅历史信息异常:', error);
            }
        }

        const add = () => {
            dialogOperation.value = true;
        }

        const handleFilter = () => {
            current.value = 1;
            fetchFreshData();
        }

        const handleFilterClear = () => {
            handleFilter();
        }

        const handleSizeChange = (val) => {
            size.value = val;
            current.value = 1;
            fetchFreshData();
        }

        const handleCurrentChange = (val) => {
            current.value = val;
            fetchFreshData();
        }

        const messagePush = (row) => {
            Object.assign(data, { ...row });
        }

        const handleEdit = (row) => {
            dialogOperation.value = true;
            Object.assign(data, { ...row });
        }

        const handleDelete = (row) => {
            batchDelete(row);
        }

        // 批量删除数据
        const batchDelete = async (save) => {
            const confirmed = await proxy.$swalConfirm({
                title: '取消收藏？',
                text: `操作不可回复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = [save.id];
                    const response = await proxy.$axios.post(`/bookSave/batchDelete`, ids);
                    if (response.data.code === 200) {
                        proxy.$notify({
                            duration: 1000,
                            title: '取消收藏操作',
                            message: '取消收藏成功',
                            type: 'success'
                        });
                        fetchFreshData();
                    }
                } catch (e) {
                    proxy.$notify({
                        duration: 3000,
                        title: '取消收藏操作',
                        message: e,
                        type: 'error'
                    });
                    console.error(`取消收藏异常：`, e);
                }
            }
        }

        // 生命周期
        onMounted(() => {
            fetchFreshData();
            fetchCategory();
            fetchBookshelf();
        })

        return {
            data,
            current,
            cover,
            size,
            totalCount,
            dialogOperation,
            isOperation,
            tableData,
            selectedRows,
            bookSaveQueryDto,
            options,
            bookshelfOptions,
            bookOrderHistorys,
            searchTime,
            cannel,
            addOperation,
            parseLocation,
            bookshelfConfig,
            fetchBookshelf,
            fetchCategory,
            resetQueryCondition,
            getBookOrderHistory,
            returnOperation,
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
            handleDelete,
            batchDelete
        }
    }
};
</script>
<style scoped lang="scss">
.book-save-view-container {
    padding: 10px;
    border-radius: 16px;
    background-color: transparent;
    backdrop-filter: none;
    box-shadow: none;
}

.word-search {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;

    .item {
        padding: 10px 20px;
        width: 500px;
        background-color: rgba(255, 255, 255, 0.8);
        backdrop-filter: blur(10px);
        border-radius: 40px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        box-sizing: border-box;
        box-shadow: 0 4px 12px rgba(0,0,0,0.06);
        border: 1px solid rgba(255, 255, 255, 0.3);
        transition: all 0.3s ease;

        &:hover, &:focus-within {
            box-shadow: 0 6px 16px rgba(255, 87, 34, 0.12);
            border-color: rgba(255, 87, 34, 0.2);
        }

        input {
            flex: 1;
            border: none;
            background-color: transparent;
            outline: none;
            font-size: 16px;
            color: #333;
            margin: 0 15px;
            height: 24px;
        }

        i {
            font-size: 18px;
            color: #ff5722;
        }

        .search-text {
            display: inline-block;
            padding: 6px 16px;
            border-radius: 20px;
            background-color: #ff5722;
            color: white;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s ease;
            text-align: center;
            
            &:hover {
                background-color: #f4511e;
                transform: translateY(-1px);
            }
        }
    }
}

.book-save-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 20px 0;
}

.book-save-table {
    width: 90%;
    max-width: 1200px;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    background-color: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(8px);
}

.save-book {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid rgba(240, 240, 240, 0.5);
    transition: all 0.3s ease;
    
    &:last-child {
        border-bottom: none;
    }

    div {
        padding: 16px 20px;
        flex: 1;
        min-width: 120px;
        color: #333;
        font-size: 14px;
        
        &:last-child {
            flex: 1;
            min-width: 200px;
            display: flex;
            justify-content: flex-start;
        }
    }

    .title {
        font-weight: 600;
        color: #555;
    }

    .action-buttons {
        display: flex;
        gap: 10px;
        justify-content: flex-start;
    }

    .action-btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        padding: 3px 8px !important;
        border-radius: 12px !important;
        font-size: 11px !important;
        cursor: pointer;
        transition: all 0.3s ease;
        min-width: 60px !important;
        height: auto !important;
        border: 1px solid transparent;
        width: auto !important;
        
        i {
            margin-right: 3px;
            font-size: 11px !important;
        }
        
        &.borrow {
            background-color: rgba(255, 87, 34, 0.08);
            color: #ff5722;
            border-color: rgba(255, 87, 34, 0.2);
            
            &:hover {
                background-color: rgba(255, 87, 34, 0.12);
                transform: translateY(-1px);
                box-shadow: 0 2px 5px rgba(255, 87, 34, 0.15);
            }
        }
        
        &.cancel {
            background-color: rgba(64, 158, 255, 0.08);
            color: #409eff;
            border-color: rgba(64, 158, 255, 0.2);
            
            &:hover {
                background-color: rgba(64, 158, 255, 0.12);
                transform: translateY(-1px);
                box-shadow: 0 2px 5px rgba(64, 158, 255, 0.15);
            }
        }
    }
}

.save-book:hover {
    background-color: rgba(249, 249, 249, 0.5);
}

.save-book.header {
    background-color: rgba(245, 247, 250, 0.6);
    border-bottom: 1px solid rgba(235, 238, 245, 0.5);
    
    &:hover {
        background-color: rgba(245, 247, 250, 0.6);
    }
}

.pager {
    display: flex;
    margin: 30px 0;
    justify-content: center;
    align-items: center;
}

.borrow-dialog {
    :deep(.el-dialog) {
        border-radius: 16px;
        overflow: hidden;
        background-color: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(10px);
        box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
    }

    :deep(.el-dialog__header) {
        display: none;
    }

    :deep(.el-dialog__body) {
        padding: 0;
    }
}

.dialog-header {
    background-color: rgba(249, 249, 249, 0.7);
    padding: 14px 20px;
    border-bottom: 1px solid rgba(235, 238, 245, 0.5);
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
        background-color: #ff5722;
    }
}

.dialog-content {
    padding: 16px 20px;
}

.dialog-footer {
    padding: 10px 20px 16px;
    text-align: right;
    border-top: 1px solid rgba(235, 238, 245, 0.5);
    background-color: rgba(249, 249, 249, 0.3);
}

.form-group {
    margin-bottom: 16px;
}

.form-label {
    font-size: 14px;
    color: #606266;
    margin-bottom: 8px;
    font-weight: 500;
    display: block;
    
    i {
        margin-right: 5px;
        color: #ff5722;
    }
}

.borrow-notice {
    background-color: rgba(255, 248, 230, 0.8);
    border-radius: 4px;
    padding: 10px 12px;
    font-size: 12px;
    color: #e6a23c;
    display: flex;
    align-items: flex-start;
    margin-top: 16px;
    
    i {
        margin-right: 6px;
        margin-top: 2px;
    }
    
    span {
        line-height: 1.4;
    }
}

.cancel-btn, .confirm-btn {
    padding: 7px 16px;
    font-size: 13px;
    border-radius: 20px;
    
    &:focus {
        outline: none;
    }
}

.cancel-btn {
    background-color: rgba(245, 247, 250, 0.8);
    color: #606266;
    border: 1px solid rgba(220, 223, 230, 0.7);
    
    &:hover {
        color: #409EFF;
        border-color: rgba(198, 226, 255, 0.7);
        background-color: rgba(236, 245, 255, 0.8);
    }
}

.confirm-btn {
    background-color: #ff5722;
    color: white;
    border: none;
    box-shadow: 0 2px 6px rgba(255, 87, 34, 0.2);
    
    &:hover {
        background-color: #f4511e;
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(255, 87, 34, 0.3);
    }
}

/* 分页组件样式 */
:deep(.el-pagination) {
    padding: 15px 0;
    text-align: center;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pager li) {
    border-radius: 8px;
    transition: all 0.3s ease;
    background-color: rgba(255, 255, 255, 0.7);
}

:deep(.el-pager li.active) {
    background-color: #ff5722;
    color: white;
}
</style>

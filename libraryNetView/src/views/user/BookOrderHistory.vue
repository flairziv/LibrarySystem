<template>
    <div class="history-container">
        <!-- 条件搜索 -->
        <div class="word-search">
            <div class="item">
                <el-icon><Search /></el-icon>
                <input type="text" placeholder="搜索书籍ID" v-model="bookQueryDto.bookId">
                <span class="search-text" @click="fetchFreshData">搜索</span>
            </div>
        </div>
        <div class="books-grid">
            <el-row v-if="tableData.length === 0">
                <el-empty description="暂无书籍借阅历史" class="empty-data"></el-empty>
            </el-row>
            <el-row v-else>
                <el-col v-for="(book, index) in tableData" :key="index" :sm="12" :md="8" :lg="6" :xl="6">
                    <div class="book-card" :class="{'returned': book.isReturn}">
                        <div class="book-cover">
                            <img :src="book.bookCover" alt="">
                            <div class="book-status" :class="{'return-pending': !book.isReturn}">
                                {{ book.isReturn ? '已归还' : '待归还' }}
                            </div>
                        </div>
                        <div class="book-info">
                            <h3 class="book-title" :title="book.bookName">{{ book.bookName }}</h3>
                            <div class="book-quantity">
                                借阅数量: <span>{{ book.deadlineNum }}</span> 本
                            </div>
                            <div class="book-dates">
                                <div class="date-item">
                                    <el-icon><Clock /></el-icon>
                                    <span>借阅: {{ book.createTime }}</span>
                                </div>
                                <div class="date-item">
                                    <el-icon><Calendar /></el-icon>
                                    <span>归还: {{ book.returnTime }}</span>
                                </div>
                            </div>
                            <div class="book-actions">
                                <button v-if="!book.isReturn" class="action-btn return-btn" @click="handleReturn(book)">
                                    <el-icon><RefreshLeft /></el-icon><span>归还</span>
                                </button>
                                <button v-else class="action-btn delete-btn" @click="handleDelete(book)">
                                    <el-icon><Delete /></el-icon><span>删除</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
        <div class="pager" v-if="tableData.length !== 0">
            <el-pagination 
                @size-change="handleSizeChange" 
                @current-change="handleCurrentChange"
                v-model:current-page="current" 
                :page-size="size" 
                layout="total, prev, pager, next"
                :total="totalCount">
            </el-pagination>
        </div>
    </div>
</template>

<script>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { Search, Clock, Calendar, RefreshLeft, Delete } from '@element-plus/icons-vue'

export default {
    components: {
        Search,
        Clock,
        Calendar,
        RefreshLeft,
        Delete
    },
    setup() {
        const { proxy } = getCurrentInstance()
        
        // 响应式数据
        const deadlineNum = ref(1)
        const data = reactive({})
        const current = ref(1)
        const cover = ref(null)
        const size = ref(8)
        const totalCount = ref(0)
        const dialogOperation = ref(false) // 开关
        const isOperation = ref(false) // 开关-标识新增或修改
        const tableData = ref([])
        const selectedRows = ref([])
        const bookQueryDto = reactive({}) // 搜索条件
        const options = ref([])
        const bookshelfOptions = ref([])
        const categoryItemSelected = ref(null)
        const categoryList = ref([])
        const bookOrderHistorys = ref([])

        // 方法
        const handleReturn = (book) => {
            const bookRssHistory = {
                id: book.id
            }
            proxy.$axios.put('/bookOrderHistory/update', bookRssHistory).then(res => {
                if (res.data.code === 200) {
                    proxy.$notify({
                        duration: 1000,
                        title: '还书操作',
                        message: '还书成功',
                        type: 'success'
                    });
                    fetchFreshData();
                }
            }).catch(error => {
                console.log("还书异常：", error);
            })
        }

        // 批量删除数据
        const batchDelete = async (orderHistory) => {
            const confirmed = await proxy.$swalConfirm({
                title: '删除书籍预约历史数据',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = [orderHistory.id];
                    const response = await proxy.$axios.post(`/bookOrderHistory/batchDelete`, ids);
                    const { data } = response;
                    if (data.code === 200) {
                        proxy.$notify({
                            duration: 1000,
                            title: '删除操作',
                            message: '删除成功',
                            type: 'success'
                        });
                        fetchFreshData();
                    }
                } catch (error) {
                    proxy.$notify({
                        duration: 2000,
                        title: '删除失败',
                        message: error,
                        type: 'error'
                    });
                    console.error(`书籍预约信息删除异常：`, error);
                }
            }
        }

        const fetchFreshData = async () => {
            try {
                const params = {
                    current: current.value,
                    size: size.value,
                    ...bookQueryDto
                };
                const response = await proxy.$axios.post('/bookOrderHistory/queryUser', params);
                const { data } = response;
                tableData.value = data.data;
                totalCount.value = data.total;
            } catch (error) {
                console.error('查询书籍借阅历史信息异常:', error);
            }
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

        const handleDelete = (row) => {
            batchDelete(row);
        }

        // 生命周期
        onMounted(() => {
            fetchFreshData();
        })

        return {
            deadlineNum,
            data,
            current,
            cover,
            size,
            totalCount,
            dialogOperation,
            isOperation,
            tableData,
            selectedRows,
            bookQueryDto,
            options,
            bookshelfOptions,
            categoryItemSelected,
            categoryList,
            bookOrderHistorys,
            handleReturn,
            batchDelete,
            fetchFreshData,
            handleSizeChange,
            handleCurrentChange,
            handleDelete
        }
    }
};
</script>
<style scoped lang="scss">
.history-container {
    padding: 10px;
    border-radius: 16px;
    background-color: transparent;
    box-shadow: none;
}

.books-grid {
    max-width: 1200px;
    margin: 0 auto;
}

.book-card {
    margin: 12px;
    background-color: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(8px);
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
    height: 100%;
    min-height: 380px;
    display: flex;
    flex-direction: column;
    
    &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
        background-color: rgba(255, 255, 255, 0.9);
    }
    
    &.returned {
        border-left: 4px solid rgba(103, 194, 58, 0.8);
    }
    
    &:not(.returned) {
        border-left: 4px solid rgba(230, 162, 60, 0.8);
    }
}

.book-cover {
    position: relative;
    overflow: hidden;
    background-color: rgba(255, 255, 255, 0.5);
    aspect-ratio: 1/1;
    display: flex;
    align-items: center;
    justify-content: center;
    
    img {
        width: auto;
        height: auto;
        max-width: 85%;
        max-height: 85%;
        object-fit: contain;
        transition: transform 0.5s ease;
    }
    
    &:hover img {
        transform: scale(1.1);
    }
    
    .book-status {
        position: absolute;
        top: 12px;
        right: 0;
        background-color: rgba(103, 194, 58, 0.9);
        color: white;
        padding: 4px 12px;
        font-size: 12px;
        border-radius: 4px 0 0 4px;
        font-weight: 500;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
        
        &.return-pending {
            background-color: rgba(230, 162, 60, 0.9);
        }
    }
}

.book-info {
    padding: 16px;
    flex: 1;
    display: flex;
    flex-direction: column;
}

.book-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 0 0 12px 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.book-quantity {
    font-size: 14px;
    color: #606266;
    margin-bottom: 12px;
    
    span {
        font-weight: 600;
        color: #ff5722;
    }
}

.book-dates {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 16px;
}

.date-item {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #909399;
    
    i {
        margin-right: 6px;
        color: #909399;
    }
    
    span {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
}

.book-actions {
    margin-top: auto;
}

.action-btn {
    width: 100%;
    padding: 8px 0;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
    height: 36px;
    
    i {
        margin-right: 6px;
        font-size: 14px;
    }
    
    span {
        font-size: 14px;
    }
    
    &.return-btn {
        background-color: rgba(230, 162, 60, 0.9);
        color: white;
        
        &:hover {
            background-color: rgba(230, 162, 60, 1);
            transform: translateY(-1px);
        }
    }
    
    &.delete-btn {
        background-color: rgba(245, 108, 108, 0.9);
        color: white;
        
        &:hover {
            background-color: rgba(245, 108, 108, 1);
            transform: translateY(-1px);
        }
    }
}

.empty-data {
    margin: 40px 0;
}

.pager {
    display: flex;
    justify-content: center;
    margin: 30px 0 10px;
}

// 搜索框样式
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

// 分页样式
:deep(.el-pagination) {
    padding: 15px 0;
    text-align: center;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pager li) {
    border-radius: 8px;
    transition: all 0.3s ease;
    background-color: rgba(255, 255, 255, 0.2);
}

:deep(.el-pager li.active) {
    background-color: #ff5722;
    color: white;
}

// 响应式调整
@media (max-width: 768px) {
    .word-search .item {
        width: 90%;
    }
}
</style>

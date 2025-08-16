<template>
    <div class="book-operation-container">
        <!-- 条件搜索 -->
        <div class="word-search">
            <div class="item">
                <el-icon><Search /></el-icon>
                <input type="text" placeholder="搜索书籍" v-model="bookQueryDto.name">
                <span class="search-text" @click="fetchFreshData">搜索</span>
            </div>
        </div>
        <div class="category-container">
            <span class="category" 
                  :class="{'active-category': categoryItemSelected === categoryItem}"
                  @click="categoryChoose(categoryItem)" 
                  v-for="(categoryItem, index) in options" 
                  :key="index">
                {{ categoryItem.name }}
            </span>
        </div>
        <div class="books-grid">
            <el-row v-if="tableData.length === 0">
                <el-empty description="暂无书籍"></el-empty>
            </el-row>
            <el-row v-else>
                <el-col v-for="(book, index) in tableData" :key="index" :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                    <div class="item-book">
                        <div class="book-cover">
                            <img :src="book.cover" alt="">
                        </div>
                        <div class="book-info">
                            <div class="book-title">
                                <el-tooltip class="item" effect="dark" :content="book.name" placement="bottom-end">
                                    <div class="title-text">{{ book.name }}</div>
                                </el-tooltip>
                            </div>
                            <div class="book-status">
                                <el-icon v-if="book.isPlanBuy"><Warning /></el-icon>
                                <el-icon v-else><SuccessFilled /></el-icon>
                                    <el-tooltip v-if="book.isPlanBuy" class="item" effect="dark"
                                        content="计划上架的书籍，为预售书籍。用户可以订阅，书籍上架之后将做通知" placement="bottom-end">
                                    <span class="status-text">预售书籍</span>
                                    </el-tooltip>
                                    <span v-else>书籍已上架</span>
                                    <span> - {{ book.categoryName }}</span>
                                </div>
                            <div class="book-publisher">
                                <el-tooltip class="item" effect="dark" :content="book.publisher" placement="bottom-end">
                                    <div class="info-text">由【{{ book.publisher }}】出版</div>
                                    </el-tooltip>
                                </div>
                            <div class="book-author">
                                <el-tooltip class="item" effect="dark" :content="book.author" placement="bottom-end">
                                    <div class="info-text">
                                        <span>作者：{{ book.author }}</span>
                                            <span>库存{{ book.num }}本</span>
                                        </div>
                                    </el-tooltip>
                                </div>
                            <div class="book-actions">
                                    <span v-if="!book.isPlanBuy">
                                    <span class="edit-round status" v-if="book.isSave">已收藏</span>
                                    <span v-else class="edit-round" @click="saveBook(book)">
                                            <el-icon><StarFilled /></el-icon>
                                            收藏
                                        </span>
                                    <span class="action-divider"></span>
                                    <span v-if="book.num === 0" class="stock-out">库存不足</span>
                                            <span v-else>
                                                <span v-if="book.isReturn === null">
                                            <span class="edit-round" @click="handleEdit(book)">借书</span>
                                                </span>
                                                <span v-else>
                                                    <span v-if="!book.isReturn" class="edit-round status">待归还</span>
                                            <span v-else class="edit-round" @click="handleEdit(book)">借书</span>
                                            </span>
                                        </span>
                                    </span>
                                    <span v-else>
                                    <span class="edit-round status" v-if="book.isRss">已订阅</span>
                                        <span v-else class="edit-round" @click="rssBook(book)">订阅</span>
                                    </span>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
        <div class="pager">
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    v-model:current-page="current" :page-size="size" layout="total, prev, pager, next"
                    :total="totalCount">
                </el-pagination>
        </div>
        <el-dialog v-model="dialogOperation" :show-close="false" width="280px" custom-class="borrow-dialog">
            <div class="dialog-content">
                <h3 class="dialog-title">借阅书籍</h3>
                <div class="form-container">
                    <div class="form-item">
                        <div class="point">借书数量</div>
                        <el-input-number class="custom-input" size="small" v-model="deadlineNum" :min="1" :max="10"
                            label="数量" controls-position="right"></el-input-number>
                    </div>
                    <div class="form-item">
                        <div class="point">归还日期</div>
                        <el-date-picker class="custom-input custom-date-picker" size="small" v-model="data.returnTime" type="date"
                            placeholder="选择日期">
                        </el-date-picker>
                    </div>
                </div>
            </div>
            <div class="dialog-footer">
                <span style="display: inline-block; padding: 7px 18px; border-radius: 20px; cursor: pointer; font-size: 14px; margin: 0 8px; background-color: #f5f5f5; color: #606266; font-weight: 500; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border: none; transition: all 0.3s;" 
                      onmouseover="this.style.backgroundColor='#e8e8e8'; this.style.transform='translateY(-2px)'; this.style.boxShadow='0 4px 8px rgba(0,0,0,0.15)'"
                      onmouseout="this.style.backgroundColor='#f5f5f5'; this.style.transform=''; this.style.boxShadow='0 2px 4px rgba(0,0,0,0.1)'"
                      @click="dialogOperation = false">取消</span>
                <span style="display: inline-block; padding: 7px 18px; border-radius: 20px; cursor: pointer; font-size: 14px; margin: 0 8px; background-color: #ff5722; color: white; font-weight: 500; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border: none; transition: all 0.3s;"
                      onmouseover="this.style.backgroundColor='#ff6e40'; this.style.transform='translateY(-2px)'; this.style.boxShadow='0 4px 8px rgba(0,0,0,0.15)'"
                      onmouseout="this.style.backgroundColor='#ff5722'; this.style.transform=''; this.style.boxShadow='0 2px 4px rgba(0,0,0,0.1)'"
                      @click="addOperation()">确定</span>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { Search, Warning, SuccessFilled, StarFilled } from '@element-plus/icons-vue'

export default {
    components: {
        Search,
        Warning,
        SuccessFilled,
        StarFilled
    },
    setup() {
        const { proxy } = getCurrentInstance()
        
        // 响应式数据
        const deadlineNum = ref(1)
        const data = ref({})
        const current = ref(1)
        const cover = ref(null)
        const size = ref(12)
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

        const categoryChoose = (category) => {
            categoryItemSelected.value = category
            bookQueryDto.categoryId = category.id
            fetchFreshData()
        }

        // 订阅书籍
        const rssBook = async (book) => {
            const confirmed = await proxy.$swalConfirm({
                title: '订阅这本书？',
                text: `订阅后，上架时会接收通知`,
                icon: 'info',
            })
            if (confirmed) {
                try {
                    const bookSave = { bookId: book.id }
                    const response = await proxy.$axios.post(`/bookRssHistory/save`, bookSave)
                    proxy.$notify({
                        title: '订阅操作',
                        message: response.data.code === 200 ? '订阅成功' : response.data.msg,
                        type: response.data.code === 200 ? 'success' : 'warning'
                    })
                    fetchFreshData()
                } catch (e) {
                    console.error(`订阅错误：`, e)
                }
            }
        }

        // 收藏书籍
        const saveBook = async (book) => {
            const confirmed = await proxy.$swalConfirm({
                title: '收藏这本书？',
                text: `收藏后方便后续操作`,
                icon: 'info',
            })
            if (confirmed) {
                try {
                    const bookSave = { bookId: book.id }
                    const response = await proxy.$axios.post(`/bookSave/save`, bookSave)
                    proxy.$notify({
                        title: '收藏操作',
                        message: response.data.code === 200 ? '收藏成功' : response.data.msg,
                        type: response.data.code === 200 ? 'success' : 'warning'
                    })
                    fetchFreshData()
                } catch (e) {
                    console.error(`收藏错误：`, e)
                }
            }
        }

        const bookshelfConfig = (item) => {
            return item.floor + "-" + item.area + "-" + item.frame
        }

        const fetchBookshelf = () => {
            proxy.$axios.post('bookshelf/query', {}).then(res => {
                if (res.data.code === 200) {
                    bookshelfOptions.value = res.data.data
                }
            })
        }

        const fetchCategory = () => {
            proxy.$axios.post('category/query', {}).then(res => {
                if (res.data.code === 200) {
                    options.value = res.data.data
                    // 添加全部
                    options.value.unshift({ id: null, name: '全部' })
                    // 默认加载全部
                    categoryChoose(options.value[0])
                }
            })
        }

        const resetQueryCondition = () => {
            Object.keys(bookQueryDto).forEach(key => delete bookQueryDto[key])
            fetchFreshData()
        }

        // 查询用户的预约记录
        const getBookOrderHistory = () => {
            proxy.$axios.post('bookOrderHistory/queryUser', {}).then(res => {
                if (res.data.code === 200) {
                    bookOrderHistorys.value = res.data.data
                }
            })
        }

        // 信息新增
        const addOperation = async () => {
            try {
                data.value.bookId = data.value.id
                data.value.deadlineNum = deadlineNum.value
                const response = await proxy.$axios.post('/bookOrderHistory/save', data.value)
                if (response.data.code === 200) {
                    fetchFreshData()
                    data.value = {}
                    dialogOperation.value = false
                    proxy.$notify({
                        title: '借书结果',
                        message: '借书成功，请及时归还',
                        type: 'success'
                    })
                    fetchFreshData()
                } else {
                    proxy.$notify({
                        title: '借书结果',
                        message: response.data.msg,
                        type: 'error'
                    })
                }
            } catch (error) {
                proxy.$notify({
                    title: '借书结果',
                    message: error,
                    type: 'error'
                })
            }
        }

        const closeDialog = () => {
            dialogOperation.value = false
        }

        const clearFormData = () => {
            data.value = {}
            cover.value = null
        }

        const fetchFreshData = async () => {
            try {
                // 请求参数
                const params = {
                    current: current.value,
                    size: size.value,
                    ...bookQueryDto
                }
                const response = await proxy.$axios.post('/book/query', params)
                const { data: responseData } = response
                tableData.value = responseData.data
                totalCount.value = responseData.total
            } catch (error) {
                console.error('查询书籍书籍信息异常:', error)
            }
        }

        const add = () => {
            dialogOperation.value = true
        }

        const handleFilter = () => {
            current.value = 1
            fetchFreshData()
        }

        const handleFilterClear = () => {
            handleFilter()
        }

        const handleSizeChange = (val) => {
            size.value = val
            current.value = 1
            fetchFreshData()
        }

        const handleCurrentChange = (val) => {
            current.value = val
            fetchFreshData()
        }

        const messagePush = (row) => {
            data.value = { ...row }
        }

        const handleEdit = (row) => {
            dialogOperation.value = true
            data.value = { ...row }
        }

        const handleDelete = (row) => {
            selectedRows.value.push(row)
            // batchDelete() // 这个方法在原代码中没有定义
        }

        // 生命周期
        onMounted(() => {
            fetchFreshData()
            fetchCategory()
            fetchBookshelf()
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
            categoryChoose,
            rssBook,
            saveBook,
            bookshelfConfig,
            fetchBookshelf,
            fetchCategory,
            resetQueryCondition,
            getBookOrderHistory,
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
.book-operation-container {
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

.category-container {
    max-width: 1200px;
    margin: 0 auto 20px;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 8px;
}

.category {
    font-size: 15px;
    color: #666;
    cursor: pointer;
    display: inline-block;
    transition: all 0.3s ease;
    padding: 6px 14px;
    border-radius: 20px;
    background-color: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(5px);
    box-shadow: 0 2px 6px rgba(0,0,0,0.04);
}

.category:hover {
    color: #ff5722;
    background-color: rgba(255, 255, 255, 0.9);
    transform: translateY(-2px);
}

.active-category {
    color: white;
    background-color: #ff5722;
    font-weight: 600;
}

.active-category:hover {
    color: white;
    background-color: #ff5722;
}

.books-grid {
    max-width: 1200px;
    margin: 0 auto;
}

.item-book {
    border-radius: 16px;
    margin: 10px;
    background-color: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(8px);
    box-shadow: 0 2px 10px rgba(0,0,0,0.05);
    overflow: hidden;
    transition: all 0.3s ease;
    height: 100%;
    display: flex;
    flex-direction: column;
}

.item-book:hover {
    transform: translateY(-3px);
    box-shadow: 0 5px 15px rgba(0,0,0,0.08);
    background-color: rgba(255, 255, 255, 0.95);
}

.book-cover {
    display: flex;
    justify-content: center;
    padding: 15px;
    
    img {
        width: 140px;
        height: 140px;
        object-fit: cover;
        border-radius: 16px;
        box-shadow: 0 3px 10px rgba(0,0,0,0.1);
    }
}

.book-info {
    padding: 15px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    flex: 1;
}

.book-title {
    .title-text {
        font-size: 16px;
        font-weight: 700;
        color: #333;
        margin-bottom: 5px;
        width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
}

.book-status {
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 5px;
    
    i.el-icon-warning {
        color: #E6A23C;
    }
    
    i.el-icon-success {
        color: #67C23A;
    }
    
    .status-text {
        text-decoration: underline;
        text-decoration-style: dashed;
    }
}

.book-publisher, .book-author {
    font-size: 13px;
    color: #666;
    
    .info-text {
        width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        display: flex;
        justify-content: space-between;
    }
}

.book-actions {
    margin-top: 10px;
    display: flex;
    flex-wrap: wrap;
    gap: 5px;
}

.edit-round {
    background-color: rgba(255, 255, 255, 0.7);
    border: 1px solid rgba(220, 223, 230, 0.5);
    padding: 6px 12px;
    border-radius: 16px;
    font-size: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    color: #333;
    display: inline-flex;
    align-items: center;
    gap: 4px;
}

.edit-round.status {
    color: #ff5722;
    border-color: rgba(255, 87, 34, 0.2);
    background-color: rgba(255, 87, 34, 0.05);
    cursor: default;
}

.edit-round:hover {
    color: #ff5722;
    border-color: rgba(255, 87, 34, 0.3);
    background-color: rgba(255, 87, 34, 0.08);
}

.action-divider {
    display: inline-block;
    width: 5px;
}

.stock-out {
    color: #F56C6C;
    font-size: 12px;
}

.pager {
    display: flex;
    margin: 30px 0;
    justify-content: center;
}

.point {
    font-size: 14px;
    color: #333;
    margin-bottom: 8px;
    font-weight: 500;
}

/* 对话框样式 */
.borrow-dialog {
:deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
        background-color: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(10px);
        box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
    }
}

.dialog-content {
    padding: 20px 15px 10px;
}

.dialog-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    text-align: center;
    margin: 0 0 15px;
    padding-bottom: 8px;
    border-bottom: 1px dashed rgba(238, 238, 238, 0.5);
}

.form-container {
    margin-bottom: 10px;
}

.form-item {
    margin-bottom: 15px;
}

.custom-input {
    width: 100%;
}

/* 修改分页组件样式 */
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

/* 日期选择器样式 */
:deep(.custom-date-picker .el-input__inner) {
    border-radius: 8px;
    border: 1px solid rgba(224, 224, 224, 0.7);
    padding-left: 12px;
    background-color: rgba(255, 255, 255, 0.7);
}

:deep(.el-input-number) {
    width: 100%;
}

.dialog-footer {
    display: flex;
    justify-content: center;
    padding: 12px 0;
    border-top: 1px solid rgba(240, 240, 240, 0.5);
    background-color: rgba(250, 250, 250, 0.5);
}
</style>

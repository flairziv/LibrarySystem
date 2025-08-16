<template>
    <el-row style="margin-top: 10px;">
        <!-- 条件搜索 -->
        <div class="word-search" v-if="tableData.length !== 0">
            <div class="item">
                <el-icon><Search /></el-icon>
                <input type="text" placeholder="消息体" v-model="rssNotificationQueryDto.content">
                <span class="search-text" @click="fetchFreshData">搜索</span>
            </div>
        </div>
        <div class="read-bar" v-if="tableData.length !== 0">
            <div class="read" @click="readDeal">
                <el-icon><Check /></el-icon> 全部已读
            </div>
        </div>
        <el-row style="margin: 10px 0;box-sizing: border-box;">
            <el-row v-if="tableData.length === 0">
                <el-empty description="暂无通知" style="margin-top: 200px;"></el-empty>
            </el-row>
            <div v-else class="notification-container">
                <div class="notification-table">
                    <div class="save-book header">
                        <div class="title">消息体</div>
                        <div class="title">是否已读</div>
                        <div class="title">推送时间</div>
                        <div class="title">功能操作</div>
                    </div>
                    <div class="save-book" v-for="(rss, index) in tableData" :key="index">
                        <div>
                            {{ rss.content }}
                        </div>
                        <div>
                            <span :class="['status-tag', rss.isRead ? 'read' : 'unread']">
                                <el-icon v-if="rss.isRead"><Check /></el-icon>
                                <el-icon v-else><Message /></el-icon>
                                {{ rss.isRead ? '已读' : '未读' }}
                            </span>
                        </div>
                        <div>
                            {{ rss.createTime }}
                        </div>
                        <div class="action-buttons">
                            <span class="action-btn delete" @click="handleDelete(rss)">
                                <el-icon><Delete /></el-icon> 删除
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </el-row>
        <div class="pager" v-if="tableData.length !== 0">
            <div>
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    v-model:current-page="current" :page-size="size" layout="total, prev, pager, next"
                    :total="totalCount">
                </el-pagination>
            </div>
        </div>
    </el-row>
</template>

<script>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { Search, Check, Message, Delete } from '@element-plus/icons-vue'

export default {
    components: {
        Search,
        Check,
        Message,
        Delete
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
        const rssNotificationQueryDto = reactive({}) // 搜索条件
        const options = ref([])
        const bookshelfOptions = ref([])
        const bookOrderHistorys = ref([])
        const searchTime = ref([])

        // 方法
        const readDeal = () => {
            proxy.$axios.post('/rssNotification/readDeal').then(res => {
                if (res.data.code === 200) {
                    proxy.$notify({
                        title: '结果',
                        message: '全部消息已经设置为已读',
                        type: 'success'
                    });
                    fetchFreshData();
                }
            })
        }

        const fetchFreshData = async () => {
            try {
                tableData.value = [];
                // 请求参数
                const params = {
                    current: current.value,
                    size: size.value,
                    ...rssNotificationQueryDto
                };
                const response = await proxy.$axios.post('/rssNotification/queryUser', params);
                const { data: responseData } = response;
                tableData.value = responseData.data;
                totalCount.value = responseData.total;
            } catch (error) {
                console.error('订阅通知信息异常:', error);
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
            selectedRows.value.push(row);
            batchDelete();
        }

        // 批量删除数据
        const batchDelete = async () => {
            if (!selectedRows.value.length) {
                proxy.$message(`未选中任何数据`);
                return;
            }
            const confirmed = await proxy.$swalConfirm({
                title: '取消订阅？',
                text: `操作不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = selectedRows.value.map(entity => entity.id);
                    const response = await proxy.$axios.post(`/rssNotification/batchDelete`, ids);
                    if (response.data.code === 200) {
                        proxy.$swal.fire({
                            title: '删除提示',
                            text: response.data.msg,
                            icon: 'success',
                            showConfirmButton: false,
                            timer: 2000,
                        });
                        fetchFreshData();
                        return;
                    }
                } catch (e) {
                    proxy.$swal.fire({
                        title: '错误提示',
                        text: e,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: 2000,
                    });
                    console.error(`删除异常：`, e);
                }
            }
        }

        // 生命周期
        onMounted(() => {
            fetchFreshData();
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
            rssNotificationQueryDto,
            options,
            bookshelfOptions,
            bookOrderHistorys,
            searchTime,
            readDeal,
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
.word-search {
    display: flex;
    justify-content: center;
    margin-bottom: 15px;
    margin-top: -5px;

    .item {
        padding: 10px 20px;
        width: 500px;
        background-color: white;
        border-radius: 40px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        box-sizing: border-box;
        box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        border: 1px solid #eee;
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

.read-bar {
    display: flex;
    justify-content: center;
    margin-bottom: 8px;
    margin-top: 5px;
}

.read {
    background: linear-gradient(90deg, #36d1c4 0%, #5b86e5 100%);
    color: #fff;
    border-radius: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 6px 20px;
    cursor: pointer;
    font-size: 13px;
    font-weight: 600;
    box-shadow: 0 2px 8px rgba(91,134,229,0.10);
    transition: background 0.2s, box-shadow 0.2s, transform 0.2s;
    
    i {
        margin-right: 5px;
        font-size: 12px;
    }
}

.read:hover {
    background: linear-gradient(90deg, #2bc0b6 0%, #3a7bd5 100%);
    box-shadow: 0 4px 12px rgba(91,134,229,0.16);
    transform: translateY(-1px) scale(1.02);
}

.notification-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 20px 0;
}

.notification-table {
    width: 90%;
    max-width: 1200px;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    background: #fff;
}

.save-book {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #f0f0f0;
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
        padding: 6px 16px;
        border-radius: 20px;
        font-size: 13px;
        cursor: pointer;
        transition: all 0.2s;
        font-weight: 600;
        
        i {
            margin-right: 5px;
        }
        
        &.delete {
            background: linear-gradient(90deg, #ff758c 0%, #ff7eb3 100%);
            color: #fff;
            border: none;
            box-shadow: 0 2px 8px rgba(255,117,140,0.10);
            
            &:hover {
                background: linear-gradient(90deg, #ff5e62 0%, #ff9966 100%);
                transform: scale(1.06);
            }
        }
    }
}

.save-book:hover {
    background-color: #f9f9f9;
}

.save-book.header {
    background-color: #f5f7fa;
    border-bottom: 1px solid #ebeef5;
    
    &:hover {
        background-color: #f5f7fa;
    }
}

.status-tag {
    display: inline-flex;
    align-items: center;
    min-width: 48px;
    padding: 4px 12px;
    border-radius: 16px;
    font-size: 13px;
    font-weight: 600;
    text-align: center;
    box-shadow: 0 2px 8px rgba(91,134,229,0.06);
    
    i {
        margin-right: 5px;
        font-size: 12px;
    }
}

.status-tag.unread {
    background: linear-gradient(90deg, #36d1c4 0%, #5b86e5 100%);
    color: #fff;
}

.status-tag.read {
    background: #f2f6fc;
    color: #5b86e5;
    border: 1px solid #dbeafe;
}

.pager {
    display: flex;
    margin: 30px 0;
    justify-content: center;
    align-items: center;
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
}

:deep(.el-pager li.active) {
    background-color: #ff5722;
    color: white;
}

:deep(.el-pager li:hover:not(.active)) {
    background-color: rgba(255, 87, 34, 0.1);
}

.el-empty {
    margin: 100px 0;
    transition: all 0.3s;

    :deep(.el-empty__image) {
        opacity: 0.8;
    }

    :deep(.el-empty__description) {
        color: #909399;
        font-size: 16px;
    }
}
</style>

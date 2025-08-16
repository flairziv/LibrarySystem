<template>
    <div class="operation-log-container">
        <!-- 条件搜索 -->
        <div class="word-search" v-if="tableData.length !== 0">
            <div class="item">
                    <el-icon>
                        <Search />
                    </el-icon>
                    <input type="text" placeholder="搜索操作内容" v-model="userOperationLogQueryDto.content" @keyup.enter="fetchFreshData">
                    <span class="search-text" @click="fetchFreshData">搜索</span>
            </div>
        </div>

        <!-- 数据展示区域 -->
        <div class="content-wrapper">
            <el-empty 
                v-if="tableData.length === 0" 
                description="暂无操作日志" 
                :image-size="200">
            </el-empty>
            
            <el-card v-else class="log-card">
                <div class="card-header">
                    <span class="card-title">操作日志记录</span>
                </div>
                <el-table 
                    :data="tableData" 
                    style="width: 100%" 
                    border 
                    stripe
                    highlight-current-row>
                    <el-table-column prop="content" label="操作内容" min-width="300"></el-table-column>
                    <el-table-column prop="createTime" label="操作时间" width="180"></el-table-column>
                    <el-table-column label="操作" width="120" align="center">
                        <template #default="scope">
                            <el-button 
                                type="danger" 
                                size="small" 
                                circle
                                @click="handleDelete(scope.row)">
                                <el-icon>
                                    <Delete />
                                </el-icon>
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                
                <!-- 分页器 -->
                <div class="pagination-wrapper" v-if="tableData.length !== 0">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        v-model:current-page="current"
                        :page-size="size"
                        :page-sizes="[10, 20, 50, 100]"
                        layout="total, sizes, prev, pager, next, jumper"
                    :total="totalCount">
                </el-pagination>
            </div>
            </el-card>
        </div>
    </div>
</template>

<script>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Search, Delete } from '@element-plus/icons-vue';
import request from "@/utils/request.js";
import UserNavigation from '@/components/UserNavigation.vue';

export default {
    components: {
        UserNavigation,
        Search,
        Delete
    },
    setup() {
        const { proxy } = getCurrentInstance();
        const router = useRouter();
        const route = useRoute();

        // 响应式数据
        const selfPath = ref({ name: '个人中心', path: '/mySelf' });
        const userRoutes = ref([]);
        const nowRoute = ref('操作日志');
        const userInfo = reactive({
            id: null,
            url: '',
            name: '',
            role: null,
            email: ''
        });
        const flag = ref(false);
        const data = ref({});
        const current = ref(1);
        const cover = ref(null);
        const size = ref(10);
        const totalCount = ref(0);
        const dialogOperation = ref(false);
        const isOperation = ref(false);
        const tableData = ref([]);
        const selectedRows = ref([]);
        const userOperationLogQueryDto = reactive({
            current: 1,
            size: 10
        });
        const options = ref([]);
        const bookshelfOptions = ref([]);
        const bookOrderHistorys = ref([]);
        const searchTime = ref([]);

        // 方法
        const toggleSidebar = () => {
            flag.value = !flag.value;
            sessionStorage.setItem('flag', flag.value);
        };

        const refreshPage = () => {
            const currentRoute = route;
            router.replace('/refresh');
            proxy.$nextTick(() => {
                router.replace(currentRoute.fullPath);
            });
        };

        const routerClick = (routeItem) => {
            if (route.path !== routeItem.path) {
                router.push(routeItem.path);
            }
        };

        const menuOperationHistory = () => {
            flag.value = sessionStorage.getItem('flag') === 'true';
        };

        // Token检验
        const tokenCheckLoad = async () => {
            try {
                const res = await request.get('user/auth');
                // 错误处理
                if (res.data.code === 400) {
                    proxy.$message.error(res.data.msg);
                    router.push('/login');
                    return;
                }
                // 用户信息赋值
                const { id, userAvatar: url, userName: name, userRole: role, userEmail: email } = res.data.data;
                Object.assign(userInfo, { id, url, name, role, email });
                sessionStorage.setItem('userInfo', JSON.stringify(userInfo));
                sessionStorage.setItem('userId', userInfo.id);
            } catch (error) {
                console.error('获取用户认证信息时发生错误:', error);
                proxy.$message.error('认证信息加载失败，请重试！');
            }
        };

        const fetchFreshData = async () => {
            try {
                const response = await proxy.$axios.post('/userOperationLog/queryUser', userOperationLogQueryDto);
                const { data } = response;
                tableData.value = data.data;
                totalCount.value = data.total;
            } catch (error) {
                console.error('系统日志信息异常:', error);
            }
        };

        const handleSizeChange = (newSize) => {
            userOperationLogQueryDto.current = 1;
            userOperationLogQueryDto.size = newSize;
            size.value = newSize;
            fetchFreshData();
        };

        const handleCurrentChange = (newCurrent) => {
            userOperationLogQueryDto.current = newCurrent;
            current.value = newCurrent;
            fetchFreshData();
        };

        const handleDelete = (row) => {
            batchDelete(row);
        };

        // 批量删除数据
        const batchDelete = async (log) => {
            const confirmed = await proxy.$swalConfirm({
                title: '删除操作日志',
                text: `操作不可恢复，是否继续？`,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: '确认',
                cancelButtonText: '取消',
                confirmButtonColor: '#ff5722',
                cancelButtonColor: '#909399'
            });
            if (confirmed) {
                try {
                    let ids = [log.id];
                    const response = await proxy.$axios.post(`/userOperationLog/batchDelete`, ids);
                    if (response.data.code === 200) {
                        proxy.$notify({
                            duration: 1000,
                            title: '日志删除',
                            message: '删除成功',
                            type: 'success'
                        });
                        fetchFreshData();
                    }
                } catch (error) {
                    proxy.$notify({
                        title: '日志删除',
                        message: error.message || '删除失败',
                        type: 'error'
                    });
                    console.error(`删除异常：`, error);
                }
            }
        };

        onMounted(() => {
            let menus = router.options.routes.filter(route => route.path == '/user')[0];
            userRoutes.value = menus.children;
            fetchFreshData();
            tokenCheckLoad();
            menuOperationHistory();
        });

        return {
            selfPath,
            userRoutes,
            nowRoute,
            userInfo,
            flag,
            data,
            current,
            cover,
            size,
            totalCount,
            dialogOperation,
            isOperation,
            tableData,
            selectedRows,
            userOperationLogQueryDto,
            options,
            bookshelfOptions,
            bookOrderHistorys,
            searchTime,
            toggleSidebar,
            refreshPage,
            routerClick,
            menuOperationHistory,
            tokenCheckLoad,
            fetchFreshData,
            handleSizeChange,
            handleCurrentChange,
            handleDelete,
            batchDelete
        };
    }
};
</script>

<style scoped lang="scss">
.operation-log-container {
    width: 100%;
    padding: 10px 20px 20px 20px;
    box-sizing: border-box;
    background-color: transparent;
}

.word-search {
    display: flex;
    justify-content: center;
    margin-bottom: 15px;
    margin-top: 0;
    
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

    .content-wrapper {
        background-color: transparent;
    min-height: 300px;
    display: flex;
    justify-content: center;
        align-items: flex-start;
        
        .log-card {
            width: 100%;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
            border-radius: 12px;
            overflow: hidden;
            transition: all 0.3s;
            
            &:hover {
                box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
                transform: translateY(-2px);
            }
            
            .card-header {
                padding: 16px 20px;
                border-bottom: 1px solid #ebeef5;
                margin-bottom: 12px;
                
                .card-title {
                    font-size: 18px;
                    font-weight: 600;
                    color: #303133;
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
                        background: #ff5722;
                        border-radius: 2px;
                    }
                }
            }
            
            :deep(.el-card__body) {
                padding: 0 20px 20px;
            }
            
            :deep(.el-table) {
                border-radius: 8px;
                overflow: hidden;
                margin-bottom: 16px;
                
                th {
                    background-color: #f5f7fa;
                    color: #606266;
                    font-weight: 600;
                    padding: 14px 0;
                }
                
                td {
                    padding: 12px 0;
                }
                
                .el-table__row {
                    transition: all 0.2s;
                    
                    &:hover {
                        background-color: rgba(255, 87, 34, 0.05) !important;
                    }
                }
            }
            
            :deep(.el-table--border), 
            :deep(.el-table--group) {
                border-radius: 8px;
                border: 1px solid #ebeef5;
            }

            :deep(.el-table__row td:last-child) {
                .el-button {
                    transition: all 0.3s;
                    
                    &:hover {
                        transform: scale(1.1);
                        box-shadow: 0 2px 8px rgba(245, 108, 108, 0.4);
                    }
                }
            }
        }
    }
    
    .pagination-wrapper {
        display: flex;
        justify-content: center;
    margin-top: 20px;

        :deep(.el-pagination) {
            padding: 8px 16px;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
            
            .el-pagination__sizes .el-input .el-input__inner {
                border-radius: 6px;
            }
            
            button, .btn-prev, .btn-next {
                border-radius: 6px;
                transition: all 0.2s;
                
                &:hover {
                    background-color: rgba(255, 87, 34, 0.1);
        }
            }
            
            .el-pager li {
                border-radius: 6px;
                transition: all 0.2s;
                
                &:hover:not(.active) {
                    background-color: rgba(255, 87, 34, 0.1);
        }
                
                &.active {
                    background-color: #ff5722;
                    color: #fff;
                    font-weight: bold;
                }
            }
        }
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

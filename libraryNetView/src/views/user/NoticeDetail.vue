<template>
    <div class="notice-detail-container">
        <el-card class="notice-card">
            <div class="card-decoration"></div>
            <div class="notice-header">
                <div class="return-btn" @click="returnPage">
                    <el-icon><ArrowLeft /></el-icon>
                    <span>返回列表</span>
                </div>
                <div class="notice-title">{{ notice.name }}</div>
                <div class="notice-meta">
                    <span class="publish-time">
                        <el-icon><Clock /></el-icon>
                        {{ notice.createTime }}
                    </span>
                </div>
            </div>
            
            <div class="notice-content">
                <div v-if="notice.content" v-html="notice.content" class="rich-content"></div>
                <div v-else class="no-content">暂无内容</div>
            </div>
        </el-card>
    </div>
</template>

<script>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Clock } from '@element-plus/icons-vue'

export default {
    components: {
        ArrowLeft,
        Clock
    },
    name: 'NoticeDetail',
    setup() {
        const { proxy } = getCurrentInstance()
        const router = useRouter()
        
        // 响应式数据
        const notice = ref({})

        // 方法
        const returnPage = () => {
            router.go(-1);
        }

        const loadNoticeInfo = () => {
            try {
                const jsonNoticeInfo = sessionStorage.getItem('noticeInfo');
                if (!jsonNoticeInfo) {
                    proxy.$message.error('公告信息获取失败');
                    setTimeout(() => returnPage(), 1000);
                    return;
                }
                notice.value = JSON.parse(jsonNoticeInfo);
            } catch (error) {
                console.error('解析公告信息失败:', error);
                proxy.$message.error('公告信息解析失败');
                setTimeout(() => returnPage(), 1000);
            }
        }

        // 生命周期
        onMounted(() => {
            loadNoticeInfo();
        })

        return {
            notice,
            returnPage,
            loadNoticeInfo
        }
    }
};
</script>
<style scoped lang="scss">
.notice-detail-container {
    padding: 15px;
    max-width: 900px;
    margin: 0 auto;
    
    .notice-card {
        border-radius: 12px;
        box-shadow: 0 4px 16px rgba(255, 87, 34, 0.08);
        background-color: #fff;
        position: relative;
        overflow: hidden;
        border: none;
        transition: transform 0.3s ease;
        
        &:hover {
            transform: translateY(-3px);
        }
        
        .card-decoration {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #ff5722, #ff9800);
        }
        
        :deep(.el-card__body) {
            padding: 20px;
        }
    }
    
    .notice-header {
        padding-bottom: 10px;
        
        .return-btn {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 8px 14px;
            border-radius: 20px;
            background-color: rgba(255, 87, 34, 0.08);
            color: #ff5722;
            cursor: pointer;
            transition: all 0.2s ease;
            margin-bottom: 15px;
            font-weight: 500;
            
            i {
                font-size: 14px;
            }
            
            &:hover {
                background-color: rgba(255, 87, 34, 0.15);
                transform: translateX(-3px);
            }
        }
        
        .notice-title {
            font-size: 24px;
            font-weight: 600;
            color: #333;
            margin-bottom: 10px;
            line-height: 1.4;
            position: relative;
            padding-left: 12px;
            
            &:before {
                content: '';
                position: absolute;
                left: 0;
                top: 5px;
                bottom: 5px;
                width: 4px;
                background: linear-gradient(to bottom, #ff5722, #ff9800);
                border-radius: 4px;
            }
        }
        
        .notice-meta {
            display: flex;
            font-size: 14px;
            color: #909399;
            
            .publish-time {
                display: flex;
                align-items: center;
                gap: 5px;
                
                i {
                    font-size: 14px;
                    color: #ff9800;
                }
            }
        }
    }
    
    .notice-content {
        margin-top: 10px;
        padding: 15px;
        line-height: 1.7;
        color: #333;
        background-color: #fafafa;
        border-radius: 8px;
        
        .no-content {
            text-align: center;
            color: #909399;
            font-style: italic;
            padding: 10px 0;
        }
        
        .rich-content {
            :deep(img) {
                max-width: 100%;
                border-radius: 8px;
                box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
            }
            
            :deep(p) {
                margin-bottom: 15px;
            }
            
            :deep(a) {
                color: #ff5722;
                text-decoration: none;
                
                &:hover {
                    color: #ff9800;
                    text-decoration: underline;
                }
            }
            
            :deep(ul), :deep(ol) {
                padding-left: 20px;
                margin-bottom: 15px;
            }
            
            :deep(h1), :deep(h2), :deep(h3), :deep(h4) {
                color: #ff5722;
                margin: 15px 0 10px;
            }
            
            :deep(blockquote) {
                border-left: 4px solid #ff9800;
                padding: 10px 15px;
                margin: 15px 0;
                background-color: rgba(255, 152, 0, 0.05);
                border-radius: 0 4px 4px 0;
            }
        }
    }
}

@media screen and (max-width: 768px) {
    .notice-detail-container {
        padding: 10px;
        
        .notice-card {
            :deep(.el-card__body) {
                padding: 15px;
            }
        }
        
        .notice-header {
            .notice-title {
                font-size: 20px;
            }
        }
        
        .notice-content {
            padding: 12px;
        }
    }
}
</style>

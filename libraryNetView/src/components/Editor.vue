<template>
    <div>
        <Toolbar style="border-bottom: 1px solid #eae8e8;" :editor="editor" :defaultConfig="toolbarConfig"
            :mode="mode" />
        <Editor :style="{ height: height, overflowY: 'hidden' }" v-model="editorContent" :defaultConfig="editorConfig"
            :mode="mode" @onCreated="onCreated" />
    </div>
</template>
<script>
import { ref, watch, onBeforeUnmount, defineProps, defineEmits } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

export default {
    name: 'Editor',
    components: { Editor, Toolbar },
    props: {
        value: {
            type: String,
            default: '',
        },
        height: {
            type: String,
            default: 'calc(100vh - 250px)'
        }
    },
    emits: ['input'],
    setup(props, { emit }) {
        const editor = ref(null)
        const editorContent = ref(props.value || '<p>请输入内容...</p>')
        const toolbarConfig = ref({})
        const editorConfig = ref({
            placeholder: '请输入内容...',
            MENU_CONF: {
                uploadImage: {
                    server: '/api/library-system-api/v1.0/file/upload',
                    fieldName: 'file',
                    maxFileSize: 10 * 1024 * 1024,
                    maxNumberOfFiles: 10,
                    // allowedFileTypes: ['image/*'],
                    metaWithUrl: false,
                    withCredentials: true,
                    timeout: 10 * 1000,
                    headers: {
                        'token': sessionStorage.getItem('token')
                    },
                    customInsert(res, insertFn) {
                        insertFn(res.data, res.data, res.data);
                    },
                },
                uploadVideo: {
                    server: '/api/library-system-api/v1.0/file/upload',
                    fieldName: 'file',
                    maxFileSize: 100 * 1024 * 1024,
                    headers: {
                        'token': sessionStorage.getItem('token')
                    },
                    timeout: 60 * 1000,
                    customInsert(res, insertFn) {
                        insertFn(res.data, res.data);
                    },
                }
            }
        })
        const mode = ref('default')

        const onCreated = (editorInstance) => {
            editor.value = Object.seal(editorInstance);
            toolbarConfig.value.excludeKeys = ['group-video','insertLink','fullScreen','emotion','insertTable'];
        }

        // 监听 props.value 变化
        watch(() => props.value, (newVal) => {
            if (newVal !== editorContent.value) {
                editorContent.value = newVal || '<p>请输入内容...</p>';
            }
        }, { immediate: true })

        // 监听 editorContent 变化并发送事件
        watch(editorContent, (newVal) => {
            emit('input', newVal);
        }, { deep: true })

        onBeforeUnmount(() => {
            const editorInstance = editor.value;
            if (editorInstance == null) return;
            editorInstance.destroy();
        })

        return {
            editor,
            editorContent,
            toolbarConfig,
            editorConfig,
            mode,
            onCreated
        }
    }
}
</script>
<style src="@wangeditor/editor/dist/css/style.css"></style>
<style scoped>
.line-number {
    display: block;
    margin-right: 10px;
    /* 以下样式确保行号不被选中或复制 */
    pointer-events: none;
    user-select: none;
    -webkit-user-select: none;
    color: #999;
    /* 行号颜色，可自定义 */
    text-align: right;
    /* 行号对齐方式 */
}
</style>

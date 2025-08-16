<template>  
    <div>  
      <p>倒计时: {{ formattedTime }}</p>  
    </div>  
  </template>  
    
  <script>  
import { ref, computed, watch, onMounted, onBeforeUnmount, defineProps, defineEmits } from 'vue'

export default {  
  name: 'Inteval',
  props: {  
    // 接收来自父组件的初始分钟数  
    initialMinutes: {  
      type: Number,  
      required: true,  
      default: 0 // 如果未传递，则默认为0分钟  
    }  
  },
  emits: ['end'],
  setup(props, { emit }) {
    // 将从props接收的分钟数转换为总秒数  
    const totalSeconds = ref(0)
    // 用于setInterval的引用，以便稍后清除  
    const timerId = ref(null)

    // 格式化时间为 HH:MM:SS  
    const formattedTime = computed(() => {
      let hours = Math.floor(totalSeconds.value / 3600)
      let minutes = Math.floor((totalSeconds.value % 3600) / 60)
      let seconds = totalSeconds.value % 60

      hours = hours.toString().padStart(2, '0')
      minutes = minutes.toString().padStart(2, '0')
      seconds = seconds.toString().padStart(2, '0')

      return `${hours}:${minutes}:${seconds}`
    })

    const startTimer = () => {
      timerId.value = setInterval(() => {
        if (totalSeconds.value > 0) {
          totalSeconds.value--
        } else {
          // 清除定时器  
          clearInterval(timerId.value)
          timerId.value = null
          // 可选：发出倒计时结束事件  
          emit('end')
        }
      }, 1000)
    }

    // 监听initialMinutes的变化，并重新初始化倒计时  
    watch(() => props.initialMinutes, (newValue) => {
      if (timerId.value) {
        // 如果定时器已经存在，先清除它  
        clearInterval(timerId.value)
        timerId.value = null
      }
      totalSeconds.value = newValue * 60 // 更新总秒数  
      // 重新启动定时器  
      startTimer()
    })

    onMounted(() => {
      // 组件挂载时初始化倒计时  
      totalSeconds.value = props.initialMinutes * 60
      startTimer()
    })

    onBeforeUnmount(() => {
      // 组件卸载前清除定时器  
      if (timerId.value) {
        clearInterval(timerId.value)
      }
    })

    return {
      totalSeconds,
      timerId,
      formattedTime
    }
  }
}  
  </script>  
    
  <style scoped>  
  /* 添加你的样式 */  
  </style>

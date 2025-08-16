<template>
  <div class="chart-container">
    <div ref="chartRef" :style="{ width: '100%', height: height }"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'

export default {
  name: 'LineChart',
  props: {
    tag: {
      type: String,
      default: '折线图'
    },
    values: {
      type: Array,
      required: true,
      default: () => []
    },
    date: {
      type: Array,
      required: true,
      default: () => []
    },
    height: {
      type: String,
      default: '280px'
    },
  },
  setup(props) {
    const chart = ref(null)
    const chartRef = ref(null)

    const handleResize = () => {
      if (chart.value) {
        chart.value.resize()
      }
    }

    // 图表初始化
    const initChart = () => {
      // 添加数据验证
      if (!props.values || !Array.isArray(props.values) || props.values.length === 0 ||
          !props.date || !Array.isArray(props.date) || props.date.length === 0) {
        console.warn('LineChart: 数据为空或格式不正确', { values: props.values, date: props.date })
        return
      }

      if (chart.value) {
        chart.value.dispose()
      }
      
      chart.value = echarts.init(chartRef.value)
      
      const option = {
        grid: {
          left: 30,
          right: 30,
          bottom: 30,
          top: 40,
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255,255,255,0.9)',
          borderColor: '#ccc',
          borderWidth: 1,
          textStyle: {
            color: '#333'
          }
        },
        xAxis: {
          type: 'category',
          data: props.date,
          axisTick: {
            alignWithLabel: true
          },
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          }
        },
        series: [{
          name: props.tag,
          type: 'line',
          data: props.values,
          smooth: true,
          lineStyle: {
            color: '#409EFF',
            width: 3
          },
          itemStyle: {
            color: '#409EFF',
            borderColor: '#fff',
            borderWidth: 3
          }
        }]
      }
      
      chart.value.setOption(option)
    }

    // 监听数据变化
    watch(() => [props.values, props.date], () => {
      if (props.values && props.values.length > 0 && props.date && props.date.length > 0) {
        initChart()
      }
    }, { deep: true })

    onMounted(() => {
      // 延迟初始化，确保数据已加载
      setTimeout(() => {
        if (props.values && props.values.length > 0 && props.date && props.date.length > 0) {
          initChart()
        }
      }, 100)
      window.addEventListener('resize', handleResize)
    })

    onBeforeUnmount(() => {
      window.removeEventListener('resize', handleResize)
      if (chart.value) {
        chart.value.dispose()
      }
    })

    return {
      chart,
      chartRef
    }
  }
}
</script>

<style scoped lang="scss">
.chart-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}
</style>

<template>
  <div class="chart-container" :style="{ backgroundColor: bag }">
    <div ref="chartRef" :style="{ width: width, height: height }"></div>
  </div>
</template>
<script>
import * as echarts from 'echarts'
import { ref, onMounted, onBeforeUnmount, watch, defineProps } from 'vue'

export default {
  name: 'PieChart',
  props: {
    types: {
      type: Array,
      default: []
    },
    values: {
      type: Array,
      default: []
    },
    width: {
      type: String,
      default: '100%'
    },
    tag: {
      type: String,
      default: '饼状图'
    },
    height: {
      type: String,
      default: '280px'
    },
    bag: {
      type: String,
      default: '#fff'
    },
    fontColor: {
      type: String,
      default: '#333'
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

    const initChart = () => {
      if (chart.value) {
        chart.value.dispose()
      }
      chart.value = echarts.init(chartRef.value)
      let option = {
        title: {
          text: '',
          subtext: '',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)',
          backgroundColor: 'rgba(255, 255, 255, 0.9)',
          borderColor: '#eee',
          borderWidth: 1,
          textStyle: {
            color: '#333'
          },
          padding: [10, 15]
        },
        legend: {
          type: 'plain',
          orient: 'horizontal',
          bottom: 0,
          left: 'center',
          show: true,
          itemWidth: 12,
          itemHeight: 12,
          textStyle: {
            color: '#666',
            fontSize: 13,
            lineHeight: 20
          },
          icon: 'circle',
          itemGap: 16,
          formatter: function(name) {
            return name
          },
          padding: 5,
          selectedMode: false,
          inactiveColor: '#ccc'
        },
        series: [
          {
            name: props.tag,
            type: 'pie',
            radius: ['40%', '68%'],
            center: ['50%', '38%'],
            avoidLabelOverlap: true,
            emphasis: {
              label: {
                show: true,
                fontSize: 14,
                fontWeight: 'bold'
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.2)'
              }
            },
            labelLine: {
              show: true,
              smooth: true,
              length: 10,
              length2: 10
            },
            label: {
              show: true,
              formatter: '{d}%',
              fontSize: 12
            },
            data: props.values.map((value, index) => ({
              name: props.types[index],
              value: value,
            })),
            itemStyle: {
              color: function (params) {
                const colorList = [
                  '#409EFF', 
                  '#67C23A', 
                  '#E6A23C',
                  '#F56C6C', 
                  '#909399',
                  '#7265e6',
                  '#ffbf00', 
                ]
                return colorList[params.dataIndex % colorList.length]
              },
              borderRadius: 4,
              borderWidth: 2,
              borderColor: '#fff'
            }
          }
        ]
      }
      chart.value.setOption(option)
    }

    // 监听 props 变化
    watch(() => props.types, () => {
      initChart()
    })

    watch(() => props.values, () => {
      initChart()
    })

    onMounted(() => {
      initChart()
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

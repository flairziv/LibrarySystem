import { createApp } from "vue";
import router from "./router";
import App from "./App.vue";
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import { provinceAndCityData, regionData } from 'element-china-area-data';
import VueSweetalert2 from 'vue-sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import './assets/css/editor.scss'
import './assets/css/button.scss'
import './assets/css/elementui-cover.scss'
import './assets/css/basic.scss'
import './assets/css/dialog.scss'
import './assets/css/input.scss'
import './assets/styles/custom-sweetalert.scss'
import request from '@/utils/request'
import md5 from 'js-md5';
import swalPlugin from '@/utils/swalPlugin';
import * as echarts from 'echarts';

const app = createApp(App);

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 配置全局属性
app.config.globalProperties.$md5 = md5;
app.config.globalProperties.$axios = request;
app.config.globalProperties.$echarts = echarts;

// 使用插件
app.use(ElementPlus);
app.use(VueSweetalert2);
app.use(swalPlugin);
app.use(router);

// 提供全局数据
app.provide('regionData', regionData);
app.provide('provinceAndCityData', provinceAndCityData);

app.mount("#app");

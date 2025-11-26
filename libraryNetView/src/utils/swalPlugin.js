// swalPlugin.js
import Swal from 'sweetalert2';
// 五种图标： success\error\info\warning\question
// 提示框：
// this.$swal.fire({
//     title: '退出登录',
//     text: '您已成功退出登录。',
//     icon: 'success', // 使用'success'图标表示操作成功
//     showConfirmButton: false, // 隐藏确认按钮，使得弹窗只展示信息后自动关闭
//     timer: 2000, // 自动关闭弹窗的延迟时间，这里是2秒
//   });

// 完整用法示例：
// proxy.$swal.fire({
//     // 基础内容
//     title: '标题文字',
//     text: '内容文字',
//     html: '<b>HTML内容</b>',  // 可以使用 HTML
    
//     // 图标类型
//     icon: 'error',  // 'success' | 'error' | 'warning' | 'info' | 'question'
    
//     // 按钮配置
//     showConfirmButton: true,      // 是否显示确认按钮
//     confirmButtonText: '确定',    // 确认按钮文字
//     showCancelButton: true,       // 是否显示取消按钮
//     cancelButtonText: '取消',     // 取消按钮文字
    
//     // 自动关闭
//     timer: 3000,                  // 自动关闭时间（毫秒）
//     timerProgressBar: true,       // 显示倒计时进度条
    
//     // 动画效果
//     showClass: {
//         popup: 'animate__animated animate__fadeInDown'
//     },
//     hideClass: {
//         popup: 'animate__animated animate__fadeOutUp'
//     },
    
//     // 其他配置
//     allowOutsideClick: false,     // 点击外部是否关闭
//     allowEscapeKey: false,        // 按 ESC 是否关闭
//     allowEnterKey: true,          // 按 Enter 是否触发确认
    
//     // 自定义样式
//     customClass: {
//         popup: 'my-popup-class',
//         title: 'my-title-class',
//         confirmButton: 'my-confirm-class'
//     }
// })

const swalPlugin = {
  install(app) {
    app.config.globalProperties.$swalConfirm = async function(options = {}) {
      const defaultOptions = {
        title: '提示',
        text: '',
        icon: 'info',
        reverseButtons: true,
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        customClass: {
          confirmButton: 'sweet-btn-primary',
        },
        ...options,
      };

      try {
        const result = await Swal.fire(defaultOptions);
        return result.isConfirmed;
      } catch (error) {
        console.error('Swal Error:', error);
        return false;
      }
    };
  },
};

export default swalPlugin;

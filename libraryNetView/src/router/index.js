import { createRouter, createWebHistory } from "vue-router";
import { getToken } from "@/utils/storage.js";

const routes = [
  { path: "/", component: () => import(`@/views/login/Login.vue`) },
  { path: "/login", component: () => import(`@/views/login/Login.vue`) },
  { path: "/register", component: () => import(`@/views/register/Register.vue`) },
  { path: "/createNotice", component: () => import(`@/views/admin/CreateNotice.vue`), meta: { requireAuth: true } },
  {
    path: "/admin",
    component: () => import(`@/views/admin/Home.vue`),
    meta: { requireAuth: true },
    redirect: "/adminLayout",
    children: [
      { path: "/adminLayout", name: '数据总览', icon: 'DataAnalysis', component: () => import(`@/views/admin/Main.vue`), meta: { requireAuth: true } },
      { path: "/userManage", name: '用户管理', icon: 'User', component: () => import(`@/views/admin/UserManage.vue`), meta: { requireAuth: true } },
      { path: "/bookManage", name: '书籍管理', icon: 'Reading', component: () => import(`@/views/admin/BookManage.vue`), meta: { requireAuth: true } },
      { path: "/bookOrderHistoryManage", name: '借阅记录', icon: 'View', component: () => import(`@/views/admin/BookOrderHistoryManage.vue`), meta: { requireAuth: true } },
      { path: "/bookRssHistoryManage", name: '图书订阅', icon: 'Connection', component: () => import(`@/views/admin/BookRssHistoryManage.vue`), meta: { requireAuth: true } },
      { path: "/rssNotificationManage", name: '订阅通知', icon: 'Message', component: () => import(`@/views/admin/RssNotificationManage.vue`), meta: { requireAuth: true } },
      { path: "/bookshelfManage", name: '书架管理', icon: 'Folder', component: () => import(`@/views/admin/BookshelfManage.vue`), meta: { requireAuth: true } },
      { path: "/categoryManage", name: '类别管理', icon: 'CollectionTag', component: () => import(`@/views/admin/CategoryManage.vue`), meta: { requireAuth: true } },
      { path: "/readerProposalManage", name: '读者建议', icon: 'EditPen', component: () => import(`@/views/admin/ReaderProposalManage.vue`), meta: { requireAuth: true } },
      { path: "/noticeManage", name: '公告管理', icon: 'Bell', component: () => import(`@/views/admin/NoticeManage.vue`), meta: { requireAuth: true } },
      { path: "/userOperationLogManage", name: '行为日志', icon: 'Notebook', component: () => import(`@/views/admin/UserOperationLogManage.vue`), meta: { requireAuth: true } },
    ]
  },
  {
    path: "/user",
    component: () => import(`@/views/user/Home.vue`),
    meta: { requireAuth: true },
    redirect: "/main",
    children: [
      { name: '留言', path: "/main", show: true, icon: 'ChatDotSquare', component: () => import(`@/views/user/Main.vue`), meta: { requireAuth: true } },
      { name: '借阅', path: "/bookOperation", show: true, icon: 'Reading', component: () => import(`@/views/user/BookOperation.vue`), meta: { requireAuth: true } },
      { name: '历史', path: "/bookOrderHistory", show: true, icon: 'Timer', component: () => import(`@/views/user/BookOrderHistory.vue`), meta: { requireAuth: true } },
      { name: '收藏', path: "/bookSave", show: true, icon: 'Collection', component: () => import(`@/views/user/BookSave.vue`), meta: { requireAuth: true } },
      { name: '订阅', path: "/bookRssHistory", show: true, icon: 'Connection', component: () => import(`@/views/user/BookRssHistory.vue`), meta: { requireAuth: true } },
      { name: '通知', path: "/rssNotification", show: true, icon: 'Message', component: () => import(`@/views/user/RssNotification.vue`), meta: { requireAuth: true } },
      { name: '日志', path: "/userOperationLog", show: true, icon: 'Notebook', component: () => import(`@/views/user/UserOperationLog.vue`), meta: { requireAuth: true } },
      { name: '公告', path: "/notice", show: true, icon: 'Bell', component: () => import(`@/views/user/Notice.vue`), meta: { requireAuth: true } },
      { name: '公告详情', path: "/noticeDetail", show: false, icon: 'DocumentRemove', component: () => import(`@/views/user/NoticeDetail.vue`), meta: { requireAuth: true } },
      { name: '我的中心', path: "/mySelf", show: false, icon: 'User', component: () => import(`@/views/user/MySelf.vue`), meta: { requireAuth: true } },
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});
router.beforeEach((to, from, next) => {
  if (to.meta.requireAuth) {
    const token = getToken();
    if (token !== null) {
      next();
    } else {
      next("/login");
    }
  }
  else {
    next();
  }
});
export default router;

import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import Transfer from '../views/Transfer.vue'
import Records from '../views/Records.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const routes = [
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/', name: 'Dashboard', component: Dashboard, meta: { requiresAuth: true } },
  { path: '/transfer', name: 'Transfer', component: Transfer, meta: { requiresAuth: true } },
  { path: '/records', name: 'Records', component: Records, meta: { requiresAuth: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const userId = localStorage.getItem('userId');
  if (to.meta.requiresAuth && !userId) {
    next('/login');
  } else if ((to.path === '/login' || to.path === '/register') && userId) {
    next('/');
  } else {
    next();
  }
})

export default router

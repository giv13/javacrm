import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '../stores/user-store'

import AuthLayout from '../layouts/AuthLayout.vue'
import AppLayout from '../layouts/AppLayout.vue'

const routes: Array<RouteRecordRaw> = [
  {
    name: 'admin',
    path: '/',
    component: AppLayout,
    redirect: { name: 'dashboard' },
    meta: { requiresAuth: true },
    children: [
      {
        name: 'dashboard',
        path: 'dashboard',
        component: () => import('../pages/admin/dashboard/Dashboard.vue'),
        meta: { authorities: ['PROJECT_READ', 'USER_READ'] },
      },
      {
        name: 'projects',
        path: 'projects',
        component: () => import('../pages/projects/ProjectsPage.vue'),
        meta: { authorities: ['PROJECT_READ'] },
      },
      {
        name: 'users',
        path: 'users',
        component: () => import('../pages/users/UsersPage.vue'),
        meta: { authorities: ['USER_READ'] },
      },
    ],
  },
  {
    name: 'auth',
    path: '/auth',
    component: AuthLayout,
    redirect: { name: 'login' },
    meta: { requiresAuth: false },
    children: [
      {
        name: 'register',
        path: 'register',
        component: () => import('../pages/auth/Register.vue'),
      },
      {
        name: 'login',
        path: 'login',
        component: () => import('../pages/auth/Login.vue'),
      },
      {
        name: 'recover-password',
        path: 'recover-password',
        component: () => import('../pages/auth/RecoverPassword.vue'),
      },
      {
        name: 'recover-password-email',
        path: 'recover-password-email',
        component: () => import('../pages/auth/CheckTheEmail.vue'),
      },
    ],
  },
  {
    name: '404',
    path: '/:pathMatch(.*)*',
    component: () => import('../pages/404.vue'),
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    // For some reason using documentation example doesn't scroll on page navigation.
    if (to.hash) {
      return { el: to.hash, behavior: 'smooth' }
    } else {
      window.scrollTo(0, 0)
    }
  },
  routes,
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.auth) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (!userStore.hasAuthorities(to.meta.authorities as string[])) {
    to.meta.forbidden = true
    next()
  } else if (to.matched.some(r => r.name === 'auth') && userStore.auth) {
    next("/")
  } else {
    next()
  }
})

export default router

<script setup>
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Landmark, LayoutDashboard, ArrowRightLeft, History, LogOut } from 'lucide-vue-next';

const router = useRouter();
const route = useRoute();

const isLoginPage = computed(() => route.name === 'Login');
const currentTab = computed(() => route.name);

const navItems = [
  { name: 'Dashboard', path: '/', icon: LayoutDashboard, label: '首页概览' },
  { name: 'Transfer', path: '/transfer', icon: ArrowRightLeft, label: '资金业务' },
  { name: 'Records', path: '/records', icon: History, label: '查询记录' },
];

const handleLogout = () => {
  localStorage.removeItem('userId');
  localStorage.removeItem('username');
  router.push('/login');
};
</script>

<template>
  <div v-if="isLoginPage" class="min-h-screen bg-slate-50">
    <router-view />
  </div>

  <div v-else class="flex min-h-screen bg-slate-50 text-slate-900 font-sans">
    <aside class="w-64 bg-slate-900 text-white p-6 flex flex-col">
      <div class="flex items-center space-x-3 mb-10">
        <Landmark class="text-blue-500" :size="32" />
        <span class="text-xl font-bold italic">顺顺银行</span>
      </div>

      <nav class="flex-1 space-y-2">
        <button v-for="item in navItems" :key="item.name"
          @click="router.push(item.path)"
          :class="currentTab === item.name ? 'bg-blue-600' : 'hover:bg-slate-800'"
          class="w-full flex items-center space-x-3 p-3 rounded-xl transition">
          <component :is="item.icon" :size="20" />
          <span>{{ item.label }}</span>
        </button>
      </nav>

      <button @click="handleLogout"
        class="mt-auto flex items-center space-x-3 p-3 text-slate-400 hover:text-white transition w-full">
        <LogOut :size="20" />
        <span>退出登录</span>
      </button>
    </aside>

    <main class="flex-1 p-8">
      <router-view />
    </main>
  </div>
</template>

<style>
@tailwind base;
@tailwind components;
@tailwind utilities;
</style>

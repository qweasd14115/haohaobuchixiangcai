<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { login } from '../api/account';
import { Landmark } from 'lucide-vue-next';

const router = useRouter();
const username = ref('');
const password = ref('');
const loading = ref(false);
const error = ref('');

const handleLogin = async () => {
  if (!username.value || !password.value) {
    error.value = '请输入用户名和密码';
    return;
  }
  loading.value = true;
  error.value = '';
  try {
    const res = await login(username.value, password.value);
    if (res.data.success) {
      localStorage.setItem('userId', String(res.data.userId));
      localStorage.setItem('username', username.value);
      router.push('/');
    } else {
      error.value = res.data.message || '登录失败';
    }
  } catch (e) {
    error.value = '网络错误，无法连接到服务器';
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="min-h-screen bg-slate-50 flex items-center justify-center">
    <div class="bg-white p-10 rounded-3xl shadow-sm border border-slate-100 w-full max-w-md">
      <div class="flex items-center justify-center space-x-3 mb-8">
        <Landmark class="text-blue-500" :size="36" />
        <span class="text-2xl font-bold italic text-slate-900">顺顺银行</span>
      </div>

      <h2 class="text-lg font-bold text-center mb-6">登录账户</h2>

      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-slate-600 mb-1">用户名</label>
          <input v-model="username" type="text" placeholder="请输入用户名"
            class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            @keyup.enter="handleLogin" />
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-600 mb-1">密码</label>
          <input v-model="password" type="password" placeholder="请输入密码"
            class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            @keyup.enter="handleLogin" />
        </div>

        <div v-if="error" class="bg-red-50 text-red-700 text-sm p-3 rounded-xl border border-red-200">
          {{ error }}
        </div>

        <button @click="handleLogin" :disabled="loading"
          class="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition disabled:opacity-50">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </div>

      <p class="text-xs text-slate-400 text-center mt-6">测试账户：chenshun / 123456</p>
      <p class="text-sm text-slate-400 text-center mt-2">
        没有账户？
        <router-link to="/register" class="text-blue-600 hover:underline">立即注册</router-link>
      </p>
    </div>
  </div>
</template>

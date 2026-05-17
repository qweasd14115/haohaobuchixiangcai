<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { register } from '../api/account';
import { Landmark } from 'lucide-vue-next';

const router = useRouter();
const username = ref('');
const password = ref('');
const realName = ref('');
const loading = ref(false);
const error = ref('');
const success = ref('');

const handleRegister = async () => {
  if (!username.value || !password.value) {
    error.value = '请输入用户名和密码';
    return;
  }
  loading.value = true;
  error.value = '';
  success.value = '';
  try {
    const res = await register({
      username: username.value,
      password: password.value,
      realName: realName.value,
    });
    if (res.data.success) {
      success.value = `注册成功！卡号: ${res.data.cardNumber}`;
      localStorage.setItem('userId', String(res.data.userId));
      localStorage.setItem('username', username.value);
      setTimeout(() => router.push('/'), 1500);
    } else {
      error.value = res.data.message || '注册失败';
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

      <h2 class="text-lg font-bold text-center mb-6">注册新账户</h2>

      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-slate-600 mb-1">用户名</label>
          <input v-model="username" type="text" placeholder="请输入用户名"
            class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-600 mb-1">真实姓名（选填）</label>
          <input v-model="realName" type="text" placeholder="请输入真实姓名"
            class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-600 mb-1">密码</label>
          <input v-model="password" type="password" placeholder="请输入密码"
            class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            @keyup.enter="handleRegister" />
        </div>

        <div v-if="error" class="bg-red-50 text-red-700 text-sm p-3 rounded-xl border border-red-200">
          {{ error }}
        </div>

        <div v-if="success" class="bg-green-50 text-green-700 text-sm p-3 rounded-xl border border-green-200">
          {{ success }}
        </div>

        <button @click="handleRegister" :disabled="loading"
          class="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition disabled:opacity-50">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </div>

      <p class="text-sm text-slate-400 text-center mt-6">
        已有账户？
        <router-link to="/login" class="text-blue-600 hover:underline">立即登录</router-link>
      </p>
    </div>
  </div>
</template>

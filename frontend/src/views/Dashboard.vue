<script setup>
import { ref, onMounted } from 'vue';
import { getMyAccount, createAccount } from '../api/account';
import { Plus } from 'lucide-vue-next';

const accounts = ref([]);
const loading = ref(false);
const error = ref(null);
const creating = ref(false);
const message = ref('');
const username = localStorage.getItem('username') || '用户';

const loadData = async () => {
  loading.value = true;
  error.value = null;
  try {
    const res = await getMyAccount();
    accounts.value = res.data;
  } catch (e) {
    error.value = '无法连接到后端服务，请确认后端已启动。';
    accounts.value = [];
  } finally {
    loading.value = false;
  }
};

const handleCreateAccount = async () => {
  creating.value = true;
  message.value = '';
  try {
    const res = await createAccount();
    if (res.data.success) {
      message.value = `新卡创建成功！卡号: ${res.data.cardNumber}`;
      await loadData();
    } else {
      message.value = res.data.message || '创建失败';
    }
  } catch (e) {
    message.value = '网络错误，请重试';
  } finally {
    creating.value = false;
  }
};

onMounted(loadData);
</script>

<template>
  <div>
    <header class="mb-10 flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold">欢迎回来，{{ username }}</h1>
        <p class="text-slate-500">下午好，这是您的资产实时概览。</p>
      </div>
      <div class="flex gap-3">
        <button @click="handleCreateAccount" :disabled="creating"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition flex items-center gap-2 disabled:opacity-50">
          <Plus :size="16" />
          <span>{{ creating ? '创建中...' : '创建新卡' }}</span>
        </button>
        <button @click="loadData" :disabled="loading"
          class="px-4 py-2 bg-white border rounded-lg hover:bg-slate-100 transition">
          <span v-if="loading">同步中...</span>
          <span v-else>刷新数据</span>
        </button>
      </div>
    </header>

    <div v-if="message" class="mb-4 p-4 rounded-xl border text-sm"
      :class="message.includes('成功') ? 'bg-green-50 text-green-700 border-green-200' : 'bg-red-50 text-red-700 border-red-200'">
      {{ message }}
    </div>

    <div v-if="loading" class="flex items-center justify-center h-48 text-slate-400">
      加载中...
    </div>

    <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-3xl p-8 text-center">
      <p class="text-red-600 mb-4">{{ error }}</p>
      <button @click="loadData"
        class="px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition">
        重试
      </button>
    </div>

    <div v-else-if="accounts.length === 0" class="bg-white rounded-3xl p-8 text-center text-slate-400">
      暂无账户数据
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div v-for="acc in accounts" :key="acc.id"
        class="bg-white p-8 rounded-3xl shadow-sm border border-slate-100 hover:shadow-md transition">
        <div class="flex justify-between mb-8 text-sm font-bold text-slate-400">
          <span>{{ acc.status === 'ACTIVE' ? '储蓄卡' : '银行卡' }}</span>
          <span class="font-mono">{{ acc.cardNumber }}</span>
        </div>
        <p class="text-sm text-slate-500">可用余额</p>
        <h2 class="text-4xl font-black"> {{ acc.balance.toLocaleString() }}</h2>
      </div>
    </div>
  </div>
</template>

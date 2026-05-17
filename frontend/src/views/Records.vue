<script setup>
import { ref, onMounted } from 'vue';
import { getTransactions } from '../api/account';
import { History } from 'lucide-vue-next';

const transactions = ref([]);
const loading = ref(false);
const error = ref(null);

const loadRecords = async () => {
  loading.value = true;
  error.value = null;
  try {
    const res = await getTransactions();
    transactions.value = res.data;
  } catch (e) {
    error.value = '无法加载交易记录';
    transactions.value = [];
  } finally {
    loading.value = false;
  }
};

const formatTime = (time) => {
  if (!time) return '';
  const t = new Date(time);
  const pad = (n) => String(n).padStart(2, '0');
  return `${t.getFullYear()}-${pad(t.getMonth() + 1)}-${pad(t.getDate())} ${pad(t.getHours())}:${pad(t.getMinutes())}:${pad(t.getSeconds())}`;
};

const typeClass = (type) => {
  if (type.includes('转入') || type === '存钱') return 'text-green-600';
  if (type.includes('转出') || type === '取钱') return 'text-red-600';
  return 'text-slate-600';
};

onMounted(loadRecords);
</script>

<template>
  <div>
    <header class="mb-10 flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold">查询记录</h1>
        <p class="text-slate-500">交易流水与历史明细</p>
      </div>
      <button @click="loadRecords" :disabled="loading"
        class="px-4 py-2 bg-white border rounded-lg hover:bg-slate-100 transition">
        {{ loading ? '加载中...' : '刷新' }}
      </button>
    </header>

    <div v-if="loading" class="flex items-center justify-center h-48 text-slate-400">
      加载中...
    </div>

    <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-3xl p-8 text-center">
      <p class="text-red-600 mb-4">{{ error }}</p>
      <button @click="loadRecords" class="px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition">
        重试
      </button>
    </div>

    <div v-else-if="transactions.length === 0" class="bg-white rounded-3xl p-16 text-center text-slate-400">
      <History :size="48" class="mx-auto mb-4 opacity-30" />
      <p>暂无交易记录</p>
    </div>

    <div v-else class="bg-white rounded-3xl shadow-sm border border-slate-100 overflow-hidden">
      <table class="w-full">
        <thead>
          <tr class="bg-slate-50 text-left text-sm text-slate-500">
            <th class="p-4 font-medium">时间</th>
            <th class="p-4 font-medium">类型</th>
            <th class="p-4 font-medium text-right">金额</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tx in transactions" :key="tx.id" class="border-t border-slate-50 hover:bg-slate-50 transition">
            <td class="p-4 text-sm text-slate-600 font-mono">{{ formatTime(tx.createTime) }}</td>
            <td class="p-4 text-sm">
              <span :class="typeClass(tx.type)" class="font-medium">{{ tx.type }}</span>
            </td>
            <td class="p-4 text-sm text-right font-mono" :class="typeClass(tx.type)">
              {{ tx.amount > 0 ? '+' : '' }}{{ tx.amount.toLocaleString() }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

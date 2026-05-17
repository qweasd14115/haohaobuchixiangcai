<script setup>
import { ref, onMounted } from 'vue';
import { getMyAccount, transfer, deposit, withdraw } from '../api/account';

const accounts = ref([]);
const loading = ref(false);
const message = ref(null);
const messageType = ref('success');
const activeTab = ref('transfer');

const fromCardNumber = ref('');
const toCardNumber = ref('');
const amount = ref('');

const depositCard = ref('');
const depositAmount = ref('');

const withdrawCard = ref('');
const withdrawAmount = ref('');

const loadAccounts = async () => {
  try {
    const res = await getMyAccount();
    accounts.value = res.data;
    if (accounts.value.length > 0) {
      fromCardNumber.value = accounts.value[0].cardNumber;
      depositCard.value = accounts.value[0].cardNumber;
      withdrawCard.value = accounts.value[0].cardNumber;
    }
  } catch (e) {
    // silently fail
  }
};

const handleTransfer = async () => {
  if (!fromCardNumber.value || !toCardNumber.value || !amount.value) {
    message.value = '请填写完整的转账信息';
    messageType.value = 'error';
    return;
  }
  const num = parseFloat(amount.value);
  if (isNaN(num) || num <= 0) {
    message.value = '请输入有效的金额';
    messageType.value = 'error';
    return;
  }

  loading.value = true;
  message.value = null;
  try {
    const res = await transfer({
      fromCardNumber: fromCardNumber.value,
      toCardNumber: toCardNumber.value,
      amount: num,
    });
    if (res.data.success) {
      message.value = `转账成功！当前余额: ${res.data.newBalance.toLocaleString()}`;
      messageType.value = 'success';
      amount.value = '';
      toCardNumber.value = '';
      await loadAccounts();
    } else {
      message.value = res.data.message || '转账失败';
      messageType.value = 'error';
    }
  } catch (e) {
    message.value = '网络错误，请重试';
    messageType.value = 'error';
  } finally {
    loading.value = false;
  }
};

const handleDeposit = async () => {
  const num = parseFloat(depositAmount.value);
  if (isNaN(num) || num <= 0) {
    message.value = '请输入有效的存款金额';
    messageType.value = 'error';
    return;
  }
  loading.value = true;
  message.value = null;
  try {
    const res = await deposit(depositCard.value, num);
    if (res.data.success) {
      message.value = `存款成功！当前余额: ${res.data.newBalance.toLocaleString()}`;
      messageType.value = 'success';
      depositAmount.value = '';
      await loadAccounts();
    } else {
      message.value = res.data.message || '存款失败';
      messageType.value = 'error';
    }
  } catch (e) {
    message.value = '网络错误，请重试';
    messageType.value = 'error';
  } finally {
    loading.value = false;
  }
};

const handleWithdraw = async () => {
  const num = parseFloat(withdrawAmount.value);
  if (isNaN(num) || num <= 0) {
    message.value = '请输入有效的取款金额';
    messageType.value = 'error';
    return;
  }
  loading.value = true;
  message.value = null;
  try {
    const res = await withdraw(withdrawCard.value, num);
    if (res.data.success) {
      message.value = `取款成功！当前余额: ${res.data.newBalance.toLocaleString()}`;
      messageType.value = 'success';
      withdrawAmount.value = '';
      await loadAccounts();
    } else {
      message.value = res.data.message || '取款失败';
      messageType.value = 'error';
    }
  } catch (e) {
    message.value = '网络错误，请重试';
    messageType.value = 'error';
  } finally {
    loading.value = false;
  }
};

onMounted(loadAccounts);
</script>

<template>
  <div>
    <header class="mb-10">
      <h1 class="text-3xl font-bold">资金业务</h1>
      <p class="text-slate-500">转账、存款与取款操作</p>
    </header>

    <div class="max-w-2xl mx-auto">
      <div class="flex mb-6 bg-white rounded-2xl p-1 border border-slate-100">
        <button v-for="tab in [
            { key: 'transfer', label: '转账汇款' },
            { key: 'deposit', label: '存款充值' },
            { key: 'withdraw', label: '取款提现' },
          ]" :key="tab.key" @click="activeTab = tab.key"
          :class="activeTab === tab.key ? 'bg-blue-600 text-white' : 'text-slate-500 hover:text-slate-700'"
          class="flex-1 py-2.5 text-sm font-medium rounded-xl transition">
          {{ tab.label }}
        </button>
      </div>

      <div class="bg-white p-8 rounded-3xl shadow-sm border border-slate-100">

        <div v-if="activeTab === 'transfer'" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-600 mb-1">付款账户</label>
            <select v-model="fromCardNumber"
              class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
              <option v-for="acc in accounts" :key="acc.id" :value="acc.cardNumber">
                {{ acc.cardNumber }}（余额: {{ acc.balance.toLocaleString() }}）
              </option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-600 mb-1">收款卡号</label>
            <input v-model="toCardNumber" type="text" placeholder="请输入收款银行卡号"
              class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-600 mb-1">转账金额</label>
            <input v-model="amount" type="number" step="0.01" min="0.01" placeholder="0.00"
              class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <button @click="handleTransfer" :disabled="loading"
            class="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition disabled:opacity-50">
            {{ loading ? '处理中...' : '确认转账' }}
          </button>
        </div>

        <div v-if="activeTab === 'deposit'" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-600 mb-1">存入账户</label>
            <select v-model="depositCard"
              class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
              <option v-for="acc in accounts" :key="acc.id" :value="acc.cardNumber">
                {{ acc.cardNumber }}（余额: {{ acc.balance.toLocaleString() }}）
              </option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-600 mb-1">存款金额</label>
            <input v-model="depositAmount" type="number" step="0.01" min="0.01" placeholder="0.00"
              class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <button @click="handleDeposit" :disabled="loading"
            class="w-full py-3 bg-green-600 text-white rounded-xl font-bold hover:bg-green-700 transition disabled:opacity-50">
            {{ loading ? '处理中...' : '确认存款' }}
          </button>
        </div>

        <div v-if="activeTab === 'withdraw'" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-600 mb-1">取款账户</label>
            <select v-model="withdrawCard"
              class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
              <option v-for="acc in accounts" :key="acc.id" :value="acc.cardNumber">
                {{ acc.cardNumber }}（余额: {{ acc.balance.toLocaleString() }}）
              </option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-600 mb-1">取款金额</label>
            <input v-model="withdrawAmount" type="number" step="0.01" min="0.01" placeholder="0.00"
              class="w-full border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <button @click="handleWithdraw" :disabled="loading"
            class="w-full py-3 bg-orange-600 text-white rounded-xl font-bold hover:bg-orange-700 transition disabled:opacity-50">
            {{ loading ? '处理中...' : '确认取款' }}
          </button>
        </div>

        <div v-if="message" :class="messageType === 'success' ? 'bg-green-50 text-green-700 border-green-200' : 'bg-red-50 text-red-700 border-red-200'"
          class="mt-4 p-4 rounded-xl border text-sm">
          {{ message }}
        </div>
      </div>
    </div>
  </div>
</template>

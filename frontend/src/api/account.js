import request from '../request';

const getUserId = () => localStorage.getItem('userId') || '1';

export function login(username, password) {
    return request.post('/login', { username, password });
}

export function register(data) {
    return request.post('/register', data);
}

export function getMyAccount() {
    return request.get('/accounts/my', { params: { userId: getUserId() } });
}

export function createAccount() {
    return request.post('/accounts/create', null, { params: { userId: getUserId() } });
}

export function transfer(data) {
    return request.post('/accounts/transfer', data);
}

export function deposit(cardNumber, amount) {
    return request.post('/deposit', null, { params: { cardNumber, amount } });
}

export function withdraw(cardNumber, amount) {
    return request.post('/withdraw', null, { params: { cardNumber, amount } });
}

export function getTransactions() {
    return request.get(`/transactions/${getUserId()}`);
}

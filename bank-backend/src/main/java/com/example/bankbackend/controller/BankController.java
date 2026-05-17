package com.example.bankbackend.controller;

import com.example.bankbackend.entity.Account;
import com.example.bankbackend.entity.Transaction;
import com.example.bankbackend.entity.User;
import com.example.bankbackend.repository.AccountRepository;
import com.example.bankbackend.repository.TransactionRepository;
import com.example.bankbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api")
public class BankController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        User foundUser = userRepository.findByUsername(user.getUsername());

        if (foundUser != null && foundUser.getPassword().equals(user.getPassword())) {
            res.put("success", true);
            res.put("userId", foundUser.getId());
        } else {
            res.put("success", false);
            res.put("message", "账号或密码错误");
        }
        return res;
    }

    @GetMapping("/balance/{userId}")
    public Account getBalance(@PathVariable Long userId) {
        return accountRepository.findByUserId(userId).stream().findFirst().orElse(null);
    }

    @GetMapping("/accounts/my")
    public List<Account> getMyAccounts(@RequestParam(defaultValue = "1") Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @PostMapping("/register")
    @Transactional
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        Map<String, Object> res = new HashMap<>();
        try {
            String username = body.get("username");
            String password = body.get("password");
            String realName = body.getOrDefault("realName", username);

            if (username == null || password == null) {
                res.put("success", false);
                res.put("message", "用户名和密码不能为空");
                return res;
            }
            if (userRepository.findByUsername(username) != null) {
                res.put("success", false);
                res.put("message", "用户名已存在");
                return res;
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRealName(realName);
            userRepository.save(user);

            String cardNumber = generateCardNumber();
            Account acc = new Account();
            acc.setUserId(user.getId().longValue());
            acc.setCardNumber(cardNumber);
            acc.setBalance(BigDecimal.ZERO);
            acc.setStatus("ACTIVE");
            accountRepository.save(acc);

            res.put("success", true);
            res.put("userId", user.getId());
            res.put("cardNumber", cardNumber);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("success", false);
            res.put("message", "系统错误: " + e.getMessage());
        }
        return res;
    }

    @PostMapping("/accounts/create")
    @Transactional
    public Map<String, Object> createAccount(@RequestParam Long userId) {
        Map<String, Object> res = new HashMap<>();
        try {
            String cardNumber = generateCardNumber();
            Account acc = new Account();
            acc.setUserId(userId);
            acc.setCardNumber(cardNumber);
            acc.setBalance(BigDecimal.ZERO);
            acc.setStatus("ACTIVE");
            accountRepository.save(acc);

            res.put("success", true);
            res.put("cardNumber", cardNumber);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("success", false);
            res.put("message", "系统错误: " + e.getMessage());
        }
        return res;
    }

    @PostMapping("/deposit")
    @Transactional
    public Map<String, Object> deposit(@RequestParam String cardNumber, @RequestParam BigDecimal amount) {
        Map<String, Object> res = new HashMap<>();
        try {
            Account acc = accountRepository.findByCardNumber(cardNumber).orElse(null);
            if (acc == null) {
                res.put("success", false);
                res.put("message", "账户不存在");
                return res;
            }
            acc.setBalance(acc.getBalance().add(amount));
            accountRepository.save(acc);

            Transaction log = new Transaction();
            log.setUserId(acc.getUserId());
            log.setType("存钱");
            log.setAmount(amount);
            transactionRepository.save(log);

            res.put("success", true);
            res.put("newBalance", acc.getBalance());
        } catch (Exception e) {
            e.printStackTrace();
            res.put("success", false);
            res.put("message", "系统错误: " + e.getMessage());
        }
        return res;
    }

    @PostMapping("/withdraw")
    @Transactional
    public Map<String, Object> withdraw(@RequestParam String cardNumber, @RequestParam BigDecimal amount) {
        Map<String, Object> res = new HashMap<>();
        try {
            Account acc = accountRepository.findByCardNumber(cardNumber).orElse(null);
            if (acc == null) {
                res.put("success", false);
                res.put("message", "账户不存在");
                return res;
            }
            if (acc.getBalance().compareTo(amount) < 0) {
                res.put("success", false);
                res.put("message", "余额不足");
                return res;
            }
            acc.setBalance(acc.getBalance().subtract(amount));
            accountRepository.save(acc);

            Transaction log = new Transaction();
            log.setUserId(acc.getUserId());
            log.setType("取钱");
            log.setAmount(amount.negate());
            transactionRepository.save(log);

            res.put("success", true);
            res.put("newBalance", acc.getBalance());
        } catch (Exception e) {
            e.printStackTrace();
            res.put("success", false);
            res.put("message", "系统错误: " + e.getMessage());
        }
        return res;
    }

    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder("6222");
        Random r = new Random();
        for (int i = 0; i < 12; i++) {
            if (i > 0 && i % 4 == 0) sb.append(' ');
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    @PostMapping("/accounts/transfer")
    @Transactional
    public Map<String, Object> transfer(@RequestBody Map<String, Object> body) {
        Map<String, Object> res = new HashMap<>();
        try {
            String fromCardNumber = (String) body.get("fromCardNumber");
            String toCardNumber = (String) body.get("toCardNumber");
            BigDecimal amount = new BigDecimal(body.get("amount").toString());

            Account fromAcc = accountRepository.findByCardNumber(fromCardNumber).orElse(null);
            Account toAcc = accountRepository.findByCardNumber(toCardNumber).orElse(null);

            if (fromAcc == null) {
                res.put("success", false);
                res.put("message", "付款账户不存在");
                return res;
            }
            if (toAcc == null) {
                res.put("success", false);
                res.put("message", "收款账户不存在");
                return res;
            }
            if (fromAcc.getCardNumber().equals(toCardNumber)) {
                res.put("success", false);
                res.put("message", "不能转账给自己");
                return res;
            }
            if (fromAcc.getBalance().compareTo(amount) < 0) {
                res.put("success", false);
                res.put("message", "余额不足");
                return res;
            }

            fromAcc.setBalance(fromAcc.getBalance().subtract(amount));
            toAcc.setBalance(toAcc.getBalance().add(amount));
            accountRepository.save(fromAcc);
            accountRepository.save(toAcc);

            Transaction fromLog = new Transaction();
            fromLog.setUserId(fromAcc.getUserId());
            fromLog.setType("转账-转出");
            fromLog.setAmount(amount.negate());
            transactionRepository.save(fromLog);

            Transaction toLog = new Transaction();
            toLog.setUserId(toAcc.getUserId());
            toLog.setType("转账-转入");
            toLog.setAmount(amount);
            transactionRepository.save(toLog);

            res.put("success", true);
            res.put("newBalance", fromAcc.getBalance());
        } catch (Exception e) {
            e.printStackTrace();
            res.put("success", false);
            res.put("message", "系统错误: " + e.getMessage());
        }
        return res;
    }

    @GetMapping("/transactions/{userId}")
    public List<Transaction> getTransactions(@PathVariable Long userId) {
        return transactionRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
}

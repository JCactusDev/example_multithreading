package com.github.jcactusdev;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {
    private final int number;

    private final List<BankAccount> accounts;
    private final Lock bankLock;

    public Bank(int number, int accountCount) {
        this(number, accountCount, 0);
    }

    public Bank(int number, int accountCount, int startBalance) {
        this.number = number;
        accounts = new ArrayList<>(accountCount);
        for (int i = 0; i < accountCount; i++) {
            accounts.add(new BankAccount(i, startBalance));
        }
        bankLock = new ReentrantLock();
    }

    public int getNumber() {
        return number;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public BankAccount getAccount(int i) {
        bankLock.lock();
        try {
            return accounts.get(i);
        } finally {
            bankLock.unlock();
        }
    }

    public synchronized int getBalance(int from) {
        bankLock.lock();
        try {
            return accounts.get(from).getBalance();
        } finally {
            bankLock.unlock();
        }
    }

    public synchronized int getTotalBalance() {
        bankLock.lock();
        try {
            return accounts
                    .stream()
                    .mapToInt(BankAccount::getBalance)
                    .sum();
        } finally {
            bankLock.unlock();
        }
    }

    public int size() {
        return accounts.size();
    }

    public synchronized void transfer(int fromAccountId, int toAccountId, int amount) throws InterruptedException {
        bankLock.lock();
        try {
            accounts.get(fromAccountId).deposit(-amount);
            accounts.get(toAccountId).deposit(amount);
            System.out.printf(" %d from bank[%d], account[%d] to bank[%d], account[%d] to Balance: %d\n", amount, number, fromAccountId, number, toAccountId, getBalance(toAccountId));
        } finally {
            bankLock.unlock();
        }
    }

    public void transferEverythingToAccount(int toAccountId) {
        accounts
                .stream()
                .filter(a -> a.getNumber() != toAccountId && a.getBalance() > 0)
                .forEach(account -> {
                            try {
                                transfer(account.getNumber(), toAccountId, account.getBalance());
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                );
    }

    public synchronized void transfer(int fromAccountId, Bank toBank, int toAccountId, int amount) throws InterruptedException {
        bankLock.lock();
        try {
            accounts.get(fromAccountId).deposit(-amount);
            toBank.getAccount(toAccountId).deposit(amount);
            System.out.printf(" %d from bank[%d], account[%d] to bank[%d], account[%d] to Balance: %d\n", amount, number, fromAccountId, toBank.getNumber(), toAccountId, toBank.getBalance(toAccountId));
        } finally {
            bankLock.unlock();
        }
    }

    public static class BankAccount {
        private final int number;
        private final Lock accountLock;
        private volatile int balance = 0;

        public BankAccount(int number) {
            this(number, 0);
        }

        public BankAccount(int number, int startBalance) {
            this.number = number;
            this.accountLock = new ReentrantLock();
            this.balance = startBalance;
        }

        public int getNumber() {
            return number;
        }

        public synchronized int getBalance() {
            accountLock.lock();
            try {
                return balance;
            } finally {
                accountLock.unlock();
            }
        }

        public void setBalance(int balance) {
            accountLock.lock();
            try {
                this.balance = balance;
            } finally {
                accountLock.unlock();
            }
        }

        public synchronized void deposit(int amount) {
            accountLock.lock();
            try {
                balance += amount;
            } finally {
                accountLock.unlock();
            }
        }
    }

}

package com.github.jcactusdev;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    private final int number;
    private final List<BankAccount> accounts;

    public Bank(int number, int accountCount) {
        this(number, accountCount, 0);
    }

    public Bank(int number, int accountCount, int startBalance) {
        this.number = number;
        accounts = new ArrayList<>(accountCount);
        for (int i = 0; i < accountCount; i++) {
            accounts.add(new BankAccount(i, startBalance));
        }
    }

    public int getNumber() {
        return number;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public BankAccount getAccount(int i) {
        return accounts.get(i);
    }

    public long getBalance(int from) {
        return accounts.get(from).getBalance();
    }

    public long getTotalBalance() {
        return accounts
                .stream()
                .mapToLong(BankAccount::getBalance)
                .sum();
    }

    public int size() {
        return accounts.size();
    }

    public void transfer(int fromAccountId, int toAccountId, long amount) throws InterruptedException {
        accounts.get(fromAccountId).deposit(-amount);
        accounts.get(toAccountId).deposit(amount);
        System.out.printf(" %d from bank[%d], account[%d] to bank[%d], account[%d] to Balance: %d\n", amount, number, fromAccountId, number, toAccountId, getBalance(toAccountId));
    }

    public void transfer(int fromAccountId, Bank toBank, int toAccountId, long amount) throws InterruptedException {
        accounts.get(fromAccountId).deposit(-amount);
        toBank.getAccount(toAccountId).deposit(amount);
        System.out.printf(" %d from bank[%d], account[%d] to bank[%d], account[%d] to Balance: %d\n", amount, number, fromAccountId, toBank.getNumber(), toAccountId, toBank.getBalance(toAccountId));
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

    public static class BankAccount {
        private final int number;
        private volatile long balance = 0;

        public BankAccount(int number) {
            this(number, 0);
        }

        public BankAccount(int number, int startBalance) {
            this.number = number;
            this.balance = startBalance;
        }

        public int getNumber() {
            return number;
        }

        public synchronized long getBalance() {
            return balance;
        }

        public synchronized void deposit(long amount) {
            balance += amount;
        }
    }

}
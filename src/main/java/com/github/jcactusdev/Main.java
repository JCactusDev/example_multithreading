package com.github.jcactusdev;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<Bank> banks = new ArrayList<>(3);
        banks.add(new Bank(0, 1500));
        banks.add(new Bank(1, 2000, 2));
        banks.add(new Bank(2, 3000, 3));

        Bank toBank = new Bank(777, 3, 10);

        banks.forEach(bank -> {
            for (Bank.BankAccount account : bank.getAccounts()) {
                if (account.getBalance() > 0) {
                    try {
                        bank.transfer(account.getNumber(), toBank, 0, 1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        System.out.println(getTotalBalancesFromBanks(banks));
        System.out.println(toBank.getTotalBalance());
        System.out.println(toBank.getBalance(0));

        toBank.transferEverythingToAccount(0);
        System.out.println(toBank.getBalance(0));
    }

    public static int getTotalBalancesFromBanks(List<Bank> banks) {
        return banks.stream().mapToInt(Bank::getTotalBalance).sum();
    }
}



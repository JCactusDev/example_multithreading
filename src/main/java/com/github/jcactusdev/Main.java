package com.github.jcactusdev;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<Bank> banks = new ArrayList<>(3);
        banks.add(new Bank(0, 100));
        banks.add(new Bank(1, 100, 1));
        banks.add(new Bank(2, 100, 1));

        Bank toBank = new Bank(777, 10, 1);

        banks.forEach(bank -> bank
                .getAccounts()
                .stream()
                .filter(account -> account.getBalance() > 0)
                .forEach(account -> {
                        try {
                            bank.transfer(account.getNumber(), toBank, 0, 1);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                )
        );

        System.out.println(getTotalBalancesFromBanks(banks));
        System.out.println(toBank.getTotalBalance());
        System.out.println(toBank.getBalance(0));

        toBank.transferEverythingToAccount(0);
        System.out.println(toBank.getBalance(0));
    }

    public static int getTotalBalancesFromBanks(List<Bank> banks) {
        return banks
                .stream()
                .mapToInt(Bank::getTotalBalance)
                .sum();
    }
}



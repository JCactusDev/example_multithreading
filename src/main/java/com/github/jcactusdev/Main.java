package com.github.jcactusdev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        List<Bank> banks = Arrays.asList(
                new Bank(0, 1000, 2),
                new Bank(1, 1000, 2),
                new Bank(2, 1000, 2)
        );

        Bank targetBank = new Bank(777, 10, 1);

        transferAllToTargetBank(banks, targetBank);

    }

    private static void transferAllToTargetBank(List<Bank> banks, Bank targetBank) throws InterruptedException {

        List<Thread> threads = banks.stream().map(bank -> getThreadTransferAllToTargetBank(bank, targetBank)).collect(Collectors.toList());

        // Wait Threads to finish
        while (!threads.isEmpty()) {
            threads.removeIf(thread -> !thread.isAlive());
            sleep(100);
        }

        // Status
        System.out.printf("Total balance from banks - %d\n", getTotalBalancesFromBanks(banks));
        System.out.printf("Total balance in target bank - %d\n", targetBank.getTotalBalance());
        System.out.printf("Account '0' balance in target bank[%d] - %d\n", targetBank.getNumber(), targetBank.getBalance(0));

        // Transfer everything to account '0' in target bank
        if (targetBank.getTotalBalance() > targetBank.getBalance(0)) {
            targetBank.transferEverythingToAccount(0);
            System.out.printf("Account balance '0' in target bank[%d] - %d\n", targetBank.getNumber(), targetBank.getBalance(0));
        }

    }

    private static Thread getThreadTransferAllToTargetBank(Bank bank, Bank targetBank) {
        Thread thread = new Thread(() -> {
            while (bank.getTotalBalance() > 0) {
                bank.getAccounts()
                        .stream()
                        .filter(account -> account.getBalance() > 0)
                        .forEach(account -> {
                            try {
                                bank.transfer(account.getNumber(), targetBank, ThreadLocalRandom.current().nextInt(0, targetBank.size()), 1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });
            }
        });
        thread.start();
        return thread;
    }

    public static int getTotalBalancesFromBanks(List<Bank> banks) {
        return banks
                .stream()
                .mapToInt(Bank::getTotalBalance)
                .sum();
    }
}



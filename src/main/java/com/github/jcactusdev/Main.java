package com.github.jcactusdev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    private static final List<Bank> banks = new ArrayList<>(Arrays.asList(
            new Bank(0, 10000, 10000000),
            new Bank(1, 10000, 10000000),
            new Bank(2, 10000, 10000000)
    ));
    private static final Bank targetBank = new Bank(777, 10);
    private static final CyclicBarrier BARRIER = new CyclicBarrier(banks.size(), new TransferCompleted(banks, targetBank));

    public static void main(String[] args) throws InterruptedException {

        for (Bank bank : banks) {
            new Thread(new Transfer(bank, targetBank)).start();
        }

    }

    public static long getTotalBalancesFromBanks(List<Bank> banks) {
        return banks
                .stream()
                .mapToLong(Bank::getTotalBalance)
                .sum();
    }

    public static class Transfer implements Runnable {
        private final Bank bank;
        private final Bank targetBank;

        public Transfer(Bank bank, Bank targetBank) {
            this.bank = bank;
            this.targetBank = targetBank;
        }

        @Override
        public void run() {
            try {
                while (bank.getTotalBalance() > 0) {
                    bank.getAccounts()
                            .stream()
                            .filter(account -> account.getBalance() > 0)
                            .forEach(account -> {
                                try {
                                    long currentBalance = bank.getBalance(account.getNumber());
                                    long amount = currentBalance == 1 ? currentBalance : ThreadLocalRandom.current().nextLong(1, currentBalance);
                                    bank.transfer(account.getNumber(), targetBank, ThreadLocalRandom.current().nextInt(0, targetBank.size()), amount);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            });
                }
                BARRIER.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class TransferCompleted implements Runnable {
        private final List<Bank> banks;
        private final Bank targetBank;

        public TransferCompleted(List<Bank> banks, Bank targetBank) {
            this.banks = banks;
            this.targetBank = targetBank;
        }

        @Override
        public void run() {
            // Status
            System.out.printf("Total balance in 'banks' - %d\n", getTotalBalancesFromBanks(banks));
            System.out.printf("Total balance in target bank[%d] - %d\n", targetBank.getNumber(), targetBank.getTotalBalance());
            System.out.printf("Account '0' balance in 'target bank[%d] - %d\n", targetBank.getNumber(), targetBank.getBalance(0));

            // Transfer everything to account '0' in target bank
            if (targetBank.getTotalBalance() > targetBank.getBalance(0)) {
                targetBank.transferEverythingToAccount(0);
                System.out.printf("Account balance '0' in target bank[%d] - %d\n", targetBank.getNumber(), targetBank.getBalance(0));
            }
        }
    }

}
package bank.transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class BankTransaction {
        Scanner scanner = new Scanner(System.in);
        
    private  void clearScreen() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    enum AccountType {
        Checking,
        Savings
    }

    class Transaction {
        String date;
        String type;
        float amount;
    }

    class BankAccount {
        int accountNumber;
        AccountType accountType;
        float balance;
        Transaction[] transactionHistory = new Transaction[100];
        int numTransactions = 0;

        private static final int MAX_ACCOUNTS = 100;
        private static final String FILENAME = "accounts.txt";
    }

    private static final Scanner sc = new Scanner(System.in);

    private static void displayAccount(BankAccount account) {
        System.out.println("Account Number: " + account.accountNumber);
        System.out.println("Account Type: " + (account.accountType == BankTransaction.AccountType.Checking ? "Checking" : "Savings"));
        System.out.println("Balance: $" + account.balance);
        System.out.println("Transaction History:");
        for (int i = 0; i < account.numTransactions; i++) {
            Transaction transaction = account.transactionHistory[i];
            System.out.println(transaction.date + " - " + transaction.type + " $" + transaction.amount);
        }
        System.out.println();
    }

    private static int loadAccountsFromFile(BankAccount[] accounts) {
        int numAccounts = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(BankAccount.FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Account Number: ")) {
                    BankAccount account = accounts[numAccounts];
                    account.accountNumber = Integer.parseInt(line.substring(16));
                    line = reader.readLine();
                    account.accountType = (line.contains("Checking") ? BankTransaction.AccountType.Checking : BankTransaction.AccountType.Savings);
                    line = reader.readLine();
                    try {
                        account.balance = Float.parseFloat(line.substring(9));
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing balance for Account Number: " + account.accountNumber);
                        account.balance = 0.0f; 
                    }
                    account.numTransactions = 0;
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        Transaction transaction = account.transactionHistory[account.numTransactions];
                        int pos = line.indexOf(" - ");
                        transaction.date = line.substring(0, pos);
                        int pos2 = line.indexOf(" $");
                        transaction.type = line.substring(pos + 3, pos2 - pos - 3);
                        try {
                            transaction.amount = Float.parseFloat(line.substring(pos2 + 2));
                        } catch (NumberFormatException e) {
                            System.out.println("Error parsing transaction amount for Account Number: " + account.accountNumber);
                            transaction.amount = 0.0f; 
                        }
                        account.numTransactions++;
                    }
                    numAccounts++;
                }
            }
            System.out.println("Account information loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error opening file for reading.");
        }
        return numAccounts;
    }

    private static void deposit(BankAccount account, float amount) {
        account.balance += amount;
        Transaction transaction = account.transactionHistory[account.numTransactions];
        transaction.date = "Today";
        transaction.type = "Deposit";
        transaction.amount = amount;
        account.numTransactions++;
        System.out.println("Amount deposited successfully.");
    }

    private static void withdraw(BankAccount account, float amount) {
        if (account.balance >= amount) {
            account.balance -= amount;
            Transaction transaction = account.transactionHistory[account.numTransactions];
            transaction.date = "Today";
            transaction.type = "Withdrawal";
            transaction.amount = amount;
            account.numTransactions++;
            System.out.println("Amount withdrawn successfully.");
        } else {
            System.out.println("Insufficient funds.");
        }
    }

    private static void transfer(BankAccount fromAccount, BankAccount toAccount, float amount) {
        if (fromAccount.balance >= amount) {
            fromAccount.balance -= amount;
            toAccount.balance += amount;
            Transaction withdrawalTransaction = fromAccount.transactionHistory[fromAccount.numTransactions];
            withdrawalTransaction.date = "Today";
            withdrawalTransaction.type = "Transfer to Account " + toAccount.accountNumber;
            withdrawalTransaction.amount = amount;
            fromAccount.numTransactions++;
            Transaction depositTransaction = toAccount.transactionHistory[toAccount.numTransactions];
            depositTransaction.date = "Today";
            depositTransaction.type = "Transfer from Account " + fromAccount.accountNumber;
            depositTransaction.amount = amount;
            toAccount.numTransactions++;
            System.out.println("Amount transferred successfully.");
        } else {
            System.out.println("Insufficient funds in the source account.");
        }
    }

    private static void createAccount(BankAccount account) {
        System.out.println("===============================================");
        System.out.print("Enter Account Number: ");
        account.accountNumber = sc.nextInt();

        System.out.print("Enter Account Type (0 for Checking, 1 for Savings): ");
        int accountType = sc.nextInt();
        account.accountType = (accountType == 0 ? BankTransaction.AccountType.Checking : BankTransaction.AccountType.Savings);

        System.out.print("Enter Initial Balance: ");
        account.balance = sc.nextFloat();

        account.numTransactions = 0;
        System.out.println("Account created successfully.");
    }

    private static void checkBalance(BankAccount[] accounts, int numAccounts) {
        System.out.print("Enter Account Number: ");
        int accountNumber = sc.nextInt();
        boolean found = false;
        for (int i = 0; i < numAccounts; i++) {
            if (accounts[i].accountNumber == accountNumber) {
                System.out.println("Balance: $" + accounts[i].balance);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Account not found.");
        }
    }

    public static void main(String[] args) {
        performTransaction();
    }

    private static void performTransaction() {
        BankAccount[] accounts = new BankAccount[BankAccount.MAX_ACCOUNTS];
        int numAccounts = loadAccountsFromFile(accounts) ;
   

        int choice;
        do {
            System.out.println("===============================================");
            System.out.println("Bank Transaction Menu");
            System.out.println("===============================================");
            System.out.println("1. Create New Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Check Balance");
            System.out.println("6. Display Account Information");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
   
        case 1 -> {
        new BankTransaction().clearScreen(); 
        BankAccount account = accounts[numAccounts];
        createAccount(account);
        numAccounts++;
    }
    case 2 -> {
        int accountNumber;
        float amount;
        System.out.print("Enter Account Number: ");
        accountNumber = sc.nextInt();
        System.out.print("Enter Amount to deposit: ");
        amount = sc.nextFloat();
        boolean found = false;
        for (int i = 0; i < numAccounts; i++) {
            if (accounts[i].accountNumber == accountNumber) {
                deposit(accounts[i], amount);
                found = true;
                break;
            }


                    }
                    if (!found) {
                        System.out.println("Account not found.");
                    }
                }
                case 3 ->  {
                    int accountNumber;
                    float amount;
                    System.out.print("Enter Account Number: ");
                    accountNumber = sc.nextInt();
                    System.out.print("Enter Amount to withdraw: ");
                    amount = sc.nextFloat();
                    boolean found = false;
                    for (int i = 0; i < numAccounts; i++) {
                        if (accounts[i].accountNumber == accountNumber) {
                            withdraw(accounts[i], amount);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("Account not found.");
                    }
                }
                case 4 ->  {
                    int fromAccountNumber, toAccountNumber;
                    float amount;
                    System.out.print("Enter Source Account Number: ");
                    fromAccountNumber = sc.nextInt();
                    System.out.print("Enter Destination Account Number: ");
                    toAccountNumber = sc.nextInt();
                    System.out.print("Enter Amount to transfer: ");
                    amount = sc.nextFloat();
                    boolean fromAccountFound = false;
                    boolean toAccountFound = false;
                    int fromAccountIndex = -1;
                    int toAccountIndex = -1;
                    for (int i = 0; i < numAccounts; i++) {
                        if (accounts[i].accountNumber == fromAccountNumber) {
                            fromAccountFound = true;
                            fromAccountIndex = i;
                        }
                        if (accounts[i].accountNumber == toAccountNumber) {
                            toAccountFound = true;
                            toAccountIndex = i;
                        }
                    }
                    if (fromAccountFound && toAccountFound) {
                        transfer(accounts[fromAccountIndex], accounts[toAccountIndex], amount);
                    } else {
                        System.out.println("One or both accounts not found.");
                    }
                }
                case 5 -> checkBalance(accounts, numAccounts);
                case 6 ->  {
                    int accountNumber;
                    System.out.print("Enter Account Number: ");
                    accountNumber = sc.nextInt();
                    boolean found = false;
                    for (int i = 0; i < numAccounts; i++) {
                        if (accounts[i].accountNumber == accountNumber) {
                            displayAccount(accounts[i]);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("Account not found.");
                    }
                }
                case 7 -> System.out.println("Exiting program.");
                default -> System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        } while (choice != 7);
    }
}
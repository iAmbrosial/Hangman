import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;
// regex to enforce guidelines in parameters
import java.util.regex.Pattern;

/*
The main class of the program. Your class may look different.
This is just to demonstrate how to load and save users using the DataStore class.
It is assumed that accounts only exist if they belong to a user.
Hence saving all users, each with a reference to their accounts is sufficient.
*/

// implement all basic features in main class first.
// word bank of around 200 words in a separate class, could start with a base difficulty
//
public class Hangman {
    // class variables
    private static ArrayList<User> users = new ArrayList<>(); // any method in this class can access this list of users
    private static final Scanner scanner = new Scanner(System.in); // any method in this class can access this scanner
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int maxUsernameLength = 16;



    public static void main(String[] args) {
        initializeUsers();

        User currentUser = null;
        boolean loginStatus = false;

        /*
        For debugging.
        Print out all users in the system and their accounts.
        Assumes toString() is implemented for both User and Account classes.
        */
        // users.forEach(user -> {
        //     System.out.println(user);
        //     user.getAccounts().forEach(System.out::println);
        // });

        System.out.println("Welcome to your new personal banking service! These are the base commands available to you.");
        System.out.println("If you would like to login, please type \"login\". If you do not have an account and would like to register for one, please type \"signup\".");

        // store user command
        String userCmd = "";

        // while loop for user to choose options from the menu
        while (!userCmd.toLowerCase().equals("quit")) {
            if (!loginStatus) {
                baseCommands();
                System.out.print("You are not logged in yet. Please select your action: ");

                userCmd = scanner.nextLine().trim().toLowerCase();

                System.out.println();
                switch (userCmd) {
                    case "login" -> {
                        currentUser = login();
                        if (currentUser != null) {
                            loginStatus = true;
                            selectedAccount = pickAccountOrDefault(currentUser);
                        }
                        break;
                    }
                    case "signup" -> {
                        currentUser = signup();
                        if (currentUser != null) {
                            selectedAccount = pickAccountOrDefault(currentUser);
                        }
                        break;
                    }
                    case "help" -> {
                        // stays in loop so no need to recall baseCommands()
                        break;
                    }
                    case "quit" -> {
                        System.out.println("Exiting the program...");
                        break;
                    }
                    default -> {
                        System.out.println("You have not inputted a valid command. Please refer to this list once more.");
                        break;
                    }
                }
            }
            else { // after log in
                authorizedCommands();
                if (currentUser.getAdminStatus()) {
                    adminCommands();
                }
                System.out.println("You have been logged in. Here are your available options. Please select an action: ");

                userCmd = scanner.nextLine().trim();

                // may require a helper method to select the correct Account
                System.out.println();
                switch (userCmd.toLowerCase()) {
                    case "deposit" -> {
                        System.out.printf("-----------------%n");
                        System.out.print("Please input your desired deposit amount (Input 0 to exit): ");
                        double depositAmount;
                        try {
                            depositAmount = scanner.nextDouble();
                            scanner.nextLine();
                        } catch (InputMismatchException e) {
                            break;
                        }

                        try {
                            while (depositAmount < 0.01 && depositAmount != 0) {
                                System.out.print("You cannot deposit a negative amount or less than 1 cent. Please try again: ");
                                depositAmount = scanner.nextDouble();
                                scanner.nextLine();
                            }
                        } catch (Exception e) {
                            break;
                        }

                        if (depositAmount == 0) {
                            System.out.println("Exiting...");
                            break;
                        }

                        selectedAccount.deposit(depositAmount);
                        DataStore.saveUsers(users);
                        System.out.printf("Deposit successful! You now have %.2f in your account.%n", selectedAccount.getBalance());
                    }
                    case "withdraw" -> {
                        System.out.printf("-----------------%n");
                        System.out.printf("Your balance is: %.2f%n", selectedAccount.getBalance());
                        double withdrawAmount;
                        try {
                            withdrawAmount = getWithdrawAmt(selectedAccount);
                        } catch (InputMismatchException e) {
                            break;
                        }

                        if (withdrawAmount == 0) {
                            System.out.println("Exiting...");
                            break;
                        }

                        selectedAccount.withdraw(withdrawAmount);
                        DataStore.saveUsers(users);
                        System.out.printf("Withdraw successful! You have %.2f remaining in your account.%n", selectedAccount.getBalance());
                    }
                    case "transfer" -> {
                        boolean transferSuccess = handleTransfer(currentUser, selectedAccount);
                        if (!transferSuccess) {
                            System.out.println("Something went wrong during the transfer process as noted above.");
                        }
                        else {
                            System.out.print("");
                        }
                    }
                    case "view" -> {
                        System.out.printf("-----------------%n");
                        System.out.println(currentUser);
                        System.out.println("Currently active account: ");
                        System.out.println(selectedAccount);
                        System.out.println("All accounts:");
                        for (Account i : currentUser.getAccounts()) {
                            System.out.println(i);
                        }
                    }
                    case "editinfo" -> {
                        System.out.printf("-----------------%n");
                        System.out.print("If you would like to change your username, input \"1\". For changing name input \"2\", for changing email input \"3\", and for changing password input \"4\" Leave empty to exit: ");
                        int changeChoice;
                        try {
                            changeChoice = Integer.parseInt(scanner.nextLine().trim());
                        } catch (Exception e) {
                            System.out.println("You have not entered an option, exiting command.");
                            break;
                        }

                        changeOption(changeChoice, currentUser, selectedAccount);
                    }
                    case "changeacc" -> {
                        selectedAccount = findAccount(changeAcc(currentUser), currentUser);
                    }
                    case "makeacc" -> {
                        System.out.printf("-----------------%n");
                        System.out.println("Please follow the account creation steps. ");
                        currentUser.addAccount(createAcc(currentUser));
                        DataStore.saveUsers(users);
                    }
                    case "setdefault" -> {
                        setNewDefault(currentUser);
                    }
                    case "help" -> {
                        System.out.printf("-----------------%n");
                        System.out.println("Reprinting the available commands.");
                    }
                    case "logout" -> {
                        System.out.println("Logging out...");
                        loginStatus = false;
                        baseCommands();
                    }
                    case "viewall" -> {
                        ((Admin)currentUser).viewAllUsers(users);
                    }
                    case "allbal" -> {
                        ((Admin)currentUser).sortAllAccsByBalance(users);
                    }
                    case "viewfees" -> {
                        Admin currentAdmin = (Admin) currentUser;
                        System.out.printf("Total fees collected: %.2f%n", currentAdmin.getTotalFees());
                    }
                    case "quit" -> {
                        System.out.println("Exiting program...");
                    }
                    default -> {
                        System.out.println("You have not inputted a valid command. Please refer to this list once more.");
                    }
                }
            }
        }
    }

    private static void initializeUsers() {
        users = DataStore.loadUsers();

        if (users.isEmpty()) {
            Admin sysAdmin = new Admin("admin", "Administrator", "admin@gmail.com", "secretpassword");
            Account adminAcc = new VisaAccount(sysAdmin, "4111111111111111");
            sysAdmin.addAccount(adminAcc);
            users.add(0, sysAdmin);
        }
        /*
        Or hard-code users and their accounts here for the first run
        */

        // User danielJ = new User("DanJ2", "Daniel Jones", "danielj25@outlook.com", "ilovedogs22");
        // Account danielAcc = new VisaAccount(danielJ, "4045650485701222");
        // users.add(danielJ);
        // danielJ.addAccount(danielAcc);

        // User jerome = new User("JayJay", "Jerome Anderson", "janderson@gmail.com", "jjthegoat");
        // Account jeromeAcc = new MasterAccount(jerome, "5566985383536395");
        // users.add(jerome);
        // jerome.addAccount(jeromeAcc);

        DataStore.saveUsers(users);
    }

    /**
     * Handles new user creation and validating input
     */
    private static User signup() {
        System.out.println("This is the account creation process. You will be prompted to input a username of your choice, your legal name, email, and a password.");
        System.out.println("Any information entered here is editable after account creation.");
        System.out.print("Firstly, please choose a username that is 16 characters or shorter (spaces at the beginning or end of the username will not be counted): ");
        String newUsername = scanner.nextLine().trim();
        // ensuring a unique username and precondition. The precondition is fixed for the program.
        while (doesUserExist(newUsername) || newUsername.length() > maxUsernameLength) {
            System.out.println("A user profile already exists with that username or your username is too long.");
            System.out.print("Please try again with a different username: ");
            newUsername = scanner.nextLine().trim();
        }
        System.out.print("Next, please enter your legal name with only alphabetic characters: ");
        String newLegalName = scanner.nextLine().trim();
        // regex to allow character a-z, A-Z, and spaces
        while (!Pattern.matches("[a-zA-Z ]+", newLegalName)) {
            System.out.print("Please only use alphabetic characters in your legal name: ");
            newLegalName = scanner.nextLine().trim();
        }
        System.out.print("Please enter the email to be used with this account. Please ensure it matches a proper email address format: ");
        String newEmail = scanner.nextLine().trim();
        //
        while (!Pattern.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$", newEmail)){
            System.out.print("Please follow a proper email address format and use only uppercase or lowercase letters, digits, _, -, or . with one @ symbol: ");
            newEmail = scanner.nextLine().trim();
        }

        System.out.print("Please enter your desired account password: ");
        String newPassword = scanner.nextLine().trim();

        User newUser = new User(newUsername, newLegalName, newEmail, newPassword);

        // setup profile
        Account newAccount = createAcc(newUser);
        // change implementation to allow account to be created and added to the user, return void or account for the helper method??
        newUser.addAccount(newAccount);
        users.add(newUser);

        DataStore.saveUsers(users);

        return newUser;
    }

    /**
     * Handles authentication (login) logic
     */
    private static User login() {
        System.out.println("You are now logging in. Please provide required information when asked.");
        System.out.print("Username (case sensitive): ");
        String tempUsername = scanner.nextLine();

        // create helper method to check if a name exists in the arrayList
        while (!doesUserExist(tempUsername)) {
            System.out.print("The username does not match any user. Please try again (leave blank to exit): ");
            tempUsername = scanner.nextLine();
            if (tempUsername.isEmpty()) {
                return null;
            }
        }

        // retrieve user to validate password
        User desiredUser = findUser(tempUsername);

        System.out.print("Password (case sensitive): ");
        String tempPassword = scanner.nextLine();

        for (int attempts = MAX_LOGIN_ATTEMPTS - 1; attempts >= 0; attempts--) {
            if (desiredUser.checkPassword(tempPassword)) {
                System.out.println("You are now logged into the user portal.");
                return desiredUser;
            }
            else if (attempts == 0) {
                System.out.println("You have entered an incorrect password three times, exiting.");
                return null;
            }
            else {
                System.out.printf("The password is incorrect. You have %s attempts remaining.%n", attempts);
                System.out.print("Please try again: ");
                tempPassword = scanner.nextLine();
            }
        }

        return null;
    }


    /**
     * Base Functionality for Users
     */
    private static void baseCommands() {
        System.out.printf("-----------------%n");
        System.out.printf("| Base Commands | %n");
        System.out.printf("-----------------%n");
        System.out.println("Login (input \"login\")");
        System.out.println("Create New Account (input \"signup\")");
        System.out.println("Help (input \"help\")");
        System.out.println("Quit Program (input \"quit\")");

        System.out.printf("-----------------%n");
    }

    /**
     * Authorized Functionality for Logged In users
     */
    private static void authorizedCommands() {
        System.out.printf("-----------------%n");
        System.out.printf("| Auth Commands | %n");
        System.out.printf("-----------------%n");

        //
        System.out.println("Deposit (input \"deposit\")");
        System.out.println("Withdraw (input \"withdraw\")");
        System.out.println("Transfer Funds (input \"transfer\")");
        System.out.println("View User and Accounts Info (input \"view\")");
        System.out.println("Edit User Info (input \"editinfo\")");
        System.out.println("Change Account (input \"changeacc\")");
        System.out.println("Create New Account (input \"makeacc\")");
        System.out.println("Set New Default Account (input \"setdefault\")");
        System.out.println("Help (input \"help\")");
        System.out.println("Log Out (input \"logout\")");
        System.out.println("Quit (input \"quit\")");

        System.out.printf("-----------------%n");
    }

    /**
     * find user in ArrayList using username
     * @return User which was matched
     */
    private static User findUser(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }

        System.out.println("The provided user was not found.");
        return null;
    }

    /**
     * checks if a user exists in the ArrayList
     * @return boolean if the user exists or not
     */
    private static boolean doesUserExist(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    private static void changeOption(int changeChoice, User currentUser, Account selectedAccount) {
        switch (changeChoice) {
            case 1 -> {
                System.out.print("Input your new desired username which must be 16 characters or shorter: ");
                String newUsername = scanner.nextLine().trim();

                while (doesUserExist(newUsername) || newUsername == null || newUsername.isEmpty() || newUsername.length() > 16) {
                    System.out.println("Sorry but you have either provided no input, your username length is over 16, or another user profile already exists with that username.");
                    System.out.print("Please choose a different username (input nothing to exit): ");
                    newUsername = scanner.nextLine().trim();
                    if (newUsername.isEmpty()) {
                        break;
                    }
                }

                if (newUsername.isEmpty()) {
                    break;
                }

                currentUser.setUsername(newUsername);
                DataStore.saveUsers(users);
                System.out.println("Your username  successfully changed!");
            }
            case 2 -> {
                System.out.print("Input your new desired name: ");
                String newName = scanner.nextLine().trim();

                // regex to allow character a-z, A-Z, and spaces
                while (newName == null || newName.isEmpty() || !Pattern.matches("[a-zA-Z ]+", newName)) {
                    System.out.println("Sorry but you have either provided no input or your name contains other characters apart from alphabetic ones.");
                    System.out.print("Please choose a different name (input nothing to exit): ");
                    newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) {
                        break;
                    }
                }

                if (newName.isEmpty()) {
                    break;
                }

                currentUser.setName(newName);
                DataStore.saveUsers(users);
                System.out.println("Your name has been successfully changed!");
            }
            case 3 -> {
                System.out.print("Input your new desired email. Please use only uppercase or lowercase letters, digits, _, -, or . in your email address and only one @ symbol: ");
                String newEmail = scanner.nextLine().trim();

                // ^ asserts the start of the String; the first group limits the initial part of the email to a-z, A-Z, 0-9, _, -, or .;
                // @ matches the literal character needed for an email
                // the second group allows the same characters as the first group, but after the @ symbol
                // the third group matches between 2 and 5 letters for Top-Level Domains (TLD)
                while (newEmail == null || newEmail.isEmpty() || !Pattern.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$", newEmail)) {
                    System.out.println("Sorry but you have either provided no input or your email is in an incorrect format.");
                    System.out.print("Please choose a different email (input nothing to exit): ");
                    newEmail = scanner.nextLine().trim();
                    if (newEmail.isEmpty()) {
                        break;
                    }
                }

                if (newEmail.isEmpty()) {
                    break;
                }

                currentUser.setEmail(newEmail);
                DataStore.saveUsers(users);
                System.out.println("Your email has been successfully changed!");
            }
            case 4 -> {
                System.out.print("Input your new desired password: ");
                String newPassword = scanner.nextLine().trim();

                while (newPassword == null || newPassword.isEmpty()) {
                    System.out.println("Sorry but you have provided no input.");
                    System.out.print("Please choose a different password (input nothing to exit): ");
                    newPassword = scanner.nextLine().trim();
                    if (newPassword.isEmpty()) {
                        break;
                    }
                }

                if (newPassword.isEmpty()) {
                    break;
                }

                currentUser.setPassword(newPassword);
                DataStore.saveUsers(users);
                System.out.println("Your password has been successfully changed!");
            }
        }
    }
}

import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;
// regex to enforce guidelines in parameters
import java.util.regex.Pattern;

/*
The main class of the program. Your class may look different.
This is just to demonstrate how to load and save players using the DataStore class.
It is assumed that accounts only exist if they belong to a user.
Hence saving all players, each with a reference to their accounts is sufficient.
*/

// implement all basic features in main class first.
// word bank of around 200 words in a separate class, could start with a base difficulty
//
public class HangmanOld {
    // class variables
    private static ArrayList<Player> players = new ArrayList<>(); // any method in this class can access this list of players
    private static final Scanner scanner = new Scanner(System.in); // any method in this class can access this scanner
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int maxUsernameLength = 16;



    public static void main(String[] args) {
        initializePlayers();

        Player currentPlayer = null;
        boolean loginStatus = false;

        /*
        For debugging.
        Print out all players in the system and their accounts.
        Assumes toString() is implemented for both User and Account classes.
        */
        // players.forEach(user -> {
        //     System.out.println(user);
        //     user.getAccounts().forEach(System.out::println);
        // });

        System.out.println("Welcome to your new personal banking service! These are the base commands available to you.");
        System.out.println("If you would like to login, please type \"login\". If you do not have an account and would like to register for one, please type \"signup\".");

        // store user command
        String userCmd = "";

        // while loop for user to choose options from the menu
        while (!userCmd.equalsIgnoreCase("quit")) {
            if (!loginStatus) {
                baseCommands();
                System.out.print("You are not logged in yet. Please select your action: ");

                userCmd = scanner.nextLine().trim().toLowerCase();

                System.out.println();
                switch (userCmd) {
                    case "login" -> {
                        currentPlayer = login();
                        if (currentPlayer != null) {
                            loginStatus = true;
                        }
                        break;
                    }
                    case "signup" -> {
                        currentPlayer = signup();
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
                System.out.println("You have been logged in. Here are your available options. Please select an action: ");

                userCmd = scanner.nextLine().trim();

                // may require a helper method to select the correct Account
                System.out.println();
                switch (userCmd.toLowerCase()) {
                    case "scores" -> {

                    }
                    case "view" -> {
                        System.out.printf("-----------------%n");
                        System.out.println(currentPlayer);
                    }
                    case "editinfo" -> {
                        System.out.printf("-----------------%n");
                        System.out.print("If you would like to change your username, input \"1\", and to change your password input \"2\" Leave empty to exit: ");
                        int changeChoice;
                        try {
                            changeChoice = Integer.parseInt(scanner.nextLine().trim());
                        } catch (Exception e) {
                            System.out.println("You have not entered an option, exiting command.");
                            break;
                        }

                        changeOption(changeChoice, currentPlayer);
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

    private static void initializePlayers() {
        players = DataStore.loadPlayers();
        /*
        Or hard-code players and their accounts here for the first run
        */

        // User danielJ = new User("DanJ2", "Daniel Jones", "danielj25@outlook.com", "ilovedogs22");
        // Account danielAcc = new VisaAccount(danielJ, "4045650485701222");
        // players.add(danielJ);
        // danielJ.addAccount(danielAcc);

        // User jerome = new User("JayJay", "Jerome Anderson", "janderson@gmail.com", "jjthegoat");
        // Account jeromeAcc = new MasterAccount(jerome, "5566985383536395");
        // players.add(jerome);
        // jerome.addAccount(jeromeAcc);

        DataStore.savePlayers(players);
    }

    /**
     * Handles new user creation and validating input
     */
    private static Player signup() {
        System.out.println("This is the account creation process. You will be prompted to input a username of your choice, your legal name, email, and a password.");
        System.out.println("Any information entered here is editable after account creation.");
        System.out.print("Firstly, please choose a username that is 16 characters or shorter (spaces at the beginning or end of the username will not be counted): ");
        String newUsername = scanner.nextLine().trim();
        // ensuring a unique username and precondition. The precondition is fixed for the program.
        while (doesPlayerExist(newUsername) || newUsername.length() > maxUsernameLength) {
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

        Player newPlayer = new Player(newUsername, newPassword);

        players.add(newPlayer);

        DataStore.savePlayers(players);

        return newPlayer;
    }

    /**
     * Handles authentication (login) logic
     */
    private static Player login() {
        System.out.println("You are now logging in. Please provide required information when asked.");
        System.out.print("Username (case sensitive): ");
        String tempUsername = scanner.nextLine();

        // create helper method to check if a name exists in the arrayList
        while (!doesPlayerExist(tempUsername)) {
            System.out.print("The username does not match any user. Please try again (leave blank to exit): ");
            tempUsername = scanner.nextLine();
            if (tempUsername.isEmpty()) {
                return null;
            }
        }

        // retrieve user to validate password
        Player desiredPlayer = findPlayer(tempUsername);

        System.out.print("Password (case sensitive): ");
        String tempPassword = scanner.nextLine();

        for (int attempts = MAX_LOGIN_ATTEMPTS - 1; attempts >= 0; attempts--) {
            assert desiredPlayer != null;
            if (desiredPlayer.checkPassword(tempPassword)) {
                System.out.println("You are now logged into the user portal.");
                return desiredPlayer;
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
     * Authorized Functionality for Logged In players
     */
    private static void authorizedCommands() {
        System.out.printf("-----------------%n");
        System.out.printf("| Auth Commands | %n");
        System.out.printf("-----------------%n");

        //
        System.out.println("View Score History (input \"scores\")");
        System.out.println("View Player Info (input \"view\")");
        System.out.println("Edit Player Info (input \"editinfo\")");
        System.out.println("Help (input \"help\")");
        System.out.println("Log Out (input \"logout\")");
        System.out.println("Quit (input \"quit\")");

        System.out.printf("-----------------%n");
    }

    /**
     * find player in ArrayList using username
     * @return Player which was matched
     */
    private static Player findPlayer(String username) {
        for (Player p : players) {
            if (p.getUsername().equals(username)) {
                return p;
            }
        }

        System.out.println("The provided player was not found.");
        return null;
    }

    /**
     * checks if a player exists in the ArrayList
     * @return boolean if the player exists or not
     */
    private static boolean doesPlayerExist(String username) {
        for (Player p : players) {
            if (p.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    private static void changeOption(int changeChoice, Player currentPlayer) {
        switch (changeChoice) {
            case 1 -> {
                System.out.print("Input your new desired username which must be 16 characters or shorter: ");
                String newUsername = scanner.nextLine().trim();

                while (doesPlayerExist(newUsername) || newUsername == null || newUsername.isEmpty() || newUsername.length() > 16) {
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

                currentPlayer.setUsername(newUsername);
                DataStore.savePlayers(players);
                System.out.println("Your username  successfully changed!");
            }
            case 2 -> {
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

                currentPlayer.setPassword(newPassword);
                DataStore.savePlayers(players);
                System.out.println("Your password has been successfully changed!");
            }
        }
    }
}

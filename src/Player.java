import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // to identify the class even if fields/methods change

    // instance variables
    private String username;
    private String name;
    private String email;
    private String password;

    /**
     * Constructor(s)
     */

    public Player() {
        // default constructor needed for deserialization
    }

    // do not set up the accounts here yet
    public Player(String username, String name, String email, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("A username is required.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A name is required.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("An email is required.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("A password is required,");
        }
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // mutators and accessors
    // all validation done in main class

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String newUsername) {
        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("New username cannot be blank.");
        }
        this.username = newUsername;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("New name cannot be blank.");
        }
        this.name = newName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String newEmail) {
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("New email cannot be blank.");
        }
        this.email = newEmail;
    }

    protected String getPassword() {
        return this.password;
    }

    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be blank.");
        }
        this.password = newPassword;
    }

    /**
     * Authentication
     */
    public boolean checkPassword(String inputPassword) {
        return getPassword().equals(inputPassword);
    }

    public String getObfuscatedPassword() {
        System.out.println("Your password has been obfuscated.");
        String obfuscatedPassword = "";
        for (int i = 0; i < password.length(); i++) {
            obfuscatedPassword += "*";
        }
        return obfuscatedPassword;
    }

    /**
     * Output/format player info
     */
    @Override
    public String toString() {
        return String.format("Username: %s\nName: %s\nEmail: %s\nObfuscated Password: %s\nDefault Account: %s",
                getUsername(), getName(), getEmail(), getObfuscatedPassword(), getDefaultAccount().getAccountNum());
    }
}

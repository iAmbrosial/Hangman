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
    private String password;
    private int highScore;
    private final List<Integer> scoreHistory = new ArrayList<>();

    /**
     * Constructor(s)
     */

    public Player() {
        // default constructor needed for deserialization
    }

    // do not set up the accounts here yet
    public Player(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("A username is required.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("A password is required,");
        }
        this.username = username;
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

    public void addScore(int score) {
        this.scoreHistory.add(score);
        if (score > this.highScore) {
            this.highScore = score;
        }
    }

    public int getHighScore() {
        return this.highScore;
    }

    public List<Integer> getScoreHistory() {
        return Collections.unmodifiableList(this.scoreHistory);
    }

    /**
     * Output/format player info
     */
    @Override
    public String toString() {
        return String.format("Username: %s\nHighscore: %d\nObfuscated Password: %s",
                getUsername(), getHighScore(), getObfuscatedPassword());
    }
}

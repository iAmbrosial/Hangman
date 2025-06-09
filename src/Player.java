import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // to identify the class even if fields/methods change

    // instance variables
    private String username;
    private String password;
    private int overallHighScore;
    private final Map<String, Integer> highScoreMap = new HashMap<>();
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

    public void recordScore(String difficulty, int score) {
        // Add raw score to history
        this.scoreHistory.add(score);
        // Update high score for the difficulty specified
        this.highScoreMap.merge(difficulty, score, Math::max);
        // Update overall high score
        this.overallHighScore = Math.max(overallHighScore, score);
    }

    public int getHighScore(String difficulty) {
        return this.highScoreMap.getOrDefault(difficulty, 0);
    }

    public int getOverallHighScore() {
        return this.overallHighScore;
    }

    public List<Integer> getScoreHistory() {
        return Collections.unmodifiableList(scoreHistory);
    }

    /**
     * Output/format player info
     */
    @Override
    public String toString() {
        return String.format("Username: %s\nOverall Highscore: %d\nObfuscated Password: %s",
                getUsername(), getOverallHighScore(), getObfuscatedPassword());
    }
}

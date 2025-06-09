import java.util.HashSet;
import java.util.Set;

public class GameSession {
    private final String targetWord;
    private final Set<Character> guessedChar = new HashSet<>();
    private int wrongGuesses = 0;
    private static final int MAX_GUESSES = 6;

    public GameSession(String targetWord) {
        if (targetWord == null || targetWord.isBlank()) {
            throw new IllegalArgumentException("Word cannot be null or blank.");
        }
        this.targetWord = targetWord.toUpperCase();
    }

    /**
     * Process a guess and return true if correct
     */
    public boolean guess(char c) {
        c = Character.toUpperCase(c);
        if (!Character.isLetter(c) || guessedChar.contains(c)) {
            return false; // character already guessed or isn't an alphabetic character
        }
        guessedChar.add(c);
        if (!targetWord.contains(Character.toString(c))) {
            wrongGuesses++;
            return false;
        }
        return true;
    }

    /**
     * Returns:
     *   1 if the player has guessed all letters (win),
     *  -1 if wrong guesses exceed limit (lose),
     *   0 otherwise (game in progress).
     */
    public int gameState() {
        boolean allGuessed = true;
        for (char c : targetWord.toCharArray()) {
            if (!guessedChar.contains(c)) {
                allGuessed = false;
                break;
            }
        }
        if (allGuessed) {
            return 1;
        }
        if (wrongGuesses >= MAX_GUESSES) {
            return -1;
        }
        return 0; // game is still going on
    }

    public String getObfuscatedWord() {
        StringBuilder sb = new StringBuilder();
        for (char c : targetWord.toCharArray()) {
            if (guessedChar.contains(c)) {
                sb.append(c);
            } else {
                sb.append('_');
            }
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public int getWrongGuesses() {
        return this.wrongGuesses;
    }

    public int getRemainingGuesses() {
        return MAX_GUESSES - this.wrongGuesses;
    }

    public boolean hasGuessed(char c) {
        return guessedChar.contains(Character.toUpperCase(c));
    }
}


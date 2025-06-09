import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Loads words from a text file
 */
public class WordBank {
    private final List<String> words = new ArrayList<>();
    private final Random rand = new Random();

    /**
     * @param difficulty corresponding to the difficulty of the level
     * @throws IOException if the file cannot be read or contains no valid words
     */
    public WordBank(String difficulty) throws IOException {
        String filename = String.format("words_%s.txt", difficulty.toLowerCase());
        // wraps a filereader (that reads characters from filepath) in a buffered reader to allow reading of each line at a time
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            // each call of reader.readLine() returns the next line of text
            while ((line = reader.readLine()) != null) {
                line = line.strip();
                if (!line.isEmpty()) {
                    words.add(line.toUpperCase());
                }
            }
        }
        if (words.isEmpty()) {
            throw new IOException("Word list is empty or not found: " + filename);
        }
    }

    /**
     * Returns a randomly selected word
     */
    public String getRandomWord() {
        return words.get(rand.nextInt(words.size()));
    }
}

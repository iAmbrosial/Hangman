import java.io.*;
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
     * @param filePath path to the word list file (e.g., "words.txt")
     * @throws IOException if the file cannot be read or contains no valid words
     */
    public WordBank(String filePath) throws IOException {
        // wraps a filereader (that reads characters from filepath) in a buffered reader to allow reading of each line at a time
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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
            throw new IOException("Word list is empty or not found: " + filePath);
        }
    }

    /**
     * Returns a randomly selected word
     */
    public String getRandomWord() {
        return words.get(rand.nextInt(words.size()));
    }
}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hangman {
    private final List<Player> players;
    private Player currentPlayer;
    private WordBank wordBank;
    private GameSession session;
    private final HangmanGUI view;
    // Define available difficulties in a mutable list
    private final List<String> difficulties = new ArrayList<>(
            Arrays.asList("Easy", "Medium", "Hard")
    );

    public Hangman(HangmanGUI view) throws Exception {
        players = DataStore.loadPlayers();
        this.view = view;
        view.setController(this);
    }

    public static void main(String[] args) throws Exception {
        HangmanGUI gui = new HangmanGUI();
        Hangman ctrl = new Hangman(gui);
        gui.setVisible(true);
    }

    public void login(String user, String pass) {
        for (Player p: players) {
            if (p.getUsername().equals(user) && p.checkPassword(pass)) {
                currentPlayer = p;
                view.showOptionsScreen(difficulties);
                return;
            }
        }
        Player p = new Player(user, pass);
        players.add(p);
        currentPlayer = p;
        DataStore.savePlayers((ArrayList<Player>)players);
        view.showOptionsScreen(difficulties);
    }

    public void startNewGame(String diff) {
        try {
            wordBank = new WordBank(diff);
            session = new GameSession(wordBank.getRandomWord());
            view.resetGameUI(diff);
        } catch (Exception e) {
            view.showError("Failed to load words for difficulty: " + diff);
        }
    }

    public void handleGuess(char c, String diff) {
        if (!Character.isLetter(c)) { view.showError("Please enter A-Z only."); return; }
        if (session.hasGuessed(c)) { view.showError("You already tried '"+c+"'."); return; }
        boolean correct = session.guess(c);
        if (!correct) view.drawNextHangmanPart(session.getWrongGuesses());
        view.updateGameView(session);
        int state = session.gameState();
        if (state != 0) {
            int score;
            if (state == 1) {
                score = session.getRemainingGuesses() * 10;
            } else {
                score = 0;
            }
            currentPlayer.recordScore(diff, score);
            DataStore.savePlayers((ArrayList<Player>)players);
            view.showEndOptions(state==1, score);
        }
    }

    /**
     * Retrieve the high score for the current player at the given difficulty.
     * This allows the GUI to display high scores without accessing private fields directly.
     */
    public int getHighScore(String difficulty) {
        return currentPlayer.getHighScore(difficulty);
    }

    public List<Integer> getHistory() { return currentPlayer.getScoreHistory(); }
}
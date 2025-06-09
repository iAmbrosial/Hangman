import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hangman {
    private final List<Player> players;
    private Player currentPlayer;
    private WordBank wordBank;
    private GameSession session;
    // sets the viewing panel for the game
    private final HangmanGUI view;
    // Define available difficulties in a mutable list
    private final List<String> difficulties = new ArrayList<>(
            Arrays.asList("Easy", "Medium", "Hard")
    );

    /**
     * @param view frame that the all Hangman functions will be accessed
     * @note loads players for persistence
     */
    public Hangman(HangmanGUI view) throws Exception {
        players = DataStore.loadPlayers();
        this.view = view;
        // sets the current instance of the class to be the controller for the JFrame
        view.setController(this);
    }

    /**
     * sets up the game panel GUI and the controller for the JFrame
     */
    public static void main(String[] args) throws Exception {
        HangmanGUI gui = new HangmanGUI();
        Hangman ctrl = new Hangman(gui);
        // makes the JFrame GUI visible
        gui.setVisible(true);
    }

    /**
     * @param user associated with the login
     * @param pass the password of the user
     * @note merges login and signup into one functionality to reduce the amount of Frames that must be created
     */
    public void login(String user, String pass) {
        for (Player p: players) {
            if (p.getUsername().equals(user) && p.checkPassword(pass)) {
                currentPlayer = p;
                // shows the choosing difficulties screen after login
                view.showOptionsScreen(difficulties);
                return;
            }
        }
        Player p = new Player(user, pass);
        players.add(p);
        currentPlayer = p;
        // passes players to be stored as an arraylist
        // may not matter but may be best practice? unsure
        DataStore.savePlayers((ArrayList<Player>)players);
        // shows the choosing difficulties screen after signup
        view.showOptionsScreen(difficulties);
    }

    /**
     * @param diff is the difficulty of the new game started
     */
    public void startNewGame(String diff) {
        try {
            wordBank = new WordBank(diff);
            // creates a game session with a random word from the specified difficulty wordbank
            session = new GameSession(wordBank.getRandomWord());
            // resets game UI
            view.resetGameUI(diff);
        } catch (Exception e) {
            // prints the specified error messsage
            view.showError("Failed to load words for difficulty: " + diff);
        }
    }

    /**
     * @param c is the character that is being guessed
     * @param diff is the String representing the difficulty of the round
     * @note merges login and signup into one functionality to reduce the amount of Frames that must be created
     */
    public void handleGuess(char c, String diff) {
        if (!Character.isLetter(c)) {
            view.showError("Please enter A-Z only.");
            // done so the guess panel can be closed after the failed guess and can be rerun
            return;
        }
        if (session.hasGuessed(c)) {
            view.showError("You already tried '" + c + "'.");
            // done so the guess panel can be closed after the failed guess and can be rerun
            return;
        }

        // passes the guessed character to the game session
        boolean correct = session.guess(c);
        if (!correct) {
            // draws parts of the hangman based on how many wrong guesses have been made
            view.drawNextHangmanPart(session.getWrongGuesses());
        }
        view.updateGameView(session);
        int state = session.gameState();
        // 1 is for a win, -1 is failed, and 0 means that the game is ongoing
        // if the game is finished check these conditions
        if (state != 0) {
            int score;
            if (state == 1) {
                // once the game is won calculate a score based on how many guesses the user
                // has remaining multiplied by 10 to look nicer
                score = session.getRemainingGuesses() * 10;
            } else {
                // in this case the game has ended as the state could only otherwise be -1, and so the user gets a score of 0
                score = 0;
            }
            // records the score for the specified difficulty
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

    // list of past scores of the player
    public List<Integer> getHistory() { return currentPlayer.getScoreHistory(); }
}
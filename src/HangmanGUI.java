import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * HangmanGUI: the Swing-based view for the Hangman game.
 * Uses CardLayout to switch between login, options, and game panels.
 */
public class HangmanGUI extends JFrame {
    // Reference to the controller to delegate user actions
    private Hangman controller;

    // CardLayout allows stacking multiple panels and showing one at a time
    private CardLayout cards = new CardLayout();
    private JPanel mainPanel = new JPanel(cards);

    // Individual panels for different screens
    private JPanel loginPanel = new JPanel();      // login screen
    private JPanel optionsPanel = new JPanel();    // difficulty selection screen
    private JPanel gamePanel = new JPanel(new BorderLayout());  // actual game play screen

    // Components for login screen
    private JTextField userField = new JTextField(15);         // text field for username input
    private JPasswordField passField = new JPasswordField(15); // field for password input

    // Components for options screen
    private JComboBox<String> difficultyBox;   // dropdown for selecting difficulty
    private JButton historyBtn = new JButton("View History"); // button to see score history

    // Components for game screen
    private JLabel wordLabel = new JLabel();         // displays the masked word (underscores and letters)
    private JLabel livesLabel = new JLabel();        // displays remaining lives
    private JLabel scoreLabel = new JLabel();        // shows high score for current difficulty
    private JTextField guessField = new JTextField(1); // single-character text field for user guess
    private JButton guessButton = new JButton("Guess"); // button to submit the guess
    private HangmanCanvas canvas = new HangmanCanvas(); // custom JPanel to draw the stickman figure

    // Tracks the difficulty of the current game round
    private String currentDifficulty;

    /**
     * Constructor: sets up the JFrame and initializes all other panels.
     */
    public HangmanGUI() {
        super("Hangman Game");  // set window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // exit app when window closes
        setSize(800,600);  // initial size of the window
        setLocationRelativeTo(null);  // center window on screen

        initLoginPanel();   // configure loginPanel
        // Note: optionsPanel is initialized dynamically via showOptionsScreen()
        initGamePanel();    // configure gamePanel


        // Add panels to mainPanel with string keys for CardLayout
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(optionsPanel, "OPTIONS");
        mainPanel.add(gamePanel, "GAME");
        add(mainPanel);  // add main panel to the frame container

        // Show the login screen first
        cards.show(mainPanel, "LOGIN");
    }

    /**
     * Setter for controller reference. Makes up the interaction section of the JFrame.
     */
    public void setController(Hangman c) { controller = c; }

    // -----------------------------------------
    // LOGIN PANEL INITIALIZATION
    // -----------------------------------------
    private void initLoginPanel() {
        // Use GridBagLayout for flexible grid-based positioning
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5); // padding around components

        // Row 0, col 0: "Username:" label
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        // Row 0, col 1: text field for username
        gbc.gridx = 1;
        loginPanel.add(userField, gbc);

        // Row 1, col 0: "Password:" label
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        // Row 1, col 1: password field
        gbc.gridx = 1;
        loginPanel.add(passField, gbc);

        // Row 2, spans two columns: login/signup button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login/Signup");
        // When clicked, delegate to controller.login
        loginBtn.addActionListener(e -> controller.login(
                userField.getText(), new String(passField.getPassword())));
        loginPanel.add(loginBtn, gbc);
    }

    // -----------------------------------------
    // OPTIONS PANEL
    // -----------------------------------------
    /**
     * Display the difficulty selection screen.
     * Called by controller after successful login/signup.
     */
    public void showOptionsScreen(List<String> diffs) {
        optionsPanel.removeAll();            // clear old components if any
        optionsPanel.setLayout(new FlowLayout()); // simple left-to-right flow
        optionsPanel.add(new JLabel("Difficulty:"));

        // Populate difficulty dropdown
        difficultyBox = new JComboBox<>(diffs.toArray(new String[0]));
        optionsPanel.add(difficultyBox);

        // Start Game button
        JButton startBtn = new JButton("Start Game");
        startBtn.addActionListener(e -> {
            currentDifficulty = (String)difficultyBox.getSelectedItem();
            controller.startNewGame(currentDifficulty);
        });
        optionsPanel.add(startBtn);

        // History button shows a dialog listing past scores
        historyBtn.addActionListener(e -> showHistoryDialog());
        optionsPanel.add(historyBtn);

        // Switch to OPTIONS card
        cards.show(mainPanel, "OPTIONS");
    }

    /**
     * Show score history dialog.
     * Uses JList inside a JScrollPane for readability.
     */
    private void showHistoryDialog() {
        List<Integer> hist = controller.getHistory();
        JList<Integer> list = new JList<>(hist.toArray(new Integer[0]));
        JOptionPane.showMessageDialog(this,
                new JScrollPane(list),
                "Score History",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // -----------------------------------------
    // GAME PANEL INITIALIZATION
    // -----------------------------------------
    private void initGamePanel() {
        // Top area shows word, lives, and high score in a row
        JPanel top = new JPanel(new GridLayout(1,3));
        top.add(wordLabel);
        top.add(livesLabel);
        top.add(scoreLabel);
        gamePanel.add(top, BorderLayout.NORTH);

        // Center area is custom canvas for drawing hangman figure
        gamePanel.add(canvas, BorderLayout.CENTER);

        // Bottom area has guess input and button
        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Guess:"));
        bottom.add(guessField);
        bottom.add(guessButton);
        guessButton.addActionListener(e -> {
            String t = guessField.getText().toUpperCase();
            guessField.setText("");  // clear after reading
            if (!t.isEmpty()) controller.handleGuess(t.charAt(0), currentDifficulty);
        });
        gamePanel.add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Reset UI to start a fresh round: clear canvas, enable inputs,
     * reset labels and switch to GAME screen.
     */
    public void resetGameUI(String diff) {
        currentDifficulty = diff;
        canvas.setStage(0);               // clear stickman drawing
        guessField.setEnabled(true);
        guessButton.setEnabled(true);
        wordLabel.setText("Word: ");    // blank until we update
        livesLabel.setText("Lives: ");
        scoreLabel.setText("HighScore("+diff+"): "
                + controller.getHighScore(diff));
        cards.show(mainPanel, "GAME");
    }

    /**
     * Update masked word and lives after each guess.
     */
    public void updateGameView(GameSession s) {
        wordLabel.setText("Word: " + s.getObfuscatedWord());
        livesLabel.setText("Lives: " + s.getRemainingGuesses());
    }

    /**
     * Advance the stickman drawing based on number of wrong guesses.
     */
    public void drawNextHangmanPart(int wrong) {
        canvas.setStage(wrong);
        canvas.repaint();
    }

    /**
     * Show end-of-round options: either play again or return to menu.
     */
    public void showEndOptions(boolean win, int score) {
        String msg;
        if (win) {
            msg = "You Win! Score: " + score;
        } else {
            msg = "Game Over!";
        }
        Object[] options = {"Play Again","Main Menu"};
        int choice = JOptionPane.showOptionDialog(
                this, msg, "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]
        );
        // disable inputs until user chooses next action
        guessField.setEnabled(false);
        guessButton.setEnabled(false);
        // route based on choice
        if (choice == 0) controller.startNewGame(currentDifficulty);
        else showOptionsScreen(List.of("Easy","Medium","Hard"));
    }

    /**
     * Show a simple error dialog with given message.
     */
    public void showError(String m) {
        JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
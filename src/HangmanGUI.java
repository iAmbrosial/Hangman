import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;

/**
 * HangmanGUI: the Swing-based view for the Hangman game.
 * Uses CardLayout to switch between login, options, and game panels.
 */
public class HangmanGUI extends JFrame {
    // Reference to the controller to delegate user actions
    private Hangman controller;

    // CardLayout allows stacking multiple panels and shows one at a time
    // it manages JPanel instances that can be switched between
    private CardLayout cards = new CardLayout();
    // the CardLayout cards is the LayoutManager for the main JPanel
    // this allows different cards (panels) to be shown at different times, instead of side to side like in FlowLayout
    private JPanel mainPanel = new JPanel(cards);

    // Individual panels for different screens
    private JPanel loginPanel = new JPanel();
    private JPanel optionsPanel = new JPanel();
    // BorderLayout allows elements to be placed in five areas
    private JPanel gamePanel = new JPanel(new BorderLayout());

    // Components for login screen
    // text field for username input with max 15 chars
    private JTextField userField = new JTextField(15);
    // field for password input with max 15 chars
    private JPasswordField passField = new JPasswordField(15);

    // Components for options screen
    // JComboBox creates a dropdown menu for selecting difficulty
    private JComboBox<String> difficultyBox;
    // button to see score history
    private JButton historyBtn = new JButton("View History");

    // Components for game screen
    // displays the masked word (underscores and letters)
    private JLabel wordLabel = new JLabel();
    // displayed guessed letters
    private JLabel guessedChars = new JLabel();
    // displays remaining lives
    private JLabel livesLabel = new JLabel();
    // shows high score for current difficulty
    private JLabel scoreLabel = new JLabel();
    // single-character text field for user guess
    private JTextField guessField = new JTextField(1);
    // button to submit the guess
    private JButton guessButton = new JButton("Guess");
    // custom JPanel to draw the stickman figure
    private HangmanCanvas canvas = new HangmanCanvas();

    // tracks the difficulty of the current game round
    private String currentDifficulty;

    /**
     * constructor to set up the JFrame and initialize all other panels
     */
    public HangmanGUI() {
        super("Hangman Game");
        // exit app when window closes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // initial size of the window
        setSize(800,600);
        // center window on screen by making it relative to null
        setLocationRelativeTo(null);

        // configures/initializes the different panels
        initLoginPanel();
        // the optionsPanel is initialized in showOptionsScreen() when necessary
        initGamePanel();


        // adds panels to mainPanel with a String constant that represents each panel for the CardLayout
        // these names are used to access the panels
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(optionsPanel, "OPTIONS");
        mainPanel.add(gamePanel, "GAME");
        // add main panel to the frame container
        add(mainPanel);

        // Show the login screen first
        cards.show(mainPanel, "LOGIN");
    }

    /**
     * sets the controller that handles interaction with the JFrame
     */
    public void setController(Hangman c) {
        controller = c;
    }

    /**
     * initializes the login panel
     */
    private void initLoginPanel() {
        // use GridBagLayout for flexible grid-based positioning
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        // specifies the minimum padding between components
        gbc.insets = new Insets(5,5,5,5);

        // set the username label at Row 0 and col 0
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        // set the empty text field for the username at Row 0 and col 1
        gbc.gridx = 1;
        loginPanel.add(userField, gbc);

        // set the password label at Row 1 and col 0
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        // set the empty text field for the password at Row 1 and col 1
        gbc.gridx = 1;
        loginPanel.add(passField, gbc);

        // sets row two to span two columns for the login/signup button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login/Signup");

        // adds the action to the login button
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // reads the username and password text entries,
                String user = userField.getText();
                String pass = new String(passField.getPassword());
                // calls the login method from the controller to login/signup a new Player using those parameters
                controller.login(user, pass);
                // switches to the difficulty selection UI panel
            }
        });
        loginPanel.add(loginBtn, gbc);
    }

    /**
     * displays the difficulty selection screen
     * it is called by controller after successful a login/signup
     */
    public void showOptionsScreen(List<String> difficulties) {
        // clear any old components
        optionsPanel.removeAll();
        // arranges components from left to right
        optionsPanel.setLayout(new FlowLayout());
        optionsPanel.add(new JLabel("Difficulty:"));

        // add options to the difficulty dropdown
        // an Array is returned to allow the difficulty selection to be mutable
        // the new String[0] helps the JComboBox constructor to choose the right array type
        difficultyBox = new JComboBox<>(difficulties.toArray(new String[0]));
        optionsPanel.add(difficultyBox);

        // Start Game button
        JButton startBtn = new JButton("Start Game");
        // adds action to the start button
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // uses the difficulty chosen by the user and saves it to currentDifficulty
                currentDifficulty = (String) difficultyBox.getSelectedItem();
                // makes the controller start a new game session using the difficulty chosen
                controller.startNewGame(currentDifficulty);
            }
        });
        optionsPanel.add(startBtn);

        // History button shows a dialog listing past scores
        historyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // shows the list of the past scores
                showHistoryDialog();
            }
        });
        optionsPanel.add(historyBtn);

        // switch to the options card (panel) in the parent main panel
        cards.show(mainPanel, "OPTIONS");
    }

    /**
     * shows the score history dialog
     * @note uses a JList inside a JScrollPane for readability
     */
    private void showHistoryDialog() {
        // returns a score history from the game controller
        List<Integer> history = controller.getHistory();
        // an Array is returned to allow the difficulty selection to be mutable
        // the new Integer[0] helps the JComboBox constructor to choose the right array type
        JList<Integer> list = new JList<>(history.toArray(new Integer[0]));
        // this refers to the instance of the HangmanGUI Frame where JScrollPane creates a scrollable
        // view of the score history list and the message type is INFORMATION_MESSAGE
        JOptionPane.showMessageDialog(this,
                new JScrollPane(list),
                "Score History",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * initializes the main game panel for Hangman
     * @note the BorderLayout LayoutManager is used to arrange the display elements
     */
    private void initGamePanel() {
        // the top area shows word, lives, and high score in a row
        // the GridLayout makes a simple grid of cells that are all the same size
        JPanel top = new JPanel(new GridLayout(1,3));
        top.add(wordLabel);
        top.add(livesLabel);
        top.add(scoreLabel);
        top.add(guessedChars);
        // add the new panel to the top of the game panel in the north area
        gamePanel.add(top, BorderLayout.NORTH);

        // center area is used for the custom canvas where the hangman figure is drawn
        gamePanel.add(canvas, BorderLayout.CENTER);

        // the bottom area holds the guess input and button
        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Guess:"));
        bottom.add(guessField);
        bottom.add(guessButton);
        // adds action to the guess button
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // takes in the single character the user wants to guess
                String t = guessField.getText().toUpperCase();
                // clears the input field
                guessField.setText("");
                if (!t.isEmpty()) {
                    // updates the game session model
                    // redraws the stickman if applicable, updates the masked word and lives labels,
                    // and handles win/loss dialogue
                    controller.handleGuess(t.charAt(0), currentDifficulty);
                }
            }
        });
        gamePanel.add(bottom, BorderLayout.SOUTH);
    }

    /**
     * reset UI to start a fresh round by clearing the canvas, enabling inputs,
     * resetting labels and switching to the GAME screen
     */
    public void resetGameUI(String diff) {
        currentDifficulty = diff;
        // clear the stickman drawing as 0 represents no wrong guesses, so nothing is drawn
        canvas.setStage(0);
        // enables the text field
        guessField.setEnabled(true);
        // enables the button
        guessButton.setEnabled(true);
        // both are blank until the user makes a guess
        wordLabel.setText("Word: ");
        livesLabel.setText("Lives: ");
        // gets the high score of the user from the Hangman controller class and prints it
        scoreLabel.setText("HighScore("+diff+"): "
                + controller.getHighScore(diff));
        guessedChars.setText("Guessed Letters: ");
        // shows the game card (panel) from the parent panel
        cards.show(mainPanel, "GAME");
    }

    /**
     * update masked word and lives after each guess
     */
    public void updateGameView(GameSession s) {
        wordLabel.setText("Word: " + s.getObfuscatedWord());
        livesLabel.setText("Lives: " + s.getRemainingGuesses());
        guessedChars.setText("Guessed Letters: " + s.getGuessedChars());
    }

    /**
     * advance the stickman drawing based on number of wrong guesses
     */
    public void drawNextHangmanPart(int wrong) {
        // sets the stage of the Hangman in the canvas instance according to how many wrong guesses have been made
        canvas.setStage(wrong);
        // called to update the canvas
        canvas.repaint();
    }

    /**
     * show end-of-round options: either play again or return to menu
     */
    public void showEndOptions(boolean win, int score) {
        String msg;
        if (win) {
            msg = "You Win! Score: " + score;
        } else {
            msg = "Game Over!";
        }
        Object[] options = {"Play Again","Main Menu"};
        // brings up a dialog panel with the options Play Again and Main Menu, where the options are default options
        // that show information without an icon
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
        if (choice == 0) {
            controller.startNewGame(currentDifficulty);
        } else {
            // shorthand way of returning an unmodifiable list of difficulties to the options screen JFrame
            showOptionsScreen(List.of("Easy","Medium","Hard"));
        }
    }

    /**
     * shows a simple error dialog with given message
     */
    public void showError(String m) {
        // this refers to the instance of the HangmanGUI Frame and ERROR_MESSAGE is a message type in the JFrame class
        JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
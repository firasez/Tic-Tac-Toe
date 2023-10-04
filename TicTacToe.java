import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.net.URL;



/**
 * This Tic Tac Toe class is a TicTacToe GUI based game
 * that allows the user to initiate a game of Tic Tac Toe.
 *
 * It uses java's Swing GUI so that the user can
 * actually interact and see what goes on.
 *
 * NOTE: I HAVE IMPLEMENTED A DIFFERENT LOGIC THAN WHAT
 * IS USED IN LAB10. (Permission granted from both profs and no marks should be deducted.)
 * This entire Tic Tac Toe class was written from scratch.
 * 
 * Source for Images: https://www.flaticon.com/search?word=x
 *
 * @author Firas El-Ezzi (101239531)
 * @version April 4, 2023
 */
public class TicTacToe extends MouseAdapter implements ActionListener {

    private final Random random = new Random();
    private final JFrame frame = new JFrame();
    private final JPanel titlePanel = new JPanel();
    private final JPanel buttonPanel = new JPanel();
    private final JLabel textField = new JLabel();

    //Score Labels to Keep Track Of PLayer Scores
    private final JLabel xScoreLabel = new JLabel("X Score: 0");
    private final JLabel oScoreLabel = new JLabel("O Score: 0");
    private final JLabel tieScoreLabel = new JLabel("Tie: 0");

    // A 2D Array of 9 buttons
    private final JButton[] buttons = new JButton[9];
    
    //Menu Items
    private JMenuItem reset = new JMenuItem("New");
    private JMenuItem scoreReset = new JMenuItem("Score Reset");
    
    //LOAD IMAGES and Resizing them.
    private static final ImageIcon X_IMAGE = new ImageIcon("xImage.png");
    private static Image image1 = X_IMAGE.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
    private static final ImageIcon xImage = new ImageIcon(image1);

    private static final ImageIcon O_IMAGE = new ImageIcon("oImage.png");
    private static Image image2 = O_IMAGE.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
    private static final ImageIcon oImage = new ImageIcon(image2);

    private static final ImageIcon BLANKk = new ImageIcon("blank.jpg");
    private static Image image3 = BLANKk.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
    private static final ImageIcon BLANK = new ImageIcon(image3);


    //Variables and boolean to keep track of stuff.
    private boolean player1Turn;
    private int xScore = 0;
    private int oScore = 0;
    private int tieScore = 0;
    private int numSquares;
    private boolean tie;
    private boolean winner;
    
    //For audio clips
    private Clip clip, clip2,clip3; 
    
    //For Menu ShortCuts.
    final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

    /**
     * This TicTacToe method is the contructor that builds everything together
     *
     */
    public TicTacToe() {
        buildFrame();
        buildTitlePanel();
        buildButtonPanel();
        buildMenuBar();
        firstTurn();
        buildAudioClips();

        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 800);

    }

    /**
     * buildFrame method builds the entire frame of the game
     */
    private void buildFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setLayout(new BorderLayout());
    }

    /**
     * Loads and builds all audio files.
     *
     */
    private void buildAudioClips(){
        try {
            URL url = this.getClass().getResource("click.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            URL url = this.getClass().getResource("winSound.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip2 = AudioSystem.getClip();
            clip2.open(audioInputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            URL url = this.getClass().getResource("tieSound.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip3 = AudioSystem.getClip();
            clip3.open(audioInputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * buildTItlePanel builds the panel that displays the title, scores
     * and whose turn it currently is
     */
    private void buildTitlePanel() {
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBounds(0, 0, 800, 100);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(1, 2));
        scorePanel.add(xScoreLabel);
        scorePanel.add(oScoreLabel);
        scorePanel.add(tieScoreLabel);

        textField.setBackground(new Color(25, 25, 25));
        textField.setForeground(new Color(0, 50, 250));
        textField.setFont(new Font("", Font.BOLD, 40));
        textField.setHorizontalAlignment(JLabel.CENTER);
        textField.setText("Tic-Tac-Toe");
        textField.setOpaque(true);

        titlePanel.add(textField, BorderLayout.CENTER);
        titlePanel.add(scorePanel, BorderLayout.SOUTH);

        frame.add(titlePanel, BorderLayout.NORTH);
    }

    /**
     * buildButtonPanel builds a 3x3 grid for the button
     */
    private void buildButtonPanel() {
        buttonPanel.setLayout(new GridLayout(3, 3));
        buttonPanel.setBackground(new Color(150, 150, 150));

        //Initialize the buttons
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttonPanel.add(buttons[i]);
            buttons[i].setFont(new Font("", Font.BOLD, 120));
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this);
            buttons[i].setIcon(BLANK);
            //buttons[i].setDisabledIcon(BLANK);
        }
        numSquares = 9;
        tie= false;
        winner = false;
        frame.add(buttonPanel);
    }

    /**
     * buildMenuBar builds the little menu secton on the top left
     * which has 3 options
     * "Reset", which initiates a new game.
     * "Quit", which closes the entire game
     * "Reset Score", which resets the game and the score.
     */
    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menu.addMouseListener(this);
        JMenuItem reset = new JMenuItem("New");
        JMenuItem quit = new JMenuItem("Quit");
        JMenuItem scoreReset = new JMenuItem("Reset Score");

        // Add action listeners for the menu items
        reset.addActionListener(e -> {
            resetGame();
        });
        quit.addActionListener(e -> {
            System.exit(0);
        });
        scoreReset.addActionListener(e -> {
            resetScore();
        });
        
        reset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        scoreReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_MASK));

        // Add menu items to the menu bar
        menu.add(reset);
        menu.add(quit);
        menu.add(scoreReset);

        // Add the menu bar to the frame
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
    }


    /**
     * actionPerformed method deals with all the user's actions.
     * It takes care of the buttons pressed and
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        for (int i = 0; i < 9; i++) {
            if (e.getSource() == buttons[i]) {
                if (player1Turn) {
                    if (buttons[i].getIcon().equals(BLANK)) {

                        clip.setFramePosition(0); //Sound
                        clip.start();

                        buttons[i].setForeground(new Color(255, 0, 0));

                        buttons[i].setIcon(xImage);
                        buttons[i].setDisabledIcon(xImage);
                        buttons[i].setEnabled(false);
                        player1Turn = false;
                        numSquares --;
                        textField.setText("O turn");
                        textField.setForeground(new Color(0,0,255));
                        winCheck();

                    }
                }
                else {
                    clip.setFramePosition(0); // Sound
                    clip.start();

                    buttons[i].setForeground(new Color(0, 0, 255));
                    buttons[i].setIcon(oImage);
                    buttons[i].setDisabledIcon(oImage);
                    numSquares --;
                    buttons[i].setEnabled(false);
                    player1Turn = true;
                    textField.setText("X turn");
                    textField.setForeground(new Color(255,0,0));
                    winCheck();

                }
                tieCheck();
            }
            if (e.getSource() == reset) {
                resetGame();
            }
            if(e.getSource() == scoreReset){
                resetScore();
            }

        }
    }

    /**
     * Chooses a random player(O or X) to start at the beginning
     * of the game
     */
    private void firstTurn() {
        player1Turn = random.nextInt(2) == 0;
        textField.setText("Tic Tac Toe: " + (player1Turn ? "X" : "O")+" turn");
    }

    /**
     * checks the 3 buttons (index a,b and c) if they are the all equal
     * @param a index
     * @param b index
     * @param c index
     * @return boolean, True if all 3 buttons are the same, false if else.
     */
    private boolean checkButtons(int a, int b, int c) {
        ImageIcon icon1 = (ImageIcon) buttons[a].getIcon();
        ImageIcon icon2 = (ImageIcon) buttons[b].getIcon();
        ImageIcon icon3 = (ImageIcon) buttons[c].getIcon();
        
        return (!icon1.equals(BLANK) && icon1.equals(icon2) && icon2.equals(icon3));
    }

    /**
     * xWins method lets the user know that X has won and change X's score
     * 
     */
    public void xWins() {

        clip2.setFramePosition(0); //Sound
        clip2.start();

        for (int i = 0; i < 9; i++) {
            
            if(!buttons[i].getIcon().equals(xImage) &&  !buttons[i].getIcon().equals(oImage)){
                buttons[i].setIcon(null);
                buttons[i].setEnabled(false);
            }
        }
        textField.setText("X wins");
        textField.setForeground(Color.GREEN);
        winner = true;
        xScore++;
        updateScore();
        xScoreLabel.setText("X Score: " + xScore);
        oScoreLabel.setText("O Score: " + oScore);
        tieScoreLabel.setText("Tie: "+ tieScore);
    }

    /**
     * oWins method lets the user know that O has won, and change O's score
     *
     */
    public void oWins() {
        clip2.setFramePosition(0); //Sound
        clip2.start();

        for (int i = 0; i < 9; i++) {
            if(!buttons[i].getIcon().equals(xImage) && ! buttons[i].getIcon().equals(oImage)){
                buttons[i].setIcon(null);
                buttons[i].setEnabled(false);
            }
        }
        textField.setText("O wins");
        textField.setForeground(Color.GREEN);
        winner = true;
        oScore++;
        updateScore();
        xScoreLabel.setText("X Score: " + xScore);
        oScoreLabel.setText("O Score: " + oScore);
        tieScoreLabel.setText("Tie: "+ tieScore);
    }

    /**
     * Checks for all winning combinations
     * If none, then its a tie.
     *
     */

    public void winCheck() {

        // Check Horizontally
        if (checkButtons(0, 1, 2)) {
            if (buttons[0].getIcon().equals(xImage)) {
                xWins();
            } else {
                oWins();
            }
        }
        if (checkButtons(3, 4, 5)) {
            if (buttons[3].getIcon().equals(xImage)) {
                xWins();
            } else {
                oWins();
            }
        }
        if (checkButtons(6, 7, 8)) {
            if (buttons[6].getIcon().equals(xImage)) {
                xWins();
            } else {
                oWins();
            }
        }

        // Check Vertically
        if (checkButtons(0, 3, 6)) {
            if (buttons[0].getIcon().equals(xImage)) {
                xWins();
            } else {
                oWins();
            }
        }
        if (checkButtons(1, 4, 7)) {
            if (buttons[1].getIcon().equals(xImage)) {
                xWins();
            } else {
                oWins();
            }
        }
        if (checkButtons(2, 5, 8)) {
            if (buttons[2].getIcon().equals(xImage)) {
                xWins();
            } else {
                oWins();
            }
        }

        // Check Diagonals
        if (checkButtons(0, 4, 8)) {
            if (buttons[0].getIcon().equals(xImage)) {
                xWins();
            } else {
                oWins();
            }
        }
        if (checkButtons(2, 4, 6)) {
            if (buttons[2].getIcon().equals(xImage)) {
                xWins();
            } else {
                oWins();
            }
        }
        else if(! winner && numSquares == 0){
            tie = true;
        }

    }
    /**
     * tieCheck checks if the game is a tie.
     * Displays the necessary info for a tied game if true.
     */
    private void tieCheck(){
        
        if (tie && !winner ) {

            clip3.setFramePosition(0); //Sound
            clip3.start();
            for (int i = 0; i < 9; i++) {
                buttons[i].setEnabled(false);
            }
            textField.setText("Tie!");
            tieScore++;
            xScoreLabel.setText("X Score: " + xScore);
            oScoreLabel.setText("O Score: " + oScore);
            tieScoreLabel.setText("Tie: "+ tieScore);
        }


    }
    /**
     * updateScore updates the score after every win or event in the game.
     */

    private void updateScore(){
        xScoreLabel.setText("X Score: " + xScore);
        oScoreLabel.setText("O Score: " + oScore);
        tieScoreLabel.setText("Tie: "+ tieScore);

    }
    /**
     * resetGame is part of the menu item, which starts a new game
     * without resetting the score.
     */
    private void resetGame() {
        // Reset buttons
        for (int i = 0; i < 9; i++) {
            buttons[i].setEnabled(true);
            buttons[i].setIcon(BLANK);
            buttons[i].setDisabledIcon(BLANK);
            buttons[i].setBackground(new JButton().getBackground());
        }
        
        if(player1Turn){
        textField.setForeground(Color.RED);
        }
        textField.setForeground(Color.BLUE);
        numSquares = 9;
        tie = false;
        winner = false;
        firstTurn();
        xScoreLabel.setText("X Score: " + xScore);
        oScoreLabel.setText("O Score: " + oScore);
        tieScoreLabel.setText("Tie: "+ tieScore);
    }
    /**
     * resetGScore is part of the menu item, which starts a new game
     * and resets the scores back to 0.
     */
    private void resetScore(){
        resetGame();
        xScore = 0;
        oScore = 0;
        tieScore = 0;
        xScoreLabel.setText("X Score: " + xScore);
        oScoreLabel.setText("O Score: " + oScore);
        tieScoreLabel.setText("Tie: "+ tieScore);
    
    }
    /**
     * mouseEntered highlights the menu bar when user hovers over
     * @param e the event to be processed
     * 
     * from video by lynn
     */

    public void mouseEntered(MouseEvent e){
        JMenu item =(JMenu) e.getSource();
        item.setSelected(true);
    }
    /**
     * mouseExited removed the highlight off the menu bar
     * when user hovers away from it
     * @param e the event to be processed
     * 
     * from video by lynn
     */
    public void mouseExited(MouseEvent e){
        JMenu item =(JMenu) e.getSource();
        item.setSelected(false);
    }
}
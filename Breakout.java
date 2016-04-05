import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class Breakout extends JFrame{

    public static final double ratioDPI = Toolkit.getDefaultToolkit().getScreenResolution() / Constants.DEFAULT_DPI;

    // PUBLIC

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                double redraw = args.length >= 1 ? Double.parseDouble(args[0]) : Constants.DEFAULT_REDRAW;
                double speed = args.length >= 2 ? Double.parseDouble(args[1]) : Constants.DEFAULT_SPEED;
                Breakout game = new Breakout(redraw, speed);
            }
        });
    }

    // PRIVATE

    private JButton btnStart;
    private JTextArea description;
    private JTextArea title;
    private KeyAdapter gameKeyListener;
    private JPanel pnlSplash;
    private JPanel pnlGame;
    private double redrawRate;
    private double speed;

    private Breakout(double redraw, double speed) {
        this.redrawRate = redraw;
        this.speed = speed;
        this.setLocationByPlatform(true);
        this.setResizable(true);
        initUI();
    }

    private void initUI() {
        this.setTitle("Breakout");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension((int)(Constants.INITIAL_WIDTH * Breakout.ratioDPI), (int)(Constants.INITIAL_HEIGHT * Breakout.ratioDPI)));
        this.setMinimumSize(new Dimension((int)((Constants.INITIAL_WIDTH / 2) * Breakout.ratioDPI), (int)((Constants.INITIAL_HEIGHT / 2) * Breakout.ratioDPI)));

        Font menuFont = (Font)UIManager.get("Menu.font");
        Font f = new Font(menuFont.getFontName(), menuFont.getStyle(), (int)(menuFont.getSize() * Breakout.ratioDPI));
        Color color = new Color(240, 240, 240);
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while(keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if(value != null && value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
            if(value != null && value instanceof javax.swing.plaf.ColorUIResource && key.toString().contains(".background")) {
                UIManager.put(key, color);
            }
        }

        Action startAction = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                startGame();
            }
        };

        pnlSplash = new JPanel(new GridBagLayout());
        pnlSplash.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "start");
        pnlSplash.getActionMap().put("start", startAction);

        GridBagConstraints c = new GridBagConstraints();
        btnStart = new JButton();
        btnStart.setAction(startAction);
        btnStart.setText("Click to Start");
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 4;
        c.gridheight = 2;
        pnlSplash.add(btnStart, c);
        title = new JTextArea("BREAKOUT!");
        title.setFont(new Font("TimesRoman", Font.BOLD, 54));
        title.setBackground(new Color(0, 0, 0, 0));
        title.setEditable(false);
        title.setHighlighter(null);
        c.gridy = 0;
        pnlSplash.add(title, c);
        description = new JTextArea("\nHow to play:\nControl the paddle with the mouse or the left and right keys on the keyboard\nThe ball will bounce off the paddle at an angle based on where it hits the paddle\nHitting a block with the ball will cause the block to disappear, giving you one point and causing the ball to bounce\nYou start with 3 lives and you lose a life if the ball reaches the bottom edge\nYou win after destroying all the blocks and you lose if lose all your lives\nPress escape to exit the game or space to pause the game\nChange the speed of the ball with the up and down keys\n");
        description.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        description.setBackground(new Color(0, 0, 0, 0));
        description.setEditable(false);
        description.setHighlighter(null);
        c.gridy = 2;
        c.gridheight = 1;
        pnlSplash.add(description, c);
        c.gridy = 10;
        c.gridheight = 2;
        pnlSplash.add(btnStart, c);

        this.add(pnlSplash);
        pnlSplash.setVisible(true);
        this.pack();
        this.setVisible(true);
    }

    private GameView initGameView(GameModel model) {
        GameView game = new GameView(this.redrawRate, model);

        Action endAction = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                endGame();
            }
        };

        pnlGame = new JPanel(new BorderLayout());
        pnlGame.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "end");
        pnlGame.getActionMap().put("end", endAction);

        pnlGame.setBackground(Color.black);
        pnlGame.add(game);
        this.add(pnlGame);
        pnlGame.setVisible(true);
        return game;
    }

    private GameModel initModel() {
        GameModel game = new GameModel(this.speed);
        game.start();
        return game;
    }

    private void clearUI() {
        pnlSplash.setVisible(false);
        this.remove(pnlSplash);
        pnlSplash.getInputMap().clear();
    }

    private void clearGame() {
        pnlGame.setVisible(false);
        pnlGame.getInputMap().clear();
        this.remove(pnlGame);
    }

    private void startGame() {
        clearUI();
        initGameView(initModel());
    }

    private void endGame() {
        clearGame();
        initUI();
    }
}

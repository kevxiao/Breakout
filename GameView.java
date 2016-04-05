import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;

public class GameView extends JPanel implements ComponentListener {

    // PUBLIC

    public GameView(double redrawRate, GameModel game) {
        this.setBackground(Color.black);
        this.gameModel = game;
        this.redrawRate = redrawRate;
        repaintTimer = new Timer();
        repaintTimer.scheduleAtFixedRate(new RepaintTask(), 0, Math.round(1000 / redrawRate));

        Action leftKey = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                gameModel.keyEvent(KeyEvent.VK_LEFT);
            }
        };
        this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
        this.getActionMap().put("left", leftKey);

        Action rightKey = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                gameModel.keyEvent(KeyEvent.VK_RIGHT);
            }
        };
        this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
        this.getActionMap().put("right", rightKey);

        Action upKey = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                gameModel.keyEvent(KeyEvent.VK_UP);
            }
        };
        this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
        this.getActionMap().put("up", upKey);

        Action downKey = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                gameModel.keyEvent(KeyEvent.VK_DOWN);
            }
        };
        this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
        this.getActionMap().put("down", downKey);

        Action spaceKey = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                gameModel.keyEvent(KeyEvent.VK_SPACE);
            }
        };
        this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
        this.getActionMap().put("space", spaceKey);

        this.addMouseMotionListener( new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                gameModel.setPaddleX(((double)e.getX() / (double)getWidth()) - gameModel.getPaddleWidth() / 2);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }
        });
        this.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameModel.keyEvent(KeyEvent.VK_SPACE);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.white);
        g2d.drawString("Score: " + Integer.toString(this.gameModel.getScore()), (int)(20 * Breakout.ratioDPI), (int)(20 * Breakout.ratioDPI));
        g2d.drawString("Lives: " + Integer.toString(this.gameModel.getLives()), (int)(20 * Breakout.ratioDPI), this.getHeight() - (int)(15 * Breakout.ratioDPI));
        g2d.drawString("Frames per second: " + Long.toString(Math.round(redrawRate)), this.getWidth() - (int)(150 * Breakout.ratioDPI), this.getHeight() - (int)(15 * Breakout.ratioDPI));
        g2d.drawString("Speed: " + Long.toString(Math.round(this.gameModel.getSpeed())), this.getWidth() - (int)(150 * Breakout.ratioDPI), this.getHeight() - (int)(35 * Breakout.ratioDPI));
        g2d.drawString("Press Escape To Exit", this.getWidth() - (int)(135 * Breakout.ratioDPI), (int)(20 * Breakout.ratioDPI));
        g2d.setColor(Color.red);
        g2d.fillOval((int)Math.round(this.gameModel.getBallX() * this.getWidth()), (int)Math.round(this.gameModel.getBallY() * this.getHeight()), (int)Math.round(this.gameModel.getBallWidth() * this.getWidth()), (int)Math.round(this.gameModel.getBallHeight() * this.getHeight()));
        g2d.setColor(Color.lightGray);
        g2d.fillRect((int)Math.round(this.gameModel.getPaddleX() * this.getWidth()), (int)Math.round(this.gameModel.getPaddleY() * this.getHeight()), (int)Math.round(this.gameModel.getPaddleWidth() * this.getWidth()), (int)Math.round(this.gameModel.getPaddleHeight() * this.getHeight()));
        int numBlocks = this.gameModel.getNumBlocks();
        for(int i = 0; i < numBlocks; ++i) {
            g2d.setColor(this.gameModel.getBlockColor(i));
            g2d.fillRect((int)Math.round(this.gameModel.getBlockX(i) * this.getWidth()), (int)Math.round(this.gameModel.getBlockY(i) * this.getHeight()), (int)Math.round(this.gameModel.getBlockWidth(i) * this.getWidth()), (int)Math.round(this.gameModel.getBlockHeight(i) * this.getHeight()));
        }
        if(this.gameModel.hasLost() || this.gameModel.hasWon()) {
            g2d.setColor(Color.lightGray);
            g2d.fillRect((int)(this.getWidth() * 0.05), (int)(this.getHeight() * 0.05), (int)(this.getWidth() * 0.9), (int)(this.getHeight()*0.9));
            g2d.setColor(Color.black);
            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 14));
            g2d.drawString("Click or press space to play again!", (int)(this.getWidth() * 0.5 - 105), (int)(this.getHeight() * 0.5 + (int)(20 * Breakout.ratioDPI)));
            if(this.gameModel.hasLost()) {
                g2d.setColor(Color.red);
                g2d.setFont(new Font("TimesRoman", Font.BOLD, 36));
                g2d.drawString("You have lost!", (int)(this.getWidth() * 0.5 - (int)(120 * Breakout.ratioDPI)), (int)(this.getHeight() * 0.5 - (int)(10 * Breakout.ratioDPI)));
            } else {
                g2d.setColor(Color.green);
                g2d.setFont(new Font("TimesRoman", Font.BOLD, 36));
                g2d.drawString("You have won!", (int)(this.getWidth() * 0.5 - (int)(120 * Breakout.ratioDPI)), (int)(this.getHeight() * 0.5 - (int)(10 * Breakout.ratioDPI)));
            }
        } else if(!this.gameModel.isStarted()) {
            g2d.setColor(Color.white);
            g2d.drawString("Click or press space to start!", this.getWidth() / 2 - (int)(77 * Breakout.ratioDPI), (int)((Constants.PADDLE_Y - Constants.BALL_DIMENSIONS_PCT) * this.getHeight() - (int)(30 * Breakout.ratioDPI)));
        } else if(this.gameModel.isPaused()) {
            g2d.setColor(Color.white);
            g2d.drawString("Click or press space to resume!", this.getWidth() / 2 - (int)(83 * Breakout.ratioDPI), (int)((Constants.PADDLE_Y - Constants.BALL_DIMENSIONS_PCT) * this.getHeight() - (int)(30 * Breakout.ratioDPI)));
        } else  {
            g2d.setColor(Color.white);
            g2d.drawString("Press Space To Pause", this.getWidth() - (int)(135 * Breakout.ratioDPI), (int)(40 * Breakout.ratioDPI));
        }
    }

    @Override
    public void componentResized(ComponentEvent arg0) {

    }

    @Override
    public void componentShown(ComponentEvent arg0) {

    }

    @Override
    public void componentMoved(ComponentEvent arg0) {

    }

    @Override
    public void componentHidden(ComponentEvent arg0) {

    }

    // PRIVATE

    private GameModel gameModel;
    private double redrawRate;
    private Timer repaintTimer;

    private class RepaintTask extends TimerTask {

        // PUBLIC

        public RepaintTask() {

        }

        @Override
        public void run() {
            repaint();
        }
    }
}

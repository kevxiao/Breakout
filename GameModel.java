import java.awt.*;
import java.util.*;
import java.lang.Thread;
import java.awt.event.KeyEvent;

public class GameModel extends Thread {

    // PUBLIC

    public GameModel(double speed) {
        this.speed = speed;
        resetScore();
        this.lives = 3;
        this.lost = false;
        this.won = false;
        this.ball = new Ball(speed);
        this.paddle = new Paddle(Constants.PADDLE_DEFAULT_WIDTH_PCT);
        this.blocks = new ArrayList<>();
        for(int i = 0; i < Constants.NUM_ROWS; ++i) {
            Color brickColor = Color.green;
            for(int j = 0; j < Constants.NUM_COLS; ++j) {
                this.blocks.add(new Block(((double)1 - ((double)Constants.NUM_COLS * Constants.BLOCK_WIDTH_PCT * (1 + Constants.BLOCK_MARGINS_PCT))) / 2 + j * Constants.BLOCK_WIDTH_PCT * (1 + Constants.BLOCK_MARGINS_PCT), Constants.TOP_BLOCK_Y + i * Constants.BLOCK_HEIGHT_PCT * (1 + Constants.BLOCK_MARGINS_PCT * Constants.ASPECT_RATIO), brickColor));
            }
        }
    }

    @Override
    public void run() {
        initGame();
    }

    @Override
    public void start() {
        gameThread = new Thread(this, "gameModel");
        gameThread.start();
    }

    public void keyEvent(int keyCode) {
        switch(keyCode) {
            case KeyEvent.VK_LEFT: {
                setPaddleX(paddle.getX() - 0.03);
                break;
            }
            case KeyEvent.VK_RIGHT: {
                setPaddleX(paddle.getX() + 0.03);
                break;
            }
            case KeyEvent.VK_SPACE: {
                if(!hasStarted) {
                    startGame();
                } else {
                    if(paused) {
                        startGame();
                    } else {
                        pauseGame();
                    }
                }
                break;
            }
            case KeyEvent.VK_UP: {
                setSpeed(getSpeed() + 1);
                break;
            }
            case KeyEvent.VK_DOWN: {
                setSpeed(getSpeed() - 1);
                break;
            }
            default: {
                break;
            }
        }
    }

    public void initGame() {
        resetScore();
        this.lives = 3;
        this.lost = false;
        this.won = false;
        this.paused = false;
        this.hasStarted = false;
        this.ball = new Ball(speed);
        this.paddle = new Paddle(Constants.PADDLE_DEFAULT_WIDTH_PCT);
        this.blocks = new ArrayList<>();
        for(int i = 0; i < Constants.NUM_ROWS; ++i) {
            Random rand = new Random();
            Color brickColor = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
            for(int j = 0; j < Constants.NUM_COLS; ++j) {
                this.blocks.add(new Block(((double)1 - ((double)Constants.NUM_COLS * Constants.BLOCK_WIDTH_PCT * (1 + Constants.BLOCK_MARGINS_PCT))) / 2 + j * Constants.BLOCK_WIDTH_PCT * (1 + Constants.BLOCK_MARGINS_PCT), Constants.TOP_BLOCK_Y + i * Constants.BLOCK_HEIGHT_PCT * (1 + Constants.BLOCK_MARGINS_PCT * Constants.ASPECT_RATIO), brickColor));
            }
        }
        this.ball.initBallMovement();
        this.dataUpdateTimer = new Timer();
        dataUpdateTimer.scheduleAtFixedRate(new UpdateTask(), 0, Math.round(Constants.DEFAULT_REDRAW / 4));
    }

    public void startGame() {
        if(!hasStarted || paused) {
            if(lost || won) {
                initGame();
            } else {
                this.ball.startMoving();
                this.paused = false;
                this.hasStarted = true;
            }
        } else {
            initGame();
        }
    }

    public void pauseGame() {
        this.ball.stopMoving();
        this.paused = true;
    }

    public void setSpeed(double speed) {
        if(speed < 1) {
            speed = 1;
        }
        this.speed = speed;
        this.ball.setSpeed(speed);
    }

    public double getSpeed() {
        return this.speed;
    }

    public int getScore() {
        return this.score;
    }

    public int getLives() {
        return this.lives;
    }

    public void incScore() {
        ++this.score;
    }

    public void resetScore() {
        this.score = 0;
    }

    public double getBallWidth() {
        return this.ball.getWidth();
    }

    public double getBallHeight() {
        return this.ball.getHeight();
    }

    public double getBallX() {
        return this.ball.getX();
    }

    public double getBallY() {
        return this.ball.getY();
    }

    public double getPaddleWidth() {
        return this.paddle.getWidth();
    }

    public double getPaddleHeight() {
        return this.paddle.getHeight();
    }

    public void setPaddleX(double newX) {
        if(hasStarted && !paused) {
            this.paddle.setX(newX);
        }
    }

    public double getPaddleX() {
        return this.paddle.getX();
    }

    public double getPaddleY() {
        return this.paddle.getY();
    }

    public boolean hasLost() {
        return this.lost;
    }

    public boolean hasWon() {
        return this.won;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public boolean isStarted() {
        return this.hasStarted;
    }

    public int getNumBlocks() {
        return blocks.size();
    }

    public double getBlockWidth(int index) {
        if(index < blocks.size()) {
            return this.blocks.get(index).getWidth();
        } else {
            return 0;
        }
    }

    public double getBlockHeight(int index) {
        if(index < blocks.size()) {
            return this.blocks.get(index).getHeight();
        } else {
            return 0;
        }
    }

    public double getBlockX(int index) {
        if(index < blocks.size()) {
            return this.blocks.get(index).getX();
        } else {
            return 0;
        }
    }

    public double getBlockY(int index) {
        if(index < blocks.size()) {
            return this.blocks.get(index).getY();
        } else {
            return 0;
        }
    }

    public Color getBlockColor(int index) {
        if(index < blocks.size()) {
            return this.blocks.get(index).getColor();
        } else {
            return Color.black;
        }
    }

    // PRIVATE

    private Thread gameThread;
    private Timer dataUpdateTimer;
    private int score;
    private int lives;
    private double speed;
    private Ball ball;
    private Paddle paddle;
    private ArrayList<Block> blocks;
    private boolean lost;
    private boolean won;
    private boolean paused;
    private boolean hasStarted;

    private class UpdateTask extends TimerTask {

        // PUBLIC

        public UpdateTask() {

        }

        @Override
        public void run() {
            if(won) {
                this.cancel();
                ball.stopMoving();
                hasStarted = false;
                return;
            }
            if(checkLeftBoundary(ball)) {
                ball.moveRight();
            } else if(checkRightBoundary(ball)) {
                ball.moveLeft();
            }
            if(checkTopBoundary(ball)) {
                ball.moveDown();
            } else if(checkBottomBoundary(ball)) {
                --lives;
                ball.stopMoving();
                hasStarted = false;
                ball.initBall(speed);
                if(lives <= 0) {
                    lost = true;
                    this.cancel();
                }
            }
            if(checkPaddle(ball, paddle)) {
                double angle = (1 - (ball.getX() + ball.getWidth() - paddle.getX()) / (paddle.getWidth() + ball.getWidth())) * 120 + 30;
                ball.changeAngle(angle);
            }
            ArrayList<Block> hit = new ArrayList<>();
            ArrayList<Double> hitArea = new ArrayList<>();
            for(Block bl : blocks) {
                if(checkCollision(ball, bl)) {
                    double vertCollision = checkBottomCollision(ball, bl), horizCollision = checkRightCollision(ball, bl);
                    boolean isTop = false, isLeft = false;
                    if(vertCollision <= 0) {
                        isTop = true;
                        vertCollision = checkTopCollision(ball, bl);
                    }
                    if(horizCollision <= 0) {
                        isLeft = true;
                        horizCollision = checkLeftCollision(ball, bl);
                    }
                    if(vertCollision > horizCollision) {
                        if(isTop) {
                            if(!ball.moveUp()) {
                                if(horizCollision != 0) {
                                    if(isLeft) {
                                        ball.moveLeft();
                                    } else {
                                        ball.moveRight();
                                    }
                                }
                            }
                        } else {
                            if(!ball.moveDown()) {
                                if(horizCollision != 0) {
                                    if(isLeft) {
                                        ball.moveLeft();
                                    } else {
                                        ball.moveRight();
                                    }
                                }
                            }
                        }
                    } else {
                        if(isLeft) {
                            if(!ball.moveLeft()) {
                                if(horizCollision != 0) {
                                    if(isTop) {
                                        ball.moveUp();
                                    } else {
                                        ball.moveDown();
                                    }
                                }
                            }
                        } else {
                            if(!ball.moveRight()) {
                                if(horizCollision != 0) {
                                    if(isTop) {
                                        ball.moveUp();
                                    } else {
                                        ball.moveDown();
                                    }
                                }
                            }
                        }
                    }
                    hit.add(bl);
                    hitArea.add(vertCollision * horizCollision);
                }
            }
            if (hitArea.size() > 0 && hit.size() == hitArea.size() && blocks.size() > 0) {
                int maxIndex = hitArea.indexOf(Collections.max(hitArea));
                blocks.remove(hit.get(maxIndex));
                incScore();
                won = blocks.isEmpty();
                if(won) {
                    this.cancel();
                    hasStarted = false;
                    ball.stopMoving();
                }
            }
        }

        // PRIVATE

        private boolean checkTopBoundary(Ball b) {
            return b.getY() <= 0;
        }

        private boolean checkLeftBoundary(Ball b) {
            return b.getX() <= 0 ;
        }

        private boolean checkRightBoundary(Ball b) {
            return b.getX() + b.getWidth() >= 1;
        }

        private boolean checkBottomBoundary(Ball b) {
            return b.getY() >= 1;
        }

        private boolean checkPaddle(Ball b, Paddle p) {
            return b.getX() < p.getX() + p.getWidth() && b.getX() + b.getWidth() > p.getX() && b.getY() + b.getHeight() < p.getY() + p.getHeight() && b.getY() + b.getHeight() > p.getY();
        }

        private boolean checkCollision(Ball b, Block bl) {
            return b.getX() < bl.getX() + bl.getWidth() && b.getX() + b.getWidth() > bl.getX() && b.getY() < bl.getY() + bl.getHeight() && b.getY() + b.getHeight() > bl.getY();
        }

        private double checkBottomCollision(Ball b, Block bl) {
            if(b.getY() < bl.getY() + bl.getHeight() && b.getY() > bl.getY()) {
                return Math.max(Math.abs(b.getX() - bl.getX() + bl.getWidth()), Math.abs(b.getX() + b.getWidth() - bl.getX()));
            }
            return 0;
        }

        private double checkTopCollision(Ball b, Block bl) {
            if(b.getY() + b.getHeight() > bl.getY() && b.getY() + b.getHeight() < bl.getY() + bl.getHeight()) {
                return Math.max(Math.abs(b.getX() - bl.getX() + bl.getWidth()), Math.abs(b.getX() + b.getWidth() - bl.getX()));
            }
            return 0;
        }

        private double checkRightCollision(Ball b, Block bl) {
            if(b.getX() < bl.getX() + bl.getWidth() && b.getX() > bl.getX()) {
                return Math.max(Math.abs(b.getY() - bl.getY() + bl.getHeight()), Math.abs(b.getY() + b.getHeight() - bl.getY()));
            }
            return 0;
        }

        private double checkLeftCollision(Ball b, Block bl) {
            if(b.getX() + b.getWidth() > bl.getX() && b.getX() + b.getWidth() < bl.getX() + bl.getWidth()) {
                return Math.max(Math.abs(b.getY() - bl.getY() + bl.getHeight()), Math.abs(b.getY() + b.getHeight() - bl.getY()));
            }
            return 0;
        }
    }

    private class Ball {

        // PUBLIC

        public Ball(double speed) {
            this.WIDTH = Constants.BALL_DIMENSIONS_PCT / Constants.ASPECT_RATIO;
            this.HEIGHT = Constants.BALL_DIMENSIONS_PCT;
            this.INITIAL_X = 0.5 - this.WIDTH / 2;
            this.INITIAL_Y = Constants.PADDLE_Y - Constants.PADDLE_HEIGHT_PCT / 2 - this.HEIGHT;
            initBall(speed);
        }

        public void initBall(double speed) {
            Random rand = new Random();
            boolean moveRight = rand.nextBoolean();
            if(moveRight) {
                this.moveAngle = 45;
            } else {
                this.moveAngle = 135;
            }
            this.isMoving = false;
            this.x = this.INITIAL_X;
            this.y = this.INITIAL_Y;
            this.moveSpeed = speed;
        }

        public void initBallMovement() {
            moveTimer = new Timer();
            moveTimer.scheduleAtFixedRate(new MoveTask(), 0, Math.round(Constants.DEFAULT_REDRAW / 4));
        }

        public void startMoving(){
            this.isMoving = true;
        }

        public void stopMoving(){
            this.isMoving = false;
        }

        public void setSpeed(double speed) {
            this.moveSpeed = speed;
        }

        public double getWidth() {
            return this.WIDTH;
        }

        public double getHeight() {
            return this.HEIGHT;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getAngle() {
            return this.moveAngle;
        }

        public void changeAngle (double angle) {
            this.moveAngle = angle;
        }

        public boolean moveUp() {
            if(this.moveAngle % 360 > 180) {
                this.moveAngle = (this.moveAngle + (180 - this.moveAngle) * 2) % 360;
                return true;
            }
            return false;
        }

        public boolean moveDown() {
            if(this.moveAngle % 360 < 180) {
                this.moveAngle = (this.moveAngle + (180 - this.moveAngle) * 2) % 360;
                return true;
            }
            return false;
        }

        public boolean moveLeft() {
            if(this.moveAngle % 360 < 90 || this.moveAngle % 360 > 270) {
                this.moveAngle = (this.moveAngle + (270 - this.moveAngle) * 2) % 360;
                return true;
            }
            return false;
        }

        public boolean moveRight() {
            if(this.moveAngle % 360 > 90 && this.moveAngle % 360 < 270) {
                this.moveAngle = (this.moveAngle + (270 - this.moveAngle) * 2) % 360;
                return true;
            }
            return false;
        }

        // PRIVATE

        private final double INITIAL_X;
        private final double INITIAL_Y;
        private final double WIDTH;
        private final double HEIGHT;
        private double x;
        private double y;
        private double moveSpeed;
        private double moveAngle;
        private volatile boolean isMoving;
        private Timer moveTimer;

        private class MoveTask extends TimerTask {
            @Override
            public void run() {
                if(isMoving) {
                    x += Math.cos(Math.toRadians(moveAngle)) * moveSpeed / (Constants.DEFAULT_REDRAW * 60);
                    y -= Math.sin(Math.toRadians(moveAngle)) * moveSpeed / (Constants.DEFAULT_REDRAW * 60);
                }
            }
        }
    }

    private class Paddle {

        // PUBLIC

        public Paddle(double width) {
            this.width = width;
            this.HEIGHT = Constants.PADDLE_HEIGHT_PCT;
            this.INITIAL_X = 0.5 - this.width / 2;
            this.Y = Constants.PADDLE_Y;
            initPaddle();
        }

        public void initPaddle() {
            this.x = this.INITIAL_X;
        }

        public double getWidth() {
            return this.width;
        }

        public double getHeight() {
            return this.HEIGHT;
        }

        public void setX(double newX) {
            if(newX < 0 - this.width) {
                newX = 0 - this.width;
            } else if(newX > 1 + this.HEIGHT) {
                newX = 1 + this.HEIGHT;
            }
            this.x = newX;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.Y;
        }

        // PRIVATE

        private final double INITIAL_X;
        private final double Y;
        private final double HEIGHT;
        private double width;
        private double x;
    }

    private class Block {

        // PUBLIC

        public Block(double x, double y, Color color) {
            this.WIDTH = Constants.BLOCK_WIDTH_PCT;
            this.HEIGHT = Constants.BLOCK_HEIGHT_PCT;
            this.X = x;
            this.Y = y;
            initBlock(color);
        }

        public void initBlock(Color color) {
            this.color = color;
        }

        public double getWidth() {
            return this.WIDTH;
        }

        public double getHeight() {
            return this.HEIGHT;
        }

        public double getX() {
            return this.X;
        }

        public double getY() {
            return this.Y;
        }

        public Color getColor() {
            return this.color;
        }

        // PRIVATE

        private final double WIDTH;
        private final double HEIGHT;
        private final double Y;
        private final double X;
        private Color color;
    }
}

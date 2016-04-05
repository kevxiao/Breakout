public class Constants {

    // PUBLIC

    public static final int DEFAULT_DPI = 96;
    public static final int INITIAL_HEIGHT = 600;
    public static final int INITIAL_WIDTH = 1000;
    public static final double ASPECT_RATIO = 5.0 / 3.0;

    public static final double PADDLE_HEIGHT_PCT = 0.01;
    public static final double PADDLE_DEFAULT_WIDTH_PCT = 0.15;
    public static final double PADDLE_Y = 0.92;

    public static final double BALL_DIMENSIONS_PCT = 0.05;

    public static final double TOP_BLOCK_Y = 0.2;
    public static final double BLOCK_HEIGHT_PCT = 0.07;
    public static final double BLOCK_WIDTH_PCT = 0.15;
    public static final double BLOCK_MARGINS_PCT = 0.03;
    public static final int NUM_ROWS = 5;
    public static final int NUM_COLS = 5;

    public static final double DEFAULT_REDRAW = 30;
    public static final double DEFAULT_SPEED = 10;

    // PRIVATE

    // prevent creating an instance of this class
    private Constants(){
        throw new AssertionError();
    }
}

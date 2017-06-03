package QuickStartBasics;

/**
 *
 * @author Philippe
 */
public final class GlobalTimer {

    private GlobalTimer() {}

    private static long gameTime = 0;
    private static long totalTime = 0;

    public static void resetTime() {
        resetGameTime();
        resetTotalTime();
    }

    public static void incrementGameTime() {
        gameTime++;
    }

    public static void resetGameTime() {
        gameTime = 0;
    }

    public static long getGameTime() {
        return gameTime;
    }

    public static void incrementTotalTime() {
        totalTime++;
    }

    public static void resetTotalTime() {
        totalTime = 0;
    }

    public static long getTotalTime() {
        return totalTime;
    }

}

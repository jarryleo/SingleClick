package cn.leo.click;

/**
 * @author : Jarry Leo
 * @date : 2018/9/28 16:53
 */
public class SingleClickManager {
    static int clickInterval = 500;
    static boolean isCheckThreadIdle = true;

    private SingleClickManager() {
    }

    /**
     * 设置全局点击事件防重间隔
     *
     * @param clickIntervalMillis 间隔毫秒值
     */
    public static void setClickInterval(int clickIntervalMillis) {
        clickInterval = clickIntervalMillis;
    }

    /**
     * 设置线程忙碌检测,线程忙碌时其它按钮防止点击
     *
     * @param checkThreadIdle 是否检测线程空闲
     */
    public static void setCheckThreadIdle(boolean checkThreadIdle) {
        isCheckThreadIdle = checkThreadIdle;
    }
}

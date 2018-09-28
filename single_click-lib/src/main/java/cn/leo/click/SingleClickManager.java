package cn.leo.click;

/**
 * @author : Jarry Leo
 * @date : 2018/9/28 16:53
 */
public class SingleClickManager {
    static int clickInterval = 500;

    private SingleClickManager() {
    }

    /**
     * 设置全局点击事件防重间隔
     *
     * @param clickIntervalMillis 间隔毫秒值
     */
    public static void init(int clickIntervalMillis) {
        clickInterval = clickIntervalMillis;
    }
}

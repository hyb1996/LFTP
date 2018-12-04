package cn.sysu.hyb1996.lftp.util;

/**
 * Created by Stardust on 2018/12/5.
 */
public class AverageCalculator {

    private long mSum = 0;
    private long mCount = 0;

    public AverageCalculator(long initial) {
        mSum = initial;
        mCount = 1;
    }

    public AverageCalculator() {
    }

    public void update(long number) {
        mSum += number;
        mCount++;
    }

    public long average() {
        if (mCount == 0) {
            return 0;
        }
        return mSum / mCount;
    }

    public long count() {
        return mCount;
    }
}

package edu.sjsu.sase.android.spoleralert;
import java.time.YearMonth;

public class MonthlyStat {
    public YearMonth month;
    public int value;

    public MonthlyStat(YearMonth month, int value) {
        this.month = month;
        this.value = value;
    }
}
package edu.sjsu.sase.android.spoleralert;
import java.time.YearMonth;

public class MonthlyStat {
    public YearMonth month;
    public float value;

    public MonthlyStat(YearMonth month, float value) {
        this.month = month;
        this.value = value;
    }
}
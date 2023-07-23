package de.Breakcraft.Challenges.Utils;

public class Timer {
    private int s = 0;
    private int m = 0;
    private int h = 0;

    public void next() {
        s++;
        if(s == 60) {
            m++;
            s = 0;
        }
        if(m == 60) {
            h++;
            m = 0;
        }
    }

    public String formatToString() {
        String formatted = "";
        if(h < 10) formatted += "0" + h + ":";
        else formatted += h + ":";
        if(m < 10) formatted += "0" + m + ":";
        else formatted += m + ":";
        if(s < 10) formatted += "0" + s;
        else formatted += s;
        return formatted;
    }

    public void reset() {
        s = 0;
        m = 0;
        h = 0;
    }

}

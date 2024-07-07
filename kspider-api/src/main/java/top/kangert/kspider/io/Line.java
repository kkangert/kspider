package top.kangert.kspider.io;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Line {

    private long from;

    private String text;

    private long to;

    public Line(long from, String text, long to) {
        this.from = from;
        this.text = text;
        this.to = to;
    }

    @Override
    public String toString() {
        return "Line{" +
                "from=" + from +
                ", text='" + text + '\'' +
                ", to=" + to +
                '}';
    }
}

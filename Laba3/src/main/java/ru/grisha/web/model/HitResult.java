package ru.grisha.web.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "lab3_results")
public class HitResult implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double x;
    private Double y;
    private Double r;
    private boolean hit;
    private String serverTime;
    private long execTimeNs;

    public HitResult() {}

    public void checkHit() {
        if (x == null || y == null || r == null) {
            this.hit = false;
            return;
        }
        // 2 четверть: прямоугольник
        boolean q2 = (x >= -r / 2 && x <= 0) && (y >= 0 && y <= r);
        // 3 четверть: треугольник
        boolean q3 = (x <= 0 && y <= 0) && (y >= -x - r / 2);
        // 4 четверть: окружность
        boolean q4 = (x >= 0 && y <= 0) && ((x * x + y * y) <= (r * r / 4));

        this.hit = q2 || q3 || q4;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Double getR() { return r; }
    public void setR(Double r) { this.r = r; }

    public boolean isHit() { return hit; }
    public void setHit(boolean hit) { this.hit = hit; }

    public String getServerTime() { return serverTime; }
    public void setServerTime(String time) { this.serverTime = time; }

    public long getExecTimeNs() { return execTimeNs; }
    public void setExecTimeNs(long time) { this.execTimeNs = time; }

    public String getFormattedServerTime() {
        if (this.serverTime == null) return "";
        try {
            return ZonedDateTime.parse(this.serverTime)
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy"));
        } catch (Exception e) {
            return "";
        }
    }
}
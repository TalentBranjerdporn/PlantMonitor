package talent.plantmonitor.Model;

/**
 * Created by Talent on 26/02/2018.
 *
 * A class for model readings
 */

public class SensorModel {

    private int time;
    private int light;
    private int water;
    private double temp;
    private int cond;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getCond() {
        return cond;
    }

    public void setCond(int cond) {
        this.cond = cond;
    }

    @Override
    public String toString() {
        return time + ": " + light;
    }
}

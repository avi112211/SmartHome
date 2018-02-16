package com.example.avi.smarthome.OpenHab;

/**
 * Created by Avi on 31/12/2017.
 */

public class StateDescription {
    int minimum;
    int maximum;
    int step;
    String pattern;
    boolean readOnly;

    public StateDescription(){}

    public StateDescription(int minimum, int maximum, int step, String pattern, boolean readOnly) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.step = step;
        this.pattern = pattern;
        this.readOnly = readOnly;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public String toString() {
        return "StateDescription{" +
                "minimum=" + minimum +
                ", maximum=" + maximum +
                ", step=" + step +
                ", pattern='" + pattern + '\'' +
                ", readOnly=" + readOnly +
                '}';
    }
}

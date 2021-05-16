package com.oricadu.financemanager.model;

public class Aim {
    private String aimName;
    private int aimSum;
    private int aimAccumulatedSum;
    private double aimPercent;

    public Aim() {
    }

    public Aim(String aimName, int aimSum, int aimAccumulatedSum, double aimPercent) {
        this.aimName = aimName;
        this.aimSum = aimSum;
        this.aimAccumulatedSum = aimAccumulatedSum;
        this.aimPercent = aimPercent;
    }

    public String getAimName() {
        return aimName;
    }

    public void setAimName(String aimName) {
        this.aimName = aimName;
    }

    public int getAimSum() {
        return aimSum;
    }

    public void setAimSum(int aimSum) {
        this.aimSum = aimSum;
    }

    public int getAimAccumulatedSum() {
        return aimAccumulatedSum;
    }

    public void setAimAccumulatedSum(int aimAccumulatedSum) {
        this.aimAccumulatedSum = aimAccumulatedSum;
    }

    public double getAimPercent() {
        return aimPercent;
    }

    public void setAimPercent(double aimPercent) {
        this.aimPercent = aimPercent;
    }

    @Override
    public String toString() {
        return "Aim{" +
                "aimName='" + aimName + '\'' +
                ", aimSum=" + aimSum +
                ", aimAccumulatedSum=" + aimAccumulatedSum +
                ", aimPercent=" + aimPercent +
                '}';
    }
}

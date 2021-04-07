package com.oricadu.financemanager.model;

public class Category {
    private String categoryName;
    private int categorySum;
    private int categorySpentSum;

    public Category() {
    }

    public Category(String categoryName, int categorySum, int categorySpentSum) {
        this.categoryName = categoryName;
        this.categorySum = categorySum;
        this.categorySpentSum = categorySpentSum;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategorySum() {
        return categorySum;
    }

    public void setCategorySum(int categorySum) {
        this.categorySum = categorySum;
    }

    public int getCategorySpentSum() {
        return categorySpentSum;
    }

    public void setCategorySpentSum(int categorySpentSum) {
        this.categorySpentSum = categorySpentSum;
    }
}

package com.oricadu.financemanager.model;

public class Category {
    private String categoryName;
    private int categorySum;
    private int categorySpentSum;
    private int categoryDifferenceSum;

    public Category() {
    }

    public Category(String categoryName, int categorySum, int categorySpentSum, int categoryDifferenceSum) {
        this.categoryName = categoryName;
        this.categorySum = categorySum;
        this.categorySpentSum = categorySpentSum;
        this.categoryDifferenceSum = categoryDifferenceSum;
    }

    public Category(String categoryName, int categorySum) {
        this.categoryName = categoryName;
        this.categorySum = categorySum;
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

    public int getCategoryDifferenceSum() {
        return categoryDifferenceSum;
    }

    public void setCategoryDifferenceSum(int categoryDifferenceSum) {
        this.categoryDifferenceSum = categoryDifferenceSum;
    }
}

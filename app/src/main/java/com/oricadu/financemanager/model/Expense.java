package com.oricadu.financemanager.model;

public class Expense {
    private String expenseName;
    private int expenseSum;
    private String categoryName;

    public Expense() {
    }

    public Expense(String expenseName, int expenseSum, String categoryName) {
        this.expenseName = expenseName;
        this.expenseSum = expenseSum;
        this.categoryName = categoryName;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public int getExpenseSum() {
        return expenseSum;
    }

    public void setExpenseSum(int expenseSum) {
        this.expenseSum = expenseSum;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

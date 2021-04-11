package com.oricadu.financemanager.ui.expenses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExpenseViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public ExpenseViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is expence fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
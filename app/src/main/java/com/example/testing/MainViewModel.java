package com.example.testing;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    MutableLiveData<String> mutableLiveData=new MutableLiveData<String>();


    public void setText(String s)
    {
        mutableLiveData.setValue(s);
    }

    public MutableLiveData<String>  getText()
    {
        return mutableLiveData;
    }
}

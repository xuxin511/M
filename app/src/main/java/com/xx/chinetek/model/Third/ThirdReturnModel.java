package com.xx.chinetek.model.Third;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by GHOST on 2018/12/25.
 */

public class ThirdReturnModel<T> {
    private String Message;

    private int Success;

    public ArrayList<T> ReAdjust;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getSuccess() {
        return Success;
    }

    public void setSuccess(int success) {
        Success = success;
    }

    public ArrayList<T> getReAdjust() {
        return ReAdjust;
    }

    public void setReAdjust(ArrayList<T> reAdjust) {
        ReAdjust = reAdjust;
    }

    public ThirdReturnModel() {
    }
}
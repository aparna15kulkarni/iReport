package com.ireport.controller;

/**
 * Created by Somya on 11/27/2016.
 */

public class IReportException extends Exception {

    public String errorMsg;
    public IReportException(String msg) {
        this.errorMsg = msg;
    }
}

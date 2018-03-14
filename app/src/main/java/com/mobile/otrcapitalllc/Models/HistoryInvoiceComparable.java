package com.mobile.otrcapitalllc.Models;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Paul Kugaev on 14.03.2018.
 */

public class HistoryInvoiceComparable implements Comparable<HistoryInvoiceComparable> {

    private String filename;
    private HistoryInvoiceModel historyInvoiceModel;

    public HistoryInvoiceComparable(String filename, HistoryInvoiceModel historyInvoiceModel) {
        this.filename = filename;
        this.historyInvoiceModel = historyInvoiceModel;
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public int compareTo(@NonNull HistoryInvoiceComparable o) {
        long firstTimeStamp = -1;
        long secondTimeStamp = -1;
        if (this.historyInvoiceModel == null) {
            if (o.historyInvoiceModel == null)
                return 0;
            else
                return 1;
        } else if (o.historyInvoiceModel == null)
            return -1;


        try {
            firstTimeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(this.historyInvoiceModel.getTimestamp()).getTime();
            secondTimeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(o.historyInvoiceModel.getTimestamp()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (firstTimeStamp > secondTimeStamp)
            return -1;
        else if (firstTimeStamp == secondTimeStamp)
            return 0;
        else
            return 1;
    }

    public String getFilename() {
        return filename;
    }

    public HistoryInvoiceModel getHistoryInvoiceModel() {
        return historyInvoiceModel;
    }

}

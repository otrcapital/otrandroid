package com.mobile.otrcapitalllc.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.RetrofitError;

public class RetrofitHelper {

    public static String processError(RetrofitError error) {
        String toastText = "";
        try {
            if (error.getResponse().getStatus() == 400) {
                if (error.getResponse() != null) {
                    BufferedReader bufferedReader = null;
                    StringBuilder bodyBuilder = new StringBuilder();

                    String line;
                    try {
                        bufferedReader = new BufferedReader(new InputStreamReader(error.getResponse().getBody().in()));
                        while ((line = bufferedReader.readLine()) != null) {
                            bodyBuilder.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    toastText = bodyBuilder.toString();
                }
            }
            if (error.isNetworkError()) {
                toastText = "Network error, server not responding";
            }
        } catch (NullPointerException e) {
            toastText = "Network error, please try again later";
        }
        return toastText;
    }
}

package com.mobile.otrcapital.Models;

public class CustomError {

    private boolean isError;
    private String description;

    public CustomError(boolean isError, String description) {
        this.isError = isError;
        this.description = description;
    }

    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

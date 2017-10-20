package com.mobile.otrcapitalllc.Models;

public class HistoryInvoiceModel {

    private String brokerName;
    private String customerMCNumber;
    private String poNumber;
    private float invoiceAmount;
    private int customerPKey;
    private String clientLogin;
    private String clientPassword;
    private float advanceRequestAmount;
    private String advanceRequestType;
    private String phone;
    private String documentTypesString;
    private String factorType;
    private String status;
    private String timestamp;

    public HistoryInvoiceModel() {
    }

    public HistoryInvoiceModel(ApiInvoiceDataJson invoice) {
        customerMCNumber = invoice.CustomerMCNumber;
        poNumber = invoice.PoNumber;
        invoiceAmount = invoice.InvoiceAmount;
        customerPKey = invoice.CustomerPKey;
        clientLogin = invoice.ClientLogin;
        clientPassword = invoice.ClientPassword;
        advanceRequestAmount = invoice.AdvanceRequestAmount;
        advanceRequestType = invoice.AdvanceRequestType;
        phone = invoice.Phone;
        documentTypesString = null;
        status = null;
        timestamp = null;
    }

    public ApiInvoiceDataJson getApiInvoiceFromModel() {
        ApiInvoiceDataJson invoiceData = new ApiInvoiceDataJson();
        invoiceData.ClientLogin = clientLogin;
        invoiceData.ClientPassword = clientPassword;
        invoiceData.CustomerPKey = customerPKey;
        invoiceData.CustomerMCNumber = customerMCNumber;
        invoiceData.InvoiceAmount = invoiceAmount;
        invoiceData.PoNumber = poNumber;
        invoiceData.AdvanceRequestType = advanceRequestType;

        if (factorType.equals("ADV")) {
            invoiceData.AdvanceRequestAmount = advanceRequestAmount;
            invoiceData.Phone = phone;
        }
        return invoiceData;
    }

    public String getCustomerMCNumber() {
        return customerMCNumber;
    }

    public void setCustomerMCNumber(String customerMCNumber) {
        this.customerMCNumber = customerMCNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public float getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(float invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public int getCustomerPKey() {
        return customerPKey;
    }

    public void setCustomerPKey(int customerPKey) {
        this.customerPKey = customerPKey;
    }

    public String getClientLogin() {
        return clientLogin;
    }

    public void setClientLogin(String clientLogin) {
        this.clientLogin = clientLogin;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public float getAdvanceRequestAmount() {
        return advanceRequestAmount;
    }

    public void setAdvanceRequestAmount(float advanceRequestAmount) {
        this.advanceRequestAmount = advanceRequestAmount;
    }

    public String getAdvanceRequestType() {
        return advanceRequestType;
    }

    public void setAdvanceRequestType(String advanceRequestType) {
        this.advanceRequestType = advanceRequestType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDocumentTypesString() {
        return documentTypesString;
    }

    public void setDocumentTypesString(String documentTypesString) {
        this.documentTypesString = documentTypesString;
    }

    public String getFactorType() {
        return factorType;
    }

    public void setFactorType(String factorType) {
        this.factorType = factorType;
    }

    public void setBrokerName(String name) {
        this.brokerName = name;
    }

    public String getBrokerName() {
        return brokerName;
    }
}

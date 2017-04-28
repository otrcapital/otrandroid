package com.mobile.otrcapitalllc.Models;

public class Broker {
    private String _mcnumber;
    private String _brokerName;
    private String _pKey;

    private Broker() {
    }

    public Broker(String MCNumber, String BrokerName, String PKey) {
        this._mcnumber = MCNumber;
        this._brokerName = BrokerName;
        this._pKey = PKey;
    }

    public String get_mcnumber() {
        return _mcnumber;
    }

    public void set_mcnumber(String _mcnumber) {
        this._mcnumber = _mcnumber;
    }

    public String get_brokerName() {
        return _brokerName;
    }

    public void set_brokerName(String _brokerName) {
        this._brokerName = _brokerName;
    }

    public String get_pKey() {
        return _pKey;
    }

    public void set_pKey(String _pKey) {
        this._pKey = _pKey;
    }

}

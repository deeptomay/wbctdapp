package com.example.testapp.bean;

public class LoginBean {


    private String usr_nm;

    private String usrid;

    private String otp_verify_status;
    private String auth_status;

    public String getUsrid() {
        return usrid;
    }

    public void setUsrid(String usrid) {
        this.usrid = usrid;
    }

    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }

    public String getOtp_verify_status() {
        return otp_verify_status;
    }

    public void setOtp_verify_status(String otp_verify_status) {
        this.otp_verify_status = otp_verify_status;
    }

    public String getUsr_nm() {
        return usr_nm;
    }

    public void setUsr_nm(String usr_nm) {
        this.usr_nm = usr_nm;
    }
}

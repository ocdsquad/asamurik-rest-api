package com.asamurik_rest_api.dto.response;

import com.asamurik_rest_api.config.OtherConfig;
import com.fasterxml.jackson.annotation.JsonInclude;

public class OTPResponse {
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String otp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOTP() {
        return otp;
    }

    public void setOTP(String otp) {
        this.otp = OtherConfig.getEnableAutomationTesting().equals("y")
                ? otp
                : null;
    }
}

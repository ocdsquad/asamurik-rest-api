package com.asamurik_rest_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:smtpconfig.properties")
public class SMTPConfig {
    private static String emailUserName;
    private static String emailPassword;
    private static String emailHost;
    private static String emailPort;
    private static String emailPortSSL;
    private static String emailPortTLS;
    private static String emailAuth;
    private static String emailStartTLSEnable;
    private static String emailSMTPSocketFactoryClass;
    private static String emailSMTPTimeout;

    @Value("${email.username}")
    private void setEmailUserName(String emailUserName) {
//        SMTPConfig.emailUserName = Crypto.performDecrypt(emailUserName);
        SMTPConfig.emailUserName = emailUserName;
    }

    @Value("${email.password}")
    private void setEmailPassword(String emailPassword) {
//        SMTPConfig.emailPassword = Crypto.performDecrypt(emailPassword);
        SMTPConfig.emailPassword = emailPassword;
    }

    @Value("${email.host}")
    private void setEmailHost(String emailHost) {
        SMTPConfig.emailHost = emailHost;
    }

    @Value("${email.port}")
    private void setEmailPort(String emailPort) {
        SMTPConfig.emailPort = emailPort;
    }

    @Value("${email.port.ssl}")
    private void setEmailPortSSL(String emailPortSSL) {
        SMTPConfig.emailPortSSL = emailPortSSL;
    }

    @Value("${email.port.tls}")
    private void setEmailPortTLS(String emailPortTLS) {
        SMTPConfig.emailPortTLS = emailPortTLS;
    }

    @Value("${email.auth}")
    private void setEmailAuth(String emailAuth) {
        SMTPConfig.emailAuth = emailAuth;
    }

    @Value("${email.starttls.enable}")
    private void setEmailStartTLSEnable(String emailStartTLSEnable) {
        SMTPConfig.emailStartTLSEnable = emailStartTLSEnable;
    }

    @Value("${email.smtp.socket.factory.class}")
    private void setEmailSMTPSocketFactoryClass(String emailSMTPSocketFactoryClass) {
        SMTPConfig.emailSMTPSocketFactoryClass = emailSMTPSocketFactoryClass;
    }

    @Value("${email.smtp.timeout}")
    private void setEmailSMTPTimeout(String emailSMTPTimeout) {
        SMTPConfig.emailSMTPTimeout = emailSMTPTimeout;
    }

    public static String getEmailUserName() {
        return emailUserName;
    }

    public static String getEmailPassword() {
        return emailPassword;
    }

    public static String getEmailHost() {
        return emailHost;
    }

    public static String getEmailPort() {
        return emailPort;
    }

    public static String getEmailPortSSL() {
        return emailPortSSL;
    }

    public static String getEmailPortTLS() {
        return emailPortTLS;
    }

    public static String getEmailAuth() {
        return emailAuth;
    }

    public static String getEmailStartTLSEnable() {
        return emailStartTLSEnable;
    }

    public static String getEmailSMTPSocketFactoryClass() {
        return emailSMTPSocketFactoryClass;
    }

    public static String getEmailSMTPTimeout() {
        return emailSMTPTimeout;
    }
}

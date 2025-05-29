package com.example.asamurik_rest_api.utils;

import com.example.asamurik_rest_api.config.SMTPConfig;
import com.example.asamurik_rest_api.core.SMTPCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SendMailUtil {
    private static final Logger logger = LoggerFactory.getLogger(SendMailUtil.class);
    /**
     * Sends an OTP email to the user.
     *
     * @param subject   The subject of the email.
     * @param fullname  The full name of the informant.
     * @param email     The recipient's email address.
     * @param otp       The one-time password to be sent.
     * @param fileHtml  The HTML file path for the email content.
     */
    public static void sendOTP(String subject, String fullname, String email, String otp, String fileHtml) {
        try{
            String[] strVerify = new String[3];
            strVerify[0] = subject;
            strVerify[1] = fullname;
            strVerify[2] = otp;

            String  strContent = new ReadTextFileSB(fileHtml).getContentFile();

            strContent = strContent.replace("#JKVM3NH",strVerify[0]);
            strContent = strContent.replace("XF#31NN",strVerify[1]);
            strContent = strContent.replace("8U0_1GH$",strVerify[2]);

            final String content = strContent;
            System.out.println(SMTPConfig.getEmailHost());

            String [] strEmail = {email};
            String [] strImage = null;

            SMTPCore smtpCore = new SMTPCore();
            Thread newThread = new Thread(() -> smtpCore.sendMailWithAttachment(
                    strEmail,
                    subject,
                    content,
                    "TLS",
                    strImage
            ));

            newThread.start();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void sendEmail(String fullname, String message, String email) {
        try{
            String subject = "FoundIt - Ada pesan dari " + fullname;

            String [] strEmail = {email};

            SMTPCore smtpCore = new SMTPCore();
            Thread newThread = new Thread(() -> smtpCore.sendMailWithAttachment(
                    strEmail,
                    subject,
                    message,
                    "TLS",
                    null
            ));

            newThread.start();
        }catch (Exception e){
            logger.error("Error sending email: {}", e.getMessage());
        }
    }
}

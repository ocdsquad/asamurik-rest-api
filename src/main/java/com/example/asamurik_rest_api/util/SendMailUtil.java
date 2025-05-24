package com.example.asamurik_rest_api.util;

import com.example.asamurik_rest_api.config.SMTPConfig;
import com.example.asamurik_rest_api.core.SMTPCore;

public class SendMailUtil {
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
}

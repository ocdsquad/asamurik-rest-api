package com.asamurik_rest_api.core;

import com.asamurik_rest_api.config.SMTPConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.Date;
import java.util.Properties;

public class SMTPCore {
    Properties prop;

    private Message message;
    private Session session;
    private String destination;
    private StringBuilder stringBuilder = new StringBuilder();
    private MimeBodyPart messageBodyPart;
    private Multipart multipart;

    private String[] mailTo;
    private String subject;
    private String content;
    private String[] attachment;


    private Properties getTLSProp() {
        prop = new Properties();
        prop.put("mail.smtp.host", SMTPConfig.getEmailHost());
        prop.put("mail.smtp.port", SMTPConfig.getEmailPortTLS());
        prop.put("mail.smtp.auth", SMTPConfig.getEmailAuth());
        prop.put("mail.smtp.starttls.enable", SMTPConfig.getEmailStartTLSEnable());
        prop.put("mail.smtp.timeout", (Long.parseLong(SMTPConfig.getEmailSMTPTimeout()) * 1000));
        prop.put("mail.smtp.connectiontimeout", (Long.parseLong(SMTPConfig.getEmailSMTPTimeout()) * 1000));
        prop.put("mail.smtp.writetimeout", (Long.parseLong(SMTPConfig.getEmailSMTPTimeout()) * 1000));

        return prop;
    }

    private Properties getSSLProp() {
        prop = new Properties();
        prop.put("mail.smtp.host", SMTPConfig.getEmailHost());
        prop.put("mail.smtp.port", SMTPConfig.getEmailPortSSL());
        prop.put("mail.smtp.auth", SMTPConfig.getEmailAuth());
        prop.put("mail.smtp.socketFactory.port", SMTPConfig.getEmailStartTLSEnable());
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.timeout", (Long.parseLong(SMTPConfig.getEmailSMTPTimeout()) * 1000));
        prop.put("mail.smtp.connectiontimeout", (Long.parseLong(SMTPConfig.getEmailSMTPTimeout()) * 1000));
        prop.put("mail.smtp.writetimeout", (Long.parseLong(SMTPConfig.getEmailSMTPTimeout()) * 1000));

        return prop;
    }

    public boolean sendMailWithAttachment(
            String[] mailTo,
            String subject,
            String contentMessage,
            String layer,
            String[] attachFiles
    ) {
        Properties execProp;

        if (layer.equals("SSL")) {
            execProp = getSSLProp();
        } else {
            execProp = getTLSProp();
        }

        stringBuilder.setLength(0);

        for (String string : mailTo) {
            stringBuilder.append(string).append(",");
        }

        destination = stringBuilder.toString();
        destination = destination.substring(0, destination.length() - 1);

        session = Session.getInstance(execProp,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SMTPConfig.getEmailUserName(), SMTPConfig.getEmailPassword());
                    }
                });

        try {
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTPConfig.getEmailUserName()));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(destination)
            );
            message.setSentDate(new Date());
            message.setSubject(subject);

            // creates message part
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(contentMessage, "text/html");

            // creates multi-part
            multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // adds attachments
            if (attachFiles != null) {
                for (String filePath : attachFiles) {
                    MimeBodyPart attachPart = new MimeBodyPart();

                    try {
                        attachPart.attachFile(filePath);
                    } catch (Exception ex) {
                        throw new Exception(ex.getMessage());
                    }

                    multipart.addBodyPart(attachPart);
                }
            }

            // sets the multipart as e-mail's content
            message.setContent(multipart);

            // sends the e-mail
            Transport.send(message);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}

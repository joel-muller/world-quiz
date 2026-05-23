/* (C)2026 */
package com.worldquiz.service;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {
    @Value("${mailgun.api.key}")
    private String apiKey;

    @Value("${mailgun.url}")
    private String url;

    @Value("${mailgun.sender.email}")
    private String senderEmail;

    public void sendEmailVerificationMail(
            String username, String email, String token, String frontendBaseUrl)
            throws UnirestException {
        String subject = "World Quiz Email verification";
        String body =
                "Hello "
                        + username
                        + "\n\nYour link to verify the email: "
                        + frontendBaseUrl
                        + "/verify/"
                        + token;
        sendMail(username, email, subject, body);
    }

    public void sendPasswordResetMail(
            String username, String email, String token, String frontendBaseUrl)
            throws UnirestException {
        String subject = "World Quiz Email Password Reset";
        String body =
                "Hello "
                        + username
                        + "\n\nYour link to reset the password: "
                        + frontendBaseUrl
                        + "/reset-password/"
                        + token;
        sendMail(username, email, subject, body);
    }

    public void sendMail(String username, String email, String subject, String body)
            throws UnirestException {
        HttpResponse<JsonNode> request =
                Unirest.post(url)
                        .basicAuth("api", apiKey)
                        .queryString("from", "World Quiz <" + senderEmail + ">")
                        .queryString("to", username + "<" + email + ">")
                        .queryString("subject", subject)
                        .queryString("text", body)
                        .asJson();
        log.info("mail sent successfully");
        log.info(request.getBody().toString());
    }
}

package com.favoriteplace.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Configuration
@ConditionalOnProperty(prefix = "fcm", name = "enabled", havingValue = "true")
public class FcmConfig {
    @Value("${fcm.type}")
    private String type;

    @Value("${fcm.project_id}")
    private String projectId;

    @Value("${fcm.private_key_id}")
    private String privateKeyId;

    @Value("${fcm.private_key}")
    private String privateKey;

    @Value("${fcm.client_email}")
    private String clientEmail;

    @Value("${fcm.client_id}")
    private String clientId;

    @Value("${fcm.auth_uri}")
    private String authUri;

    @Value("${fcm.token_uri}")
    private String tokenUri;

    @Value("${fcm.auth_provider_x509_cert_url}")
    private String authProviderX509CertUrl;

    @Value("${fcm.client_x509_cert_url}")
    private String clientX509CertUrl;

    @Value("${fcm.universe_domain}")
    private String universeDomain;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        String jsonKey = String.format(
                "{" +
                        "\"type\":\"%s\"," +
                        "\"project_id\":\"%s\"," +
                        "\"private_key_id\":\"%s\"," +
                        "\"private_key\":\"%s\"," +
                        "\"client_email\":\"%s\"," +
                        "\"client_id\":\"%s\"," +
                        "\"auth_uri\":\"%s\"," +
                        "\"token_uri\":\"%s\"," +
                        "\"auth_provider_x509_cert_url\":\"%s\"," +
                        "\"client_x509_cert_url\":\"%s\"," +
                        "\"universe_domain\":\"%s\"" +
                        "}",
                type,
                projectId,
                privateKeyId,
                privateKey.replace("\\n", "\n"), // 🔥 핵심
                clientEmail,
                clientId,
                authUri,
                tokenUri,
                authProviderX509CertUrl,
                clientX509CertUrl,
                universeDomain
        );

        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ByteArrayInputStream(jsonKey.getBytes())
        );

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }


    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp){
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}

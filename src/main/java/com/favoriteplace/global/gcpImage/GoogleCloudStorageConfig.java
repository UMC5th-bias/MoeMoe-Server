package com.favoriteplace.global.gcpImage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Configuration
public class GoogleCloudStorageConfig {

    @Value("${gcp.type}")
    private String type;

    @Value("${gcp.project_id}")
    private String projectId;

    @Value("${gcp.private_key_id}")
    private String privateKeyId;

    @Value("${gcp.private_key}")
    private String privateKey;

    @Value("${gcp.client_email}")
    private String clientEmail;

    @Value("${gcp.client_id}")
    private String clientId;

    @Value("${gcp.auth_uri}")
    private String authUri;

    @Value("${gcp.token_uri}")
    private String tokenUri;

    @Value("${gcp.auth_provider_x509_cert_url}")
    private String authProviderX509CertUrl;

    @Value("${gcp.client_x509_cert_url}")
    private String clientX509CertUrl;

    @Value("${gcp.universe_domain}")
    private String universeDomain;

    @Bean
    public Storage storage() throws IOException {
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
            type, projectId, privateKeyId, privateKey, clientEmail, clientId,
            authUri, tokenUri, authProviderX509CertUrl, clientX509CertUrl, universeDomain
        );

        GoogleCredentials credentials = GoogleCredentials.fromStream(
            new ByteArrayInputStream(jsonKey.getBytes())
        );

        return StorageOptions.newBuilder()
            .setProjectId(projectId)
            .setCredentials(credentials)
            .build()
            .getService();
    }
}
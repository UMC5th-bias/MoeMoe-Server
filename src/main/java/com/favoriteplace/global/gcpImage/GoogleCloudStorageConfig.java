package com.favoriteplace.global.gcpImage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@Configuration
public class GoogleCloudStorageConfig {

    @Bean
    public Storage storage() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/studious-matrix-412103-04030f986b9b.yml");
        InputStreamReader reader = new InputStreamReader(inputStream);
        Map<String, Object> credentialsMap = new Yaml().load(reader);
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(new ObjectMapper().writeValueAsBytes(credentialsMap)));
        String projectId = "studious-matrix-412103";
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
    }
}
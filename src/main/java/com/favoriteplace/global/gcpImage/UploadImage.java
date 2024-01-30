package com.favoriteplace.global.gcpImage;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadImage {

    private final Storage storage =null;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public String uploadImageToCloud(MultipartFile image) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String ext = image.getContentType();

        //Cloud에 이미지 업로드
        BlobInfo blobInfo = storage.create(
                BlobInfo.newBuilder(bucketName, uuid)
                        .setContentType(ext)
                        .build(),
                image.getInputStream()
        );
        return uuid;
    }
}

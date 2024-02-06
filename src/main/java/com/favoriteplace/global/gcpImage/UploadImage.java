package com.favoriteplace.global.gcpImage;

import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.time.LocalDateTime.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadImage {

    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public String uploadImageToCloud(MultipartFile image) throws IOException {
        String uuid = UUID.randomUUID().toString();
        LocalDateTime now = now();
        log.info(String.valueOf("1 : " + ChronoUnit.MILLIS.between(now, now())));
        String ext = image.getContentType();
        InputStream resizedImage = resize(image, 500, 500);
        log.info(String.valueOf("2 : " + ChronoUnit.MILLIS.between(now, now())));

        //Cloud에 이미지 업로드
        BlobInfo blobInfo = storage.create(
                BlobInfo.newBuilder(bucketName, uuid)
                        .setContentType(ext)
                        .build(),
                //image.getInputStream()
                resizedImage
        );
        log.info(String.valueOf("3 : " +ChronoUnit.MILLIS.between(now, now())));
        return uuid;
    }

    public InputStream resize(MultipartFile multipartFile, int width, int height) throws IOException {
        try (InputStream originalInputStream = multipartFile.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // 이미지 파일을 올바르게 읽어오는지 확인
            if (!isImageFile(multipartFile)) {
                throw new RestApiException(ErrorCode.IMAGE_FORMAT_ERROR);
            }

            BufferedImage originalImage = ImageIO.read(originalInputStream);

            // 이미지를 제대로 읽어왔는지 확인
            if (originalImage == null) {
                throw new IOException("이미지 파일을 읽을 수 없습니다.");
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // 이미지의 비율을 유지하면서 width 또는 height 중 하나를 기준으로 조정
            if (originalWidth > originalHeight) {
                height = (int) Math.round((double) width / originalWidth * originalHeight);
            } else {
                width = (int) Math.round((double) height / originalHeight * originalWidth);
            }

            Thumbnails.of(originalImage)
                    .size(width, height)
                    .outputFormat(getImageFormat(multipartFile))
                    .toOutputStream(outputStream);

            byte[] resizedImageBytes = outputStream.toByteArray();
            return new ByteArrayInputStream(resizedImageBytes);
        }
    }

    private boolean isImageFile(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private String getImageFormat(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            switch (extension.toLowerCase()) {
                case "jpg":
                case "jpeg":
                    return "jpg";
                case "png":
                    return "png";
                case "gif":
                    return "gif";
                // 다른 형식에 대한 처리 추가
                default:
                    return "jpg"; // 기본적으로는 JPG 형식으로 저장
            }
        }
        return "jpg"; // 기본적으로는 JPG 형식으로 저장
    }

}

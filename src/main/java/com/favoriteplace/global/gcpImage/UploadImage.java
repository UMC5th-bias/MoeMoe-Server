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

    private final Storage storage;

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
                //resize(image, 1000, 1000)
                image.getInputStream()
        );
        return uuid;
    }

    /*
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
    */
}

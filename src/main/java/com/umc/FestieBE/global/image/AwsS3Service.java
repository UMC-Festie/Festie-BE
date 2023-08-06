package com.umc.FestieBE.global.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.umc.FestieBE.global.exception.CustomErrorCode.IMAGE_UPLOAD_FAILED;

@Service
@RequiredArgsConstructor
public class AwsS3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    // 이미지 업로드
    public String uploadImgFile(MultipartFile multipartFile) {

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            String originalFileName = multipartFile.getOriginalFilename();
            int index = originalFileName.lastIndexOf(".");
            String ext = originalFileName.substring(index + 1);
            String savedFileName = UUID.randomUUID() + "." + ext;

            amazonS3.putObject(bucket, savedFileName, multipartFile.getInputStream(), metadata);
            return amazonS3.getUrl(bucket, savedFileName).toString();
        } catch (IOException e) {
            throw new CustomException(IMAGE_UPLOAD_FAILED);
        }
    }

    // 이미지 삭제
    public void deleteImage(String imgUrl)  {
        // imgUrl 에서 해당 이미지 객체의 키 추출
        int lastSlashIndex = imgUrl.lastIndexOf("/");
        String key = imgUrl.substring(lastSlashIndex + 1);

        amazonS3.deleteObject(bucket, key);
    }
}

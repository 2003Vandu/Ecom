package com.eComm.eComm.Service.Implementation;

import com.eComm.eComm.Service.FIleUploadService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

//@Service
//@RequiredArgsConstructor
//public class FileUploadServiceImpl implements FIleUploadService
//{
//    @Value("${aws.bucket.name}")
//    private String bucketName;
//
//
//    private final S3Client s3Client;
//
//    @Override
//    public String uploadFile(MultipartFile file) {
//
//        String filenameExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
//        String key = UUID.randomUUID().toString()+"."+filenameExtension;
//
//        try{
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(key)
//                    .acl("public-read")
//                    .contentType(file.getContentType())
//                    .build();
//            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
//
//            if(response.sdkHttpResponse().isSuccessful()){
//
//                // Let the SDK build the correct URL for you
//                return s3Client.utilities()
//                        .getUrl(GetUrlRequest.builder().bucket(bucketName).key(key).build())
//                        .toString();
//
//            }else {
//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"An error occur while uploding file ");
//            }
//        }catch (IOException e){
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occur while uploding file ");
//
//        }
//    }
//
//    @Override
//    public boolean deleteFile(String imgUrl) {
//        String filename = imgUrl.substring(imgUrl.lastIndexOf("/")+1);
//
//        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                .bucket(bucketName)
//                .key(filename)
//                .build();
//        s3Client.deleteObject(deleteObjectRequest);
//        return  true;
//
//    }
//}


@Service
public class FileUploadServiceImpl implements FIleUploadService
{
    @Value("${aws.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    // Explicitly inject the "supabaseS3Client" bean we configured
    public FileUploadServiceImpl(@Qualifier("supabaseS3Client") S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile file) {

        String filenameExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String key = UUID.randomUUID().toString()+"."+filenameExtension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    // Note: Removed .acl("public-read") because Supabase doesn't support AWS S3 ACL headers.
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            if(response.sdkHttpResponse().isSuccessful()) {

                // 2. Get the raw S3 URL from the SDK
                String rawS3Url = s3Client.utilities()
                        .getUrl(GetUrlRequest.builder().bucket(bucketName).key(key).build())
                        .toString();

                // 2. CONVERT IT TO PUBLIC CDN FORMAT IN JAVA
                return rawS3Url
                        .replace(".storage.supabase.co", ".supabase.co")
                        .replace("/storage/v1/s3", "/storage/v1/object/public");

            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"An error occur while uploding file ");
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occur while uploding file ");
        }
    }

    @Override
    public boolean deleteFile(String imgUrl) {
        if (imgUrl == null || !imgUrl.contains("/")) {
            return false;
        }
        String filename = imgUrl.substring(imgUrl.lastIndexOf("/")+1);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        return  true;
    }
}
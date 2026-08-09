package com.eComm.eComm.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsCofig
{
    @Value("${aws.access.key}")
    private String accesskey;

    @Value("${aws.secret.key}")
    private String secretkey;

    @Value("${aws.region}")
    private String region;

    @Bean(name = "awsS3Client")
    @Primary// default clint for AWS
    public S3Client s3Client(){

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accesskey,secretkey)))
                .build();

    }
}

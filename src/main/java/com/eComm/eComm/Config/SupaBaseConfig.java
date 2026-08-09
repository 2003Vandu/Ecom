package com.eComm.eComm.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class SupaBaseConfig {

    @Value("${aws.access.key}")
    private String accesskey;

    @Value("${aws.secret.key}")
    private String secretkey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.endpoint}")
    private String endpoint;

    @Bean(name = "supabaseS3Client") // Named Bean to isolate it from standard AWS
    public S3Client supabaseS3Client() {
        S3Configuration s3Configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)       // Required by Supabase for path routing
                .checksumValidationEnabled(false)   // Turns off AWS validation checks
                .chunkedEncodingEnabled(false)      // Disables chunk validation headers
                .build();

        return S3Client.builder()
                .region(Region.of(region.trim()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accesskey.trim(), secretkey.trim())
                ))
                .endpointOverride(URI.create(endpoint.trim()))
                .serviceConfiguration(s3Configuration)
                .build();
    }
}
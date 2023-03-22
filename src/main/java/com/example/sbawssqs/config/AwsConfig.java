package com.example.sbawssqs.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;

@Configuration
public class AwsConfig {

    
    private String accessKey = "<accessKey>";

    
    private String secretKey = "<secretkey>";

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    @Bean
    public Region awsRegion() {
        return Region.US_EAST_2; // Change to the region you need
    }
}

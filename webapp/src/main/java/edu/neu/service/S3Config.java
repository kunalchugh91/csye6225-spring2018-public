package edu.neu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

@Configuration
public class S3Config {


    @Bean
    public AmazonS3 s3client() {

        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        // BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsId, awsKey);
        // AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
        //         .withRegion(Regions.fromName(region))
        //         .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        //         .build();

        return s3Client;
    }
}

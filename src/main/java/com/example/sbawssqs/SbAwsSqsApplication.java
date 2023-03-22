package com.example.sbawssqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;


import java.util.HashMap;
import java.util.List;


@SpringBootApplication
@RestController
public class SbAwsSqsApplication {
	
	private static final SqsClient sqsClient = SqsClient.builder().build();
    private static final SnsClient snsClient = SnsClient.builder().build();

    private static String queueUrl = "https://sqs.us-east-2.amazonaws.com/694141436833/samplequeue";
    private static String dlqQueueUrl;

	public static void main(String[] args) {
		SpringApplication.run(SbAwsSqsApplication.class, args);
	}

	@GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    	return String.format("Hello %s!", name);
    }

	@GetMapping("/send")
	public String send(){
		SendMessageRequest request = SendMessageRequest.builder().queueUrl(queueUrl).messageBody("hello sqs from sandesh").build();
        SendMessageResponse response = sqsClient.sendMessage(request);
		return response.messageId();
	}

    @GetMapping("/sendtopic")
	public String sendtopic(@RequestParam(value = "msg", defaultValue = "Hello, world!") String message){
        PublishRequest request = PublishRequest.builder()
            .topicArn("arn:aws:sns:us-east-2:694141436833:cummins-test")
            .message(message)
            .build();
            PublishResponse result = snsClient.publish(request);
		return "Message sent to topic messageid:"+ result.messageId();
	}

	@GetMapping("/receive")
	public String receive(){
		ReceiveMessageRequest request = ReceiveMessageRequest.builder().queueUrl(queueUrl).maxNumberOfMessages(1).build();
        List<Message> messages = sqsClient.receiveMessage(request).messages();
		String response = "No messages found in queue.";
        if (messages.size() > 0) {
            Message message = messages.get(0);
            String messageBody = message.body();
            String receiptHandle = message.receiptHandle();
            response = "Message body: " + messageBody  ; //+ " \n Receipt handle: " + receiptHandle;
        } 
		return response;
	}


    @GetMapping("listQueues")
    public String listQueues() {
        ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder()
                .queueNamePrefix("sample")
                .build();
        ListQueuesResponse listQueuesResponse = sqsClient.listQueues(listQueuesRequest);

        String queues = "hello there : \n" ;
        for (String url : listQueuesResponse.queueUrls()) {
            queues += url + "\n";
        }
		
        return queues;
    }

    @GetMapping("/getcreds")
    public String getcreds() {
        StsClient stsClient = StsClient.builder().build();
        GetCallerIdentityResponse response = stsClient.getCallerIdentity();
        return "Account: " + response.account() + " \n ARN: " + response.arn();
    }

    // @GetMapping("/createQueue")
    // public void createQueue() {
    //     String queueName = QUEUE_PREFIX + System.currentTimeMillis();
    
    //     CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
    //             .queueName(queueName)
    //             .build();
    
    //     SQS_CLIENT.createQueue(createQueueRequest);
    
    //     GetQueueUrlResponse getQueueUrlResponse =
    //                 SQS_CLIENT.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
    //     queueUrl = getQueueUrlResponse.queueUrl();
    // }

}



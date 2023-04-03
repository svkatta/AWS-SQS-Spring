import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SqsAsyncListener {

    private final SqsAsyncClient sqsAsyncClient;
    private final String queueUrl;

    public SqsAsyncListener(SqsAsyncClient sqsAsyncClient, String queueUrl) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.queueUrl = queueUrl;
    }

    public CompletableFuture<Void> listenAsync() {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(20)
                .maxNumberOfMessages(10)
                .build();

        return sqsAsyncClient.receiveMessage(receiveMessageRequest)
                .thenCompose(this::processMessagesAsync);
    }

    private CompletableFuture<Void> processMessagesAsync(ReceiveMessageResponse receiveMessageResponse) {
        List<Message> messages = receiveMessageResponse.messages();
        if (messages.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.allOf(messages.stream()
                .map(this::processMessageAsync)
                .toArray(CompletableFuture[]::new))
                .thenCompose(v -> listenAsync());
    }

    private CompletableFuture<Void> processMessageAsync(Message message) {
        // Do something with the message
        System.out.println("Received message with body: " + message.body());

        // Delete the message from the queue
        return sqsAsyncClient.deleteMessage(queueUrl, message.receiptHandle());
    }
}

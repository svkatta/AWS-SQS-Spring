import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageSystemAttributeName;

public class SqsListener {

    private final SqsAsyncClient sqsAsyncClient;
    private final String queueUrl;
    private final String deadLetterQueueUrl;

    public SqsListener(String queueUrl, String deadLetterQueueUrl, Region region) {
        this.sqsAsyncClient = SqsAsyncClient.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.queueUrl = queueUrl;
        this.deadLetterQueueUrl = deadLetterQueueUrl;
    }

    public void start() {
        sqsAsyncClient.receiveMessage(r -> r.queueUrl(queueUrl)).subscribe(response -> {
            for (Message message : response.messages()) {
                if (message.attributes().containsKey(MessageSystemAttributeName.DEAD_LETTER_SOURCE_QUEUE.toString())) {
                    handleDeadLetter(message);
                } else {
                    handleMessage(message);
                }
            }
        });
    }

    public void stop() {
        sqsAsyncClient.close();
    }

    private void handleMessage(Message message) {
        // handel the message here
    }

    private void handleDeadLetter(Message message) {
        // process the message from the dead letter queue here
        // you can also send the message to another queue or delete it
    }
}

# AWS-SQS-Spring

This is basic working prototype that can publish to SNS topic which is subscribed by SQS Queue and retrive msgs from Queue.

## Endpoints
* `/receive` : get msgs from SQS queue
* `/sendtopic?msg=<msg>` : publish to SNS Topic
* `/hello` : health check

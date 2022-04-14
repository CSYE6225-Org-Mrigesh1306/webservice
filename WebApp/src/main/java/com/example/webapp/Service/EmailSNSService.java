package com.example.webapp.Service;


import com.example.webapp.DAO.UserTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import java.util.Random;

@Service
public class EmailSNSService {

    SnsClient snsClient;

    @Value("${aws.sns.topic.MailNotification.ARN}")
    String snsTopicARN;

    @Autowired
    UserTokenRepository tokenrepo;

    private final static Logger logger = LoggerFactory.getLogger(EmailSNSService.class);

    public void postToTopic(String recipientEmail, String requestType) {

        try {
            System.out.println("in sns postToTopic");
            Random rand = new Random();
            int token = rand.nextInt(10000);
            String snsMessage = requestType + "|" + recipientEmail + "|" + token;

            System.out.println("message generated, now publishing");

            PublishRequest request = PublishRequest.builder()
                    .message(snsMessage)
                    .topicArn(snsTopicARN)
                    .build();

            if (snsClient == null) {
                System.out.println("snsClient object is still   ..........null");
            }
            SnsClient snsClient = SnsClient.builder()
                    .region(Region.US_EAST_1)
                    .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println("Publishing done");
            System.out.println("Message " + result.messageId() + "is successfully published to SNS Topic 'Notification_Email'");
            logger.info("Message " + result.messageId() + " is successfully published to SNS Topic 'Notification_Email'.");

            tokenrepo.storeUserTokenDynamoDB(recipientEmail,token);

        } catch (SnsException e) {
            System.out.println("sns exception: " + e.getMessage());
            e.printStackTrace();

            logger.error("SNS Exception Warning - " + e.getMessage());
        }
    }
}

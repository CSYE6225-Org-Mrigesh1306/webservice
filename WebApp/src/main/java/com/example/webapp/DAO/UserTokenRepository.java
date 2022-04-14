package com.example.webapp.DAO;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class UserTokenRepository {

    public void storeUserTokenDynamoDB(String recipientEmail,int token){

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDb = new DynamoDB(client);
        Table table = dynamoDb.getTable("UsernameTokenTable");
        long now = Instant.now().getEpochSecond(); //
        long ttl = 120;
        Item item = new Item()
                .withPrimaryKey("emailID", recipientEmail)
                .with("Token",token)
                .with("TimeToLive",ttl + now);
        PutItemOutcome outcome = table.putItem(item);
    }
}

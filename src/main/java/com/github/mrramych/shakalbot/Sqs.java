package com.github.mrramych.shakalbot;

import com.github.mrramych.json.Json;
import com.github.mrramych.json.types.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import static com.github.mrramych.json.Json.*;
import static com.github.mrramych.shakalbot.Utils.response;
import static java.net.HttpURLConnection.HTTP_OK;

public class Sqs {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sqs.class);

    private final SqsClient client;

    public Sqs() {
        client = SqsClient.builder().build();
    }

    public void sendToSqs(Json json) throws IOException {
        if (!json.isString()) {
            LOGGER.warn("Expected body to be string, but received {}", json.getType());
            throw new IllegalArgumentException();
        }

        JsonObject input = parse(json.getAsString().string).getAsObject();

        JsonObject message = parse(new String(
                Base64.getDecoder().decode(input.getObject("message").getString("data").string)
        )).getAsObject();


        LOGGER.info("Your SQS message is: {}", message.toString());

        client.sendMessage(SendMessageRequest.builder()
                .queueUrl(Utils.getAndCheckVariable("sqs_url"))
                .messageBody(message.toString())
                .build());

        LOGGER.info("Done");
    }

}
package com.yallanow.analyticsservice.messagehandlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.yallanow.analyticsservice.exceptions.ItemServiceException;
import com.yallanow.analyticsservice.services.ItemService;
import com.yallanow.analyticsservice.utils.ItemConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;


@Component
public class ItemMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(ItemMessageHandler.class);
    private final ItemService itemService;
    private final ObjectMapper objectMapper;
    private final ItemConverter itemConverter;

    private Subscriber subscriber;

    /**
     * Constructs a new ItemMessageHandler with the specified dependencies.
     *
     * @param itemService    the ItemService used for handling item-related operations
     * @param objectMapper  the ObjectMapper used for JSON serialization and deserialization
     * @param itemConverter the ItemConverter used for converting items between different formats
     */
    @Autowired
    public ItemMessageHandler(ItemService itemService, ObjectMapper objectMapper, ItemConverter itemConverter) {
        this.itemService = itemService;
        this.objectMapper = objectMapper;
        this.itemConverter = itemConverter;
    }

    /**
     * Handles the incoming message from the event input channel.
     *
     *
     * @throws IllegalArgumentException if the message does not contain an AcknowledgeablePubsubMessage.
     */
    @PostConstruct
    public void subscribeGroups() throws IOException, RuntimeException {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of("yallanow-413400", "event-sub");

        MessageReceiver receiver = (PubsubMessage message, AckReplyConsumer consumer) -> {
            String jsonData = message.getData().toStringUtf8();
            String operationType = message.getAttributesMap().get("operationType");
            try {
                JsonNode event = objectMapper.readTree(jsonData);
                Map<String, Object> dataMap = objectMapper.convertValue(event, Map.class);

                logger.info("Received message with operation: {}", operationType);

                switch (operationType) {
                    case "ADD":
                        itemService.addItem(itemConverter.getItemFromPubsubMessage(dataMap));
                        break;
                    case "UPDATE":
                        itemService.updateItem(itemConverter.getItemFromPubsubMessage(dataMap));
                        break;
                    case "DELETE":
                        itemService.deleteItem(itemConverter.getIdFromPubSubMessage(dataMap));
                        break;
                    default:
                        logger.error("Invalid operation type: {}", operationType);
                }
            } catch (IOException | ItemServiceException e) {
                logger.error("Error processing message: {}", e.getMessage(), e);
            } finally {
                consumer.ack();
                logger.info("Acknowledged message with operation: {}", operationType);
            }
        };

        subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
        subscriber.addListener(new Subscriber.Listener() {
                                   public void failed(Subscriber.State from, Throwable failure) {
                                       logger.error("Subscriber failed: {}", failure.getMessage(), failure);
                                   }
                               },
                MoreExecutors.directExecutor());
        subscriber.startAsync().awaitRunning();
        logger.info("Subscriber for events started");
    }

    /**
     * Shuts down the Google Cloud Pub/Sub subscriber.
     */
    @PreDestroy
    public void stopSubscriber(){
        if(subscriber != null){
            subscriber.stopAsync().awaitTerminated();
        }
    }

}


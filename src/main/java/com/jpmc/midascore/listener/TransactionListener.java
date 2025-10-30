package com.jpmc.midascore.listener;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service  // ← Tells Spring Boot: "This is a service component, manage it for me"
public class TransactionListener {

    // Logger to print transaction info to console
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    // This is your listener method!
    @KafkaListener(
            topics = "${general.kafka-topic}",  // ← Reads "transactions" from application.yml
            groupId = "${spring.kafka.consumer.group-id}"  // ← Reads consumer group from config
    )
    public void listen(Transaction transaction) {
        // This method is automatically called when a transaction arrives!
        logger.info("Received transaction: {}", transaction);

        // For now, just log it. In Task 3, you'll add validation and database storage
    }
}

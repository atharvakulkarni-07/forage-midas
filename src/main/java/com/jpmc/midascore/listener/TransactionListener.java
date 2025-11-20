package com.jpmc.midascore.listener;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jpmc.midascore.DebugHelper;

@Service
public class TransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    @Autowired
    private DebugHelper debugHelper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRepository;

    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void listen(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);

        // Step 1: Validate and retrieve sender
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if (sender == null) {
            logger.warn("Sender not found: {}", transaction.getSenderId());
            return;
        }

        // Step 2: Validate and retrieve recipient
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (recipient == null) {
            logger.warn("Recipient not found: {}", transaction.getRecipientId());
            return;
        }

        // Step 3: Check if sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Insufficient balance for sender: {}. Balance: {}, Amount: {}",
                    transaction.getSenderId(), sender.getBalance(), transaction.getAmount());
            return;
        }

        // Step 4: Transaction is valid - update balances
        logger.info("Transaction validated. Processing...");

        // Deduct from sender
        sender.setBalance(sender.getBalance() - transaction.getAmount());

        // Add to recipient
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());

        // Step 5: Save updated balances to database
        userRepository.save(sender);
        userRepository.save(recipient);

        // Step 6: Create and save transaction record
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount());
        transactionRepository.save(record);

        logger.info("Transaction processed successfully. New balances - Sender: {}, Recipient: {}",
                sender.getBalance(), recipient.getBalance());

        // Step 7: Query waldorf's balance after every transaction
        UserRecord waldorf = debugHelper.findUserByName("waldorf");
        if (waldorf != null) {
            logger.info("WALDORF CURRENT BALANCE: {}", waldorf.getBalance());
        }
    }
}

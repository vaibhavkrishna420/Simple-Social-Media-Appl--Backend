package com.smedia.smedia.repository;

import com.smedia.smedia.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepo extends MongoRepository<Message, String> {

    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
            Long sender1, Long receiver1,
            Long sender2, Long receiver2
    );
}

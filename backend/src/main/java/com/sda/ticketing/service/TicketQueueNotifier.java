package com.sda.ticketing.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketQueueNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public TicketQueueNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyQueueChanged() {
        messagingTemplate.convertAndSend("/topic/queue-updates", "QUEUE_UPDATED");
    }
}


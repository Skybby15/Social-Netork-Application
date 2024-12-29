package com.projectmap2.Domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long>{
    private Long sender;
    private List<Long> receivers = new ArrayList<Long>();
    private String text;
    private Message replyMessage; // mesajul caruia i se da reply
    private LocalDateTime date;

    public Message(Long sender, Long receiver, String text , LocalDateTime date) {
        this.sender = sender;
        receivers.add(receiver);
        this.text = text;
        replyMessage = null;
        this.date = date;
    }

    public Message(Long sender, Long receiver, String text, Message replyMessage , LocalDateTime date) {
        this.sender = sender;
        receivers.add(receiver);
        this.text = text;
        this.replyMessage = replyMessage;
        this.date = date;
    }

    public Message(Long sender, List<Long> receivers, String text, LocalDateTime date) {
        this.sender = sender;
        this.receivers = receivers;
        this.text = text;
        this.date = date;
        this.replyMessage = null;
    }

    public Message(Long sender, List<Long> receivers, String text,  Message replyMessage , LocalDateTime date) {
        this.sender = sender;
        this.receivers = receivers;
        this.text = text;
        this.date = date;
        this.replyMessage = replyMessage;
    }

    public Long getSender() {
        return sender;
    }

    public List<Long> getReceivers() {
        return receivers;
    }

    public String getText() {
        return text;
    }

    public Message getReplyMessage() {
        return replyMessage;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setReceivers(List<Long> receivers) {
        this.receivers = receivers;
    }
}

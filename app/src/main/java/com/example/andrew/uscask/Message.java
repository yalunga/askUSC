package com.example.andrew.uscask;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

public class Message implements Serializable {

    private static final long serialVersionUID = -4030241954210383602L;

    private String type;
    private String data;
    private Set<String> voters = new HashSet<>();
    private String messageID;
    private String classID;
    private String sender;
    private String TIMESTAMP;

    // Use for type == "NewMessage"
    // This is an actual message containing the question.
    public Message(String data, String classID, String messageID, String sender) {
        this.type = "NewMessage";
        this.messageID = messageID;
        this.classID = classID;
        TIMESTAMP = setTimeStamp();
        this.sender = sender;
        this.data = data;
        vote(sender);
    }

    // Use for type == "Vote"
    // This is only used for votes in the WebSocket. Also Message class for convenience.
    public Message(String classID, String messageID, String sender) {
        this.type = "Vote";
        this.messageID = messageID;
        this.sender = sender;
        this.classID = classID;
    }

    public String setTimeStamp() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no time-zone offset
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getVotes() {
        return voters.size();
    }

    public void setVoters(Set<String> voters) {
        this.voters = voters;
    }

    public void setVoters(String votersCSV) {
        String[] temp = votersCSV.split(",");
        voters = new HashSet<String>(Arrays.asList(temp));
    }

    public String votersToCSV() {
        return String.join(",", voters);
    }

    // Acts as a toggle.
    public boolean vote(String userID) {
        if (voters.contains(userID)) {
            voters.remove(userID);
            return false;
        } else {
            voters.add(userID);
            return true;
        }
    }

    public String getMessageID() {
        return messageID;
    }

    public String getClassID() {
        return classID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(String t) {
        TIMESTAMP = t;
    }


}
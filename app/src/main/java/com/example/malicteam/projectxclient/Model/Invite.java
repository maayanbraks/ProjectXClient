package com.example.malicteam.projectxclient.Model;

/**
 * Created by Charcon on 07/03/2018.
 */

public class Invite {
    private String eventId;
    private String userId;
    private String inviteFromId;


    public Invite(String eventId, String userId, String inviteFromId) {
        this.eventId = eventId;
        this.userId = userId;
        this.inviteFromId = inviteFromId;
    }

    public void setInviteFrom(String _inviteFromId) {
        this.inviteFromId = _inviteFromId;
    }

    public String getInviteFromId() {
        return this.inviteFromId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getEventId() {
        return this.eventId;
    }

    public String getuserId() {
        return this.userId;
    }


}

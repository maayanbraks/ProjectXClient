package com.example.malicteam.projectxclient.Model;

/**
 * Created by Charcon on 07/03/2018.
 */

public class Invite {
    private String eventId;
    private String email;
    private String inviteFrom;


    public Invite(String eventId, String email, String inviteFrom) {
        this.eventId = eventId;
        this.email = email;
        this.inviteFrom = inviteFrom;
    }

    public void setInviteFrom(String _inviteFrom) {
        this.inviteFrom = _inviteFrom;
    }

    public String getInviteFrom() {
        return this.inviteFrom;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getEventId() {
        return this.eventId;
    }

    public String getEmail() {
        return this.email;
    }
}

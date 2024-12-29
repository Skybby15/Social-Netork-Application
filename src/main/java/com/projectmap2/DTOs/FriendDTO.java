package com.projectmap2.DTOs;

import com.projectmap2.Domain.FriendshipStatus;
import com.projectmap2.Domain.Utilizator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FriendDTO {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Utilizator friend;
    private final LocalDateTime date;
    private final FriendshipStatus status;

    public FriendDTO(Utilizator friend, LocalDateTime date, FriendshipStatus status) {
        this.friend = friend;
        this.date = date;
        this.status = status;
    }

    public Utilizator getFriend()
    {
        return friend;
    }

    public String getFriendUsername()
    {
        return friend.getUserName();
    }

    public String getFriendFirstName()
    {
        return friend.getFirstName();
    }

    public String getFriendLastName()
    {
        return friend.getLastName();
    }

    public LocalDateTime getDate()
    {
        return date;
    }

    public FriendshipStatus getStatus()
    {
        return status;
    }

}

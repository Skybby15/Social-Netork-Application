package com.projectmap2.Domain;

import com.projectmap2.Controllers.GraphicController;
import com.projectmap2.Controllers.MainController;
import com.sun.tools.javac.Main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Prietenie extends Entity<Tuple<Long,Long>> {
    Utilizator user1;
    Utilizator user2;
    LocalDateTime date;
    FriendshipStatus status;


    public Prietenie(Utilizator user1,Utilizator user2, LocalDateTime friendDate,FriendshipStatus status) {
        this.user1 = user1;
        this.user2 = user2;
        date = friendDate;
        this.status = status;
        super.setId(new Tuple<>(user1.getId(), user2.getId()));

    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
    public FriendshipStatus getStatus() {
        return status;
    }

    public Utilizator getUser1() {
        return user1;
    }

    public Utilizator getUser2() {
        return user2;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public String getFriendUsername()
    {
        if(MainController.loggedUser.getId().equals(user1.getId()))
            return user2.getUserName();
        else
            return user1.getUserName();
    }

    public Long getFriendId()
    {
        if (MainController.loggedUser.getId().equals(user1.getId()))
            return user2.getId();
        else
            return user1.getId();
    }

    @Override
    public String toString() {
        return "[ Prietenie:( "+user1.getUserName()+ ":" + user1.getId()+" ; "+user2.getUserName()+":"+user2.getId()+" ) date: "+this.date+
                " status: "+ status.toString()+ " ]";
    }
}
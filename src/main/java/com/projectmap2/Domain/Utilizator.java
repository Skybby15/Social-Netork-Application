package com.projectmap2.Domain;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Utilizator extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String userName;
    private String parola;
    public static AtomicLong idGenerator = new AtomicLong(0);

    public Utilizator(String firstName, String lastName,String userName, String parola) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.parola = parola;
        this.setId(idGenerator.incrementAndGet()-1);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return  "id='" + getId() + '\''+
                ",firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", parola='" + parola + '\'' ;
    }

    public String getParola() {
        return parola;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator that)) return false;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getParola().equals(that.getParola()) &&
                getUserName().equals(that.getUserName());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(),getParola(),getUserName());
    }
}
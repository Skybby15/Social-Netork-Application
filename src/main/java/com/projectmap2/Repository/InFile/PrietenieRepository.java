package com.projectmap2.Repository.InFile;

import com.projectmap2.Domain.FriendshipStatus;
import com.projectmap2.Domain.Tuple;
import com.projectmap2.Domain.Utilizator;
import com.projectmap2.Domain.Validators.Validator;
import com.projectmap2.Domain.Prietenie;

import java.time.LocalDateTime;

public class PrietenieRepository extends AbstractFileRepository<Tuple<Long,Long>,Prietenie> {
    public PrietenieRepository(Validator<Prietenie> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public Prietenie createEntity(String line)
    {
        String[] split = line.split(";");
        Utilizator user1 = createUser1(split);
        Utilizator user2 = createUser2(split);
        Prietenie prietenie = new Prietenie(user1,user2,LocalDateTime.parse(split[10]),FriendshipStatus.valueOf(split[11]));
        Tuple<Long,Long> Id = new Tuple<>(Long.parseLong(split[0]),Long.parseLong(split[1]));
        prietenie.setId(Id);

        return prietenie;
    }

    private Utilizator createUser1(String[] splitLine)
    {
        Long user1Id = Long.parseLong(splitLine[0]);
        String firstName = splitLine[2];
        String lastName = splitLine[3];
        String userName = splitLine[4];
        String parola = splitLine[5];

        Utilizator user1 = new Utilizator(firstName,lastName,userName,parola);
        user1.setId(user1Id);
        return user1;
    }

    private Utilizator createUser2(String[] splitLine)
    {
        Long user1Id = Long.parseLong(splitLine[1]);
        String firstName = splitLine[6];
        String lastName = splitLine[7];
        String userName = splitLine[8];
        String parola = splitLine[9];

        Utilizator user1 = new Utilizator(firstName,lastName,userName,parola);
        user1.setId(user1Id);
        return user1;
    }

    public String saveEntity(Prietenie entity)
    {
        Long stanga = entity.getId().getLeft();
        Long dreapta = entity.getId().getRight();
        Utilizator user1 = entity.getUser1();
        Utilizator user2 = entity.getUser2();
        FriendshipStatus status = entity.getStatus();
        LocalDateTime data = entity.getDate();

        return stanga.toString() + ";" + dreapta.toString() + ";"+
                user1.getFirstName() +";" + user1.getLastName() + ";" +
                user2.getFirstName() +";" + user2.getLastName() + ";" +
                data.toString() + ";" + status.toString();
    }

}
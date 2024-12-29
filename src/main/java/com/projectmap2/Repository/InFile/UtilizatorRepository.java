package com.projectmap2.Repository.InFile;

import com.projectmap2.Domain.Utilizator;
import com.projectmap2.Domain.Validators.Validator;

import java.util.concurrent.atomic.AtomicLong;

public class UtilizatorRepository extends AbstractFileRepository<Long, Utilizator>{
    public UtilizatorRepository(Validator<Utilizator> validator, String fileName) {
        super(validator, fileName);
        var users = super.findAll();
        Long maxId = null;
        for (Utilizator utilizator : super.findAll()) {
            if (maxId == null)
                maxId = utilizator.getId();
            else if (utilizator.getId() > maxId) {
                maxId = utilizator.getId();
            }
        }
        if (maxId == null)
            Utilizator.idGenerator = new AtomicLong(0);
        else
            Utilizator.idGenerator = new AtomicLong(maxId + 1);
    }

    @Override
    public Utilizator createEntity(String line) {
        String[] splited = line.split(";");
        Utilizator u = new Utilizator(splited[1], splited[2],splited[3],splited[4]);
        u.setId(Long.parseLong(splited[0]));
        return u;
    }

    @Override
    public String saveEntity(Utilizator entity) {
        return entity.getId() + ";" +
                entity.getFirstName() + ";" +
                entity.getLastName() + ";" +
                entity.getUserName() + ";" +
                entity.getParola();

    }
}
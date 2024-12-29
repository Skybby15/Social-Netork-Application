package com.projectmap2.Domain.Validators;

import com.projectmap2.Domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        if (entity.getFirstName().isEmpty() || entity.getLastName().isEmpty()) {
            throw new ValidationException("Numele utilizatorului nu poate fi gol!");
        } else if (entity.getUserName().isEmpty()) {
            throw new ValidationException("Username-ul utilizatorului nu poate fi gol!");
        } else if (entity.getParola().isEmpty()) {
            throw new ValidationException("Parola utilizatorului nu poate fi goala!");
        } else
            for(Character ch : entity.getFirstName().concat(entity.getLastName()).toCharArray())
            {
                if("1234567890".contains(ch.toString()))
                {
                    throw new ValidationException("Numele utilizatorului nu poate contine numere!");
                }
            }
    }
}

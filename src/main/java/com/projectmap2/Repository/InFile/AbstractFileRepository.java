package com.projectmap2.Repository.InFile;

import com.projectmap2.Domain.Entity;
import com.projectmap2.Domain.Validators.Validator;
import com.projectmap2.Repository.InMemory.InMemoryRepository;

import java.io.*;
import java.util.Optional;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String filename;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        filename=fileName;
        loadData();
    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String linie;
            while ((linie = br.readLine()) != null) {
                E e = createEntity(linie);
                super.save(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract E createEntity(String line); // how is it read from file - has to be similar to saveEntity
    public abstract String saveEntity(E entity); // how it is written on file - has to be similar to createEntity

    @Override
    public Optional<E> findOne(ID id) {
        return super.findOne(id);
    }

    @Override
    public Iterable<E> findAll() {
        return super.findAll();
    }

    @Override
    public Optional<E> save(E entity) {
        Optional<E> e = super.save(entity);
        if (e.isEmpty())
            writeToFile();
        return e;
    }

    public void writeToFile() {

        try  ( BufferedWriter writer = new BufferedWriter(new FileWriter(filename))){
            for (E entity: entities.values()) {
                String ent = saveEntity(entity);
                writer.write(ent);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> deletedEntity = super.delete(id); // Use the inherited method from InMemoryRepository
        if (deletedEntity.isPresent()) {
            writeToFile(); // Update the file after deletion
        }
        return deletedEntity;
    }

    @Override
    public Optional<E> update(E entity) {
        Optional<E> updatedEntity = super.update(entity); // Use the inherited method from InMemoryRepository
        if (updatedEntity.isEmpty()) {
            writeToFile(); // Update the file after the update
        }
        return updatedEntity;
    }
}

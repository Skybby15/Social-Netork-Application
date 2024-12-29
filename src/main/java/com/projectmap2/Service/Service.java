package com.projectmap2.Service;

import com.projectmap2.DTOs.FriendDTO;
import com.projectmap2.Domain.*;
import com.projectmap2.Domain.Validators.ValidationException;
import com.projectmap2.Repository.DbRepos.MessageDbRepo;
import com.projectmap2.Repository.DbRepos.PrietenieDbRepo;
import com.projectmap2.Repository.PagedRepository;
import com.projectmap2.Repository.Repository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.projectmap2.Utils.Events.*;
import com.projectmap2.Utils.Graph;
import com.projectmap2.Utils.ObserverClasses.Observable;
import com.projectmap2.Utils.ObserverClasses.Observer;
import com.projectmap2.Utils.Paging.Page;
import com.projectmap2.Utils.Paging.Pageable;

public class Service implements Observable<Event> {
    Repository<Long,Utilizator> userRepo;
    PrietenieDbRepo friendRepo;
    MessageDbRepo messageRepo;

    List<Observer<Event>> observers = new ArrayList<>();

    //observer implementation


    @Override
    public void addObserver(Observer<Event> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<Event> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(Event t) {
        observers.forEach(e -> e.update(t));
    }

    public Service(Repository<Long,Utilizator> userRepository, PrietenieDbRepo friendRepository , MessageDbRepo messageRepository)
    {
        this.userRepo = userRepository;
        this.friendRepo = friendRepository;
        this.messageRepo = messageRepository;
    }

    public void AdaugaUser(String firstName, String lastName, String userName, String parola) throws ValidationException
    {
        Utilizator nouUser = new Utilizator(firstName, lastName, userName, parola);
        Optional<Utilizator> salvat = userRepo.save(nouUser);
        if(salvat.isPresent())
            throw new ValidationException("Un utilizator cu acest username este deja salvat!");
        else
            notifyObservers(new UtilizatorEvent(EntityEventType.ADD, nouUser));
    }

    public void StergeUser(long idUser) throws ValidationException
    {
        Optional<Utilizator> deletedUser = userRepo.delete(idUser);
        if(deletedUser.isPresent())
            notifyObservers(new UtilizatorEvent(EntityEventType.REMOVE, deletedUser.get()));
        else
            throw new ValidationException("Utilizatorul cu acest id nu exista!");

    }

    public void AdaugaPrietenie(long idUser1,long idUser2) throws ValidationException {
        //verific daca exista si o actualizez daca e nevoie
        FriendshipStatus statusToSave = FriendshipStatus.SENT;

        Optional<Prietenie> test1 = friendRepo.findOne(new Tuple<>(idUser1, idUser2));
        Optional<Prietenie> test2 = friendRepo.findOne(new Tuple<>(idUser2, idUser1));
        if (test1.isPresent()) {
            Prietenie prietenie = test1.get();
            if (prietenie.getStatus() == FriendshipStatus.REJECTED) //user1 a fost respins de user2
                throw new ValidationException("Aceasta prietenie a fost respinsa anterior!");
            if (prietenie.getStatus() == FriendshipStatus.ACCEPTED)//user1 a fost acceptat de user2
                throw new ValidationException("Aceasta prietenie deja exista!");
        }

        if (test2.isPresent()) {
            Prietenie prietenie = test2.get();
            if(prietenie.getStatus() == FriendshipStatus.SENT)//user2 i-a trimis o cerere anterior lui user1
            {
                statusToSave = FriendshipStatus.ACCEPTED;
                long auxU1 = idUser1;// se schimba ordinea si se marcheaza ca acceptata!
                idUser1 = idUser2;
                idUser2 = auxU1;
                friendRepo.delete(new Tuple<>(idUser1, idUser2));
            } else if (prietenie.getStatus() == FriendshipStatus.ACCEPTED)//user1 i-a acceptat lui user2
                throw new ValidationException("Aceasta prietenie a fost deja acceptata!");
            else if (prietenie.getStatus() == FriendshipStatus.REJECTED) //user1 i-a respins lui user2
                throw new ValidationException("Aceasta prietenie a fost respinsa anterior!");
        }


        Prietenie deSalvat;
        Optional<Utilizator> user1 = userRepo.findOne(idUser1);
        Optional<Utilizator> user2 = userRepo.findOne(idUser2);
        if (user1.isPresent() && user2.isPresent()) {
            deSalvat = new Prietenie(user1.get(), user2.get(), LocalDateTime.now(), statusToSave);
            deSalvat.setId(new Tuple<>(idUser1, idUser2));//primul a trimis cererea , al doilea trebuie sa accepte/respinga
        } else {
            throw new ValidationException("Numele nu apartine unui utilizator existent!");
        }

        Optional<Prietenie> savedF = friendRepo.save(deSalvat);
        if (savedF.isPresent())
            throw new ValidationException("Deja ai trimis aceasta cerere!");
        else {
            notifyObservers(new PrietenieEvent(EntityEventType.ADD, deSalvat));
        }
    }

    public void actualizeazaPrietenie(Long idUser1, Long idUser2,FriendshipStatus status)
    {   // user1 accepta / respinge cererea lui user2
        Optional<Prietenie> test1 = friendRepo.findOne(new Tuple<>(idUser1, idUser2));
        Optional<Prietenie> test2 = friendRepo.findOne(new Tuple<>(idUser2, idUser1));
        if (test1.isPresent()) {
            Prietenie prietenie = test1.get();
            if (prietenie.getStatus() == FriendshipStatus.REJECTED && status == FriendshipStatus.ACCEPTED)
                throw new ValidationException("Aceasta prietenie a fost respinsa!");
            else if (prietenie.getStatus() == FriendshipStatus.SENT)
                throw new ValidationException("Aceasta cerere a fost trimisa de TINE !");
            else if (prietenie.getStatus() == FriendshipStatus.ACCEPTED)
                throw new ValidationException("Aceasta cerere a fost deja acceptata!");
        }

        if (test2.isPresent()) {
            Prietenie prietenie = test2.get();
            if (prietenie.getStatus() == FriendshipStatus.REJECTED && status == FriendshipStatus.ACCEPTED)
                throw new ValidationException("Aceasta prietenie a fost respinsa!");
            else if (prietenie.getStatus() == FriendshipStatus.SENT)
            {
                Prietenie copyFriend = new Prietenie(prietenie.getUser1(),prietenie.getUser2(),prietenie.getDate(),prietenie.getStatus());
                prietenie.setStatus(status);
                prietenie.setDate(LocalDateTime.now());
                friendRepo.update(prietenie);
                notifyObservers(new PrietenieEvent(EntityEventType.UPDATE, copyFriend));
            }
            else if (prietenie.getStatus() == FriendshipStatus.ACCEPTED)
                throw new ValidationException("Aceasta cerere a fost deja acceptata!");
        }

    }


    public void adaugaPrietenieId_Username(Long idUser1,String Username)
    {
        Optional<Utilizator> searchedUser = Optional.empty();
        for(Utilizator u : userRepo.findAll())
            if(u.getUserName().equals(Username))
                searchedUser = Optional.of(u);

        if(searchedUser.isPresent()) {
            AdaugaPrietenie(idUser1, searchedUser.get().getId());
        }else
            throw  new ValidationException("Nu exista utilizator cu acest username!");

    }

    public void StergePrietenie(Tuple<Long,Long> idFriendship) throws IllegalArgumentException {
        Optional<Prietenie> deletedF = friendRepo.delete(idFriendship);
        Optional<Prietenie> deletedF2 = friendRepo.delete(new Tuple<>(idFriendship.getRight(), idFriendship.getLeft()));
        if (deletedF.isEmpty() && deletedF2.isEmpty())
            throw new ValidationException("Prietenia cu acest id nu exista!");
        else
        {
            if(deletedF.isPresent())
                notifyObservers(new PrietenieEvent(EntityEventType.UPDATE, deletedF.get()));
            else
                deletedF2.ifPresent(prietenie -> notifyObservers(new PrietenieEvent(EntityEventType.UPDATE, prietenie)));
        }
    }

    public Iterable<Utilizator> GetUseri()
    {
        return (Iterable<Utilizator>) userRepo.findAll();
    }

    public Iterable<Prietenie> GetPrietenii()
    {
        return (Iterable<Prietenie>) friendRepo.findAll();
    }

    public ArrayList<ArrayList<Long>> GetComunitati()
    {

        Graph grafComunitati = new Graph(userRepo.findLength());

        grafComunitati.addVertices(userRepo.findAll());

        friendRepo.findAll().forEach(friendship->
        {
            long stanga = friendship.getId().getLeft();
            long dreapta = friendship.getId().getRight();

            grafComunitati.addEdge(stanga,dreapta);
        });

        return grafComunitati.connectedComponents();
    }

    public ArrayList<Long> ComunitateSociabila()
    {
        int longestListPos = 0;
        int maxElements = 0;

        int currentPos = -1;
        for(var list : GetComunitati())
        {
            currentPos++;
            if (list.size() > maxElements)
            {
                maxElements = list.size();
                longestListPos = currentPos;
            }
        }

        return GetComunitati().get(longestListPos);
    }

    public List<FriendDTO> findAllFriendsDTO(Long userId) {
        List<FriendDTO> friendsList = new ArrayList<>();
        friendRepo.findAll().forEach(friendShip->
        {
            if(friendShip.getId().getLeft().equals(userId) && friendShip.getStatus() == FriendshipStatus.ACCEPTED)
            {
                Utilizator friend = friendShip.getUser2();
                LocalDateTime date = friendShip.getDate();
                FriendshipStatus status = friendShip.getStatus();
                friendsList.add(new FriendDTO(friend,date,status));
            }else if(friendShip.getId().getRight().equals(userId) && friendShip.getStatus() == FriendshipStatus.ACCEPTED)
            {
                Utilizator friend = friendShip.getUser1();
                LocalDateTime date = friendShip.getDate();
                FriendshipStatus status = friendShip.getStatus();
                friendsList.add(new FriendDTO(friend,date,status));
            }

        });

        return friendsList;
    }

    public List<FriendDTO> findAllRequestsDTO(Long userId) {
        List<FriendDTO> friendsList = new ArrayList<>();
        friendRepo.findAll().forEach(friendShip->
        {
            if(friendShip.getId().getLeft().equals(userId) && friendShip.getStatus() == FriendshipStatus.SENT)
            {
                Utilizator friend = friendShip.getUser2();
                LocalDateTime date = friendShip.getDate();
                FriendshipStatus status = friendShip.getStatus();
                friendsList.add(new FriendDTO(friend,date,status));
            }else if(friendShip.getId().getRight().equals(userId) && friendShip.getStatus() == FriendshipStatus.SENT)
            {
                Utilizator friend = friendShip.getUser1();
                LocalDateTime date = friendShip.getDate();
                FriendshipStatus status = friendShip.getStatus();
                friendsList.add(new FriendDTO(friend,date,FriendshipStatus.PENDING));
            }

        });

        return friendsList;
    }

    public Utilizator findUserByUsername(String username) {
        Utilizator searchedUser = null;
        for (Utilizator u : userRepo.findAll())
            if (u.getUserName().equals(username)) {
                searchedUser = u;
                break;
            }
        return searchedUser;
    }

    public Iterable<Message> findAllMessages()
    {
        return messageRepo.findAll();
    }

    public Message findMessage(Long messageId)
    {
        Optional<Message> messageO = messageRepo.findOne(messageId);
        if (messageO.isPresent())
            return messageO.get();
        else
            throw new ValidationException("Message does not exist!");
    }

    public void saveMessage(Message message)
    {
        messageRepo.save(message);
        notifyObservers(new MessageEvent(EntityEventType.ADD,message));
    }

    public List<Message> getUserMessages(Long loggedId, Long receiverId) {
        return messageRepo.findAllPrivateMessages(loggedId,receiverId);
    }

    public List<FriendDTO> findAllFriendsDTOPage(Pageable pageable,Long userId) {
        List<FriendDTO> friendsDtoList = new ArrayList<>();
        friendRepo.findAllOnPage(pageable,userId).getElementsOnPage().forEach(friendShip ->
                {
                    if (friendShip.getId().getLeft().equals(userId) && friendShip.getStatus() == FriendshipStatus.ACCEPTED) {
                        Utilizator friend = friendShip.getUser2();
                        LocalDateTime date = friendShip.getDate();
                        friendsDtoList.add(new FriendDTO(friend, date, FriendshipStatus.ACCEPTED));
                    } else if (friendShip.getId().getRight().equals(userId) && friendShip.getStatus() == FriendshipStatus.ACCEPTED) {
                        Utilizator friend = friendShip.getUser1();
                        LocalDateTime date = friendShip.getDate();
                        friendsDtoList.add(new FriendDTO(friend, date, FriendshipStatus.ACCEPTED));
                    }
                }
        );

        return friendsDtoList;
    }
}
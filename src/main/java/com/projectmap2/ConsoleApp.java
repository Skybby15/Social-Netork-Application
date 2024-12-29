package com.projectmap2;

import com.projectmap2.Domain.Prietenie;
import com.projectmap2.Domain.Tuple;
import com.projectmap2.Domain.Utilizator;
import com.projectmap2.Repository.DbRepos.PrietenieDbRepo;
import com.projectmap2.Repository.DbRepos.UtilizatorDbRepo;
import com.projectmap2.Repository.Repository;
import com.projectmap2.Service.Service;
import com.projectmap2.UserInterface.ConsoleUI;
import com.projectmap2.Utils.Passes;

public class ConsoleApp {
    public static void main(String[] args) {
        runApplication();
    }

    public static void runApplication()
    {
        Repository<Long, Utilizator> userRepo = new UtilizatorDbRepo("jdbc:postgresql://localhost:5432/ProjectMAP","postgres", Passes.postgresPass);
        Repository<Tuple<Long,Long>, Prietenie> friendRepo = new PrietenieDbRepo("jdbc:postgresql://localhost:5432/ProjectMAP","postgres",Passes.postgresPass);
        //Service servo = new Service(userRepo,friendRepo);
        //ConsoleUI console = new ConsoleUI(servo);
        //console.Run();
    }
}

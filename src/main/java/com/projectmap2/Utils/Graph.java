package com.projectmap2.Utils;

import com.projectmap2.Domain.Utilizator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class Graph {
    // A user define class to represent a graph.
    // A graph is an array of adjacency lists.
    // Size of array will be V (number of vertices
    // in graph)
    int V;
    ArrayList<ArrayList<Integer> > adjListArray;
    ArrayList<ArrayList<Integer> > resList;
    AtomicInteger counter;
    Map<Integer,Long> mapper; // index -> id
    Map<Long,Integer> reppam; // id -> index

    // constructor
    public Graph(int V)
    {
        counter = new AtomicInteger(0);
        mapper = new HashMap<>();
        reppam = new HashMap<>();
        this.V = V;
        // define the size of array as
        // number of vertices
        adjListArray = new ArrayList<>();
        resList = new ArrayList<>();

        // Create a new list for each vertex
        // such that adjacent nodes can be stored

        for (int i = 0; i < V; i++) {
            adjListArray.add(i, new ArrayList<>());
        }
    }
    public void addVertices(Iterable<Utilizator> varfuri)
    {
        varfuri.forEach(v -> {
            int idNum = counter.get();
            if (mapper.putIfAbsent(idNum, v.getId()) == null && reppam.putIfAbsent(v.getId(), idNum) == null)
                counter.incrementAndGet();
        });
    }

    // Adds an edge to an undirected graph
    public void addEdge(long src, long dest)
    {
        // Add an edge from src to dest.
        adjListArray.get(reppam.get(src)).add(reppam.get(dest));
        // Since graph is undirected, add an edge from dest
        // to src also
        adjListArray.get(reppam.get(dest)).add(reppam.get(src));

    }

    void DFSUtil(int v, boolean[] visited,int assigned)
    {
        // Mark the current node as visited and print it
        visited[v] = true;
        resList.get(assigned).add(v);
        // Recur for all the vertices
        // adjacent to this vertex
        for (int x : adjListArray.get(v)) {
            if (!visited[x])
                DFSUtil(x, visited, assigned);
        }
    }
    public ArrayList<ArrayList<Long>> connectedComponents()
    {
        resList.clear();
        resList = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            resList.add(i, new ArrayList<>());
        }
        // Mark all the vertices as not visited
        boolean[] visited = new boolean[V];
        for(int i = 0; i < V; i++)
            visited[i] = false;
        for (int v = 0; v < V; ++v) {
            if (!visited[v]) {
                // print all reachable vertices
                // from v
                DFSUtil(v, visited, v);
            }
        }

        BiFunction<ArrayList<ArrayList<Integer>>,Map<Integer,Long>,ArrayList<ArrayList<Long>>> RemapList = (x, y)->
        {
            ArrayList<ArrayList<Long>> mappedList = new ArrayList<>();
            x.forEach(list->{
                ArrayList<Long> toAdd = new ArrayList<>();
                list.forEach(item->{
                    toAdd.add(y.get(item));
                });
                mappedList.add(toAdd);
            });
            return mappedList;
        };

        /*
        resList.forEach(list->{
            ArrayList<Long> toAdd = new ArrayList<>();
            list.forEach(item->{
                toAdd.add(maper.get(item));
            });
            mappedList.add(toAdd);
        });
         */

        return RemapList.apply(resList, mapper);
    }
}
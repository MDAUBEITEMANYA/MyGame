package com.mygdx.game.obj;


import com.mygdx.game.curlygame.Sheep;

import java.util.HashSet;
import java.util.Set;


public class Inventar {

    private Set<Subject> food;
   // private Set<Subject> subjects;
  //  private Set<Subject> subjects;


    private int maxSize;



    public Inventar(Sheep sheep) {
       Sheep sheep1 = sheep;
        food = new HashSet<>();
        maxSize = 6;
    }


    public boolean add(Subject subject) {
        if (maxSize == food.size())
            return false;
        food.add(subject);
        return true;
    }

    public boolean delete(Subject subject) {
        if (maxSize == 0)
            return false;
        food.remove(subject);
        return true;
    }
    public Inventar(Hero hero) {
        Hero hero1 = hero;
        food = new HashSet<>();
        maxSize = 6;
    }


    public Set<Subject> getSubjects() {
        return food;
    }
}



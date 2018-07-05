package com.halcyonmobile.adoption.model;

import java.util.ArrayList;
import java.util.List;

public class Trait {
    public String id;
    public String name;

    public Trait(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Trait(){}


    public static List<Trait> getAllTraitNames(){
       List<Trait> traitNames = new ArrayList<>();
       traitNames.add(new Trait("0","Kind"));
       traitNames.add(new Trait("1","Good with children"));
       traitNames.add(new Trait("2","Patient"));
       traitNames.add(new Trait("3","Active"));
       traitNames.add(new Trait("4","Lazy"));
       traitNames.add(new Trait("5","Energetic"));
       traitNames.add(new Trait("6","Loyal"));
       traitNames.add(new Trait("7","Dislikes other pets"));
       traitNames.add(new Trait("8","Likes other pets"));
       traitNames.add(new Trait("9","Silent"));
       traitNames.add(new Trait("10","Obedient"));
       traitNames.add(new Trait("11","Independent"));

       return traitNames;
    }
}

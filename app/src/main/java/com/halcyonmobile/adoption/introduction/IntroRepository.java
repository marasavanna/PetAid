package com.halcyonmobile.adoption.introduction;

import com.halcyonmobile.adoption.model.Intro;

import java.util.ArrayList;
import java.util.List;

public class IntroRepository {

    public static  List<Intro> introItems = new ArrayList<>();

    public static List<Intro> initializeList(){
        introItems.add(new Intro("https://i1.wp.com/freepngimages.com/wp-content/uploads/2014/04/dog_and_cat_1_2.png?fit=472%2C261",
                "Find your new best friend"));
        introItems.add(new Intro("http://backgroundcheckall.com/wp-content/uploads/2017/12/kitten-transparent-background-1.png",
                "Get smart-matched with the best option for both parties"));
        introItems.add(new Intro("https://petsfirsthospital.com/wp-content/uploads/2018/03/Puppy-Crash-Course-Pic.png"
                ,"Make a change. Adopt"));

        return introItems;
    }

    public static List<Intro> getIntroItems() {
        return initializeList();
    }
}

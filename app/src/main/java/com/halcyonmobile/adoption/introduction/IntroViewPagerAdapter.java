package com.halcyonmobile.adoption.introduction;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.halcyonmobile.adoption.introduction.IntroFragment;
import com.halcyonmobile.adoption.introduction.IntroRepository;

public class IntroViewPagerAdapter extends FragmentPagerAdapter {

    public IntroViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0: {
                return IntroFragment.newInstance(IntroRepository.getIntroItems().get(0).description,IntroRepository.getIntroItems().get(0).image);
            }
            case 1: {
                return IntroFragment.newInstance(IntroRepository.getIntroItems().get(1).description,IntroRepository.getIntroItems().get(1).image);
            }
            case 2: {
                return IntroFragment.newInstance(IntroRepository.getIntroItems().get(2).description,IntroRepository.getIntroItems().get(2).image);
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}

package org.team7.TakeMyPackage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

      switch (position){
            case 0:
                return new RequestFragment();
            case 1:
                return new AssignmentFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

}

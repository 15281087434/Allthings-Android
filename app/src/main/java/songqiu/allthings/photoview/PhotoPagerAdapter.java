package songqiu.allthings.photoview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/11/7
 *
 *类描述：
 *
 ********/
public class PhotoPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<String> urlList;

    public PhotoPagerAdapter(FragmentManager fm, ArrayList<String> urlList) {
        super(fm);
        this.urlList=urlList;
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoFragment.newInstance(urlList.get(position));
    }

    @Override
    public int getCount() {
        return urlList.size();
    }
}

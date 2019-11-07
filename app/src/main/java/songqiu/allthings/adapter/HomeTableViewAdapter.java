package songqiu.allthings.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

import songqiu.allthings.bean.TabClassBean;

/**
 * 首页 viewPager 适配器
 */

public class HomeTableViewAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragment;
//    private List<String> mTabName;
    private List<TabClassBean> list;

    public HomeTableViewAdapter(FragmentManager fm, List<Fragment> fragmentList, List<TabClassBean> list) {
        super(fm);
        this.mFragment = fragmentList;
        this.list = list;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragment.get(position);
    }

    @Override
    public int getCount() {
        return mFragment.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position % list.size()).name;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}

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

public class TablayoutViewAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragment;
    private List<String> mTabName;

    public TablayoutViewAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> mTabName) {
        super(fm);
        this.mFragment = fragmentList;
        this.mTabName = mTabName;
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
        return mTabName.get(position);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}

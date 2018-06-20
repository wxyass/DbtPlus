package et.tsingtaopad.guide;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BasePagerAdapter extends PagerAdapter {

    protected Context context;
    protected LayoutInflater mInflater;
    protected List<View> mList;
    protected ViewPager mViewPager;
    protected Resources mResources;

    public BasePagerAdapter(List<View> mList, Context context) {
        this.context = context;
        if (mInflater == null) {
            mInflater = LayoutInflater.from(context);
        }
        mResources = context.getResources();
        this.mList = mList;
    }

    public BasePagerAdapter(List<View> mList, Context context, ViewPager mViewPager) {
        this.context = context;
        if (mInflater == null) {
            mInflater = LayoutInflater.from(context);
        }
        this.mList = mList;
        mResources = context.getResources();
        this.mViewPager = mViewPager;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(mList.get(arg1));
    }

    @Override
    public void finishUpdate(View arg0) {

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == (arg1);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = null;
        try {
            if (mList.size() > 0) {
                object = MyinstantiateItem(container, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public int getItemPosition(Object object) {
        //        return POSITION_NONE;
        return super.getItemPosition(object);
    }

    /**
     * 为了防止子类忘了覆盖 instantiateItem 这个方法 所以自己写一个抽象的方法,用法一样
     * @param container
     * @param position
     * @return
     */
    protected abstract Object MyinstantiateItem(ViewGroup container, int position);
}

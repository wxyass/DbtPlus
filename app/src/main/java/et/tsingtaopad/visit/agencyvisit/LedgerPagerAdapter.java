package et.tsingtaopad.visit.agencyvisit;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：LedgerPagerAdapter.java</br>
 * 作者：@吴欣伟    </br>
 * 创建时间：2013/12/02</br>      
 * 功能描述: 进销存台账和调货台账ViewPager界面</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class LedgerPagerAdapter extends PagerAdapter {

    private List<View> viewLst;
    private List<String> titleLst;
    
    public LedgerPagerAdapter(List<View> viewLst, List<String> titleLst) {
        this.viewLst = viewLst;
        this.titleLst = titleLst;
    }
    
    @Override
    public int getCount() {
        
        return viewLst.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {

        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        
        ((ViewPager)container).removeView(viewLst.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        
        return titleLst.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ((ViewPager) container).addView(viewLst.get(position));
        return viewLst.get(position);
    }

}

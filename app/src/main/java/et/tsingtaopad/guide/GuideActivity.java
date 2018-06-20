package et.tsingtaopad.guide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import et.tsingtaopad.R;
import et.tsingtaopad.login.LoginActivity;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：GuideActivity.java</br>
 * 作者：wangfei   </br>
 * 创建时间：2014-9-24</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class GuideActivity extends Activity {
    ViewPager viewPager;
    LinearLayout viewpager_bottom;
    ArrayList<View> pageViews;

    int selectNum = 0;
    GuideService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.guide);
        super.onCreate(savedInstanceState);

        viewPager = (ViewPager) findViewById(R.id.guide_viewpage);
        viewpager_bottom = (LinearLayout) findViewById(R.id.guide_viewpage_bottom);

        initView();
        initData();

    }

    private void initData() {
        service = new GuideService(this);
        if (!service.isShowGuide(this)) {
            Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 初始化viewpage
     */
    public void initView() {
        LayoutInflater inflater = getLayoutInflater();
        pageViews = new ArrayList<View>();
        View view = inflater.inflate(R.layout.guide_viewpager, null);
        view.findViewById(R.id.guide_page).setBackgroundResource(R.drawable.ico_guide_page_o);
        pageViews.add(view);
        view = inflater.inflate(R.layout.guide_viewpager, null);
        view.findViewById(R.id.guide_page).setBackgroundResource(R.drawable.ico_guide_page_t);
        pageViews.add(view);
        view = inflater.inflate(R.layout.guide_viewpager, null);
        view.findViewById(R.id.guide_page).setBackgroundResource(R.drawable.ico_guide_page_th);
        pageViews.add(view);
        for (int i = 0; i < pageViews.size(); i++) {
            ImageView lliv = new ImageView(this);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (i == selectNum) {
                lliv.setImageResource(R.drawable.ico_guide_dot_checked);
            } else {
                lliv.setImageResource(R.drawable.ico_guide_dot_unchecked);
            }
            if (i == pageViews.size() - 1) {
                params.setMargins(5, 0, 0, 5);
            } else {
                params.setMargins(5, 0, 0, 0);
            }
            lliv.setLayoutParams(params);
            viewpager_bottom.addView(lliv);
        }
        BasePagerAdapter adapter = new BasePagerAdapter(pageViews, GuideActivity.this) {
            @Override
            protected Object MyinstantiateItem(ViewGroup container, int position) {
                View view = pageViews.get(position);
                if (position == 2) {
                    View into = view.findViewById(R.id.into);
                    into.setVisibility(View.VISIBLE);
                    into.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            service.setShowGuide(false);
                            Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                ((ViewPager) container).addView(pageViews.get(position), 0);
                return view;
            }
        };
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                for (int i = 0; i < viewpager_bottom.getChildCount(); i++) {
                    ImageView lliv = (ImageView) viewpager_bottom.getChildAt(i);
                    if (arg0 == i) {
                        lliv.setImageResource(R.drawable.ico_guide_dot_checked);
                    } else {
                        lliv.setImageResource(R.drawable.ico_guide_dot_unchecked);
                    }
                }
                selectNum = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}

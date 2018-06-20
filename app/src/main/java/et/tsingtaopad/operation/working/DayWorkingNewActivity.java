package et.tsingtaopad.operation.working;




import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import et.tsingtaopad.R;
import et.tsingtaopad.operation.working.fragment.WorkFragment;
import et.tsingtaopad.operation.working.fragment.SummaryFragment;
import et.tsingtaopad.operation.working.fragment.RemarkFragment;

/**
 * 项目名称：营销移动智能工作平台 </br> 
 * 文件名：WorkingFragment.java</br> 
 * 作者：@ray </br>
 * 创建时间：2013/11/26</br> 
 * 功能描述: 日工作推进界面</br> 
 * 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期 原因 BUG号 修改人 修改版本</br>
 */
public class DayWorkingNewActivity extends FragmentActivity implements OnClickListener
{

    private WorkingService service;

    private Button backBt;
    private TextView titleTv;
 
    private RadioGroup tagRg;

    private AlertDialog dialog;
    
    private Fragment fragment;
    private RadioButton workRb, summaryRb, remarkRb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.operation_daywork_activity);
    	initView();
        initData();
    	
    };

    /**
     * 初始化界面组件
     */
    private void initUI()
    {

        // 实例化WebView查询等待dialog
        dialog = new AlertDialog.Builder(this).setCancelable(false).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);//以上两句是控制dialog在访问网络超时时，按返回键可消失dialog
        View progressView = this.getLayoutInflater().inflate(R.layout.login_progress, null);
        dialog.setView(progressView, 0, 0, 0, 0);
        TextView tv = (TextView) progressView.findViewById(R.id.textView1);
        tv.setText(getString(R.string.progressing));
    
	}
    
    
    private void initView() {
    	
    	// 绑定界面组件
        backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
        titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);
        
		RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
        // 绑定事件
        backBt.setOnClickListener(this);
    	
		fragment = getSupportFragmentManager().findFragmentById(R.id.fl_content);
		tagRg = (RadioGroup) findViewById(R.id.weekwork_rg_tag);
		workRb = (RadioButton) findViewById(R.id.weekwork_rb_work);
		summaryRb = (RadioButton) findViewById(R.id.weekwork_rb_summary);
		remarkRb = (RadioButton) findViewById(R.id.weekwork_rb_remark);
		workRb.setOnClickListener(this);
		summaryRb.setOnClickListener(this);
		remarkRb.setOnClickListener(this);
		//tagRg.setOnCheckedChangeListener(this);
		
		
	}

    private void initData()
    {
        titleTv.setText(R.string.daywork_title);
        
        showWorkFragment();

    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {

	        // 界面返回按钮
	        case R.id.banner_navigation_rl_back:
            case R.id.banner_navigation_bt_back:
                this.finish();
                break;
                // 日工作推进
            case R.id.weekwork_rb_work:
            	showWorkFragment();
                break;
                // 偏差分析
            case R.id.weekwork_rb_summary:
            	showSummaryFragment();
                break;
                // 工作点评
            case R.id.weekwork_rb_remark:
            	showRemarkFragment();
                break;
        }
    }

    public void showWorkFragment() {
		if (!(fragment instanceof WorkFragment)) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.fl_content, WorkFragment.getInstance());
			ft.commit();
		}
	}

	public void showSummaryFragment() {
		
		if (!(fragment instanceof SummaryFragment)) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.fl_content, SummaryFragment.getInstance());
			ft.commit();
		}
	}

	public void showRemarkFragment() {
		if (!(fragment instanceof RemarkFragment)) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.fl_content, RemarkFragment.getInstance());
			ft.commit();
		}
	}

    
}

package et.tsingtaopad;

import et.tsingtaopad.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：PlatformMenuActivity.java</br>
 * 作者：hongen   </br>
 * 创建时间：2014-1-19</br>      
 * 功能描述:平台的快捷菜单 </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class PlatformMenuActivity extends Activity implements OnClickListener {

    private LinearLayout popLayout;
    private ImageView shopvisitIv;
    private ImageView desktopIv;
    private ImageView syncIv;
    private ImageView questionIv;
    private ImageView systemIv;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenOrientation();
        this.initView();
    }
    private void setScreenOrientation(){
        Intent intent = getIntent();
        String screenOrientation =intent.getStringExtra("SCREEN_ORIENTATION");
        if(screenOrientation.equals("landscape")){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    private void initView() {
        setContentView(R.layout.platform_menu);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        
        popLayout = (LinearLayout)findViewById(R.id.menu_lo_pop);
        desktopIv = (ImageView)findViewById(R.id.menu_iv_desktop);
        shopvisitIv = (ImageView)findViewById(R.id.menu_iv_shopvisit);
    
        syncIv = (ImageView)findViewById(R.id.menu_iv_sync);
        questionIv = (ImageView)findViewById(R.id.menu_iv_question);
        systemIv = (ImageView)findViewById(R.id.menu_iv_system);
        
        popLayout.setOnClickListener(this);
        desktopIv.setOnClickListener(this);
        shopvisitIv.setOnClickListener(this);
        syncIv.setOnClickListener(this);
        questionIv.setOnClickListener(this);
        systemIv.setOnClickListener(this);
    }

    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity  
    @Override  
    public boolean onTouchEvent(MotionEvent event){  
        finish();  
        return true;  
    } 

    @Override
    public void onClick(View v) {

        Intent intent = null;
        Bundle bundle = new Bundle();
        
        switch (v.getId()) {
        case R.id.menu_iv_desktop:
            intent = new Intent(this, MainActivity.class);
            break;
        case R.id.menu_iv_shopvisit:
            intent = new Intent(this, MainActivity.class);
            bundle.putInt("tabIndex", 1);
            bundle.putString("funId", "shopVisit");
            intent.putExtra("mainArgs", bundle);
            break;
            
        case R.id.menu_iv_sync:
            intent = new Intent(this, MainActivity.class);
            bundle.putInt("tabIndex", 1);
            bundle.putString("funId", "sync");
            intent.putExtra("mainArgs", bundle);
            break;
            
        case R.id.menu_iv_question:
            intent = new Intent(this, MainActivity.class);
            bundle.putInt("tabIndex", 3);
            bundle.putString("funId", "question");
            intent.putExtra("mainArgs", bundle);
            break;
            
        case R.id.menu_iv_system:
            intent = new Intent(this, MainActivity.class);
            bundle.putInt("tabIndex", 3);
            intent.putExtra("mainArgs", bundle);
            break;

        default:
            break;
        }
        if (intent != null) {
            startActivity(intent);
        }
        finish();
    }

}

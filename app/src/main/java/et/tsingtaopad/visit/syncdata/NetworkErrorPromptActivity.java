package et.tsingtaopad.visit.syncdata;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.NetStatusUtil;
/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：NetworkErrorPromptActivity.java</br>
 * 作者：薛敬飞   </br>
 * 创建时间：2014-5-15</br>      
 * 功能描述:网络错误提示 </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class NetworkErrorPromptActivity extends BaseActivity implements OnClickListener{
    private TextView prompt_content_tv;
    private Button  prompt_cancel_bt;
    private Button  prompt_ok_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_error_prompt);
        this.initView();
        this.initData();
        }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
        switch (v.getId()) {
        case R.id.dialog_button_ok:
            if (NetStatusUtil.isNetValid(getApplicationContext())) {
                Intent download = new Intent(getApplicationContext(), DownLoadDataProgressActivity.class);
                startActivity(download);
                NetworkErrorPromptActivity.this.finish();
            } else {
                //提示修改网络
                Builder builder = new Builder(this);
                builder.setTitle("网络错误");
                builder.setMessage("请连接好网络再同步数据");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NetworkErrorPromptActivity.this.startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
                    }
                }).create().show();
            }
            break;
            
        case R.id.dialog_button_cancel:
            NetworkErrorPromptActivity.this.finish();
            break;
        default:
            break;
        }
    }
    /**
     * 初始化界面组件
     */
    private void initView() {
        prompt_content_tv=(TextView) findViewById(R.id.dialog_prompt_content);
        prompt_cancel_bt=(Button) findViewById(R.id.dialog_button_cancel);
        prompt_ok_bt=(Button) findViewById(R.id.dialog_button_ok);
        prompt_cancel_bt.setOnClickListener(this);
        prompt_ok_bt.setOnClickListener(this);
}
    private void initData(){
        Intent intent=getIntent();
        String  prompt_content=intent.getStringExtra("prompt_content");
        prompt_content_tv.setText(prompt_content);
    }
}

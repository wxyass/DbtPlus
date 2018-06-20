package et.tsingtaopad.visit.syncdata;

import java.text.NumberFormat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.R;
import et.tsingtaopad.initconstvalues.InitConstValues;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：DownLoadDataProgressActivity.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2014年2月13日</br>      
 * 功能描述: 数据下载进度条</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */

public class DownLoadDataProgressActivity extends BaseActivity {
	String TAG="DownLoadDataProgressActivity";
    public static final int SYNDATA_RESULT_Success = 10;
    public static final int SYNDATA_RESULT_Failure = 12;
    public static final int SYNDATA_PROGRESS = 11;
    private ProgressBar download_progressBar;
    private TextView download_progressTV;
    private TextView download_percentTV;
    private int progressMax = 45;//进度总数 按模块划分计算出来的数量/比如 联网 ，更新1表， 更新2表 可以通过计算DownLoadDataService 调用了sendProgressMessage多少次计算出来
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
  
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (DownLoadDataProgressActivity.this.isFinishing()) {
                return;
            }
            switch (msg.what) {
            //同步结果比如一些错误提示
            case SYNDATA_RESULT_Failure:// 同步出错：1，网络连接失败， 2，发生异常
                // 初始化静态变量
                new InitConstValues(DownLoadDataProgressActivity.this).start();
                Intent prompt= new Intent();
                prompt.putExtra("prompt_content", (String) msg.obj);
                prompt.setClass(getApplicationContext(), NetworkErrorPromptActivity.class);
                startActivity(prompt);
                DownLoadDataProgressActivity.this.finish();
                break;
                //同步进度
            case SYNDATA_RESULT_Success:    //同步成功结束
                 new InitConstValues(DownLoadDataProgressActivity.this).start();
                  Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                  DownLoadDataProgressActivity.this.finish();
                break;
            case SYNDATA_PROGRESS:
                Bundle data = msg.getData();
                String string = data.getString("progressStr");
                int progress = data.getInt("progress");
                download_progressBar.setProgress(progress);
                download_progressTV.setText(string+"更新中...");
                //计算百分比
                float gress = progress;
                NumberFormat numberFormat = NumberFormat.getPercentInstance();
                numberFormat.setMinimumFractionDigits(0);
                String percent = numberFormat.format(gress / progressMax);
                Log.d(TAG, "percent:"+percent);
                download_percentTV.setText(percent);
                break;
            default:
                break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloadprogress); 
        download_progressBar = (ProgressBar) findViewById(R.id.download_progressBar);
        download_progressTV = (TextView) findViewById(R.id.download_progressTV);
        download_percentTV = (TextView) findViewById(R.id.download_percentTV);
        
        download_progressBar.setMax(progressMax);
        //下载数据
 
        DownLoadDataService down = new DownLoadDataService(this, handler);
        //三个线程进行下载
        down.asyndatas();
      
//        down.synDatas();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "下载数据中", Toast.LENGTH_LONG).show();
    }

}

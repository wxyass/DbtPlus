package et.tsingtaopad.syssetting.zxing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import et.tsingtaopad.R;

/**
 * 
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：ResultActivity.java</br>
 * 作者：yangwenmin   </br>
 * 创建时间：2016-12-5</br>      
 * 功能描述: 扫描后 结果显示界面</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class ResultActivity extends Activity {

	private TextView tv;
	private ImageView img;
	private Button btn;
	private Button btnintent;
	private Bundle bundle;
	private WebView web;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zxing_result);
		initView();

		initdata();

	}

	private void initdata() {
		Intent intentvalue = getIntent();
		bundle = intentvalue.getExtras();
		url = bundle.getString("result");
		tv.setText(bundle.getString("result"));
		img.setImageBitmap((Bitmap) intentvalue.getParcelableExtra("bitmap"));
		// 跳转webview
		btnintent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String str = bundle.getString("result");
				String substr = str.substring(0, 4);
				if (substr.equals("http")) {
					Intent intent = new Intent(ResultActivity.this,LoadAppActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("result", url);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					Toast.makeText(ResultActivity.this, "不是网址你点粑粑！", Toast.LENGTH_SHORT).show();
				}
			}
		});

		/*
		 * 点击关闭当前页面
		 */
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ResultActivity.this.finish();
			}
		});
	}

	private void initView() {
		tv = (TextView) findViewById(R.id.result_name);
		img = (ImageView) findViewById(R.id.result_bitmap);
		btn = (Button) findViewById(R.id.button_back);
		btnintent = (Button) findViewById(R.id.intent2view);
	}
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
			web.goBack();
			return true;
		}
		return false;
		
	}*/

}

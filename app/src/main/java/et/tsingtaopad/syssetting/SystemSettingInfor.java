package et.tsingtaopad.syssetting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：SystemSettingInfor.java</br>
 * 作者：dbt   </br>
 * 创建时间：2014-2-10</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class SystemSettingInfor extends BaseFragmentSupport implements OnClickListener {

	private final static String TAG = "SystemSettingInfor";
	
	private Button btn_back;
	
	private TextView tv_title;
	
	private TextView tv_update;
	private ImageView copyBase;

	private List<Long> times;
	
	private String[] fileSrcStrings;// 指定压缩源，可以是目录或文件的数组
	private String decompressDirString = "";// 解压路径
	private String archiveString = "";// 压缩包路径
	private String commentString = "Androi Java Zip 测试.";// 压缩包注释
	private ZipControl mZipControl;
	private String srcString;// 第一个文件的路径
	private String srcTwoString;// 第二个文件的路径
	private String srcthreeString;// 第三个文件的路径
	private String srcfourString;// 第四个文件的路径
	
	String LOGPATH;// 定义内存卡路径
	String BUGPATH;// 定义内存卡路径

	private String dbtPATH;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.system_setting_info, null);
		view.setOnClickListener(null);
		initView(view);
		initData();
		return view;
	}

	/**
	 * @param view
	 */
	private void initView(View view) {
		tv_title = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		btn_back = (Button) view.findViewById(R.id.banner_navigation_bt_back);
		tv_update = (TextView) view.findViewById(R.id.syssetting_tv_update);
		copyBase = (ImageView) view.findViewById(R.id.imageView1);
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		copyBase.setOnClickListener(this);
		
		// 获取文件夹路径(内存卡路径)
		String sdcardPath = Environment.getExternalStorageDirectory() + "";
		
        dbtPATH = sdcardPath + "/dbt/et.tsingtaopad";
        LOGPATH = dbtPATH + "/log";
        BUGPATH = dbtPATH + "/bug";
		
	}
	
	/**
	 * 
	 */
	private void initData() {
		tv_title.setText(getString(R.string.systerm_info));
		times = new ArrayList<Long>();
		try {
			tv_update.setText("V" + getActivity().getPackageManager().getPackageInfo(
					getActivity().getPackageName(), 0).versionName);

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.banner_navigation_rl_back://返回
		case R.id.banner_navigation_bt_back://返回
			this.getFragmentManager().popBackStack();
			break;
		
		case R.id.imageView1://复制数据库
			this.click2();
			break;
		}
		
	}
	
	public void copyBase() {
	      try {
	    	  // 压缩文件 (将数据库和日志压缩)
	    	  zip2File();
	    	  // 上传文件
	          updatebase();
	          
	      } catch (Exception e) {
	            System. out.println( "复制单个文件操作出错" );
	            e.printStackTrace();
	      }
	}
	
	/**
	 * 
	 */
	private void zip2File() {
		
	    // 创建存放压缩包的文件夹,若没有则创建
		archiveString = dbtPATH + "/zip";
		File zipFile = new File(archiveString);
		if (!zipFile.exists()) {
			zipFile.mkdir();
			Log.e(TAG, "make zipdir success");
		} else {
			Log.e(TAG, "exit zipdir");
		}
		 
		// 第一个文件的路径 全部崩溃日志
		srcString = BUGPATH;// /dbt/et.tsingtaopad/bug bug文件夹下所有文件
		// 第二个文件的路径 数据库文件
		srcTwoString = "/data/data/et.tsingtaopad/databases/FsaDBT.db";
		// 第三个文件的路径 操作日志 /dbt/et.tsingtaopad/log/log.txt
		srcthreeString = LOGPATH + "/log.txt";
		// 第四个文件的路径 缓存shared_prefs
		srcfourString = "/data/data/et.tsingtaopad/shared_prefs";
		
		fileSrcStrings = new String[] { srcString, srcTwoString, srcthreeString, srcfourString };
		mZipControl = new ZipControl();
		
		try {
			// 压缩 (要压缩的文件路径列表,压缩包文件路径,描述)
			mZipControl.writeByApacheZipOutputStream(fileSrcStrings, archiveString + "/databases.zip", commentString);
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
     
	}

	/**
	 * 
	 */
	private void updatebase() {
		
		RequestParams params = new RequestParams();
		//params.addHeader("name", "value");
		//params.addQueryStringParameter("name", "value");
		//params.addQueryStringParameter("name", ConstValues.loginSession.getUserGongHao()); 
		params.addQueryStringParameter("name", PrefUtils.getString(getActivity(), "userGongHao", "")); 
		//params.addBodyParameter("name", ConstValues.loginSession.getUserGongHao());
		//params.addBodyParameter("file", new File("/data/data/et.tsingtaopad/databases/FsaDBT.db"));
		params.addBodyParameter("file", new File(archiveString+"/databases.zip"));

		HttpUtils http = new HttpUtils();
		//String savedb = PropertiesUtil.getProperties("platform_savedb");
		http.send(HttpRequest.HttpMethod.POST,PropertiesUtil.getProperties("platform_savedb"),params,new RequestCallBack<String>() {

		        @Override
		        public void onStart() {
		        	
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		            if (isUploading) {
		                //testTextView.setText("upload: " + current + "/" + total);
		            } else {
		                //testTextView.setText("reply: " + current + "/" + total);
		            }
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	PrefUtils.putInt(getActivity(), "isCopyBase", 0);// 1:已经上传  0:未上传
		        	Toast. makeText(getActivity(), "数据库上传成功", Toast.LENGTH_SHORT).show();
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	PrefUtils.putInt(getActivity(), "isCopyBase", 0);// 1:已经上传  0:未上传
		        	Toast. makeText(getActivity(), "数据库上传失败",Toast.LENGTH_SHORT).show();
		        }
		});
	}

	// 双击事件监听,去复制数据库
	public void click2() {
	    times.add(SystemClock.uptimeMillis());
	    if (times.size() == 2) {
	        //已经完成了一次双击，list可以清空了
	        if (times.get(times.size()-1)-times.get(0) < 1000) {
	            times.clear();
	            // 复制数据库并上传
	            if(0==PrefUtils.getInt(getActivity(), "isCopyBase", 0)){// 需要上传
	            	Toast. makeText(getActivity(), "正在上传,请稍后", Toast.LENGTH_SHORT).show();
	            	copyBase();// 上传
		            PrefUtils.putInt(getActivity(), "isCopyBase", 1);// 1:已经上传  0:未上传
	            }else{
	            	Toast.makeText(getActivity(), "已上传,请不要重复上传", Toast.LENGTH_SHORT).show();
	            	PrefUtils.putInt(getActivity(), "isCopyBase", 0);// 1:已经上传  0:未上传
	            }
	            
	        } else {
	            //这种情况下，第一次点击的时间已经没有用处了，第二次就是“第一次”
	            times.remove(0);
	        }
	    }
	}
}

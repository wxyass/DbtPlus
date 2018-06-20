package et.tsingtaopad.syssetting.zxing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.DeleteTools;
import et.tsingtaopad.R;
import et.tsingtaopad.visit.syncdata.UploadDataService;

public class LoadAppActivity extends Activity {
	public static final String TAG = "LoadAppActivity";

	private Bundle bundle;
	private String appurl;
	private Dialog downloadDialog;
	private int progress;
	private String apkPath;
	private boolean interceptFlag = false;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConstValues.WAIT1:
				progressDialog.setProgress(progress);
				// 判断进度 取消progressDialog
				
				break;

			case ConstValues.WAIT2:
				if (downloadDialog != null && downloadDialog.isShowing()) {
					downloadDialog.dismiss();
				}
				installApk();
				break;

			default:
				break;
			}
		};
	};

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉Activity上面的状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_loadapp);

		initdata();

		down2();

	}

	/**
	 * 获取传递的数据
	 */
	private void initdata() {
		Intent intentvalue = getIntent();
		bundle = intentvalue.getExtras();
		appurl = bundle.getString("result");
	}
	
	/**
	 * 下载apk弹窗
	 */
	private void down2() {
		showNotifyDialog();
	}

	/**
	 * 下载安装程序
	 */
	public void downApk() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					URL url = new URL(appurl);

					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					// conn.setConnectTimeout(3000);
					conn.connect();
					if (conn.getResponseCode() == 200) {
						System.out.println("hjkkllll");
					}
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();

					File file = getApplication().getFilesDir();
					if (!file.exists()) {
						file.mkdir();
					}
					apkPath = file.getPath() + "/DbtPlus.apk";
					File ApkFile = new File(apkPath);
					FileOutputStream fos = new FileOutputStream(ApkFile);

					int count = 0;
					byte buf[] = new byte[1024];
					do {
						int numread = is.read(buf);
						count += numread;
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						handler.sendEmptyMessage(ConstValues.WAIT1);
						if (numread <= 0) {
							// 下载完成通知安装
							handler.sendEmptyMessage(ConstValues.WAIT2);
							break;
						}
						fos.write(buf, 0, numread);
					} while (!interceptFlag);// 点击取消就停止下载.

					fos.close();
					is.close();
				} catch (MalformedURLException e) {
					Log.e(TAG, "", e);
				} catch (IOException e) {
					Log.e(TAG, "", e);
				}
			}
		}).start();
	}

	/**
	 * 安装apk之前先检是否有未上传的数据，如果有就上传数据并清空本地数据库，如果没有就直接删除本地数据
	 * 
	 */
	private void installApk() {

		UploadDataService uploadDataService = new UploadDataService(getApplicationContext(), null);
		try {
			if (uploadDataService.isAllEmpty()) {
				DeleteTools.deleteDatabase(getApplicationContext());
			} else {
				uploadDataService.uploadTables(false);
				DeleteTools.deleteDatabase(getApplicationContext());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.exec(new String[] { "chmod", "604", apkPath });

		File apkfile = new File(apkPath);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(i);
	}

	/**
	 * 更改文件权限
	 * 
	 * @param args
	 * @return
	 */
	private String exec(String[] args) {
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			baos.write('n');
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
		return result;
	}

	/**
	 * 提示下载apk
	 */
	public void showNotifyDialog() {

		// 1. 通知对话框
		Builder builder = new Builder(this);
		// 设置图标
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		// 设置标题
		builder.setTitle("提醒:");
		// 设置提醒内容
		builder.setMessage("是否下载该apk");

		builder.setPositiveButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(LoadAppActivity.this, "确认", Toast.LENGTH_SHORT).show();
				showProgressDialog();

			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(LoadAppActivity.this, "取消", Toast.LENGTH_SHORT).show();
				LoadAppActivity.this.finish();
			}
		});

		builder.setCancelable(true); // 是否可以通过返回键 关闭

		// 创建AlertDialog
		// AlertDialog dialog = builder.create();
		// dialog.show();

		// 直接show();
		builder.show();

	}

	/**
	 * 进度条
	 */
	public void showProgressDialog() {
		// 显示一个加载的对话框
		// ProgressDialog.show(this, "提示: ", "正在加载中,请稍后...");
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("提示: ");
		progressDialog.setMessage("正在下载中...");
		// 设置样式为 横向 的
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		progressDialog.setMax(100);

		// 显示的时候, 会把进度归零
		progressDialog.show();

		downApk();
	}

}

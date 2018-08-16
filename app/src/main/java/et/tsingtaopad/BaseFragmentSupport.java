package et.tsingtaopad;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import cn.com.benyoyo.manage.Struct.ResponseStructBean;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.tools.DataCleanManager;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.FileTool;
import et.tsingtaopad.tools.FileUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.NetStatusUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.visit.shopvisit.camera.domain.CameraImageBean;
import et.tsingtaopad.visit.syncdata.DownLoadDataProgressActivity;

public class BaseFragmentSupport extends Fragment {
	

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			View currentFocus = getActivity().getCurrentFocus();
			if (currentFocus != null) {
				IBinder windowToken = currentFocus.getWindowToken();
				imm.hideSoftInputFromWindow(windowToken, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	public boolean allowBackPressed() {
		FragmentManager fm = getChildFragmentManager();
		//从fragment管理器中得到Fragment当前已加入Fragment回退栈中的fragment的数量。
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
			return true;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	/**
	 * 通过uri获取图片并进行压缩
	 *
	 * @param uri
	 */
	public static Bitmap getBitmapFormUri(Context ac, Uri uri) throws FileNotFoundException, IOException {
		InputStream input = ac.getContentResolver().openInputStream(uri);
		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;//optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		int originalWidth = onlyBoundsOptions.outWidth;
		int originalHeight = onlyBoundsOptions.outHeight;
		if ((originalWidth == -1) || (originalHeight == -1))
			return null;
		//图片分辨率以480x800为标准
		float hh = 640f;//这里设置高度为640f
		float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (originalWidth / ww);
		} else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (originalHeight / hh);
		}
		if (be <= 0)
			be = 1;
		//比例压缩
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = be;//设置缩放比例
		bitmapOptions.inDither = true;//optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		input = ac.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();

		return compressImage(bitmap);//再进行质量压缩
	}

	/**
	 * 质量压缩方法
	 *
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 25) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();//重置baos即清空baos
			//第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 1;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	// Android 7.0调用系统相机适配 使用FileUtils库中的方法转化
	public Intent toCameraByContentResolver(Intent intent, File tempFile, String currentPhotoName){
		final ContentValues contentValues = new ContentValues(1);// ?
		contentValues.put(MediaStore.Images.Media.DATA, tempFile.getPath());//?
		final Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
		// 需要将Uri路径转化为实际路径?
		final File realFile = FileUtils.getFileByPath(FileTool.getRealFilePath(getContext(), uri));
		// 将File转为Uri
		final Uri realUri = Uri.fromFile(realFile);
		//将拍取的照片保存到指定URI
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		CameraImageBean cameraImageBean = CameraImageBean.getInstance();
		cameraImageBean.setmPath(realUri);
		cameraImageBean.setPicname(currentPhotoName);
		return intent;
	}

	// 权限相关 ↓--------------------------------------------------------------------------

	/**
	 * 判断是否有指定的权限
	 */
	public boolean hasPermission(String... permissions) {

		for (String permisson : permissions) {
			if (ContextCompat.checkSelfPermission(getActivity(), permisson)
					!= PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 申请指定的权限.
	 */
	public void requestPermission(int code, String... permissions) {

		if (Build.VERSION.SDK_INT >= 23) {
			requestPermissions(permissions, code);
		}
	}

	// 定义几个常量

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case GlobalValues.HARDWEAR_CAMERA_CODE:
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					doOpenCamera();
				}else{
					Toast.makeText(getActivity(),"请先开启相机权限",Toast.LENGTH_SHORT).show();
				}
				break;
			case GlobalValues.WRITE_READ_EXTERNAL_CODE:
				if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					doWriteSDCard();
				}else{
					Toast.makeText(getActivity(),"请先开启读写存储权限",Toast.LENGTH_SHORT).show();
				}
				break;
			case GlobalValues.LOCAL_CODE:
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					doLocation();
				}else{
					Toast.makeText(getActivity(),"请先开启定位权限",Toast.LENGTH_SHORT).show();
				}
				break;
			case GlobalValues.WRITE_LOCAL_CODE:
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					doCameraWriteSD();
				}else{
					Toast.makeText(getActivity(),"请先开启读写存储权限",Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}

	public void doCameraWriteSD() {

	}

	// 定位
	public void doLocation() {
	}


	// 拍照
	public void doOpenCamera() {

	}

	// 读写SD卡业务逻辑,由具体的子类实现
	public void doWriteSDCard() {

	}

	// 权限相关 ↑--------------------------------------------------------------------------


	// 同步数据
	public void syncDownData(){
		// 如果网络可用
		if (NetStatusUtil.isNetValid(getActivity())) {
			// 根据后台标识   "0":需清除数据 ,"1":不需清除数据,直接同步
			if ("0".equals(ConstValues.loginSession.getIsDel())) {
				//弹窗是否删除之前所有数据
				showNotifyDialog();
			} else {
				// 打标记
				PrefUtils.putString(getActivity(),ConstValues.SYNCDATA, DateUtil.getDateTimeStr(7));
				Intent download = new Intent(getActivity(),DownLoadDataProgressActivity.class);
				startActivity(download);
			}
		} else {
			// 提示修改网络
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("网络错误");
			builder.setMessage("请连接好网络再同步数据");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							getActivity()
									.startActivityForResult(
											new Intent(
													android.provider.Settings.ACTION_WIRELESS_SETTINGS),
											0);
						}
					}).create().show();
			builder.setCancelable(false); // 是否可以通过返回键 关闭
		}
	}

	/**
	 * 同步数据-弹窗显示需清除数据
	 *
	 */
	public void showNotifyDialog(){

		//提示删除数据
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("初始化");
		builder.setMessage("初次登陆，您的账号需要初始化。");
		builder.setCancelable(false);
		//builder.setCanceledOnTouchOutside(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// 缓冲界面
				AlertDialog dialog1 = new DialogUtil().progressDialog(getActivity(), R.string.dialog_msg_delete);
				dialog1.setCancelable(false);
				dialog1.setCanceledOnTouchOutside(false);
				dialog1.show();

				// 网络请求 传递参数
				HttpUtil httpUtil = new HttpUtil(60*1000);
				httpUtil.configResponseTextCharset("ISO-8859-1");

				//
				StringBuffer buffer = new StringBuffer();
				//buffer.append("{userid:'").append(ConstValues.loginSession.getUserGongHao());
				buffer.append("{userid:'").append(PrefUtils.getString(getActivity(), "userGongHao", ""));
				buffer.append("', isdel:'").append("1").append("'}");

				// qingqiu
				httpUtil.send("opt_get_status", buffer.toString(),new RequestCallBack<String>() {
					public void onSuccess(ResponseInfo<String> responseInfo) {

						ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
						if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
							// 删除数据库表数据 然后直接同步
							// new DeleteTools().deleteDatabase(getActivity());

							// 删除数据库表数据 然后重启
							new DeleteTools().deleteDatabaseAll(getActivity());

							// 删除缓存数据
							new DataCleanManager().cleanSharedPreference(getActivity());

							// 删除bug文件夹
							String bugPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dbt/et.tsingtaopad" + "/bug/" ;
							FileUtil.deleteFile(new File(bugPath));

							// 删除log文件夹
							String logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dbt/et.tsingtaopad" + "/log/" ;
							FileUtil.deleteFile(new File(logPath));

							// 重新启动本应用
							restartApplication();
							android.os.Process.killProcess(android.os.Process.myPid());

							// 关闭缓冲界面
							/*Message message = new Message();
							message.what = ConstValues.WAIT2;
							handler.sendMessage(message);*/
						}
					}

					@Override
					public void onFailure(HttpException error,
										  String msg) {

					}
				});
			}
		}).create().show();

		//builder.setCancelable(false); // 是否可以通过返回键 关闭

		// 直接show();
		//builder.show();
	}

	/**
	 * 同步数据-弹窗显示需清除数据-重新启动本应用
	 */
	private void restartApplication() {
		final Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("et.tsingtaopad");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	// 查询终端表数量
	public long getTermnalNum() {
		DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
		SQLiteDatabase db = helper.getReadableDatabase();
		String querySql = "SELECT COUNT(*)  FROM MST_TERMINALINFO_M";
		Cursor cursor = db.rawQuery(querySql, null);
		cursor.moveToFirst();
		return cursor.getLong(0);
	}
}

package et.tsingtaopad.visit.shopvisit.camera.container;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.camera.CameraPreview;
import et.tsingtaopad.visit.shopvisit.camera.container.CameraContainer.TakePictureListener;
import et.tsingtaopad.visit.shopvisit.camera.container.CameraView.FlashMode;
import et.tsingtaopad.visit.shopvisit.camera.container.ScreenListener.ScreenStateListener;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：TakeCameraActivity.java</br> 作者：ywm </br>
 * 创建时间：2015-11-17</br> 功能描述: 自定义照相机主界面</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
 * 修改人 修改版本</br>
 */
public class TakeCameraAty extends Activity implements View.OnClickListener, TakePictureListener {
	public final static String TAG = "TakeCameraAty";
	private Camera mCamera;
	private CameraPreview mPreview;
	private int position = 1;
	private String pictypekey;
	private String pictypename;
	private String focus;// 后台设定清晰度
	private static int definition = 25;// 保存本地时清晰度
	private static String termname;// 终端名称

	private TextView titleTv;
	private Button backBt;
	//private Button captureBt;
	//private ImageButton cameraBt;

	private Camera.Size previewSize;
	private List<Camera.Size> previewSizeLst;
	private Camera.Size pictureSize;
	private List<Camera.Size> pictureSizeLst;

	private boolean mIsRecordMode = false;
	private String mSaveRoot;
	private CameraContainer mContainer;
	private ImageButton mCameraShutterButton;
	//private ImageButton mRecordShutterButton;
	private ImageView mFlashView;
	//private ImageButton mSwitchModeButton;
	private ImageView mSwitchCameraView;
	private ImageView mSettingView;
	private ImageView mVideoIconView;
	private View mHeaderBar;
	private boolean isRecording = false;
	
	private int cameratype = 0;// 0:后置(默认)  1:前置
	private ScreenListener l;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DbtLog.logUtils(TAG, "onCreate()-打开摄像头界面");
		setContentView(R.layout.camera_newtakepic);
		initView();
		initData();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		DbtLog.logUtils(TAG, "initView()-初始化摄像头界面显示");
		/*titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);
		backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
		
		backBt.setOnClickListener(this);*/
		//captureBt = (Button) findViewById(R.id.button_ca_capture);
		//cameraBt = (ImageButton) findViewById(R.id.btn_shutter_camera);
		//captureBt.setOnClickListener(this);
		//cameraBt.setOnClickListener(this);

		mContainer = (CameraContainer) findViewById(R.id.container);
		//mVideoIconView = (ImageView) findViewById(R.id.videoicon_jian);
		mCameraShutterButton = (ImageButton) findViewById(R.id.btn_shutter_camera);
		//mRecordShutterButton = (ImageButton) findViewById(R.id.btn_shutter_record);
		mSwitchCameraView = (ImageView) findViewById(R.id.btn_switch_camera);// 前后摄像头
		mFlashView = (ImageView) findViewById(R.id.btn_flash_mode);
		//mSwitchModeButton = (ImageButton) findViewById(R.id.btn_switch_mode);
		mSettingView = (ImageView) findViewById(R.id.btn_other_setting);
		
		// 闪光灯
		mFlashView.setOnClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		DbtLog.logUtils(TAG, "initData()-初始化摄像头界面数据");

		//titleTv.setText("拜访拍照");

		Intent intent = getIntent();
		// 直接获取封装在intent中的数据
		position = intent.getIntExtra("position", position);
		pictypekey = intent.getStringExtra("pictypekey");
		pictypename = intent.getStringExtra("pictypename");
		focus = intent.getStringExtra("focus");
		termname = intent.getStringExtra("termname");

		DbtLog.logUtils(TAG, "initData()-获取传递过来的数据");
		
		definition = 25;
		
		/*// 设置清晰度
		switch (Integer.parseInt(focus)) {
		case 1:// 勉强清晰
				// definition = 100;
			definition = 25;
			break;
		case 2:// 清晰
			definition = 20;
			break;
		case 3:// 不清晰
			definition = 15;
			break;

		default:
			break;
		}*/

		mCameraShutterButton.setOnClickListener(this);// 拍照按钮
		//mRecordShutterButton.setOnClickListener(this);// 录像按钮

		DbtLog.logUtils(TAG, "1");
		// mFlashView.setOnClickListener(this);// 极速
		// mSwitchModeButton.setOnClickListener(this);// 
		mSwitchCameraView.setOnClickListener(this);// 前置后置摄像头
		// mSettingView.setOnClickListener(this);// 设置
		DbtLog.logUtils(TAG, "2");

		mSaveRoot = "test";
		mContainer.setRootPath(mSaveRoot);// 设置文件保存路径
		DbtLog.logUtils(TAG, "3");
		mContainer.setDefinition(definition);// 设置文件保存时的清晰度
		
		DbtLog.logUtils(TAG, "初始化摄像头界面成功");
		
		
		// 开启锁屏监听
		l = new ScreenListener(this);
        l.begin(new ScreenStateListener() {

            @Override
            public void onUserPresent() {
                //Log.e("onUserPresent", "onUserPresent");
            }

            @Override
            public void onScreenOn() {
                //Log.e("onScreenOn", "onScreenOn");
            }

            @Override
            public void onScreenOff() {
                //Log.e("onScreenOff", "onScreenOff");
            	TakeCameraAty.this.finish();
            }
        });
        
	}

	@Override
	protected void onResume() {
		super.onResume();
		initView();
		initData();

	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// 销毁锁屏监听
		if(l!=null){
			l.unregisterListener();
		}
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		// 拍照按钮 // 原自带
		case R.id.btn_shutter_camera:// 原自带
			DbtLog.logUtils(TAG, "拍照按钮");
			if (ViewUtil.isDoubleClick(view)) return;
			mCameraShutterButton.setClickable(false);
			mContainer.takePicture(this);
			break;
		// 重设拍照按钮 
		case R.id.button_capture:// 原自带
			DbtLog.logUtils(TAG, "重设拍照按钮");
			if (ViewUtil.isDoubleClick(view)) return;
			mCameraShutterButton.setClickable(false);
			mContainer.takePicture(this);
			break;
		// 返回
		case R.id.banner_navigation_bt_back:
			this.finish();
			break;
		// 切换前置后置摄像头
		case R.id.btn_switch_camera:
			DbtLog.logUtils(TAG, "切换前置后置摄像头");
			if(cameratype==0){//后置(默认)
				mContainer.switchCamera();
				cameratype = 1;// 变成前置
			}else{
				mContainer.switchCamera();
				cameratype = 0;// 变成后置
			}
			
			break;
			
		case R.id.btn_flash_mode:
			if(mContainer.getFlashMode()==FlashMode.ON){
				mContainer.setFlashMode(FlashMode.OFF);
				mFlashView.setImageResource(R.drawable.btn_flash_off);
			}else if (mContainer.getFlashMode()==FlashMode.OFF) {
				mContainer.setFlashMode(FlashMode.AUTO);
				mFlashView.setImageResource(R.drawable.btn_flash_auto);
			}
			else if (mContainer.getFlashMode()==FlashMode.AUTO) {
				mContainer.setFlashMode(FlashMode.TORCH);
				mFlashView.setImageResource(R.drawable.btn_flash_torch);
			}
			else if (mContainer.getFlashMode()==FlashMode.TORCH) {
				mContainer.setFlashMode(FlashMode.ON);
				mFlashView.setImageResource(R.drawable.btn_flash_on);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onTakePictureEnd(Bitmap bm) {// 拍照结束 保存图片
		// 可再拍
		mCameraShutterButton.setClickable(true);
		// 校正图片保存时角度
		Matrix m = new Matrix();
	    m.setRotate(90,(float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
	    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
		// 压缩 已经在拍照时压缩了
		// 加水印(在CameraFragment中加水印)
	    //bm = createWateBitmap1(bm, termname, 275, 570, ConstValues.loginSession.getUserGongHao(), 275,590, pictypename, 275, 610, DateUtil.getDateTimeStr(1), 275, 630);
		// 图片保存本地
		// 图片名称 以时间命名
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date1 = new Date(System.currentTimeMillis());
		final String name = format.format(date1) + ".jpg";
		String path = getFilesDir().getAbsolutePath() + File.separator + "photo" + File.separator;
		//saveHeadImg(path, bm, name, 100, definition);
		
		// 返回上一界面→保存记录
		Intent data1 = new Intent();
		data1.putExtra("path", path);// 图片路径 (如: sdcard/)
		data1.putExtra("position", position);// 条目位置
		data1.putExtra("name", name);// 图片名称
		data1.putExtra("pictypekey", pictypekey);
		data1.putExtra("focus", definition);
		data1.putExtra("pictypename", pictypename);
		data1.putExtra("cameratype", cameratype);// 前/后摄像头 0后,1前
		// data1.putExtra("photo", photo);//图片Bitmap
		ByteArrayOutputStream baos=new ByteArrayOutputStream();  
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
		byte [] bitmapByte =baos.toByteArray();  
		data1.putExtra("bitmap", bitmapByte);
		PrefUtils.putInt(getApplicationContext(), "idsavesuccess", 2);// 2:开始保存照片, 1:保存成功
		// 设置一个结果数据，数据会返回给调用者
		setResult(3, data1);
		DbtLog.logUtils(TAG, "拍照按钮结束,返回");
		finish();
		if(bm!=null){
			bm.recycle();
			bm=null;
		}
	}

	@Override
	public void onAnimtionEnd(Bitmap bm, boolean isVideo) {

	}

	/**
	 * 保存图片到本地()
	 * 
	 * @param filePath
	 *            图片保存路径 (如: sdcard/)
	 * @param bitmap
	 *            图片数据源
	 * @param name
	 *            图片名称 (如: 201511121138.jpg)
	 * @param definition
	 *            图片质量 (1最差 100最好)(图片最大压缩到50k以下)
	 * @param cleardata
	 *            图片最高大小 (1最差 100最好)(图片最大压缩到50k以下)
	 */
	public static void saveHeadImg(String filePath, Bitmap bitmap, String name, int definition, int cleardata) {
		DbtLog.logUtils(TAG, "saveHeadImg()-保存图片到本地");
		// 存储路径处理
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// 文件保存
		File file = null;
		if (name == null) {
			file = new File(filePath, "ywm" + ".jpg");// 名字可用随意
		} else {
			file = new File(filePath, name);
		}
		FileOutputStream fos = null;
		try {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// 图片压缩 把压缩后的数据存放到baos中
			bitmap.compress(Bitmap.CompressFormat.JPEG, definition, bos);
			// 图片大于50K继续压缩
			while (bos.toByteArray().length / 1024 > cleardata) {
				// 重置baos即清空baos
				bos.reset();
				// 每次都减少1
				definition -= 1;
				bitmap.compress(Bitmap.CompressFormat.JPEG, definition, bos);
			}
			// 生成文件
			fos = new FileOutputStream(file);
			fos.write(bos.toByteArray());
			fos.flush();
			fos.close();

		} catch (Exception e) {

		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

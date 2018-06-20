package et.tsingtaopad.visit.shopvisit.camera;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FileUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TakeCameraActivity.java</br>
 * 作者：ywm   </br>
 * 创建时间：2015-11-17</br>      
 * 功能描述: 自定义照相机主界面</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TakeCameraActivity extends Activity {
	private static final String TAG = "TakeCameraActivity";
	private Camera mCamera;
	private CameraPreview mPreview;
	private int position;
	private String pictypekey;
	private String pictypename;
	private String focus;//后台设定清晰度
	private static int definition;//保存本地时清晰度
	private static String termname;//终端名称
	
	private TextView titleTv;
	private Button backBt;
	
	private Size previewSize;
    private List<Size> previewSizeLst;
    private Size pictureSize;
    private List<Size> pictureSizeLst;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DbtLog.logUtils(TAG, "onCreate()-打开摄像头界面");
		setContentView(R.layout.camera_takepic);
		initView();
		initData();
		
		
	}
	
	/**
	 * 初始化控件
	 */
	private void initView() {
		DbtLog.logUtils(TAG, "initView()-初始化摄像头界面显示");
		titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);
		backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
		backBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		DbtLog.logUtils(TAG, "initData()-初始化摄像头界面数据");
		
		titleTv.setText("拜访拍照");
		
		Intent intent = getIntent();
        //直接获取封装在intent中的数据
        position = intent.getIntExtra("position", position);
        pictypekey = intent.getStringExtra("pictypekey");
        pictypename = intent.getStringExtra("pictypename");
        focus = intent.getStringExtra("focus");
        termname = intent.getStringExtra("termname");
        
        switch ( Integer.parseInt(focus)) {
		case 1://勉强清晰
			//definition = 100;
			definition = 25;
			break;
		case 2://清晰
			definition = 20;
			break;
		case 3://不清晰
			definition = 15;
			break;

		default:
			break;
		}
	}

	

	@Override
	protected void onResume() {
		super.onResume();
		if (checkCameraHardware(this)) {
			// 创建摄像头实例
			mCamera = getCameraInstance();
			DbtLog.logUtils(TAG, "onResume()-设置摄像头参数,聚焦等");
			
			try {
				// 相机旋转90°
				mCamera.setDisplayOrientation(90);

				// 相机参数
				Parameters params = mCamera.getParameters();
				
				//params.setPreviewSize(480,640);//width， height
				params.setPreviewSize(640,480);//width， height
				params.setPreviewFrameRate(3);
				params.setPictureFormat(PixelFormat.JPEG);
				params.set("jpeg-quality", 85);
				params.setPictureSize(640,480);//width， height
				mCamera.setParameters(params);
				
				/*
				this.pictureSizeLst = params.getSupportedPictureSizes();
		        this.previewSizeLst = params.getSupportedPreviewSizes();
				//this.pictureSize = getBestPictureSize(disH, disW);
				this.pictureSize = getBestPictureSize(240,320);
		        this.previewSize = getBestPreviewSize(this.pictureSize);
		        
		        //Camera.Parameters params = this.camera.getParameters();
	            params.setFlashMode(Parameters.FLASH_MODE_AUTO);
	            params.setPreviewSize(previewSize.width, previewSize.height);
	            params.setPictureSize(pictureSize.width, pictureSize.height);
	            //params.setPictureSize(700,700);
	            //this.mCamera.setParameters(params);
	            mCamera.setParameters(params);
		        */
				DbtLog.logUtils(TAG, "mPreview-开始设置预览界面");
				// 创建预览界面
				mPreview = new CameraPreview(this, mCamera);

				FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
				// 把SurfaceView设置为帧布局的子节点
				preview.removeAllViews();
				preview.addView(mPreview);
				DbtLog.logUtils(TAG, "mPreview-完成预览界面设置");
				
			} catch (Exception e) {
				DbtLog.logUtils(TAG, "onPause()-设置相机参数出错,销毁摄像头");
				Toast.makeText(getApplicationContext(), "调用摄像头出错,请重新打开", Toast.LENGTH_SHORT).show();
		        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
		        releaseCamera();
			}
			

			// 给拍照按钮设置点击侦听
			final Button captureButton = (Button) findViewById(R.id.button_capture);
			captureButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					//  防止按钮快速重复单击
					//if (ViewUtil.isDoubleClick(R.id.button_capture,5000)) return;
					if (ViewUtil.isDoubleClick(v)) return;
					

					FileUtil.createphotoFile(new File(getFilesDir()
							.getAbsolutePath() + File.separator + "photo" + File.separator));
					//getFilesDir().getAbsolutePath() + File.separator + "photo"+ File.separator
					
					// 自动对焦
					mCamera.autoFocus(new AutoFocusCallback() {

						// 自动对焦完成时，此方法调用
						@Override
						public void onAutoFocus(boolean success, Camera camera) {
							// 拍照
							mCamera.takePicture(null, null, mPicture);

						}
					});
					//mCamera.takePicture(null, null, mPicture);

				}
			});
		} else {
			Toast.makeText(this, "此设备没有摄像头", Toast.LENGTH_SHORT).show();
		}

		
        
        // 另外需要获取到的数据 终端主键 
	}

	/** 检测是否有摄像头 */
	private boolean checkCameraHardware(Context context) {
		DbtLog.logUtils(TAG, "checkCameraHardware()-检测是否有摄像头");
		
		boolean sd = false;
		// 检测是否具有指定的系统功能
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// 有摄像头
			//return true;
			sd = true;
		} else {
			// 没有摄像头
			//return false;
			sd = true;
		}
		return sd;
	}

	/** 这是一个获取摄像头实例的安全的途径 */
	public static Camera getCameraInstance() {
		DbtLog.logUtils(TAG, "getCameraInstance()-获取摄像头实例");
		Camera c = null;
		try {
			// 返回后置摄像头的实例，如果没有后置摄像头，返回空
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private PictureCallback mPicture = new PictureCallback() {

		// 摄像头照相方法执行时，会调用此方法
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			DbtLog.logUtils(TAG, "onPictureTaken()-点击拍照后执行此方法");
			// 指定相片保存的路径和文件名
			
			//图片名称 以时间命名
	        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	        Date date1 = new Date(System.currentTimeMillis());
	        final String name = format.format(date1)+".jpg";
	        
	        //创建File对象用于存储拍照的图片 SD卡根目录           
	        //File outputImage = new File(Environment.getExternalStorageDirectory(),test.jpg);
	        //存储至DCIM文件夹
	        //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);  
	        //File outputImage = new File(path,filename+".jpg");
			//final String path = "sdcard/";
			String path = getFilesDir().getAbsolutePath() + File.separator + "photo"+ File.separator;
			//String path = Environment.getExternalStorageDirectory()+ ""+ "/dbt/et.tsingtaopad"+ "/photo/";
			//String name = "yass.jpg";
			//File pictureFile = new File("sdcard/name.jpg");
			
			Bitmap bm0 = BitmapFactory.decodeByteArray(data, 0, data.length);// 此行会导致OOM系统崩溃
		    Matrix m = new Matrix();
		    m.setRotate(90,(float) bm0.getWidth() / 2, (float) bm0.getHeight() / 2);
		    final Bitmap bm = Bitmap.createBitmap(bm0, 0, 0, bm0.getWidth(), bm0.getHeight(), m, true);
		    //final Bitmap bm = Bitmap.createBitmap(bm0, 0, 0, (bm0.getWidth()/3)*2, (bm0.getHeight()/3)*2, m, true);
			// 加水印
		    //final Bitmap photo1 = createWateBitmap(bm, termname +" "+pictypename+"  "+DateUtil.getDateTimeStr(1),90);
		    //final Bitmap photo = createWateBitmap(photo1, ConstValues.loginSession.getUserGongHao(),30);
		    
		   /* new Thread() {
				public void run() {
					// 保存到本地
					saveHeadImg(path, photo,name);
					
				};
			}.start();*/
		    // 压缩到20k
		    //Bitmap bm1 = compressImage(bm, 5);
		    
		    
			// 保存到本地  最后优化 放在子线程中(最后还是决定不优化)
			//saveHeadImg(path, bm,name,definition);
		    // 1 压缩图片
			//saveHeadImg(path, bm,name,100,definition);
			
			//Bitmap bm1 = ratio(path+name, 320, 426);//尺寸压缩
			//Bitmap bm1 = ratio(path+name, 240, 320);//尺寸压缩
			
			//2 
			//Bitmap imageBitmap = BitmapFactory.decodeFile(path+name);
		    // 压缩bitmap
		    Bitmap imageBitmap = compressBitmap(bm, definition);
		    //Bitmap imageBitmap = compressBitmap(bm0, definition);
			// 640x320
		    // 加水印
		    final Bitmap photo = createWateBitmap1(imageBitmap, termname , 275, 570, 
		    		//ConstValues.loginSession.getUserGongHao(),275,590, 
		    		PrefUtils.getString(getApplicationContext(), "userGongHao", ""),275,590, 
		    		pictypename, 275, 610, 
		    		DateUtil.getDateTimeStr(1) , 275, 630);
		    
		    /*
			// 加终端
			final Bitmap termnam = createWateBitmap(imageBitmap, termname , 275, 570);
			//final Bitmap termnam = createWateBitmap(bm1, termname , 275, 570);
			// 加工号
		    final Bitmap gonghao = createWateBitmap(termnam, ConstValues.loginSession.getUserGongHao(),275,590);
		    // 加类型
		    final Bitmap pictypenam = createWateBitmap(gonghao, pictypename, 275, 610);
		    // 加时间
		    final Bitmap photo = createWateBitmap(pictypenam, DateUtil.getDateTimeStr(1) , 275, 630);
		    */
		    
		  
		    /*
		    // 480x320
		    final Bitmap termnam = createWateBitmap(imageBitmap, termname , 100, 280);
			//final Bitmap termnam = createWateBitmap(bm1, termname , 275, 570);
			// 加工号
		    final Bitmap gonghao = createWateBitmap(termnam, ConstValues.loginSession.getUserGongHao(),100,292);
		    // 加类型
		    final Bitmap pictypenam = createWateBitmap(gonghao, pictypename, 100, 304);
		    // 加时间
		    final Bitmap photo = createWateBitmap(pictypenam, DateUtil.getDateTimeStr(1) , 100, 316);
		    */
		    
		    
		    //3
		    //FileUtil.deleteFile(new File(path+name));
		    // 保存到本地  最后优化 放在子线程中(最后还是决定不优化)
		 	saveHeadImg(path, photo,name,100,definition);
		 	//saveHeadImg1(path, photo,name,100);
		    
		    Intent data1 = new Intent();
		    data1.putExtra("path", path);// 图片路径 (如:  sdcard/)
		    data1.putExtra("position", position);//条目位置
		    data1.putExtra("name", name);//图片名称
		    data1.putExtra("pictypekey", pictypekey);
		    data1.putExtra("pictypename", pictypename);
		    
		    ByteArrayOutputStream baos=new ByteArrayOutputStream();  
		    photo.compress(CompressFormat.PNG, 100, baos);
			byte [] bitmapByte =baos.toByteArray();  
			data1.putExtra("bitmap", bitmapByte);
		    //data1.putExtra("photo", photo);//图片Bitmap 
		    PrefUtils.putInt(getApplicationContext(), "idsavesuccess", 2);
		    //设置一个结果数据，数据会返回给调用者
	        setResult(3, data1);
		    
			finish();
			
		}
	};
	
	/**
	 * 生成文字水印
	 * 
	 * @param src
	 * 			原始图片
	 * @param mstrTitle
	 * 			需要添加的文字
	 * @return
	 */
	private Bitmap createWateBitmap(Bitmap src, String mstrTitle,int weith,int heigh) {
		DbtLog.logUtils(TAG, "createWateBitmap()-生成不同文字水印");
		int w = src.getWidth();
		int h = src.getHeight();
		// 创建一个新的和SRC长度宽度一样的位图
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		// 相当于创建一个和原图大小相等的画板
		Canvas cv = new Canvas(newb);
		// 在 0，0坐标开始画入src
		cv.drawBitmap(src, 0, 0, null);
		// 画笔
		Paint p = new Paint();
		String familyName = "宋体";
		Typeface font = Typeface.create(familyName, Typeface.BOLD);
		p.setColor(Color.RED);
		p.setTypeface(font);
		p.setTextSize(22);
		cv.drawText(mstrTitle, weith, heigh, p);
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储

		return newb;
	}
	private Bitmap createWateBitmap1(Bitmap src, String mstrTitle,int weith,int heigh
			, String mstrTitle2,int weith2,int heigh2
			, String mstrTitle3,int weith3,int heigh3
			, String mstrTitle4,int weith4,int heigh4) {
		DbtLog.logUtils(TAG, "createWateBitmap()-生成不同文字水印");
		int w = src.getWidth();
		int h = src.getHeight();
		// 创建一个新的和SRC长度宽度一样的位图
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		// 相当于创建一个和原图大小相等的画板
		Canvas cv = new Canvas(newb);
		// 在 0，0坐标开始画入src
		cv.drawBitmap(src, 0, 0, null);
		// 画笔
		Paint p = new Paint();
		String familyName = "宋体";
		Typeface font = Typeface.create(familyName, Typeface.BOLD);
		p.setColor(Color.RED);
		p.setTypeface(font);
		p.setTextSize(22);
		cv.drawText(mstrTitle, weith, heigh, p);
		cv.drawText(mstrTitle2, weith2, heigh2, p);
		cv.drawText(mstrTitle3, weith3, heigh3, p);
		cv.drawText(mstrTitle4, weith4, heigh4, p);
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		
		return newb;
	}
	
	/**
	 * 保存图片到本地()
	 * @param filePath 		图片保存路径  (如:  sdcard/)
	 * @param bitmap   		图片数据源
	 * @param name     		图片名称   (如: 201511121138.jpg)
	 * @param definition    图片质量   (1最差    100最好)(图片最大压缩到50k以下)
	 * @param cleardata     图片最高大小   (1最差    100最好)(图片最大压缩到50k以下)
	 */
	public static void saveHeadImg(String filePath, Bitmap bitmap,String name,int definition,int cleardata) {
		DbtLog.logUtils(TAG, "saveHeadImg()-保存图片到本地");
		// 存储路径处理
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// 文件保存
		File file = null;
		if(name == null){
			file = new File(filePath, "ywm" + ".jpg");// 名字可用随意
		}else{
			file = new File(filePath, name);
		}
		FileOutputStream fos = null;
		try {
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();  
	        // 图片压缩  把压缩后的数据存放到baos中
	        bitmap.compress(CompressFormat.JPEG, definition, bos);
	        // 图片大于50K继续压缩  
	        while ( bos.toByteArray().length / 1024 > cleardata) {  
	            // 重置baos即清空baos 
	        	bos.reset();  
	            // 每次都减少1
	        	definition -= 1;  
	            bitmap.compress(CompressFormat.JPEG, definition, bos);
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
	/**
	 * 保存图片到本地()
	 * @param filePath 		图片保存路径  (如:  sdcard/)
	 * @param bitmap   		图片数据源
	 * @param name     		图片名称   (如: 201511121138.jpg)
	 * @param definition    图片质量   (1最差    100最好)(图片最大压缩到50k以下)
	 */
	public static void saveHeadImg1(String filePath, Bitmap bitmap,String name,int definition) {
		DbtLog.logUtils(TAG, "saveHeadImg1()-保存图片到本地");
		// 存储路径处理
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		// 文件保存
		File file = null;
		if(name == null){
			file = new File(filePath, "ywm" + ".jpg");// 名字可用随意
		}else{
			file = new File(filePath, name);
		}
		FileOutputStream fos = null;
		try {
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();  
			// 图片压缩  把压缩后的数据存放到baos中
			bitmap.compress(CompressFormat.JPEG, definition, bos);
			// 图片大于50K继续压缩  
			while ( bos.toByteArray().length / 1024 > 10) {  
				// 重置baos即清空baos 
				bos.reset();  
				// 每次都减少1
				definition -= 1;  
				bitmap.compress(CompressFormat.JPEG, definition, bos);
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
	
	@Override
    protected void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()-销毁摄像头");
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder(){
        
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    
    /**
     * 获取最合适的预览尺寸
     */
    private Size getBestPreviewSize(Size size) {
        String scale = String.format("%.4f", size.width / (double)size.height).substring(0, 3);
        Collections.sort(previewSizeLst, new Comparator<Size>() {
            @Override
            public int compare(Size lhs, Size rhs) {
                return ((rhs.height + rhs.width) - (lhs.height + lhs.width));
            }
        });
        
        Size bestSize = null;
        String tempScale = "";
        for (Size item : previewSizeLst) {
            tempScale = String.format("%.4f", (item.width / (double)item.height)).substring(0, 3);
            if (scale.equals(tempScale)) {
                bestSize = item;
                break;
            }
        }
        return bestSize == null ? previewSizeLst.get(0) : bestSize;
    }
    
    /**
     * 获取最合适的图片尺寸
     * 
     * @param width     最低尺寸的宽度
     * @param height    最低尺寸的高度
     */
    private Size getBestPictureSize(int width, int height) {
        String scale = String.format("%.4f", width / (double)height).substring(0, 3);
        
        // 排序Camera支持的图片尺寸列表
        Collections.sort(pictureSizeLst, new Comparator<Size>() {
            @Override
            public int compare(Size lhs, Size rhs) {
                return ((lhs.height +lhs.width) - (rhs.height + rhs.width));
            }
        });
        
        Size bestSize = null;
        String tempScale = "";
        for (Size item : pictureSizeLst) {
            tempScale = String.format("%.4f", (item.width / (double)item.height)).substring(0, 3);
            if (tempScale.equals(scale) ) {
                bestSize = item;
                if (item.width >= 640) {
                    break;
                }
            }
        }
        return bestSize == null ? pictureSizeLst.get(0) : bestSize;
    }
    
	/**
	 * 压缩bitmap
	 * @param bitmap
	 * @param cleardata 要压到的大小
	 * @return
	 */
	public static Bitmap compressBitmap(Bitmap bitmap,int cleardata){
		DbtLog.logUtils(TAG, "compressBitmap()-压缩bitmap照片");
			
			FileOutputStream fos = null;
			try {
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();  
		        // 图片压缩  把压缩后的数据存放到baos中
		        bitmap.compress(CompressFormat.JPEG, definition, bos);
		        // 图片大于50K继续压缩  
		        while ( bos.toByteArray().length / 1024 > cleardata) {  
		            // 重置baos即清空baos 
		        	bos.reset();  
		            // 每次都减少1
		        	definition -= 1;  
		            bitmap.compress(CompressFormat.JPEG, definition, bos);
		        }  
				
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
		
			return bitmap;
		}
    
}

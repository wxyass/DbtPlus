package et.tsingtaopad;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
}

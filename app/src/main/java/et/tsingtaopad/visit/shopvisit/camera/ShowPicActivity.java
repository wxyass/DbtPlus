package et.tsingtaopad.visit.shopvisit.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstAgencyKFM;

public class ShowPicActivity extends Activity {

	// private ImageView imageView;
	private com.github.chrisbanes.photoview.PhotoView imageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visit_camera_show);

		// imageView = (ImageView) this.findViewById(R.id.showpic_iv_imageView);
		imageView = (com.github.chrisbanes.photoview.PhotoView) this.findViewById(R.id.showpic_iv_imageView);

		initData();
		
		// imageView.setOnTouchListener(new TouchListener());
	}

	/**
	 * 获取图片数据
	 */
	private void initData() {

		// 获取传递过来的数据
		Intent intent = getIntent();
		/*if (intent != null) {
			Bitmap bitmap = intent.getParcelableExtra("bitmap");
			imageView.setImageBitmap(bitmap);
		}*/
		if(intent !=null)  
        {  
            byte [] bis=intent.getByteArrayExtra("bitmap");  
            Bitmap bitmap=BitmapFactory.decodeByteArray(bis, 0, bis.length);  
            //bitmap = zoomBitmap(bitmap,imageView.getWidth(), imageView.getHeight());
            imageView.setImageBitmap(bitmap);
            //imageView.set
            //imageView.setImageBitmap(bitmap);  
        } 
	}

	private final class TouchListener implements OnTouchListener {
		
		/** 记录是拖拉照片模式还是放大缩小照片模式 */
		private int mode = 0;// 初始状态  
		/** 拖拉照片模式 */
		private static final int MODE_DRAG = 1;
		/** 放大缩小照片模式 */
		private static final int MODE_ZOOM = 2;
		
		/** 用于记录开始时候的坐标位置 */
		private PointF startPoint = new PointF();
		/** 用于记录拖拉图片移动的坐标位置 */
		private Matrix matrix = new Matrix();
		/** 用于记录图片要进行拖拉时候的坐标位置 */
		private Matrix currentMatrix = new Matrix();
	
		/** 两个手指的开始距离 */
		private float startDis;
		/** 两个手指的中间点 */
		private PointF midPoint;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			imageView.setScaleType(ImageView.ScaleType.MATRIX);
			
			/** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			
			
			// 手指压下屏幕
			case MotionEvent.ACTION_DOWN:
				mode = MODE_DRAG;
				// 记录ImageView当前的移动位置
				currentMatrix.set(imageView.getImageMatrix());
				startPoint.set(event.getX(), event.getY());
				break;
			// 手指在屏幕上移动，改事件会被不断触发
			case MotionEvent.ACTION_MOVE:
				// 拖拉图片
				if (mode == MODE_DRAG) {
					float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
					float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
					// 在没有移动之前的位置上进行移动
					matrix.set(currentMatrix);
					matrix.postTranslate(dx, dy);
				}
				// 放大缩小图片
				else if (mode == MODE_ZOOM) {
					float endDis = distance(event);// 结束距离
					if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
						float scale = endDis / startDis;// 得到缩放倍数
						matrix.set(currentMatrix);
						matrix.postScale(scale, scale,midPoint.x,midPoint.y);
					}
				}
				break;
			// 手指离开屏幕
			case MotionEvent.ACTION_UP:
				// 当触点离开屏幕，但是屏幕上还有触点(手指)
			case MotionEvent.ACTION_POINTER_UP:
				mode = 0;
				break;
			// 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
			case MotionEvent.ACTION_POINTER_DOWN:
				mode = MODE_ZOOM;
				/** 计算两个手指间的距离 */
				startDis = distance(event);
				/** 计算两个手指间的中间点 */
				if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
					midPoint = mid(event);
					//记录当前ImageView的缩放倍数
					currentMatrix.set(imageView.getImageMatrix());
				}
				break;
			}
			imageView.setImageMatrix(matrix);
			return true;
		}

		/** 计算两个手指间的距离 */
		private float distance(MotionEvent event) {
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			/** 使用勾股定理返回两点之间的距离 */
			// return FloatMath.sqrt(dx * dx + dy * dy);
			return  (float)Math.sqrt(dx * dx + dy * dy);
		}

		/** 计算两个手指间的中间点 */
		private PointF mid(MotionEvent event) {
			float midX = (event.getX(1) + event.getX(0)) / 2;
			float midY = (event.getY(1) + event.getY(0)) / 2;
			return new PointF(midX, midY);
		}

	}
	
}
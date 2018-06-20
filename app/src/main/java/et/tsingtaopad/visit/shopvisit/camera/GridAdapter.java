package et.tsingtaopad.visit.shopvisit.camera;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import et.tsingtaopad.R;
import et.tsingtaopad.visit.shopvisit.camera.domain.PictypeDataStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：GridAdapter.java</br>
 * 作者：   </br>
 * 创建时间：2015-11-11</br>      
 * 功能描述: 拍照GridView适配器,展示每张缩略图</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
class GridAdapter extends BaseAdapter {
	
	private Activity context;
    private List<PictypeDataStc> dataLst;
    Map<Integer,Bitmap> cameraPicMap;
    
	public GridAdapter(Activity context, List<PictypeDataStc> valueLst,Map<Integer,Bitmap> cameraPicMap ) {
        this.context = context;
        this.dataLst = valueLst;
        this.cameraPicMap = cameraPicMap;
    }

	// item的个数
	@Override
	public int getCount() {
		return dataLst.size();
	}

	// 根据位置获取对象
	@Override
	public Bitmap getItem(int position) {
		return cameraPicMap.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	// 初始化每一个item的布局(待优化)
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = View.inflate(context,R.layout.camera_takepic_item, null);
		ImageView picIv = (ImageView) view.findViewById(R.id.camera_iv_pic);
		TextView descTv = (TextView) view.findViewById(R.id.camera_tv_desc);

		/*for (PictypeDataStc item : dataLst) {
			if(String.valueOf(position+1).equals(item.getOrderno())){
				descTv.setText(item.getPictypename());
			}
		}*/
		
		PictypeDataStc pictypedatastc = (PictypeDataStc)dataLst.get(position);
		
		descTv.setText(pictypedatastc.getPictypename());
		picIv.setImageBitmap(cameraPicMap.get(Integer.parseInt(pictypedatastc.getOrderno())));
		
		/*descTv.setText(dataLst.get(position).getPictypename());
		picIv.setImageBitmap(cameraPicMap.get(position));*/
		return view;
	}

}

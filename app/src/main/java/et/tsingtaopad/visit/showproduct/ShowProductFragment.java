package et.tsingtaopad.visit.showproduct;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;

import et.tsingtaopad.BitmapHelp;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstProductshowM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.PropertiesUtil;

public class ShowProductFragment extends Fragment implements OnClickListener, OnItemClickListener {

	public static BitmapUtils bitmapUtils;

	private GridView mGridView;
	private ImageListAdapter imageListAdapter;
	private List<MstProductshowM> productShows;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 查询产品展示表,搂出数据
		DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
		try {
			Dao<MstProductshowM, String> mstProductshowMDao = helper.getMstProductshowMDao();
			productShows = mstProductshowMDao.queryForEq("showstatus", "0");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.show_product_fragment, container, false); // 加载fragment布局
		TextView titleTV = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		view.findViewById(R.id.banner_navigation_bt_back).setOnClickListener(this);
		titleTV.setText(getString(R.string.show_product_title));
		mGridView = (GridView) view.findViewById(R.id.show_product_gridview);
		bitmapUtils = BitmapHelp.getBitmapUtils(this.getActivity().getApplicationContext());
		bitmapUtils.configDefaultLoadingImage(R.drawable.bg_login);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.bg_login);
		bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		//ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		//animation.setDuration(800);
		//bitmapUtils.configDefaultImageLoadAnimation(animation);
		// 设置最大宽高, 不设置时更具控件属性自适应.
		bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(getActivity()).scaleDown(3));
		// 滑动时加载图片，快速滑动时不加载图片
		mGridView.setOnScrollListener(new PauseOnScrollListener(bitmapUtils, false, true));
		if (!CheckUtil.IsEmpty(productShows)) {
			imageListAdapter = new ImageListAdapter(getActivity(), productShows);
			mGridView.setAdapter(imageListAdapter);
		} else {
			view.findViewById(R.id.nodata_promotion).setVisibility(View.VISIBLE);
		}
		
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
		mGridView.setOnItemClickListener(this);
		view.setOnClickListener(null);
		return view;
	}

	//查询数据

	private class ImageListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<MstProductshowM> productShows;
		String url = PropertiesUtil.getProperties("platform_web");

		public ImageListAdapter(Context context, List<MstProductshowM> productShows) {
			this.productShows = productShows;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return productShows.size();
		}

		@Override
		public Object getItem(int position) {
			return productShows.get(position);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.show_product_gridview_item, null);
				holder.pruductIcon = (ImageView) view.findViewById(R.id.pruductIcon);
				//holder.product_price = (TextView) view.findViewById(R.id.product_price);
				holder.pruduct_name = (TextView) view.findViewById(R.id.pruduct_name);
				holder.pruduct_desc = (TextView) view.findViewById(R.id.pruduct_desc);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			MstProductshowM mstProductshowM = productShows.get(position);
			String picurl = url + mstProductshowM.getPropic();
			bitmapUtils.display(holder.pruductIcon, picurl);
			holder.pruduct_name.setText(mstProductshowM.getProname());
			holder.pruduct_desc.setText(mstProductshowM.getNorms());
		//	holder.product_price.setText("￥" + mstProductshowM.getProprice());

			return view;
		}
	}

	static class ViewHolder {
		ImageView pruductIcon;
		//TextView product_price;
		TextView pruduct_name;
		TextView pruduct_desc;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			this.getFragmentManager().popBackStack();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		ShowProductDetailFragment fragment = new ShowProductDetailFragment();
		Bundle b = new Bundle();
		MstProductshowM mstProductshowM = productShows.get(position);
		b.putString("showkey", mstProductshowM.getShowkey());
		b.putString("price", mstProductshowM.getProprice());
		b.putString("norms", mstProductshowM.getNorms());
		b.putString("proName", mstProductshowM.getProname());
		b.putString("proDetail", mstProductshowM.getProdetail());
		fragment.setArguments(b);
		transaction.replace(R.id.visit_container, fragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(null);
		transaction.commit();

	}

}

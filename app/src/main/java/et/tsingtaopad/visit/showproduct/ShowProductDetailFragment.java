package et.tsingtaopad.visit.showproduct;

import java.sql.SQLException;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.lidroid.xutils.BitmapUtils;

import et.tsingtaopad.BitmapHelp;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstShowpicInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.PropertiesUtil;

public class ShowProductDetailFragment extends Fragment implements OnClickListener {

	public static BitmapUtils bitmapUtils;
	private MstShowpicInfo showPicInfo;
	private String price;
	private String proName;
	private String norms;
	private String proDetail;
	String url = PropertiesUtil.getProperties("platform_web");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arguments = getArguments();
		String showkey = arguments.getString("showkey");
		price = arguments.getString("price");
		proName = arguments.getString("proName");
		norms = arguments.getString("norms");
		proDetail = arguments.getString("proDetail");
		DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
		try {
			Dao<MstShowpicInfo, String> mstShowpicInfoDao = helper.getMstShowpicInfoDao();
			List<MstShowpicInfo> lst = mstShowpicInfoDao.queryForEq("showkey", showkey);
			if (!CheckUtil.IsEmpty(lst)) {
			    showPicInfo = lst.get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.show_product_detail_fragment, container, false);
		initView(view);

		return view;
	}

	private void initView(View view) {
		
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);

		bitmapUtils = BitmapHelp.getBitmapUtils(this.getActivity().getApplicationContext());
		bitmapUtils.configDefaultLoadingImage(R.drawable.bg_login);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.bg_login);
		bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		ImageView product_img = (ImageView) view.findViewById(R.id.product_img);
		ImageView show_product_detail_img = (ImageView) view.findViewById(R.id.show_product_detail_img);
		TextView show_product_netcontent = (TextView) view.findViewById(R.id.show_product_netcontent);
		TextView banner_navigation_tv_title = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		TextView show_product_stock = (TextView) view.findViewById(R.id.show_product_stock);
		TextView show_product_maltosedu = (TextView) view.findViewById(R.id.show_product_maltosedu);
		TextView show_product_alcoholic = (TextView) view.findViewById(R.id.show_product_alcoholic);
		TextView show_product_standards = (TextView) view.findViewById(R.id.show_product_standards);
		TextView show_product_levels = (TextView) view.findViewById(R.id.show_product_levels);
		TextView show_product_temp = (TextView) view.findViewById(R.id.show_product_temp);
		TextView show_product_drinktemp = (TextView) view.findViewById(R.id.show_product_drinktemp);
		TextView show_product_safedate = (TextView) view.findViewById(R.id.show_product_safedate);
		TextView product_remarks = (TextView) view.findViewById(R.id.product_remarks);
		TextView show_product_detail_price = (TextView) view.findViewById(R.id.show_product_detail_price);
		TextView show_product_detail_proname = (TextView) view.findViewById(R.id.show_product_detail_proname);
		TextView show_product_detail_prodesc = (TextView) view.findViewById(R.id.show_product_detail_prodesc);
		TextView show_product_detail_prolevel = (TextView) view.findViewById(R.id.show_product_detail_prolevel);
		view.findViewById(R.id.banner_navigation_bt_back).setOnClickListener(this);
		banner_navigation_tv_title.setText(proName);
		if (showPicInfo != null) {
			bitmapUtils.display(product_img, url + showPicInfo.getShowpicurl());
			// bitmapUtils.display(show_product_detail_img, url +
			// showPicInfo.getShowpicurl());
			// show_product_detail_price.setText("￥" + price);
			show_product_detail_proname.setText(proName);
			show_product_detail_prodesc.setText(norms);
			show_product_netcontent.setText("[净 含 量]：" + showPicInfo.getNetcontent());
			show_product_stock.setText("[原  料]：" + showPicInfo.getStock());
			show_product_maltosedu.setText("[原麦汁浓度]：" + showPicInfo.getMaltosedu());
			show_product_alcoholic.setText("[酒 精 度]：" + showPicInfo.getAlcoholic());
			show_product_standards.setText("[执行标准]：" + showPicInfo.getStandards());
			show_product_levels.setText("[质量等级]：" + showPicInfo.getLevels());
			show_product_temp.setText("[保 温 度]：" + showPicInfo.getTemp());
			show_product_drinktemp.setText("[饮用温度]：" + showPicInfo.getDrinktemp());
			show_product_safedate.setText("[保 质 期]：" + showPicInfo.getSafedate());
			product_remarks.setText(proDetail);
		}
		view.setOnClickListener(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.banner_navigation_bt_back:
		case R.id.banner_navigation_rl_back:
			getFragmentManager().popBackStack();
			break;

		default:
			break;
		}

	}
}

package et.tsingtaopad.operation.zhuguan;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.com.benyoyo.manage.Struct.ResponseStructBean;
import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.operation.orders.TermProductAdapter;
import et.tsingtaopad.operation.orders.VHTableAdapter;
import et.tsingtaopad.operation.orders.domain.OrdersDataStcN;
import et.tsingtaopad.operation.orders.vhtableview.VHTableView;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：workplanFragment.java</br> 作者：@ywm </br>
 * 创建时间：2013-11-29</br> 功能描述: 主管系统</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class ZhuguanFragment extends BaseFragmentSupport implements OnClickListener {

	private final String TAG = "ZhuguanFragment";

	private TextView tv_title;
	private Button btn_back;
	private WebView web_zhuguan;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.operation_zhuguan, null);
		view.setOnClickListener(null);

		initView(view);
		
		initData();
		

		return view;
	}

	/**
	 * 初始化界面组件
	 */
	private void initView(View view) {
		// 绑定界面组件
		btn_back = (Button) view.findViewById(R.id.banner_navigation_bt_back);
		tv_title = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		 web_zhuguan = (WebView) view.findViewById(R.id.weekwork_web_zhuguan);
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		btn_back.setOnClickListener(this);

	}

	/**
	 * 运用管理模块工作计划-->本周计划完成目标汇总预览
	 */
	private void showWebView() {
		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append("http://cms.tsingtao.com.cn:8001/da/zg/index.html");
		//urlBuffer.append("?areaId=").append(PrefUtils.getString(getActivity(), "disId", ""));
		//urlBuffer.append("&userId=").append(PrefUtils.getString(getActivity(), "userCode", ""));
		urlBuffer.append("?areaId=").append("1-7F88J0");
		urlBuffer.append("&userId=").append("bfabba24-df1d-40ed-9c0d-42959a16f079");
		web_zhuguan.loadUrl(urlBuffer.toString());
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		tv_title.setText(R.string.operation_zhuguan);
		showWebView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 界面返回按钮
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			this.getFragmentManager().popBackStack();
			break;

		}
	}

}

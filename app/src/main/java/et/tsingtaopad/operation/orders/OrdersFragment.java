package et.tsingtaopad.operation.orders;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.operation.orders.domain.OrdersDataStcN;
import et.tsingtaopad.operation.orders.vhtableview.VHTableView;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：workplanFragment.java</br> 作者：@ywm </br>
 * 创建时间：2013-11-29</br> 功能描述: 当日订单明细</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class OrdersFragment extends BaseFragmentSupport implements OnClickListener {

	private final String TAG = "OrdersFragment";

	private TextView tv_title;
	private TextView termname;
	private TextView proname;
	private TextView ordernum;
	private Button btn_back;

	private List<OrdersDataStcN> orderDataStcs = new ArrayList<OrdersDataStcN>();
	private ArrayList<String> titleAll = new ArrayList<String>();;// 标题栏集合
	private ArrayList<ArrayList<String>> contentAll = new ArrayList<ArrayList<String>>();;// 表格内容
	private VHTableView vht_table;

	private CheckBox cb1, cb2, cb3, cb4;
	private Button search; 
	// 声明一个集合
	private List<CheckBox> checkBoxs = new ArrayList<CheckBox>();
	
	// 默认显示的数据(3列)
	private LinearLayout topLl;
	private LinearLayout initLl;
	private ListView tableLv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.operation_orders, null);
		view.setOnClickListener(null);

		initView(view);
		
		initData();
		
		getDataFromNet();// 请求网络

		return view;
	}

	/**
	 * 初始化界面组件
	 */
	private void initView(View view) {
		// 绑定界面组件
		btn_back = (Button) view.findViewById(R.id.banner_navigation_bt_back);
		tv_title = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		vht_table = (VHTableView) view.findViewById(R.id.vht_table);
		initLl = (LinearLayout)view.findViewById(R.id.orders_ll_initlv);
		topLl = (LinearLayout)view.findViewById(R.id.orders_ll_top);
		tableLv = (ListView)view.findViewById(R.id.orders_lv_table);
		termname = (TextView) view.findViewById(R.id.orders_tv_termname);
		proname = (TextView) view.findViewById(R.id.orders_tv_proname);
		ordernum = (TextView) view.findViewById(R.id.orders_tv_ordernum);

		// 获取组件
		cb1 = (CheckBox) view.findViewById(R.id.orders_checkBox1);
		cb2 = (CheckBox) view.findViewById(R.id.orders_checkBox2);
		cb3 = (CheckBox) view.findViewById(R.id.orders_checkBox3);
		cb4 = (CheckBox) view.findViewById(R.id.orders_checkBox4);
		search = (Button) view.findViewById(R.id.orders_search);

		// 注册事件
		cb1.setOnCheckedChangeListener(listener);
		cb2.setOnCheckedChangeListener(listener);
		cb3.setOnCheckedChangeListener(listener);
		cb4.setOnCheckedChangeListener(listener);
		// 把四个组件添加到集合中去
		checkBoxs.add(cb1);
		checkBoxs.add(cb2);
		checkBoxs.add(cb3);
		checkBoxs.add(cb4);

		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
		btn_back.setOnClickListener(this);
		search.setOnClickListener(this);

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		tv_title.setText(R.string.operation_orders);
	}

	// checkbox的点击监听
	CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			CheckBox box = (CheckBox) buttonView;
			//Toast.makeText(getActivity(), "获取的值:" + box.getText(),0).show();
		}
	};

	// 从后台拉取订单数据, 并展示
	public void getDataFromNet(){

		StringBuffer buffer = new StringBuffer();
		//buffer.append("{userid:'").append(ConstValues.loginSession.getUserCode());
		buffer.append("{userid:'").append(PrefUtils.getString(getActivity(), "userCode", ""));
		//buffer.append("', gridkey:'").append(ConstValues.loginSession.getGridId());
		buffer.append("', gridkey:'").append(PrefUtils.getString(getActivity(), "gridId", ""));
		buffer.append("', visitdate:'").append(DateUtil.getDateTimeStr(0)).append("'}");

		//String ds ="{userid:'4dfe8651-9678-4f6b-89fc-c7201bba156f', gridkey:'1-63UNDH', visitdate:'20161101'}";
		// 请求网络
		HttpUtil httpUtil = new HttpUtil(60 * 1000);
		httpUtil.configResponseTextCharset("ISO-8859-1");
		httpUtil.send("opt_get_order", buffer.toString(), new RequestCallBack<String>() {
		//httpUtil.send("opt_get_order", ds, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				topLl.setVisibility(View.VISIBLE);
				ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
				if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
				    // 解析json
					orderDataStcs.clear();
				    orderDataStcs = JsonUtil.parseList(resObj.getResBody().getContent(), OrdersDataStcN.class);
				    setTableData(0, titleAll);// 设置表格数据 // 默认3列 (此时titleAll.size()是0个)

				} 
			}

			@Override
			public void onFailure(HttpException error, String errMsg) {
				Log.e(TAG, errMsg, error);
				topLl.setVisibility(View.GONE);
				Toast.makeText(getActivity(), "网络异常 获取订单明细失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 界面返回按钮
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			this.getFragmentManager().popBackStack();
			break;

		
			
		case R.id.orders_search:// 查询 用户选择了那几列
			titleAll.clear();// 清空标题栏所有列
			titleAll.add(getString(R.string.orders_termname));// 终端名称
			
			int count = 0;// 0:默认没勾选  1:有勾选
			for (CheckBox cbx : checkBoxs) {
				if (cbx.isChecked()) {// 该项若被选中
					
					count = 1;
					if ("定格/线路".equals(cbx.getText())) {// 标题栏添加 大区二级区域定格线路这4列
						titleAll.add(getString(R.string.orders_bigarea));// 大区
						titleAll.add(getString(R.string.orders_areaname));// 二级区域
						titleAll.add(getString(R.string.orders_gridname));// 定格 
						titleAll.add(getString(R.string.orders_linename));// 路线
						
					} else if ("终端详情".equals(cbx.getText())) {
						titleAll.add(getString(R.string.orders_terminalkey));// 终端编码
						titleAll.add(getString(R.string.orders_contact));// 终端联系人
						titleAll.add(getString(R.string.orders_mobile));// 联系电话
						titleAll.add(getString(R.string.orders_address));// 地   址
						
					} else if ("拜访信息".equals(cbx.getText())) {
						titleAll.add(getString(R.string.orders_visitperson));// 拜访对象
						titleAll.add(getString(R.string.orders_visitdate));// 拜访时间
						
					} else if ("供货商".equals(cbx.getText())) {
						titleAll.add(getString(R.string.orders_agencyname));// 供货商
						
					}
				}
			}
			
			// 只要有选,就添加产品 订单量两列
			if(count == 1){
				titleAll.add(getString(R.string.orders_productname));// 产   品
				titleAll.add(getString(R.string.orders_prevnum));// 订单量
			}
			
			// 根据标题栏列数 填充表格数据
			if (titleAll.size()==1) {// 只有一个终端名称 默认3列 终端名称 产品 订单量
				setTableData(0, titleAll);// 设置表格数据 默认3列
				
			} else if(titleAll.size()==3){// 标题栏有3列 终端名称 拜访对象 拜访时间(废弃)
				setTableData(2, titleAll);// 设置表格数据 
				
			} else {
				setTableData(1, titleAll);// 根据用户选中展示所选列
			}
			break;
		}
	}

	/**
	 * 展示表格数据
	 * 
	 * @param index
	 *            0:默认3列(此时titleAll集合的个数可以是任意,都会变成默认3列 终端名称 产品 订单量) 
	 *            1:根据用户选中展示所选列 
	 *            2:标题栏有3列(此时titleAll集合的个数可以是任意,都会变成3列 终端名称 拜访对象 拜访时间)
	 * @param titleAll
	 *            列名集合(index为0是填 为1时填集合),
	 */
	public void setTableData(int index, ArrayList<String> titleAll) {
		if (0 == index) {// 0:标题栏默认3列(样式一)
			initLl.setVisibility(View.VISIBLE);
			vht_table.setVisibility(View.GONE);
			termname.setText(R.string.orders_termname);
			proname.setText(R.string.orders_productname);
			ordernum.setText(R.string.orders_prevnum);
			if(orderDataStcs.size()>0){// 订单数量大于0,展示数据
				TermProductAdapter productAdapter = new TermProductAdapter(getActivity(), orderDataStcs,0);// 0:产品 订单量
				tableLv.setAdapter(productAdapter);
		    }else {
		    	Toast.makeText(getActivity(), "今日拜访未填写订单数据", Toast.LENGTH_SHORT).show();
		    }
		} else if(2 == index){// 2:标题栏有3列(样式一) 终端名称 拜访对象 拜访时间
			initLl.setVisibility(View.VISIBLE);
			vht_table.setVisibility(View.GONE);
			termname.setText(R.string.orders_termname);
			proname.setText(R.string.orders_visitperson);
			ordernum.setText(R.string.orders_visitdate);
			if(orderDataStcs.size()>0){// 订单数量大于0,展示数据
				TermProductAdapter productAdapter = new TermProductAdapter(getActivity(), orderDataStcs,1);// 拜访信息
				tableLv.setAdapter(productAdapter);
		    }else {
		    	Toast.makeText(getActivity(), "今日拜访未填写订单数据", Toast.LENGTH_SHORT).show();
		    }
		
		} else { // 1:根据用户的选择去展示数据(样式二)
			
			initLl.setVisibility(View.GONE);
			vht_table.setVisibility(View.VISIBLE);
			
			if(orderDataStcs.size()>0){// 订单数量大于0,展示数据
				
				// 设置表格内容 (根据标题栏 给每个格子设置内容)
				contentAll.clear();// 清空之前的表格内容
				for (int i = 0; i < orderDataStcs.size(); i++) {// 有几个对象,就有几行数据

					ArrayList<String> contentRowData = new ArrayList<String>();// 每一行的数据

					for (String singTitle : titleAll) {// 遍历标题栏,一一填充数据

						// 将对象的字段数据一一对应,随之赋值
						if (getString(R.string.orders_bigarea).equals(singTitle)) {// 大区
							contentRowData.add(orderDataStcs.get(i).getBigareaname());

						} else if (getString(R.string.orders_areaname).equals(singTitle)) {// 二级区域
							contentRowData.add(orderDataStcs.get(i).getSecareaname());

						} else if (getString(R.string.orders_gridname).equals(singTitle)) {// 定格
							contentRowData.add(orderDataStcs.get(i).getGridname());

						} else if (getString(R.string.orders_linename).equals(singTitle)) {// 路线
							contentRowData.add(orderDataStcs.get(i).getRoutename());

						} else if (getString(R.string.orders_terminalkey).equals(singTitle)) {// 终端编码
							contentRowData.add(orderDataStcs.get(i).getTerminalcode());

						} else if (getString(R.string.orders_termname).equals(singTitle)) {// 终端名称
							contentRowData.add(orderDataStcs.get(i).getTerminalname());

						} else if (getString(R.string.orders_contact).equals(singTitle)) {// 终端联系人
							contentRowData.add(orderDataStcs.get(i).getContact());

						} else if (getString(R.string.orders_mobile).equals(singTitle)) {// 联系电话
							contentRowData.add(orderDataStcs.get(i).getMobile());

						} else if (getString(R.string.orders_address).equals(singTitle)) {// 地   址
							contentRowData.add(orderDataStcs.get(i).getAddress());

						} else if (getString(R.string.orders_visitperson).equals(singTitle)) {// 拜访对象
							contentRowData.add(orderDataStcs.get(i).getVisituser());

						} else if (getString(R.string.orders_visitdate).equals(singTitle)) {// 拜访时间
							contentRowData.add(orderDataStcs.get(i).getVisittime());

						} else if (getString(R.string.orders_agencyname).equals(singTitle)) {// 供货商
							contentRowData.add(orderDataStcs.get(i).getAgencyname());

						} else if (getString(R.string.orders_productname).equals(singTitle)) {// 产   品
							contentRowData.add(orderDataStcs.get(i).getProname());

						} else if (getString(R.string.orders_prevnum).equals(singTitle)) {// 订单量
							contentRowData.add(Long.toString(orderDataStcs.get(i).getOrdernum()));
						}
					}
					contentAll.add(contentRowData);
				}

				VHTableAdapter tableAdapter = new VHTableAdapter(getActivity(), titleAll, contentAll);
				// vht_table.setFirstColumnIsMove(true);//设置第一列是否可移动,默认不可移动
				// vht_table.setShowTitle(false);//设置是否显示标题行,默认显示
				// 一般表格都只是展示用的，所以这里没做刷新，真要刷新数据的话，重新setadaper一次吧
				vht_table.setAdapter(tableAdapter);
		    }else {
		    	// 只设置标题栏 没有表格数据
				contentAll.clear();// 清空之前的表格内容
		    	VHTableAdapter tableAdapter = new VHTableAdapter(getActivity(), titleAll, contentAll);
				vht_table.setAdapter(tableAdapter);
				
		    	Toast.makeText(getActivity(), "今日拜访您还未填写相关订单", Toast.LENGTH_SHORT).show();
		    }
		}
	}
}

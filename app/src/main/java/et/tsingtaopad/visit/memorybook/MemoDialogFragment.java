package et.tsingtaopad.visit.memorybook;

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstVisitmemoInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.TerminalName;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：MemoDialogFragment.java</br> 作者：吴欣伟 </br>
 * 创建时间：2013-12-19</br> 功能描述: 客情备忘录</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
// 客情备忘录
public class MemoDialogFragment extends DialogFragment implements
		OnClickListener, OnCheckedChangeListener {

	private MemoDialogService service;

	private EditText contentEt;
	private TextView startDateTv;
	private TextView endDateTv;
	private TextView endOverTv;
	private TextView routeName;
	private TextView terminalName;
	private CheckBox isCallCb;
	private ImageView saveIv;
	private ImageView cancelIv;
	private LinearLayout dateLl;
	private MstVisitmemoInfo info;
	private String seeFlag;
	private boolean isFromVisit;// 是否来自拜访
	private String memokey;// 备忘录主键

	private TerminalName name;
	private String termKey;
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {

			// 提示信息
			case ConstValues.WAIT1:
				Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}

	};

	/***
	 * 备忘录初始类
	 * 
	 * @param isFromVisit
	 *            是否来自拜访界面
	 * @param termKey
	 *            终端主键
	 * @param seeflag
	 *            是否查看
	 * @param memokey
	 *            备忘录key
	 * @return
	 */
	public static MemoDialogFragment getInstance(boolean isFromVisit,
			String termKey, String seeflag, String memokey) {
		MemoDialogFragment dialog = new MemoDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isFromVisit", isFromVisit);
		bundle.putString("termKey", termKey);
		bundle.putString("seeFlag", seeflag);
		bundle.putString("memokey", memokey);
		dialog.setArguments(bundle);
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.visit_memory_book, null);
		initView(view);
		initDate();
		AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		return dialog;
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 *            界面View
	 */
	private void initView(View view) {

		// 界面组件初始化
		contentEt = (EditText) view.findViewById(R.id.memo_et_content);
		startDateTv = (TextView) view.findViewById(R.id.memo_tv_timestart);
		endDateTv = (TextView) view.findViewById(R.id.memo_tv_timeend);
		endOverTv = (TextView) view.findViewById(R.id.memo_tv_timeover);
		isCallCb = (CheckBox) view.findViewById(R.id.memo_cb_iscall);
		saveIv = (ImageView) view.findViewById(R.id.memo_iv_save);
		cancelIv = (ImageView) view.findViewById(R.id.memo_iv_cancel);
		dateLl = (LinearLayout) view.findViewById(R.id.memo_ll_datelayout);
		routeName = (TextView) view.findViewById(R.id.memo_tv_routeName);
		terminalName = (TextView) view.findViewById(R.id.memo_tv_terminalName);

		// 帮定事件
		startDateTv.setOnClickListener(this);
		//endDateTv.setOnClickListener(this);
		saveIv.setOnClickListener(this);
		cancelIv.setOnClickListener(this);
		isCallCb.setOnCheckedChangeListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initDate() {
		service = new MemoDialogService(getActivity(),handler);

		// 获取参数
		Bundle bundle = getArguments();
		seeFlag = FunUtil.isBlankOrNullTo(bundle.getString("seeFlag"), "");
		memokey = FunUtil.isBlankOrNullTo(bundle.getString("memokey"), "");
		termKey = FunUtil.isBlankOrNullTo(bundle.getString("termKey"), "");
		isFromVisit = bundle.getBoolean("isFromVisit", false);
		// 获取当然日期
		String date = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")
				.substring(0, 10);

		name = service.findTerminalName(termKey);
		routeName.setText(name.getRouteName());
		terminalName.setText(name.getTerminalName());
		//endOverTv.setText("3000-12-01");

		// 获取客情备忘数据
		if (!isFromVisit) {
			info = service.getDataFromMemokey(memokey);
		} else {
			info = service.getDataFromTermid(termKey);
		}
		if (info != null) {
			contentEt.setText(info.getContent());
			if (ConstValues.FLAG_1.equals(seeFlag) || !isFromVisit) {
				contentEt.setKeyListener(null);
				// 如果处于查看状态，编辑框不可编辑
				// contentEt.setFilters(new InputFilter[] { new InputFilter() {
				// @Override
				// public CharSequence filter(CharSequence source, int start,
				// int end, Spanned dest, int dstart, int dend) {
				// return source.length() < 1 ? dest.subSequence(dstart, dend) :
				// "";
				// }
				// } });
			}
			if (info.getIswarn().equals(ConstValues.FLAG_1)) {
				isCallCb.setChecked(true);
			} else {
				isCallCb.setChecked(false);
			}
			if (!CheckUtil.isBlankOrNull(info.getStartdate())) {
				startDateTv.setText(DateUtil.formatDate(0, info.getStartdate())
						.substring(0, 10));
			} else {
				startDateTv.setText(date);
			}
			if (!CheckUtil.isBlankOrNull(info.getEnddate())) {
				endDateTv.setText(DateUtil.formatDate(0, info.getEnddate())
						.substring(0, 10));
			} else {
				endDateTv.setText(date);
			}
		} else {
			info = new MstVisitmemoInfo();
			info.setTerminalkey(termKey);
			info.setCredate(new Date());
			isCallCb.setChecked(false);
			startDateTv.setText(date);
			endDateTv.setText(date);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.memo_tv_timestart:
			service.showDatePicDialog(getActivity(), startDateTv);
			break;
		case R.id.memo_tv_timeend:
			service.showDatePicDialog(getActivity(), endDateTv);
			break;
		case R.id.memo_iv_save:

			if (ConstValues.FLAG_1.equals(seeFlag)) {
				// 处于查看状态
				getDialog().dismiss();
			} else {
				// 处于拜访状态
				// checked();
				// if (ConstValues.FLAG_1.equals(seeFlag)) return;

				String startDate = startDateTv.getText().toString();
				//String endDate = endDateTv.getText().toString();
				String endDate = "3000-12-01";
				String currDate = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
				String content = contentEt.getText().toString();
				// 结束日期应大于等于开始日期
				if (startDate.compareTo(endDate) >= 1) {
					Toast.makeText(getActivity(), R.string.memo_msg_errordate, Toast.LENGTH_LONG).show();
				// 日期已过期
				} else if (currDate.compareTo(endDate) >= 1) {
					Toast.makeText(getActivity(), R.string.memo_msg_invalid, Toast.LENGTH_LONG).show();
				// 请设置合理日期并且日期间隔不要超过三天
				/*
				} else if (DateUtil.diffDays(DateUtil.parse(endDate, "yyyy-MM-dd"), DateUtil.parse(startDate, "yyyy-MM-dd")) > 2) {
					Toast.makeText(getActivity(), R.string.memo_msg_date, Toast.LENGTH_LONG).show();
				*/	
				// 备忘录内容不能为空
				} else if (CheckUtil.isBlankOrNull(content)) {
					Toast.makeText(getActivity(), R.string.memo_msg_contentnull, Toast.LENGTH_LONG).show();
				// 保存到本地数据库
				} else {
					service.saveData(info, isCallCb, contentEt, startDateTv, endDateTv);
					getDialog().dismiss();
				}
			}
			break;
		case R.id.memo_iv_cancel:
			getDialog().dismiss();
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// if (isChecked) {
		// dateLl.setVisibility(View.VISIBLE);
		// } else {
		// dateLl.setVisibility(View.GONE);
		// }
	}
}

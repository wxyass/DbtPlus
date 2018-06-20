package et.tsingtaopad.visit.memorybook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Checkable;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.db.tables.MstVisitmemoInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProItem;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.TerminalName;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：MemoDialogService.java</br> 作者：Administrator </br>
 * 创建时间：2013-12-20</br> 功能描述: </br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
public class MemoDialogService {

	private Context context;
	private Handler handler;

	private final String TAG = "MemoDialogService";

	public MemoDialogService(Context context,Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	/**
	 * 日期选择器的创建
	 * 
	 * @param context
	 *            上下文
	 * @param tv
	 *            显示日期的TextView控件
	 */
	public void showDatePicDialog(Context context, final TextView tv) {

		Calendar c = Calendar.getInstance();
		Dialog date = new DatePickerDialog(context, new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {

				// 选择日期写入EditText
				tv.setText(year + "-" + String.format("%02d", monthOfYear + 1)
						+ "-" + String.format("%02d", dayOfMonth));
			}
		}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
		date.setCancelable(false);
		date.show();
	}
	
	
	public void showDatePicAllDialog(Context context, final TextView tv) {

		Calendar c = Calendar.getInstance();

		DatePickerDialog dateDialog = new DatePickerDialog(context,
				new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {// 选择日期写入EditText
						tv.setText(year + "-" + String.format("%02d", monthOfYear + 1)
								+ "-" + String.format("%02d", dayOfMonth));}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
		if (!dateDialog.isShowing()) {
			dateDialog.show();
		}
	
	}

	/**
	 * 获取客情备忘录的本地数据
	 * 
	 * @param termId
	 *            终端Id
	 * @return
	 */
	public MstVisitmemoInfo getDataFromTermid(String termId) {
		MstVisitmemoInfo info = null;
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
			Dao<MstVisitmemoInfo, String> dao = helper.getMstVisitmemoInfoDao();
			List<MstVisitmemoInfo> infoLst = dao.queryForEq("terminalkey",
					termId);
			if (infoLst.size() > 0) {
				info = infoLst.get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * 获取客情备忘录的终端名称和线路名称本地数据
	 * 
	 * @param termId
	 *            终端Id
	 * @return
	 */

	public TerminalName findTerminalName(String termId) {

		TerminalName name = null;
		try {
			DatabaseHelper databaseHelper = DatabaseHelper.getHelper(context);
			MstTerminalinfoMDao dao = databaseHelper
					.getDao(MstTerminalinfoM.class);
			name = dao.findByIdName(databaseHelper, termId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;

	}

	/**
	 * 获取客情备忘录的本地数据
	 * 
	 * @param memokey
	 *            终端Id
	 * @return
	 */
	public MstVisitmemoInfo getDataFromMemokey(String memokey) {
		MstVisitmemoInfo info = null;
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
			Dao<MstVisitmemoInfo, String> dao = helper.getMstVisitmemoInfoDao();
			info = dao.queryForId(memokey);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * 保存客情备忘录数据
	 * 
	 * @param info
	 *            客情备忘信息
	 * @param isCallCb
	 * @param contentEt
	 *            EditText输入
	 * @param startDateTv
	 *            开始日期
	 * @param endDateTv
	 *            结束日期
	 */
	public void saveData(MstVisitmemoInfo info, Checkable isCallCb,
			EditText contentEt, TextView startDateTv, TextView endDateTv) {
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
			Dao<MstVisitmemoInfo, String> dao = helper.getMstVisitmemoInfoDao();
			if (CheckUtil.isBlankOrNull(info.getMemokey())) {
				info.setMemokey(FunUtil.getUUID());
			}
			info.setContent(contentEt.getText().toString());
			if (isCallCb.isChecked()) {
				info.setIswarn(ConstValues.FLAG_1);
			} else {
				info.setIswarn(ConstValues.FLAG_0);
			}
			info.setStartdate(DateUtil.formatDate(DateUtil.parse(startDateTv
					.getText().toString(), "yyyy-MM-dd"), "yyyyMMdd")
					+ "000000");
			info.setEnddate(DateUtil.formatDate(DateUtil.parse(endDateTv
					.getText().toString(), "yyyy-MM-dd"), "yyyyMMdd")
					+ "235959");
			info.setPadisconsistent(ConstValues.FLAG_0);
			//info.setCreuser(ConstValues.loginSession.getUserCode());
			info.setCreuser(PrefUtils.getString(context, "userCode", ""));
			//info.setUpdateuser(ConstValues.loginSession.getUserCode());
			info.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
			info.setUpdatetime(new Date());
			dao.createOrUpdate(info);

			Message message1 = new Message();
			message1.what = ConstValues.WAIT1;
			handler.sendMessage(message1);// 刷新UI
			
			//ViewUtil.sendMsg(context, R.string.agencyvisit_msg_oksave, handler);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

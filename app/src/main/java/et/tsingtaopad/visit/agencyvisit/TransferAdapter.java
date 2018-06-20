package et.tsingtaopad.visit.agencyvisit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstAgencyinfoM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.visit.agencyvisit.domain.TransferStc;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：TransferAdapter.java</br> 作者：@吴欣伟 </br>
 * 创建时间：2013/11/26</br> 功能描述: 调货台账Adapter</br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
public class TransferAdapter extends BaseAdapter implements OnClickListener {

	private LedgerService service;
	private Context context;
	private List<TransferStc> dataLst;
	private String agencykey;
	private List<MstAgencyinfoM> agencyStcLst;
	private Map<String, String> tagAgencyPinyinMap;
	private SearchTagAgencyAdapter tagAdapter;
	private AlertDialog alertDialog;
	
	private List<MstAgencyinfoM> tagStcLst;
	private int type = -1;// 1直接从经销商列表选择 2从经搜索形成的列表中选择

	public TransferAdapter(Context context, List<TransferStc> dataLst, String agencykey) {
		this.context = context;
		this.dataLst = dataLst;
		this.agencykey = agencykey;
		service = new LedgerService(context);
		agencyStcLst = service.tagAgencyQuery(agencykey);
	}

	public int getCount() {

		if (CheckUtil.IsEmpty(this.dataLst)) {
			return 0;

		} else {
			return this.dataLst.size();
		}
	}

	@Override
	public Object getItem(int position) {

		if (CheckUtil.IsEmpty(this.dataLst)) {
			return null;

		} else {
			return this.dataLst.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.agencyvisit_transfer_lvitem, null);
		ImageButton delete = (ImageButton) convertView.findViewById(R.id.transfer_ib_delete);
		EditText variety = (EditText) convertView.findViewById(R.id.transfer_et_proname);
		EditText agency = (EditText) convertView.findViewById(R.id.transfer_et_agencyname);
		EditText inEt = (EditText) convertView.findViewById(R.id.transfer_et_turnenter);
		EditText outEt = (EditText) convertView.findViewById(R.id.transfer_et_turnout);
		TransferStc info = dataLst.get(position);
		variety.setText(info.getProName());
		agency.setText(info.getTagAgencyName());
		if (info.getTranin() == null) {
			info.setTranin(0.0);
		}
		if (info.getTranout() == null) {
			info.setTranout(0.0);
		}
		if (info.getTranin().intValue() == 0 && info.getTranout().intValue() == 0) {
			inEt.setText("");
			outEt.setText("");
		} else {
			double in = info.getTranin();
			inEt.setText(Integer.toString((int) in));
			double out = info.getTranout();
			outEt.setText(Integer.toString((int) out));
		}

		delete.setTag(position);
		variety.setTag(position);
		agency.setTag(position);
		inEt.setTag(position);
		outEt.setTag(position);

		delete.setOnClickListener(this);
		agency.setOnClickListener(this);
		variety.setOnClickListener(this);

		inEt.addTextChangedListener(new MyTextWathcer(R.id.transfer_et_turnenter, inEt));
		outEt.addTextChangedListener(new MyTextWathcer(R.id.transfer_et_turnout, outEt));

		return convertView;
	}

	private class MyTextWathcer implements TextWatcher {
		private EditText edittext;
		private int viewID;

		public MyTextWathcer(int viewID, EditText edittext) {
			this.edittext = edittext;
			this.viewID = viewID;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			String content = s.toString();
			if ("".equals(content)) {
				content = "0";
			}
			Double contentNumber = Double.parseDouble(content);
			int position = (Integer) edittext.getTag();
			TransferStc info = dataLst.get(position);

			switch (viewID) {
			case R.id.transfer_et_turnenter:
				info.setTranin(contentNumber);
				break;
			case R.id.transfer_et_turnout:
				info.setTranout(contentNumber);
				break;

			default:

				break;
			}
		}
	}

	@Override
	public void onClick(final View v) {

		switch (v.getId()) {
		case R.id.transfer_ib_delete:
			AlertDialog dialog = new AlertDialog.Builder(context).setMessage(R.string.transfer_msg_suredelete).setPositiveButton(R.string.transfer_msg_sure, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					ImageButton delete = (ImageButton) v;
					int position = (Integer) delete.getTag();
					TransferStc info = dataLst.get(position);
					service.deleteTransfer(info);
					dataLst.remove(position);
					notifyDataSetChanged();
					dialog.dismiss();
				}
			}).setNegativeButton(R.string.transfer_msg_cancle, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).create();
			dialog.setCanceledOnTouchOutside(false);//点击dialog以外的区域dialog不消失
			dialog.show();
			break;

		case R.id.transfer_et_proname:
			EditText proText = (EditText) v;
			service.showProDialog(dataLst, proText, agencykey);
			break;

		case R.id.transfer_et_agencyname:
	        if (alertDialog == null || !alertDialog.isShowing()) {
                if (tagAgencyPinyinMap == null) {
                    tagAgencyPinyinMap = service.getTagAgencyPinyin(agencyStcLst);
                }
    			final EditText tagAgencytext = (EditText) v;
    			if (agencyStcLst == null || agencyStcLst.size() == 0) {
    				Toast.makeText(context, "没有可选经销商", Toast.LENGTH_SHORT).show();
    				return;
    			}
    			View view = LayoutInflater.from(context).inflate(R.layout.agencyvisit_transfer_searchtagagency, null);
    			final EditText searchEt = (EditText) view.findViewById(R.id.searchtagagency_tv_searchcontent);
    			Button searchBt = (Button) view.findViewById(R.id.searchtagagency_bt_search);
    			final ListView searchTagagencyLv = (ListView) view.findViewById(R.id.searchtagagency_lv_agencyname);
    			tagAdapter = new SearchTagAgencyAdapter(context, agencyStcLst);
    			searchTagagencyLv.setAdapter(tagAdapter);
    			type = 1;
    			alertDialog = new AlertDialog.Builder(context).create();
    			alertDialog.setView(view, 0, 0, 0, 0);
    			alertDialog.show();
    			searchEt.addTextChangedListener(new TextWatcher() {
    
    				@Override
    				public void onTextChanged(CharSequence s, int start, int before, int count) {
    
    				}
    
    				@Override
    				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    
    				}
    
    				@Override
    				public void afterTextChanged(Editable s) {
    					tagStcLst = new ArrayList<MstAgencyinfoM>();
    					String searchStr = searchEt.getText().toString();
    					if (!CheckUtil.isBlankOrNull(searchStr)) {
    						tagStcLst = service.searchTagAgency(agencyStcLst, searchStr.replace(" ", ""), tagAgencyPinyinMap);
    						type = 2;// 搜索模式形成的列表
    					}
    					tagAdapter = new SearchTagAgencyAdapter(context, tagStcLst);
    					searchTagagencyLv.setAdapter(tagAdapter);
    					searchTagagencyLv.refreshDrawableState();
    				}
    			});
    
    			searchBt.setOnClickListener(new OnClickListener() {
    
    				@Override
    				public void onClick(View arg0) {
    
    					tagStcLst = agencyStcLst;
    					String searchStr = searchEt.getText().toString();
    					if (!CheckUtil.isBlankOrNull(searchStr)) {
    						tagStcLst = service.searchTagAgency(agencyStcLst, searchStr.replace(" ", ""), tagAgencyPinyinMap);
    					}
    					tagAdapter = new SearchTagAgencyAdapter(context, tagStcLst);
    					searchTagagencyLv.setAdapter(tagAdapter);
    					searchTagagencyLv.refreshDrawableState();
    				}
    			});
    
    			searchTagagencyLv.setOnItemClickListener(new OnItemClickListener() {
    
    				@Override
    				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    					arg1.setBackgroundResource(R.color.bg_content_color_orange);
    					int tag = (Integer) tagAgencytext.getTag();
    					MstAgencyinfoM item;
						if (type == 2) {
							item = tagStcLst.get(arg2);
							type = -1; // 改为不是搜索模式
						}else{
    						item = agencyStcLst.get(arg2);
    					}
    					tagAgencytext.setText(item.getAgencyname());
    					dataLst.get(tag).setTagAgencyName(item.getAgencyname());
    					dataLst.get(tag).setTagencykey(item.getAgencykey());
    					alertDialog.dismiss();
    				}
    			});
	        }
			break;
		default:
			break;
		}
	}
	

}

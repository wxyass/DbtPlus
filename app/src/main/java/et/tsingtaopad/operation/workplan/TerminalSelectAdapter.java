package et.tsingtaopad.operation.workplan;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;

/**
 * 
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：PopAdapter.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2014年2月11日</br>      
 * 功能描述: 终端列表选择adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TerminalSelectAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<MstTermListMStc> termListMStcs = new ArrayList<MstTermListMStc>();//所有的
	private List<String> terminalSelect = new ArrayList<String>();//记录已经选择的对象的key
	private int type;

	public List<String> getTerminalSelect() {
		return terminalSelect;
	}
 
	public void setTerminalSelect(List<String> terminalSelect) {
		this.terminalSelect = terminalSelect;
	}
	

	/**
	 * 
	 * @param context
	 * @param termlist 单击选择的对象
	 * @param termlist 显示的数据
	 * @param terminalSelect 已经选择的数据
	 * @param type 界面类型: 0空白终端 有效铺货目标终端 有效销售目标终端   ,1 其他界面,2促销活动推进 产品生动化
	 */
	public TerminalSelectAdapter(Context context, List<MstTermListMStc> termlist, List<String> terminalSelect,int type) {
		this.context = context;
		this.termListMStcs = termlist;
		this.type = type;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (terminalSelect != null) {
			this.terminalSelect = terminalSelect;
		} else {
			this.terminalSelect = new ArrayList<String>();
		}
	}

	@Override
	public int getCount() {
		return termListMStcs.size();
	}

	@Override
	public Object getItem(int position) {
		return termListMStcs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.operation_pop_lvitem, null);
			holder = new ViewHolder();
			holder.tv = (TextView) convertView.findViewById(R.id.pop_tv_lvitem);
			holder.pop_tv_lvitem_statuse = (TextView) convertView.findViewById(R.id.pop_tv_lvitem_statuse);
			holder.pop_tv_lvitem_statuse2 = (TextView) convertView.findViewById(R.id.pop_tv_lvitem_statuse2);
			holder.pop_iv_lvitem_check = (TextView) convertView.findViewById(R.id.pop_iv_lvitem_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final MstTermListMStc mstTermListMStc = termListMStcs.get(position);
		if (terminalSelect.contains(mstTermListMStc.getTerminalkey())) {//已经选中
			convertView.setBackgroundColor(context.getResources().getColor(R.color.bg_content_color_orange));
			holder.pop_iv_lvitem_check.setVisibility(View.VISIBLE);// 对号出现
		} else {
			convertView.setBackgroundColor(Color.WHITE);
			holder.pop_iv_lvitem_check.setVisibility(View.GONE);// 对号消失
		}
		holder.tv.setText(mstTermListMStc.getTerminalname());
		String terminalStatus = mstTermListMStc.getTerminalStatus();
		
		// 其他界面 添加我品合作状态标识
		if(type == 1){
			String mineProtocolFlag = mstTermListMStc.getMineProtocolFlag();
			if ("1".equals(mineProtocolFlag)) {
				holder.pop_tv_lvitem_statuse2.setVisibility(View.VISIBLE);
				
				holder.pop_tv_lvitem_statuse2.setBackgroundResource(R.drawable.ico_terminal_mineprotocol);
			} else {
	            holder.pop_tv_lvitem_statuse2.setVisibility(View.GONE);
	            
			}
		}
		
		if (!CheckUtil.isBlankOrNull(terminalStatus)) {
			holder.pop_tv_lvitem_statuse.setVisibility(View.VISIBLE);
			holder.pop_tv_lvitem_statuse.setText(terminalStatus);
		} else {
            holder.pop_tv_lvitem_statuse.setText("");
		}
		
		// 选中设置背景
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (terminalSelect.contains(mstTermListMStc.getTerminalkey())) {//已经选中
					v.setBackgroundColor(Color.WHITE);
					terminalSelect.remove(mstTermListMStc.getTerminalkey());
					notifyDataSetChanged();// 刷新listview 会将对号刷出来
				} else {
					v.setBackgroundColor(context.getResources().getColor(R.color.bg_content_color_orange));
					terminalSelect.add(mstTermListMStc.getTerminalkey());
					notifyDataSetChanged();// 刷新listview 会将对号刷出来
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView tv;
		TextView pop_tv_lvitem_statuse;
		TextView pop_tv_lvitem_statuse2;
		TextView pop_iv_lvitem_check;
	}

}

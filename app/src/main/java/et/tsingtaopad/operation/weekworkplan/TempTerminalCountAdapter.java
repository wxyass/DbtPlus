package et.tsingtaopad.operation.weekworkplan;

import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MakePlanAdapter.java</br>
 * 作者：@ray   </br>
 * 创建时间：2013-12-13</br>      
 * 功能描述: 制定计划显示终端统计Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TempTerminalCountAdapter extends BaseAdapter {

	private Context context;
	private List<BlankTermLevelStc> list;

	public TempTerminalCountAdapter(Context context, List<BlankTermLevelStc> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.operation_makeweekplanvp_listview_listviewitem, null);
			TextView tv_terminal = (TextView) convertView.findViewById(R.id.makeweekplan_tv_temp_listviewitem_term);
			final EditText tv_num = (EditText) convertView.findViewById(R.id.makeweekplan_et_temp_listviewitem_rate);
			final BlankTermLevelStc stc = list.get(position);
			tv_num.addTextChangedListener(new MyTextWatcher(tv_num, stc));
			tv_terminal.setText(stc.getValue());
			if(CheckUtil.isBlankOrNull(stc.getRate())) {
			    tv_num.setText("");
			}else
			tv_num.setText(stc.getRate() + "%");
			
		return convertView;
	}
	class MyTextWatcher implements TextWatcher {

	    private EditText tv_num;
	    private BlankTermLevelStc stc;
	    public MyTextWatcher (EditText tv_num, BlankTermLevelStc stc){
	        this.tv_num = tv_num;
	        this.stc = stc;
	    }
	    
        @Override
        public void afterTextChanged(Editable s) {
          String str = s.toString().replace("%", "");
          stc.setRate(str);
            
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            
        }
	    
	}

}

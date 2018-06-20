package et.tsingtaopad.syssetting.queryfeedback;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.business.DateUtils;
import et.tsingtaopad.db.tables.MstQuestionsanswersInfo;

/**
 * 项目名称：营销移动智能工作平台<br>
 * 文件名：QueryFeedbackAdapter.java<br>
 * 作者：@沈潇<br>
 * 创建时间：2013/11/24<br>
 * 功能描述: 问题反馈Adapter<br>
 * 版本 V 1.0<br>
 * 修改履历 日期 原因 BUG号 修改人 修改版本
 */
public class QueryFeedbackAdapter extends BaseAdapter {
	private Context context;
	private List<MstQuestionsanswersInfo> list = new ArrayList<MstQuestionsanswersInfo>();

	public QueryFeedbackAdapter(Context context, List<MstQuestionsanswersInfo> list) {
		this.context = context;
		this.list = list;
	}

	/**
	 * 添加数据
	 * @param list
	 */
	public void addData(List<MstQuestionsanswersInfo> list) {
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	public void setData(List<MstQuestionsanswersInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int i, View convertView, ViewGroup arg2) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.business_queryfeedback_item, null);
		//		try {
		//			//{"PROB_ID","PROB_TYPE","PROB_CONTENT","PROB_TIME","USER_ID","PROB_REPLY","PROB_RTIME","UPLOADFLAG","DELETEFLAG"}};
		////			JSONObject jo = ja.getJSONObject(i);
		//
		TextView feedbacktitle = (TextView) v.findViewById(R.id.business_tv_feedbacktitle);

		TextView questiontitle = (TextView) v.findViewById(R.id.business_tv_questiontitle);

		TextView userquestion = (TextView) v.findViewById(R.id.business_tv_userquestion);
		//反馈用户和时间
		//			if(jo.has("qaadate")&&!jo.isNull("qaadate")&&!jo.getString("qaadate").toString().equals("null"))

		TextView userfeedback = (TextView) v.findViewById(R.id.business_tv_userfeedback);
		//问题用户和时间
		//			if(jo.has("qaquser")&&!jo.isNull("qaquser")&&!jo.getString("qaquser").toString().equals("null"))

		TextView question = (TextView) v.findViewById(R.id.question);
		//			if(jo.has("qaqcontent")&&!jo.isNull("qaqcontent")&&!jo.getString("qaqcontent").toString().equals("null"))

		TextView feedback = (TextView) v.findViewById(R.id.feedback);
		//			if(jo.has("qaacontent")&&!jo.isNull("qaacontent")&&!jo.getString("qaacontent").toString().equals("null"))

		MstQuestionsanswersInfo itemObject = list.get(i);
		feedbacktitle.setText(context.getString(R.string.business_feedback));
		questiontitle.setText(context.getString(R.string.business_question));

		if (null != itemObject.getQaqdate()) {
			String qaqdate = itemObject.getQaqdate();
			if (qaqdate.contains("-")) {
				userquestion.setText("[" + qaqdate + "]");
			} else {
				userquestion.setText("[" + DateUtils.divide(qaqdate) + "]");
			}
		}
		if (null != itemObject.getQaadate()) {
			String qaadate = itemObject.getQaadate();
			if (qaadate.contains("-")) {
				userfeedback.setText("[" + qaadate + "]");
			} else {
				userfeedback.setText("[" + DateUtils.divide(qaadate) + "]");
			}
		}

		question.setText(itemObject.getQaqcontent());
		feedback.setText(itemObject.getQaacontent());
		//		} catch (JSONException e) {
		//			e.printStackTrace();
		//		}  
		return v;
	}

}

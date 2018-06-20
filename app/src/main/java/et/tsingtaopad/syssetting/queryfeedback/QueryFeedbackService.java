///////////////////////////////////////////////////////////////////////////////
//Copyright ownership belongs to SINOSOFT CO.,LTD.shall not be reproduced ,  //
//copied,or used in other ways without permission.Otherwise SINOSOFT CO.,LTD.//
//will have the right to pursue legal responsibilities.                      //
//                                                                           //
//Copyright &copy; 2011 SINOSOFT CO., LTD. All rights reserved.              //
///////////////////////////////////////////////////////////////////////////////
package et.tsingtaopad.syssetting.queryfeedback;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.CmmDatadicM;
import et.tsingtaopad.db.tables.MstQuestionsanswersInfo;
import et.tsingtaopad.service.support.ServiceSupport;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.NetStatusUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.visit.syncdata.UploadDataService;

/**
 * 项目名称：营销移动智能工作平台<br>
 * 文件名：QueryFeedbackService.java<br>
 * 作者：@沈潇<br>
 * 创建时间：2013/11/24<br>
 * 功能描述: 问题反馈<br>
 * 版本 V 1.0<br>
 * 修改履历 日期 原因 BUG号 修改人 修改版本
 */
public class QueryFeedbackService extends ServiceSupport {
	
	private final String TAG = "QueryFeedbackService";
	private QueryFeedbackAdapter Query_adapter;
	private ListView querfeedback_listview;
	private String[] where;// sql条件
	private RadioButton[] radiobuttons;


	//	private RadioButton querfeedback_cb1_qs;
//	private RadioButton querfeedback_cb2_qs;
//	private RadioButton querfeedback_cb3_qs;
	private EditText querfeedback_et_question;
	private RelativeLayout querfeedback_rl_form;
	private RelativeLayout bg_up_arrow_rl;
	private Button bg_up_arrow;
	private Button bg_up_arrow_top_line;
	
	
	private Dao<MstQuestionsanswersInfo, String> getMstQuestionsanswersInfoDao;
	private Dao<CmmDatadicM,String> getCmmDatadicDao;
	private String uuid;
	private Context context ;
	private Handler handler = null;
	private UploadDataService uploadDataService = null;
	
	private String updateTime = "";
	
	private Date updateDate = null;
	/**是否正在查询*/
	private boolean isQuerying = false;
	/**服务器数据是否已经查询末尾*/
	private boolean isEnd = false;
	
	private String oldInput = "";
	
	private String oldType = "";
	
	public QueryFeedbackService(Handler handler, Context context) {
		super(handler, context);
		this.handler = handler ;
		this.context = context;
		updateTime = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
		updateDate = new Date();
		uploadDataService =new UploadDataService(context, handler);
		try {
			getMstQuestionsanswersInfoDao = DatabaseHelper.getHelper(context)
					.getMstQuestionsanswersInfoDao();
			getCmmDatadicDao = DatabaseHelper.getHelper(context)
					.getCmmDatadicMDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否与上次输入一致
	 * @return
	 */
	public boolean isSame(String str,String type){
		return str.equals(oldInput)&&type.equals(oldType);
	}
	
	/**
	 * 搜索本地类型
	 * @return
	 */
	public List<CmmDatadicM> searchLocalQuestionType(){
		List<CmmDatadicM> list = new ArrayList<CmmDatadicM>();
		try {
			QueryBuilder<CmmDatadicM, String> queryBuilder = getCmmDatadicDao.queryBuilder();
			queryBuilder.where().eq("parentcode", PropertiesUtil.getProperties("datadic_questionType", ""));
			queryBuilder.orderBy("orderbyno", true);
			list = queryBuilder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 本地数据查询
	 * 
	 * @return
	 * */
	public List<MstQuestionsanswersInfo> searchLocalData(Context c) {
		List<MstQuestionsanswersInfo> query = null;
		try {
			QueryBuilder<MstQuestionsanswersInfo, String> queryBuilder = getMstQuestionsanswersInfoDao
					.queryBuilder();
			
			queryBuilder.orderBy("updatetime", false);
			query = queryBuilder.query();
//			 if (query == null || query.size() < 1) {
//			 for (int i = 0; i < 10; i++) {
//			 MstQuestionsanswersInfo vo = new MstQuestionsanswersInfo(
//			 "qakey", "qaqcontent", "qaquser", "qaqdate",
//			 "qatype", "qaacontent", "qaauserid", "qaadate",
//			 " qaadegree", "sisconsistent", new Date(),
//			 "padisconsistent", new Date(), "comid", "remarks",
//			 "orderbyno", " deleteflag", new BigDecimal(1),
//			 new Date(), "creuser", new Date(), " updateuser");
//			 query.add(vo);
//			 }
//			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return query;
	}

	/**
	 * 异步数据处理
	 * 
	 * @return
	 * */
	public class QueryFeedbackServiceThread extends Thread {

		@Override
		public void run() {

			getHandler().post(new Runnable() {

				@SuppressLint("NewApi")
				@Override
				public void run() {
					if (getStyle() == 1) {
						List<MstQuestionsanswersInfo> l = searchLocalData(getContext());
						if (l != null && l.size() > 0) {// 本地数据库查询

							String json = JsonUtil.toJson(l);
							Log.d("tag", "local-->" + json);
							Message msg = handler.obtainMessage();
							msg.what = ConstValues.WAIT2;
							msg.obj = l;
							handler.sendMessage(msg);

						} else {// 网络请求
							if (getGetResBody() != null
									&& getGetResBody().indexOf("{") != -1) {
								JSONTokener jsontokener = new JSONTokener(
										getGetResBody());
								JSONArray ja = null;
								try {
									ja = (JSONArray) jsontokener.nextValue();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					} else if (getStyle() == 2) {
						getGetResBody();
						UpdateBuilder<MstQuestionsanswersInfo, String> updateBuilder = getMstQuestionsanswersInfoDao
								.updateBuilder();
						try {
							updateBuilder.updateColumnValue("padisconsistent",
									ConstValues.FLAG_1);
							updateBuilder.where().eq("qakey", uuid);
							updateBuilder.update();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						// 如果同步成功修改数据库
					}
				}
			});
		}

	}

	

	/**
	 * 问题反馈表插入
	 * 
	 * @throws SQLException
	 */
	public void newDataInsert(String querytype, String mobile) throws SQLException {
		if (querytype == null) {
			Toast.makeText(getContext(), context.getString(R.string.business_please_select_question_type), Toast.LENGTH_SHORT).show();
			return;
		}
		if (querfeedback_et_question.getText() == null
				|| querfeedback_et_question.getText().toString().equals("")) {
			Toast.makeText(getContext(), context.getString(R.string.business_please_input_content), Toast.LENGTH_SHORT).show();
			return;
		}
		if(isSame(querfeedback_et_question.getText().toString(), querytype)){
			return;
		}
		oldInput = querfeedback_et_question.getText().toString();
		oldType = querytype;
		uuid = FunUtil.getUUID();
		//String userCode = ConstValues.loginSession.getUserCode();
		String userCode = PrefUtils.getString(context, "userCode", "");
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String date = df.format(new Date());// new Date()为获取当前系统时间
		Dao<MstQuestionsanswersInfo, String> getMstQuestionsanswersInfoDao = DatabaseHelper
				.getHelper(getContext()).getMstQuestionsanswersInfoDao();
		MstQuestionsanswersInfo data = new MstQuestionsanswersInfo(uuid,
				querfeedback_et_question.getText().toString(),userCode, date,
				querytype , null, null, null, null, null, null,
				ConstValues.FLAG_0, new Date(), null, null, null,
				ConstValues.FLAG_0, null, new Date(), userCode, new Date(),
				userCode,mobile);
		int flag = getMstQuestionsanswersInfoDao.create(data);
		if (flag > 0) {
			//
			Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
			uploadDataService.upload_question_feedback_infos(false,data);
		} else {
			Toast.makeText(getContext(), "添加失败", Toast.LENGTH_SHORT).show();
		}
		asynchronousDataHandler();
	}

	
	/**
	 * 查询服务器中更多反馈问题
	 * sqm
	 */
	@SuppressLint("SimpleDateFormat")
	public void QueryFeedbackServiceGet(){
		if(!NetStatusUtil.isNetValid(context)){//网络不可用
			return;
		}
		isQuerying = true;
		//请求userId
		//String uid = ConstValues.loginSession.getUserCode();//"100001";//FunUtil.getUUID()
		String uid = PrefUtils.getString(context, "userCode", "");//"100001";//FunUtil.getUUID()
		Log.d(TAG,"uid-<"+uid);
		
		int days = Integer.parseInt(ConstValues.kvMap.get("MST_QUESTIONSANSWERS_INFO").getSyncDay());//;
		days = days==0?30:days;
//		try {
//			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Log.d("tag","updateTime-->"+ConstValues.kvMap.get("MST_QUESTIONSANSWERS_INFO").getUpdatetime());
//			Date date = dateformat.parse("2013-11-12 12:11:11");//ConstValues.kvMap.get("MST_QUESTIONSANSWERS_INFO").getUpdatetime());//2013-11-12 12:11:11"
//			Calendar calendar =Calendar.getInstance();
//			calendar.setTime(date);
//			calendar.add(Calendar.DATE,-days);
//			updateTime = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		
		
		 StringBuffer buffer = new StringBuffer();
         buffer.append("{userId:'").append(uid);
         buffer.append("', qaqDate:'").append(updateTime);
         buffer.append("', strDays:'").append((days+"'}"));
         Log.i("tag","request--->"+buffer.toString());
         // 请求网络
         HttpUtil httpUtil = new HttpUtil(6000);
         httpUtil.configResponseTextCharset("ISO-8859-1");
         httpUtil.send("opt_get_questionsans",
                 buffer.toString(), new RequestCallBack<String>() {

             @Override
             public void onSuccess(ResponseInfo<String> responseInfo) {
                 
                 ResponseStructBean resObj = 
                         HttpUtil.parseRes(responseInfo.result);
                 if (ConstValues.SUCCESS.equals(
                               resObj.getResHead().getStatus())) {
                     Log.d(TAG,"success--"+resObj.getResBody().getContent());
                     
                     List<MstQuestionsanswersInfo> list = JsonUtil.parseList(resObj.getResBody().getContent(), MstQuestionsanswersInfo.class);
                     
                 	if(list.size()==0){
						Log.d("tag","end--isEnd-->");
						isEnd = true;
					}else if(list.get(list.size()-1).getUpdatetime().equals(updateDate)){//返回日期 与上次更新日期 相同
						Log.d("tag","end--相同=结束>");
						isEnd = true;
					}else{
						isEnd = false;
						Log.d("tag","end-->"+list.get(list.size()-1).getUpdatetime());
						updateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(list.get(list.size()-1).getUpdatetime());
						updateDate = list.get(list.size()-1).getUpdatetime();
						sendMsg(list,ConstValues.WAIT1);
					}
                     
                 } else {
                     sendMsg(resObj.getResBody().getContent(),ConstValues.WAIT3);
                 }
             }
 
             @Override
             public void onFailure(HttpException error, String errMsg) {
                 Log.e(TAG, errMsg, error);
                 sendMsg(R.string.msg_err_netfail,ConstValues.WAIT4);
             }
         });
		
	}
	
	
	  /**
     * 向界面发送提示消息
     * 
     * @param msg   提示消息
     */
    private void sendMsg(Object msg,int what) {
    	isQuerying = false;
        Message message = new Message();
       
        message.what = what;
        message.obj =msg;
        handler.sendMessage(message);
    }
    
	
	/**
	 * 展现隐藏 按钮点击方法
	 */
	public void display() {
		// 隐藏
		if (querfeedback_rl_form.getVisibility() == View.GONE) {

			AnimationSet asin = new AnimationSet(true);
			asin.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					RelativeLayout.LayoutParams r = new RelativeLayout.LayoutParams(
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					r.addRule(RelativeLayout.CENTER_HORIZONTAL,
							RelativeLayout.TRUE);
					r.addRule(RelativeLayout.BELOW, R.id.querfeedback_rl_form);
					bg_up_arrow.setLayoutParams(r);
					r = new RelativeLayout.LayoutParams(
							android.view.ViewGroup.LayoutParams.MATCH_PARENT,
							bg_up_arrow_top_line.getHeight());
					r.addRule(RelativeLayout.BELOW, R.id.bg_up_arrow);
					bg_up_arrow_top_line.setLayoutParams(r);
					bg_up_arrow.setBackgroundResource(R.drawable.bg_down_arrow);
//					r = new RelativeLayout.LayoutParams(
//							android.view.ViewGroup.LayoutParams.MATCH_PARENT,
//							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
//					r.addRule(RelativeLayout.BELOW, R.id.ll1);
//					querfeedback_listview.setLayoutParams(r);
					
					setRadioButtonsVisiable(View.VISIBLE);
					querfeedback_rl_form.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {

				}

				@Override
				public void onAnimationEnd(Animation arg0) {

				}
			});
			TranslateAnimation tain = new TranslateAnimation(0, 0, -150, 0);

			AlphaAnimation aain = new AlphaAnimation(0f, 1f);
			tain.setDuration(1000);
			aain.setDuration(1000);
			asin.addAnimation(tain);
			asin.addAnimation(aain);
			querfeedback_rl_form.startAnimation(asin);
			
			startAnimationRadioButton(asin);
			bg_up_arrow.startAnimation(asin);
			bg_up_arrow_top_line.startAnimation(asin);

		} else {
			AnimationSet as = new AnimationSet(true);
			as.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
//					arg0.cancel();
					RelativeLayout.LayoutParams r = new RelativeLayout.LayoutParams(
							android.view.ViewGroup.LayoutParams.MATCH_PARENT,
							bg_up_arrow_top_line.getHeight());
					r.addRule(RelativeLayout.ALIGN_PARENT_TOP,
							RelativeLayout.TRUE);
					bg_up_arrow_top_line.setLayoutParams(r);
					r = new RelativeLayout.LayoutParams(
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					r.addRule(RelativeLayout.CENTER_HORIZONTAL,
							RelativeLayout.TRUE);
					r.addRule(RelativeLayout.BELOW, R.id.bg_up_arrow_top_line);
					
					bg_up_arrow.setLayoutParams(r);
					bg_up_arrow.setBackgroundResource(R.drawable.bg_uparrow);
					setRadioButtonsVisiable(View.GONE);
					querfeedback_rl_form.setVisibility(View.GONE);
				}
			});
			TranslateAnimation ta = new TranslateAnimation(0, 0, 0, -150);

			AlphaAnimation aa = new AlphaAnimation(1f, 0f);
			ta.setDuration(1000);
			aa.setDuration(1000);
			as.addAnimation(ta);
			as.addAnimation(aa);
			querfeedback_rl_form.startAnimation(as);
			startAnimationRadioButton(as);
			bg_up_arrow.startAnimation(as);
			bg_up_arrow_top_line.startAnimation(as);

		}
	}

	@Override
	public void asynchronousDataHandler() {
		new QueryFeedbackServiceThread().start();
	}

	
	
	/**
	 * 是否正在查询
	 * @return
	 */
	public boolean isQuerying() {
		return isQuerying;
	}

	
	public boolean isEnd(){
		return isEnd;
	}
	
	@Override
	public void onFailure() {

	}

	public ListView getQuerfeedback_listview() {
		return querfeedback_listview;
	}

	public void setQuerfeedback_listview(ListView querfeedback_listview) {
		this.querfeedback_listview = querfeedback_listview;
	}

	public String[] getWhere() {
		return where;
	}

	public void setWhere(String[] where) {
		this.where = where;
	}

	public QueryFeedbackAdapter getQuery_adapter() {
		return Query_adapter;
	}

	public void setQuery_adapter(QueryFeedbackAdapter query_adapter) {
		Query_adapter = query_adapter;
	}

	public RadioButton[] getRadiobuttons() {
		return radiobuttons;
	}

	public void setRadiobuttons(RadioButton[] radiobuttons) {
		this.radiobuttons = radiobuttons;
	}
	
//	public RadioButton getQuerfeedback_cb1_qs() {
//		return querfeedback_cb1_qs;
//	}
//
//	public void setQuerfeedback_cb1_qs(RadioButton querfeedback_cb1_qs) {
//		this.querfeedback_cb1_qs = querfeedback_cb1_qs;
//	}
//
//	public RadioButton getQuerfeedback_cb2_qs() {
//		return querfeedback_cb2_qs;
//	}
//
//	public void setQuerfeedback_cb2_qs(RadioButton querfeedback_cb2_qs) {
//		this.querfeedback_cb2_qs = querfeedback_cb2_qs;
//	}
//
//	public RadioButton getQuerfeedback_cb3_qs() {
//		return querfeedback_cb3_qs;
//	}
//
//	public void setQuerfeedback_cb3_qs(RadioButton querfeedback_cb3_qs) {
//		this.querfeedback_cb3_qs = querfeedback_cb3_qs;
//	}

	public EditText getQuerfeedback_et_question() {
		return querfeedback_et_question;
	}

	public void setQuerfeedback_et_question(EditText querfeedback_et_question) {
		this.querfeedback_et_question = querfeedback_et_question;
	}

	public RelativeLayout getQuerfeedback_rl_form() {
		return querfeedback_rl_form;
	}

	public void setQuerfeedback_rl_form(RelativeLayout querfeedback_rl_form) {
		this.querfeedback_rl_form = querfeedback_rl_form;
	}

	public Button getBg_up_arrow() {
		return bg_up_arrow;
	}

	public void setBg_up_arrow(Button bg_up_arrow) {
		this.bg_up_arrow = bg_up_arrow;
	}

	public Button getBg_up_arrow_top_line() {
		return bg_up_arrow_top_line;
	}

	public void setBg_up_arrow_top_line(Button bg_up_arrow_top_line) {
		this.bg_up_arrow_top_line = bg_up_arrow_top_line;
	}

	public void setBg_up_arrow_rl(RelativeLayout bg_up_arrow_rl){
		this.bg_up_arrow_rl = bg_up_arrow_rl;
	}
	
	private void startAnimationRadioButton(AnimationSet asin){
		for(RadioButton btn :radiobuttons){
			btn.startAnimation(asin);
		}
	}
	
	private void setRadioButtonsVisiable(int flag){
		for(RadioButton btn :radiobuttons){
			btn.setVisibility(flag);
		}
	}
	
}

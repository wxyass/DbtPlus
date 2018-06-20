package et.tsingtaopad;

import java.sql.SQLException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.login.LoginService;
import et.tsingtaopad.login.domain.LoginSession;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.NetStatusUtil;
import et.tsingtaopad.visit.shopvisit.ShopVisitService;
import et.tsingtaopad.visit.syncdata.UploadDataService;

@SuppressLint("HandlerLeak")
public class TitleLayout extends RelativeLayout implements OnClickListener {

	private ImageView banner_im_menu;
	private ImageView banner_person_img;
	private TextView banner_person_tv;
	private TextView banner_welcome_tv;
	private TextView visitTermNumTv;
	private TextView planTermNumTv;
	private TextView banner_progress_tv;
	private ProgressBar banner_plan_pb;
	private Button banner_exit_bt;
	public ProgressDialog mProgressDialog;
	private UploadDataService upload;
	private final int QUERY_ISALLEMPTY = 1;
	public static final int UPLOAD_DATA = 2;
	public static final int LOGIN_OUT = 3;
	public static final int LOGIN_OUT_FAIL = 4;
	private String errmsg = null;
	private boolean isUpLoadingError = false;
	private int exitCount = 0; // 记录退出的时候 已经更新多少个任务 ，通过任务量来判断是否已经全部更新完毕
	private final String TAG = "TitleLayout";
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOGIN_OUT_FAIL:
				String content = (String) msg.obj;
				Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
				break;
			case LOGIN_OUT:
				mProgressDialog.dismiss();
				ExitAppUtils.getInstance().exit();
				break;
			case QUERY_ISALLEMPTY:
				boolean isAllEmpty = (Boolean) msg.obj;
				Log.i("UploadDataService", "UploadDataService 是否上传完？" + isAllEmpty);
				if (isAllEmpty) {
					// 退出
					login_out();
				} else {
					exitCount = 0;
					//上传所有数据
					upload.uploadTables(true);
				}
				break;
			case UPLOAD_DATA:
				errmsg = (String) msg.obj;
				// 上传没有错误的情况下 是无法收到内容的
				if (errmsg != null) {
					Log.e(TAG, errmsg);
					isUpLoadingError = true;// 总共有9个上传模块 任何其中一个模块出错都不允许退出

				}

				exitCount++;
				// 所有上传模块执行完毕后判断是否有出错的模块
				if (isUpLoadingError) {
					mProgressDialog.dismiss();
					Toast.makeText(getContext(), "上传出错了，请重新上传", Toast.LENGTH_SHORT).show();
				} else {
					int adk = exitCount;
					if (exitCount >= 11 && mProgressDialog.isShowing()) {// 总共有9个上传模块 任何其中一个模块出错都不允许退出
						login_out();//数据上传完，请求退出
					}
				}

			default:
				break;
			}
		}

	};

	public TitleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TitleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TitleLayout(Context context) {
		super(context);
		init();

	}

	public void login_out() {
		HttpUtil httpUtil = new HttpUtil(60*1000);
		httpUtil.configResponseTextCharset("ISO-8859-1");
		StringBuffer buffer = new StringBuffer();
		LoginSession loginSession = ConstValues.loginSession;
		buffer.append("{userid:'").append(loginSession.getUserCode());
		buffer.append("', padid:'").append(loginSession.getPadId()).append("'}");
		
		httpUtil.send("opt_get_logout", buffer.toString(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
				if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
					handler.sendEmptyMessage(LOGIN_OUT);
				} else {
					Message msssage = new Message();
					msssage.what = LOGIN_OUT_FAIL;
					msssage.obj = resObj.getResBody().getContent();
					handler.sendMessage(msssage);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Message msssage = new Message();
				msssage.what = LOGIN_OUT_FAIL;
				msssage.obj = error.toString();
				if(!CheckUtil.isBlankOrNull(errmsg)){
				    Log.e(TAG, errmsg);
				}
				msssage.obj="网络信号差,上传失败，请重新退出！";
				handler.sendMessage(msssage);
			}
		});

	}

	private void init() {
		((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.banner, this);
		upload = new UploadDataService(getContext(), handler);
		banner_im_menu = (ImageView) findViewById(R.id.banner_im_menu);
		banner_person_img = (ImageView) findViewById(R.id.banner_person_img);
		banner_person_tv = (TextView) findViewById(R.id.banner_person_tv);
		banner_welcome_tv = (TextView) findViewById(R.id.banner_welcome_tv);
		visitTermNumTv = (TextView) findViewById(R.id.banner_tv_visitnum);//拜访的数据
		planTermNumTv = (TextView) findViewById(R.id.banner_tv_plannum);//计划数量
		banner_progress_tv = (TextView) findViewById(R.id.banner_progress_tv);
		banner_plan_pb = (ProgressBar) findViewById(R.id.banner_plan_pb);
		banner_exit_bt = ((Button) findViewById(R.id.banner_exit_bt));

		banner_im_menu.setOnClickListener(this);
		banner_person_tv.setText(new LoginService().getLoginSession(getContext()).getUserName());
		String hour = DateUtil.formatDate(new Date(), "HH");
		if (hour.compareTo("12") <= -1) {
			banner_welcome_tv.setText(R.string.banner_welcome_am);
		} else {
			banner_welcome_tv.setText(R.string.banner_welcome_pm);
		}

		ShopVisitService shopVisitService = new ShopVisitService(getContext(), null);
		// 获取当天结束且有效的拜访的终端 个数
		int visitTermNum = shopVisitService.queryVisitTermNum();
		visitTermNumTv.setText(String.valueOf(visitTermNum));
		int planTermNum = shopVisitService.queryPlanTermNum();
		planTermNumTv.setText(String.valueOf(planTermNum));
		if (planTermNum == 0) {
			banner_plan_pb.setProgress(100);
		} else {
			banner_plan_pb.setProgress((int) ((float) visitTermNum / planTermNum * 100));
		}

		mProgressDialog = new ProgressDialog(getContext());
		mProgressDialog.setMessage("正在努力上传数据，请等待....");
		getBanner_exit_bt().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Builder builder = new Builder(getContext());
				builder.setTitle("EXIT");
				builder.setMessage("确定退出应用？");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						// 检查网络
						if (NetStatusUtil.isNetValid(getContext())) {
							mProgressDialog.show();
							// 检查是否有未上传完的数据
							try {
								new Thread() {
									public void run() {
										try {
											boolean allEmpty = upload.isAllEmpty();
											Message msg = new Message();
											msg.what = QUERY_ISALLEMPTY;
											msg.obj = allEmpty;
											handler.sendMessage(msg);
										} catch (SQLException e) {
											e.printStackTrace();
										}
									}
								}.start();
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							// 提示修改网络
							Builder builder = new Builder(getContext());
							builder.setTitle("网络错误");
							builder.setMessage("还有未上传数据，请连接好网络上传数据再退出");
							builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									getContext().startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
								}
							}).create().show();
						}
					}

				});
				builder.setNegativeButton("取消", null).create().show();
			}
		});

		// 当用户取消上传的时候 移除所有的消息
		mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				Log.i(TAG, "upload remove");
				handler.removeCallbacksAndMessages(null);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.banner_im_menu:
		    //得到activty的屏幕方向
		    int orient = ((Activity) getContext()).getRequestedOrientation();
		    Intent intent =new Intent(getContext(), PlatformMenuActivity.class);
		    
		    if(orient == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
		        intent.putExtra("SCREEN_ORIENTATION", "landscape");
		    }else{
		        intent.putExtra("SCREEN_ORIENTATION", "portrait");
		    }
			getContext().startActivity(intent);
			break;

		default:
			break;
		}

	}

	public void setBanner_person_img(int resID) {
		this.banner_person_img.setImageDrawable(getResources().getDrawable(resID));
	}

	public void setBanner_person_tv(String person) {
		this.banner_person_tv.setText(person);
	}

	public void setBanner_progress_tv(String progress) {
		this.banner_progress_tv.setText(progress);
	}

	public void setBanner_plan_pb(int progress) {
		this.banner_plan_pb.setProgress(progress);
	}

	public Button getBanner_exit_bt() {
		return banner_exit_bt;
	}

}

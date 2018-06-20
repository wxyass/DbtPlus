package et.tsingtaopad.syssetting.todaything;

import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.db.tables.MstVisitmemoInfo;
import et.tsingtaopad.visit.memorybook.MemoDialogFragment;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.TerminalName;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：ToDayThingFragmentTest.java</br> 作者：dbt </br>
 * 创建时间：2014-1-19</br> 功能描述: </br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
public class ToDayThingFragmentTest extends BaseFragmentSupport implements OnClickListener, OnItemSelectedListener, OnItemClickListener, OnCheckedChangeListener, OnScrollListener {
    // private Button banner2_back_bt;
    // private TextView banner2_title_tv;

    // private ImageView todaything_bt_noticeicon;
    private ToDayThingService service;
    private ListView todaything_lv_display;
    /** 线路选择 */
    private Spinner belongLineSp = null;

    private android.app.Fragment frag;

    private ToDayThingAdapter toDay_adapter = null;

    private List<MstVisitmemoInfo> list = null;
    /** 对话框内容 */
    private TextView dialog_content_tv;
    /** 开始时间，结束时间 */
    private TextView dialog_startTime_tv;

    private LinearLayout dialog_memoryTime_ll;

    private CheckBox dialog_remeber_cb = null;
    /** 当前线路号 */
    private int currentLineNumber = -1;
    /**是否包含footview*/
    private boolean hasFootView = false;

    private View footView = null;

    private Dialog detailDialog = null;

    private DatePickerDialog Datedialog = null;
    
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConstValues.WAIT1:// 从网络获取数据
                    List<MstVisitmemoInfo> list1 = (List<MstVisitmemoInfo>) msg.obj;
                    list.addAll(remove(list1));
                    toDay_adapter.setData(list);
                    break;
                case ConstValues.WAIT2:
                    break;
            }
        }

    };

    private List<MstVisitmemoInfo> remove(List<MstVisitmemoInfo> list1) {
        for (int j = 0; j < list.size(); j++) {
            for (int i = 0; i < list1.size(); i++) {
                if (list1.get(i).getMemokey().equals(list.get(j).getMemokey())) {//主键一致,主键为空
                    synchronized (list1) {
                        list1.remove(i);
                    }

                }
            }
        }
        return list1;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.business_todaything_test, null);
        this.initView(view);
        this.initData();

        return view;

    }

    /**
     * 初始化界面组件
     */
    private void initView(View view) {
        // 绑定页面组件
        todaything_lv_display = (ListView) view.findViewById(R.id.todaything_lv_display);

        belongLineSp = (Spinner) view.findViewById(R.id.todaything_spinner_routeline);

        belongLineSp.setSelection(1, false);
        //		footView = View.inflate(getActivity(), R.layout.visit_agencyvisit_footview, null);
        SpinnerKeyValueAdapter adapter = new SpinnerKeyValueAdapter(getActivity(), ConstValues.lineLst, new String[] { "routekey", "routename" }, null);
        //		todaything_lv_display.addFooterView(footView);
        //		hasFootView = true;
        belongLineSp.setAdapter(adapter);
        Log.d("tag", "spinner--size" + ConstValues.lineLst.size());

        // 绑定事件
        belongLineSp.setOnItemSelectedListener(this);
        todaything_lv_display.setOnItemClickListener(this);
        todaything_lv_display.setOnScrollListener(this);
        view.setOnClickListener(null);
    }

    /**
     * 初始化界面数据
     */
    private void initData() {
        // 初始化service
        service = new ToDayThingService(handler, getActivity());
        service.setTodaything_lv_display(todaything_lv_display);

        // 页面显示数据初始化
        list = service.searchLocalDate();
        toDay_adapter = new ToDayThingAdapter(getActivity(), list, R.drawable.bg_todaywords_fill);
        todaything_lv_display.setAdapter(toDay_adapter);
        //		addFootView();
    }

    private void addFootView() {
        if (list.size() > 0) {
            Log.d("tag", "AddFootView");
            //			todaything_lv_display.addFooterView(footView);
            hasFootView = true;
        } else {
            service.searchRemoteData((int) belongLineSp.getSelectedItemId());
        }
        Log.d("tag", "spinner-->" + ((MstRouteM) belongLineSp.getSelectedItem()).getRoutekey());

    }

    @Override
    public void onClick(View v) {
        Fragment fm = null;

        switch (v.getId()) {
            case R.id.banner_navigation_bt_back:
                // 退出
                this.getFragmentManager().popBackStack();
                break;
            case R.id.todaything_bt_noticeicon:
                // 跳转,显示上一层
                this.getFragmentManager().popBackStack();
                return;

                // fm = new NoticeFragment();
                // break;
            case R.id.business_tadaything_dialog_img_close:// 关闭对话框
                detailDialog.dismiss();
                break;
            default:
                break;
        }
        // if (fm != null) {
        // FragmentTransaction transaction = getFragmentManager()
        // .beginTransaction();
        // transaction.replace(R.id.business_container, fm);
        // transaction
        // .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //
        // transaction.addToBackStack(null);
        // transaction.commit();
        // }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {// 选择了几号路线
        Log.d("tag", "ItemSelected--><" + arg2);
        currentLineNumber = arg2;
        ConstValues.lineLst.get(arg2).getRoutename();

        list = service.searchLocalData(ConstValues.lineLst.get(arg2).getRoutekey());
        toDay_adapter.setData(list);

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Log.d("tag", "ItemSelected-->nothing<");
    }

    /**
     * 点击今日要事 的item
     * 
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        showItemDialog((int) arg3);

    }

    private void showItemDialog(int position) {
        if (position < 0)
            return;
        MstVisitmemoInfo mstVisitmemoInfo = list.get(position);
        MemoDialogFragment fragment = MemoDialogFragment.getInstance(false, mstVisitmemoInfo.getTerminalkey(), "", mstVisitmemoInfo.getMemokey());
        fragment.show(getFragmentManager(), "dialog");
        //		LayoutInflater inflater = getActivity().getLayoutInflater();
        //		View layout = inflater.inflate(R.layout.business_todaything_detail_dialog, null);
        //		detailDialog = new Dialog(getActivity());
        //		detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //		detailDialog.setContentView(layout);
        //		detailDialog.show();
        //		layout.findViewById(R.id.business_tadaything_dialog_img_close).setOnClickListener(this);
        //		dialog_memoryTime_ll = (LinearLayout) layout.findViewById(R.id.business_tadaything_dialog_ll_time);
        //		dialog_content_tv = (TextView) layout.findViewById(R.id.business_tadaything_dialog_tv_content);
        //		dialog_startTime_tv = (TextView) layout.findViewById(R.id.business_tadaything_dialog_tv_start_time);
        //		dialog_remeber_cb = (CheckBox) layout.findViewById(R.id.business_tadaything_dialog_checkbox_memo);
        //		MstVisitmemoInfo mstVisitmemoInfo = list.get(position);
        //		dialog_content_tv.setText(mstVisitmemoInfo.getContent());
        //		String startDate = DateUtils.divide(mstVisitmemoInfo.getStartdate());
        //		String endDate =   DateUtils.divide(mstVisitmemoInfo.getEnddate());
        //		if (!CheckUtil.isBlankOrNull(startDate) && !CheckUtil.isBlankOrNull(endDate)) {
        //			dialog_startTime_tv.setText(startDate + " -- " + endDate);
        //		}
        //		dialog_remeber_cb.setOnCheckedChangeListener(this);
    }

    // private void showDatePickDialog(final View textView,int year,int
    // monthOfYear,int dayOfMonth){
    //
    // Datedialog = new DatePickerDialog(getActivity(), new OnDateSetListener(){
    //
    // @Override
    // public void onDateSet(DatePicker view, int year, int monthOfYear,
    // int dayOfMonth) {
    // ((TextView) textView).setText(year+"-"+monthOfYear+"-"+dayOfMonth);
    // }
    // }, year, monthOfYear, dayOfMonth);
    // Datedialog.show();
    // }
    //
    // private void closeDatePickDialog(){
    // Datedialog.dismiss();
    // }

    /**
     * 是否记住
     */
    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
        if (arg1) {
            dialog_memoryTime_ll.setVisibility(View.VISIBLE);
        } else {
            dialog_memoryTime_ll.setVisibility(View.GONE);

        }

    }

    @Override
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
        Scroll_arg1 = arg1;
        Scroll_arg2 = arg2;
        Scroll_arg3 = arg3;
    }

    /** 滑动状态 */
    private int Scroll_arg1, Scroll_arg2, Scroll_arg3;

    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        Log.d("tag", "scroll--state->" + service.isEnd() + "isquery->" + service.isQuerying());
        if ((arg1 == SCROLL_STATE_TOUCH_SCROLL || arg1 == SCROLL_STATE_FLING) && (Scroll_arg3 == Scroll_arg1 + Scroll_arg2) && service != null) {// 当滑动时，触摸时

            //			if (service.isEnd()) {// 如果已经到末尾了
            //				return;
            //			} else if (!service.isQuerying()) {// 没有正在查询
            //				service.searchRemoteData(currentLineNumber);
            //			}
        }
    }
    /**
     * 1.当显示数据很少，比如只有一条，touch时，加载更多 2.当数据很多时，touch ，或者fling 状态时，当到底了，加载更多
     * 
     */

}

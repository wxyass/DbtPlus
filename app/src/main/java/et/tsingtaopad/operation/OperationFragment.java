package et.tsingtaopad.operation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.R;
import et.tsingtaopad.operation.distirbution.DistirbutionActivity;
import et.tsingtaopad.operation.indexstatus.IndexstatusFragment;
import et.tsingtaopad.operation.promotion.PromotionActivity;
import et.tsingtaopad.operation.weekworkplan.WeekWorkPlanFragment;
import et.tsingtaopad.operation.working.DayWeekingSdFragment;
import et.tsingtaopad.operation.working.DayWorkingFragment;
import et.tsingtaopad.operation.working.MonthWorkingFragment;
import et.tsingtaopad.operation.working.WeekWorkingFragment;
import et.tsingtaopad.operation.workplan.WorkPlanFragment;
import et.tsingtaopad.visit.tomorrowworkrecord.TomorrowWorkRecordFragment;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：OperationFragment.java</br>
 * 作者：hongen   </br>
 * 创建时间：2014-2-13</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class OperationFragment extends 
            BaseFragmentSupport implements OnClickListener {

	private Button workplanBt;
	private Button dayWorkBt;
	private Button weekWorkBt;
	private Button monthWorkBt;
	private Button workDetialBt;
	private Button omnipotentBt;
	private Button promotionBt;
	private Button indexstatusBt;
	private Button monthWorkplanBt;
	private Button dayworksd;

	@Override
	public View onCreateView(LayoutInflater inflater, 
	                ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.platform_operation, null);
		workplanBt = (Button) view.findViewById(R.id.operation_bt_wrokplan);
		dayWorkBt = (Button)view.findViewById(R.id.operation_bt_daywork);
		dayworksd = (Button) view.findViewById(R.id.operation_bt_dayworksd);
		weekWorkBt = (Button)view.findViewById(R.id.operation_bt_weekwork);
		monthWorkBt = (Button)view.findViewById(R.id.operation_bt_monthwork);
		workDetialBt = (Button)view.findViewById(R.id.operation_bt_workdetial);
		omnipotentBt = (Button) view.findViewById(R.id.operation_bt_omnipotent);
		promotionBt = (Button) view.findViewById(R.id.operation_bt_promotion);
		indexstatusBt = (Button) view.findViewById(R.id.operation_bt_indexsearch);
		monthWorkplanBt = (Button) view.findViewById(R.id.operation_bt_weekworkplan);
		
		workplanBt.setOnClickListener(this);
		dayWorkBt.setOnClickListener(this);
		weekWorkBt.setOnClickListener(this);
		monthWorkBt.setOnClickListener(this);
		workDetialBt.setOnClickListener(this);
		omnipotentBt.setOnClickListener(this);
		promotionBt.setOnClickListener(this);
		indexstatusBt.setOnClickListener(this);
		monthWorkplanBt.setOnClickListener(this);
		dayworksd.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {

		Fragment fragment = null;
		Intent intent = null;

		switch (v.getId()) {

		// 日周工作计划
		case R.id.operation_bt_wrokplan:
			fragment = new WorkPlanFragment();
			break;
			
		// 日工作推进
		case R.id.operation_bt_daywork:
		    fragment = new DayWorkingFragment();
		    break;
			
		// 周工作总结
		case R.id.operation_bt_weekwork:
		    fragment = new WeekWorkingFragment();
		    break;
			
		// 月工作推进
		case R.id.operation_bt_monthwork:
			fragment = new MonthWorkingFragment();
			break;
			
		// 日工作明细
		case R.id.operation_bt_workdetial:
            intent = new Intent(getActivity(), TomorrowWorkRecordFragment.class);
		    break;
		
		// 促销活动查询
		case R.id.operation_bt_promotion:
			intent = new Intent(getActivity(), PromotionActivity.class);
			break;
			
		// 万能铺货率查询
		case R.id.operation_bt_omnipotent:
			intent = new Intent(getActivity(), DistirbutionActivity.class);
			break;
		
		// 指标状态查询
		case R.id.operation_bt_indexsearch:
		    fragment = new IndexstatusFragment();
		    break;
		    
		// 周工作计划查询
		case R.id.operation_bt_weekworkplan:
		    fragment = new WeekWorkPlanFragment();
		    break;
		    
		//日工作推进(山东)
		case R.id.operation_bt_dayworksd:
			fragment=new DayWeekingSdFragment();
			break;
			
		default:
			break;
		}
		if (intent != null) {
			getActivity().startActivity(intent);
		}

		if (fragment != null) {
			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			transaction.replace(R.id.operation_container, fragment);
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
}

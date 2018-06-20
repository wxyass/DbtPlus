package et.tsingtaopad.visit.shopvisit.checkindex;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.cui.SlideSwitch;
import et.tsingtaopad.cui.SlideSwitch.OnSwitchChangedListener;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.visit.shopvisit.camera.domain.PictypeDataStc;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexPromotionStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：PromotionAdapter.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-20</br>      
 * 功能描述: 巡店拜访-查指标-促销活动Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class PromotionAdapter extends BaseAdapter {
    
    private Activity context;
    private List<CheckIndexPromotionStc> dataLst;
    private int countonoff = 0;
    private String visitDate;
    private String isbigarea;
    private String seeFlag;
    private String twoareaid;
    Button activityPicBt;
    List<PictypeDataStc> valueLst;
    private IClick mListener; 
    
    public PromotionAdapter(Activity context, List<CheckIndexPromotionStc> dataLst, String visitDate,Button activityPicBt,List<PictypeDataStc> valueLst,String isbigarea,String seeFlag,IClick listener) {
        this.context = context;
        this.dataLst = dataLst;
        this.visitDate = visitDate;
        this.activityPicBt = activityPicBt;
        this.valueLst = valueLst;
        this.isbigarea = isbigarea;
        this.seeFlag = seeFlag;
        this.twoareaid = PrefUtils.getString(context, "disId", "");
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        if (CheckUtil.IsEmpty(dataLst)) {
            return 0;
        } else {
            return dataLst.size();
        }
    }

    @Override
    public Object getItem(int arg0) {
        if (CheckUtil.IsEmpty(dataLst)) {
            return null;
        } else {
            return dataLst.get(arg0);
        }
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d("promotion ", position+"  "+DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:SSS"));
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.checkindex_promotion_lvitem, null);
            holder.promotionNameTv = (TextView)convertView.findViewById(R.id.promotion_tv_name);
            holder.startDateTv = (TextView)convertView.findViewById(R.id.promotion_tv_startdate);
            holder.endDateTv = (TextView)convertView.findViewById(R.id.promotion_tv_enddate);
            holder.proNameTv = (TextView)convertView.findViewById(R.id.promotion_tv_proname);
            holder.statusSw = (SlideSwitch)convertView.findViewById(R.id.promotion_sw_status);
            holder.reachnum = (EditText)convertView.findViewById(R.id.promotion_et_reachnum);
            holder.button = (TextView)convertView.findViewById(R.id.promotion_btn_camera);
            
            holder.statusSw.setOnSwitchChangedListener(new MySwitchChangedListener(holder) {
    			
    			@Override
    			public void afterTextChanged(SlideSwitch obj, int status, ViewHolder holder) {
    				if(status==SlideSwitch.SWITCH_ON){
    					holder.reachnum.setVisibility(View.VISIBLE);// 达成组数
    					dataLst.get(position).setIsAccomplish(ConstValues.FLAG_1);// 活动设为达成
    					
    					if("1".equals(isbigarea)){// 二级配置  0:大区  1:二级区域
    						// 根据是否达成 拍照按钮显示
        			        if("1".equals(dataLst.get(position).getIspictype())&&twoareaid.equals(dataLst.get(position).getAreaid())){
        			        	holder.button.setVisibility(View.VISIBLE);
        			        	holder.button.setEnabled(true);
        			        }else{
        			        	//holder.button.setVisibility(View.INVISIBLE);
        			        	holder.button.setVisibility(View.GONE);
        			        	holder.button.setEnabled(false);
        			        }
    					} else {
    						// 根据是否达成 拍照按钮显示
        			        if("1".equals(dataLst.get(position).getIspictype())&&(!twoareaid.equals(dataLst.get(position).getAreaid()))){
        			        	holder.button.setVisibility(View.VISIBLE);
        			        	holder.button.setEnabled(true);
        			        }else{
        			        	//holder.button.setVisibility(View.INVISIBLE);
        			        	holder.button.setVisibility(View.GONE);
        			        	holder.button.setEnabled(false);
        			        }
    					}
    					
    			        
    	            }else{
    	            	holder.reachnum.setVisibility(View.INVISIBLE);// 达成组数
    	            	holder.reachnum.setText(null);// 达成组数
    	            	dataLst.get(position).setIsAccomplish(ConstValues.FLAG_0);// 活动设为未达成
    	            	
    	            	if("1".equals(isbigarea)){// 二级配置  0:大区  1:二级区域
    	            		if("1".equals(dataLst.get(position).getIspictype())&&twoareaid.equals(dataLst.get(position).getAreaid())){
        			        	//holder.button.setVisibility(View.VISIBLE);
    	            			//holder.button.setVisibility(View.INVISIBLE);
    	            			holder.button.setVisibility(View.GONE);
        			        	holder.button.setEnabled(false);
        			        }else{
        			        	//holder.button.setVisibility(View.INVISIBLE);
        			        	holder.button.setVisibility(View.GONE);
        			        	holder.button.setEnabled(false);
        			        }
    	            	} else {// 大区
    	            		// 根据是否达成 拍照按钮显示
        			        if("1".equals(dataLst.get(position).getIspictype())&&(!twoareaid.equals(dataLst.get(position).getAreaid()))){
        			        	//holder.button.setVisibility(View.VISIBLE);
        			        	//holder.button.setVisibility(View.INVISIBLE);
        			        	holder.button.setVisibility(View.GONE);
        			        	holder.button.setEnabled(false);
        			        }else{
        			        	//holder.button.setVisibility(View.INVISIBLE);
        			        	holder.button.setVisibility(View.GONE);
        			        	holder.button.setEnabled(false);
        			        }
    	            	}
    	            }
    				
    				// 根据是否有达成的活动,显示活动拍照按钮--160927-------------------
    		        int IsAccomplishcount = 0;
    		        for (int i = 0; i < dataLst.size(); i++) {
    		            
    		            if("1".equals(dataLst.get(i).getIsAccomplish())){// 达成
    		            	IsAccomplishcount++;
    		            }
    		        }
    		        if(IsAccomplishcount>0&&valueLst.size()<3&&(!"1".equals(seeFlag))){// 查看模式不显示拍照按钮
    		        	//activityPicBt.setVisibility(View.VISIBLE);// 如果有促销活动,拍照按钮显示
    		        	activityPicBt.setVisibility(View.GONE);// 这个拍照按钮不再显示
    		        }else{
    		        	activityPicBt.setVisibility(View.GONE);
    		        }
    		        // 根据是否有达成的活动,显示活动拍照按钮--160927-------------------
    		        
    			}
    		});
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        holder.statusSw.setTag(position);
        
        CheckIndexPromotionStc item = dataLst.get(position);
        holder.promotionNameTv.setHint(item.getPromotKey());
        holder.promotionNameTv.setText(item.getPromotName());
        holder.startDateTv.setText(item.getStartDate());
        holder.endDateTv.setText(item.getEndDate());
        holder.proNameTv.setHint(item.getProId());
        holder.proNameTv.setText(item.getProName());
        
        // 添加促销活动 隔天默认关闭 //20160301(原因:需求设计如此)
        String todaytime = DateUtil.formatDate(new Date(),"yyyy-MM-dd");
        String accepttime = visitDate.substring(0, 10);
        
        //
        //String todaytime = DateUtil.formatDate(new Date(),"yyyy.MM.dd");
        //String accepttime = item.getStartDate();
        if(todaytime.equals(accepttime)){// 当天 多次进入这家终端
        	if (ConstValues.FLAG_1.equals(item.getIsAccomplish())) {
                holder.statusSw.setStatus(true);
                holder.reachnum.setVisibility(View.VISIBLE);
                holder.reachnum.setText(item.getReachNum());
                
                // 条目拍照按钮是否打开
                /*if("1".equals(dataLst.get(position).getIspictype())){
		        	holder.button.setVisibility(View.VISIBLE);
		        	holder.button.setEnabled(true);
		        }else{
		        	holder.button.setVisibility(View.INVISIBLE);
		        	holder.button.setEnabled(false);
		        }*/
                
                if("1".equals(isbigarea)){// 二级配置  0:大区  1:二级区域
					// 根据是否达成 拍照按钮显示
			        if("1".equals(dataLst.get(position).getIspictype())&&twoareaid.equals(dataLst.get(position).getAreaid())){
			        	holder.button.setVisibility(View.VISIBLE);
			        	holder.button.setEnabled(true);
			        }else{
			        	//holder.button.setVisibility(View.INVISIBLE);
			        	holder.button.setVisibility(View.GONE);
			        	holder.button.setEnabled(false);
			        }
				} else {// 大区配置
					// 根据是否达成 拍照按钮显示
			        if("1".equals(dataLst.get(position).getIspictype())&&(!twoareaid.equals(dataLst.get(position).getAreaid()))){
			        	holder.button.setVisibility(View.VISIBLE);
			        	holder.button.setEnabled(true);
			        }else{
			        	//holder.button.setVisibility(View.INVISIBLE);
			        	holder.button.setVisibility(View.GONE);
			        	holder.button.setEnabled(false);
			        }
				}
                
            } else {
                holder.statusSw.setStatus(false);
                holder.reachnum.setVisibility(View.INVISIBLE);
                holder.reachnum.setText(null);
                
                // 条目拍照按钮是否打开
                /*if("1".equals(dataLst.get(position).getIspictype())){
		        	holder.button.setVisibility(View.VISIBLE);
		        	holder.button.setEnabled(false);
		        }else{
		        	holder.button.setVisibility(View.INVISIBLE);
		        	holder.button.setEnabled(false);
		        }*/
                
                if("1".equals(isbigarea)){// 二级配置  0:大区  1:二级区域
            		if("1".equals(dataLst.get(position).getIspictype())&&twoareaid.equals(dataLst.get(position).getAreaid())){
			        	//holder.button.setVisibility(View.VISIBLE);
            			//holder.button.setVisibility(View.INVISIBLE);
            			holder.button.setVisibility(View.GONE);
			        	holder.button.setEnabled(false);
			        }else{
			        	//holder.button.setVisibility(View.INVISIBLE);
			        	holder.button.setVisibility(View.GONE);
			        	holder.button.setEnabled(false);
			        }
            	} else {// 大区
            		// 根据是否达成 拍照按钮显示
			        if("1".equals(dataLst.get(position).getIspictype())&&(!twoareaid.equals(dataLst.get(position).getAreaid()))){
			        	//holder.button.setVisibility(View.VISIBLE);
			        	//holder.button.setVisibility(View.INVISIBLE);
			        	holder.button.setVisibility(View.GONE);
			        	holder.button.setEnabled(false);
			        }else{
			        	//holder.button.setVisibility(View.INVISIBLE);
			        	holder.button.setVisibility(View.GONE);
			        	holder.button.setEnabled(false);
			        }
            	}
            }
        }else{// 上次拜访 不是当天 (即:当天第一次拜访)
        	holder.statusSw.setStatus(false);
            holder.reachnum.setVisibility(View.INVISIBLE);
            holder.reachnum.setText(null);
            
            // 条目拍照按钮 关闭
            //holder.button.setVisibility(View.INVISIBLE);
            if("1".equals(isbigarea)){// 二级配置  0:大区  1:二级区域
        		if("1".equals(dataLst.get(position).getIspictype())&&twoareaid.equals(dataLst.get(position).getAreaid())){
		        	//holder.button.setVisibility(View.VISIBLE);
        			//holder.button.setVisibility(View.INVISIBLE);
        			holder.button.setVisibility(View.GONE);
		        	holder.button.setEnabled(false);
		        }else{
		        	//holder.button.setVisibility(View.INVISIBLE);
		        	holder.button.setVisibility(View.GONE);
		        	holder.button.setEnabled(false);
		        }
        	} else {// 大区
        		// 根据是否达成 拍照按钮显示
		        if("1".equals(dataLst.get(position).getIspictype())&&(!twoareaid.equals(dataLst.get(position).getAreaid()))){
		        	//holder.button.setVisibility(View.VISIBLE);
		        	//holder.button.setVisibility(View.INVISIBLE);
		        	holder.button.setVisibility(View.GONE);
		        	holder.button.setEnabled(false);
		        }else{
		        	//holder.button.setVisibility(View.INVISIBLE);
		        	holder.button.setVisibility(View.GONE);
		        	holder.button.setEnabled(false);
		        }
        	}
        }
        
        // 拍照按钮显示
        /*if("1".equals(item.getIspictype())){
        	holder.button.setVisibility(View.VISIBLE);
        }else{
        	holder.button.setVisibility(View.INVISIBLE);
        }*/
        
        
        holder.button.setOnClickListener(mListener);  
        holder.button.setTag(position);
        
        /*holder.statusSw.setOnSwitchChangedListener(new MySwitchChangedListener(holder) {
			
			@Override
			public void afterTextChanged(SlideSwitch obj, int status, ViewHolder holder) {
				if(status==SlideSwitch.SWITCH_ON){
					holder.reachnum.setVisibility(View.VISIBLE);
	            }else{
	            	holder.reachnum.setVisibility(View.INVISIBLE);
	            }
				
			}
		});*/
        //holder.reachnum.addTextChangedListener(watcher);
        
        
        return convertView;
    }

    private class ViewHolder {
        private TextView promotionNameTv;
        private TextView startDateTv;
        private TextView endDateTv;
        private TextView proNameTv;
        private SlideSwitch statusSw;
        private EditText reachnum;
        public TextView button;
    }
    
    abstract class MySwitchChangedListener implements OnSwitchChangedListener{

    	private ViewHolder mHolder;
		
		public MySwitchChangedListener(ViewHolder holder) {
			mHolder = holder;
		}
		@Override
		public void onSwitchChanged(SlideSwitch obj, int status) {
			 afterTextChanged(obj, status,mHolder);
		}
		public abstract void afterTextChanged(SlideSwitch obj, int status, ViewHolder holder);
    }
    
    
}

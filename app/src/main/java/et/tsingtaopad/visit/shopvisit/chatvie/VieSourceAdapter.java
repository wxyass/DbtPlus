package et.tsingtaopad.visit.shopvisit.chatvie;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstCmpagencyInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.chatvie.domain.ChatVieStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：VieSourceAdapter.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-6</br>      
 * 功能描述: 巡店拜访--聊竞品，竞品来源Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class VieSourceAdapter extends BaseAdapter 
            implements OnClickListener, OnFocusChangeListener {

    private final String TAG = "VieSourceAdapter";
    private Activity context;
    private List<ChatVieStc> dataLst;
    private String termId;
    private VieStatusAdapter statusAdapter;
    private List<MstCmpagencyInfo> agencyLst;
    private int delPosition = -1;
    private AlertDialog dialog;
    private String seeFlag;
    private ListView vieStatusLv;
    private ListView vieSourceLv;
    
    public VieSourceAdapter(Activity context,String seeFlag, List<ChatVieStc> dataLst,
            String termId, VieStatusAdapter statusAdapter, List<MstCmpagencyInfo> agencyLst,ListView vieStatusLv,ListView viewSourceLv) {
        this.context = context;
        this.seeFlag = seeFlag;
        this.dataLst = dataLst;
        this.termId = termId;
        this.vieStatusLv = vieStatusLv;
        this.vieSourceLv = viewSourceLv;
        
        this.statusAdapter = statusAdapter;
        this.agencyLst = agencyLst;
    }

    @Override
    public int getCount() {
        /*if (CheckUtil.IsEmpty(dataLst)) {
            return 0;
        } else {
            return dataLst.size();
        }*/
        return dataLst.size();
    }

    @Override
    public Object getItem(int arg0) {
        /*if (CheckUtil.IsEmpty(dataLst)) {
            return null;
        } else {
            return dataLst.get(arg0);
        }*/
        return dataLst.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("vieSource ", position+"===  "+DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:SSS"));
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.chatvie_viesource_lvitem, null);
            holder.proTv = (TextView)convertView.findViewById(R.id.viesource_tv_viename);
            holder.channelPriceEt = (EditText)convertView.findViewById(R.id.viesource_et_channelprice);
            holder.sellPriceEt = (EditText)convertView.findViewById(R.id.viesource_et_sellprice);
            //holder.agencyBt = (Button)convertView.findViewById(R.id.viesource_bt_agency);
            holder.agencyEt = (EditText)convertView.findViewById(R.id.viesource_et_agency);
            holder.deleteBt = (Button)convertView.findViewById(R.id.viesource_bt_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        ChatVieStc item = dataLst.get(position);
        holder.deleteBt.setTag(position);
        
       
        if(ConstValues.FLAG_1.equals(seeFlag)){
        	//处于查看状态
        	
        }else{
        	 holder.deleteBt.setOnClickListener(this);
        }
        holder.proTv.setHint(item.getProId());
        holder.proTv.setText(item.getProName());
        
        if (ConstValues.FLAG_0.equals(item.getChannelPrice())) {
            holder.channelPriceEt.setHint(item.getChannelPrice());
            holder.channelPriceEt.setText(null);
        } else if(!CheckUtil.isBlankOrNull(item.getChannelPrice())){
            holder.channelPriceEt.setText(item.getChannelPrice());
        }else{
            holder.channelPriceEt.setHint(R.string.hit_input);
            holder.channelPriceEt.setText(null);
        }
        holder.channelPriceEt.setTag(position);
        holder.channelPriceEt.setOnFocusChangeListener(this);
        
        if (ConstValues.FLAG_0.equals(item.getSellPrice())) {
            holder.sellPriceEt.setHint(item.getSellPrice());
            holder.sellPriceEt.setText(null);
        } else if(!CheckUtil.isBlankOrNull(item.getSellPrice())) {
            holder.sellPriceEt.setText(item.getSellPrice());
        }else{
            holder.sellPriceEt.setHint(R.string.hit_input);
            holder.sellPriceEt.setText(null);
        }
        holder.sellPriceEt.setTag(position);
        holder.sellPriceEt.setOnFocusChangeListener(this);
        
        // 经销商名称
        if ("".equals(item.getAgencyName())) {
        	holder.agencyEt.setHint(item.getAgencyName());
        	holder.agencyEt.setText(null);
        } else if(!CheckUtil.isBlankOrNull(item.getAgencyName())) {
        	holder.agencyEt.setText(item.getAgencyName());
        }else{
        	holder.agencyEt.setHint(R.string.hit_input);
        	holder.agencyEt.setText(null);
        }
        holder.agencyEt.setTag(position);
        holder.agencyEt.setOnFocusChangeListener(this);
        
        
        /*holder.agencyBt.setText(item.getAgencyName());
        holder.agencyBt.setTag(item.getAgencyId());
        holder.agencyBt.setOnClickListener(this);*/
        
        return convertView;
    }

    private class ViewHolder {
        private TextView proTv;
        private EditText channelPriceEt;
        private EditText sellPriceEt;
        //private Button agencyBt;
        private EditText agencyEt;
        private Button deleteBt;
    }

    /**
     * 单击删除
     */
    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
        /*case R.id.viesource_bt_agency:
            DialogUtil.showSingleDialog((Activity)context, (Button)v, agencyLst, new String[]{"cmpagencykey","cmpagencyname"});
            break;*/
            
        case R.id.viesource_bt_delete:
            DbtLog.logUtils(TAG,"删除");
            if (dialog == null || !dialog.isShowing()) {
                Builder builder = new Builder(context);
                builder.setTitle(R.string.dialog_title);
                builder.setMessage(R.string.dialog_msg_del);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.dialog_bt_sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        delPosition = (Integer)v.getTag();
                        ChatVieStc item = dataLst.get(delPosition);
                        DbtLog.logUtils(TAG,"删除产品:"+item.getProName()+"、产品key："+item.getProId());
                        // 删除对应的数据
                        if (!CheckUtil.isBlankOrNull(item.getRecordId())) {
                            DbtLog.logUtils(TAG,"解除供货关系");
                            ChatVieService service = new ChatVieService(context, null);
                            boolean isFlag=service.deleteSupply(item.getRecordId(), termId, item.getProId());
                            if(isFlag){
                                // 删除界面listView相应行
                                dataLst.remove(delPosition);
                                notifyDataSetChanged();
                                statusAdapter.setDelPosition(delPosition);
                                statusAdapter.notifyDataSetChanged();
                                ViewUtil.setListViewHeight(vieSourceLv);
                                ViewUtil.setListViewHeight(vieStatusLv);
                                
                            }else{
                                Toast.makeText(context, "删除产品失败!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            // 删除界面listView相应行
                            dataLst.remove(delPosition);
                            notifyDataSetChanged();
                            statusAdapter.setDelPosition(delPosition);
                            statusAdapter.notifyDataSetChanged();
                            ViewUtil.setListViewHeight(vieSourceLv);
                            ViewUtil.setListViewHeight(vieStatusLv);
                        }
                        
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.dialog_bt_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
            break;
        
        default:
            break;
        }
    }
    
    public void onFocusChange(View v, boolean hasFocus) {
        EditText et = (EditText)v;
        int position = (Integer)v.getTag();
        if (position == delPosition) return;
        if (position > delPosition && delPosition != -1) {
            position = position -1;
        }
        if (position > -1) {
            ChatVieStc stc = dataLst.get(position);
            String content = et.getText().toString();
            switch (et.getId()) {
            case R.id.viesource_et_channelprice:
            	// 进店价 判断小数点
            	/*if(".".equals(content)){//
            		stc.setChannelPrice("0.0");// 数据库
            		et.setText("0.0");// 页面
            	}
            	else if(content.length()>1&&content.endsWith(".")){
            		stc.setChannelPrice(content+"0");// 数据库
            		et.setText(content+"0");// 页面
            	}
            	else if(content.length()>1&&content.startsWith(".")){
            		stc.setChannelPrice("0"+content);// 数据库
            		et.setText("0"+content);// 页面
            	}
            	else{
            		stc.setChannelPrice(content);// 数据库
            		et.setText(content);// 页面
            	}*/
            	
            	
                stc.setChannelPrice(content);
                break;
                
            case R.id.viesource_et_sellprice:
            	// 零售价 判断小数点
            	/*if(".".equals(content)){//
            		stc.setSellPrice("0.0");// 数据库
            		et.setText("0.0");// 页面
            	}
            	else if(content.length()>1&&content.endsWith(".")){
            		stc.setSellPrice(content+"0");// 数据库
            		et.setText(content+"0");//页面
            	}
            	else if(content.length()>1&&content.startsWith(".")){
            		stc.setSellPrice("0"+content);// 数据库
            		et.setText("0"+content);// 页面
            	}
            	else{
            		stc.setSellPrice(content);// 数据库
            		et.setText(content);// 页面
            	}*/
            	
                stc.setSellPrice(content);
                break;
            case R.id.viesource_et_agency:
            	stc.setAgencyName(content);
            	break;
                
            default:
    
                break;
            }
        }
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }
}

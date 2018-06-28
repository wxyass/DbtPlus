package et.tsingtaopad.visit.shopvisit.invoicing;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.invoicing.domain.InvoicingStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：InvoicingAskGoodsAdapter.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-6</br>      
 * 功能描述: 巡店拜访--进销存，问货源Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class InvoicingAskGoodsAdapter extends BaseAdapter 
                            implements OnClickListener, OnFocusChangeListener {

    private final String TAG = "InvoicingAskGoodsAdapter";
    private Activity context;
    private List<InvoicingStc> dataLst;
    ListView askGoodsLv;
    ListView checkGoodsLv;
    
    private String termId;
    private String visitId;
    private InvoicingCheckGoodsAdapter checkAdapter;
    private int delPosition = -1;
    private AlertDialog dialog;
    private String seeFlag;

    public InvoicingAskGoodsAdapter(Activity context, String seeFlag, List<InvoicingStc> 
              dataLst, String termId, String visitId, InvoicingCheckGoodsAdapter checkAdapter,ListView askGoodsLv,ListView checkGoodsLv) {
        this.context = context;
        this.seeFlag = seeFlag;
        this.dataLst = dataLst;
        this.termId = termId;
        this.visitId = visitId;
        this.checkAdapter = checkAdapter;
        this.askGoodsLv=askGoodsLv;
        this.checkGoodsLv=checkGoodsLv;
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
        Log.d("ask ", position+"  "+DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:SSS"));
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.invoicing_askgoods_lvitem, null);
            holder.productNameTv = (TextView)convertView.findViewById(R.id.askgoods_tv_proname);
            holder.channelPriceEt = (EditText)convertView.findViewById(R.id.askgoods_et_channelprice);
            holder.sellPriceEt = (EditText)convertView.findViewById(R.id.askgoods_et_sellproce);
            holder.agencyTv = (TextView)convertView.findViewById(R.id.askgoods_tv_agency);
            holder.deleteIv = (Button)convertView.findViewById(R.id.askgoods_bt_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        InvoicingStc  item = dataLst.get(position);
        holder.deleteIv.setTag(position);
        if(ConstValues.FLAG_1.equals(seeFlag)){
            //属于查看状态，这里不进行操作
        }else{
            //不属于查看状态 ，点击事件有效
            holder.deleteIv.setOnClickListener(this);
        }
        holder.productNameTv.setHint(item.getProId());
        holder.productNameTv.setText(item.getProName());
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
        //holder.channelPriceEt.setOnFocusChangeListener(this);
        if (ConstValues.FLAG_0.equals(item.getSellPrice())) {
            holder.sellPriceEt.setHint(item.getSellPrice());
            holder.sellPriceEt.setText(null);
        } else if(!CheckUtil.isBlankOrNull(item.getSellPrice())){
            holder.sellPriceEt.setText(item.getSellPrice());
        }else{
            holder.sellPriceEt.setHint(R.string.hit_input);
            holder.sellPriceEt.setText(null);
        }
        holder.sellPriceEt.setTag(position);
        //holder.sellPriceEt.setOnFocusChangeListener(this);
        holder.agencyTv.setHint(item.getAgencyId());
        holder.agencyTv.setText(item.getAgencyName());
        
        return convertView;
    }

    private class ViewHolder {
        private TextView productNameTv;
        private EditText channelPriceEt;
        private EditText sellPriceEt;
        private TextView agencyTv;
        private Button deleteIv;
    }

    /**
     * 单击删除
     */
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
        case R.id.askgoods_bt_delete:
            DbtLog.logUtils(TAG,"删除");
            if (dialog == null || !dialog.isShowing()) {
            	delPosition = (Integer)v.getTag();
            	final InvoicingStc item = dataLst.get(delPosition);
                Builder builder = new Builder(context);
                builder.setTitle(R.string.dialog_title);
                builder.setMessage("是否删除"+'"'+item.getProName()+'"');
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.dialog_bt_sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DbtLog.logUtils(TAG,"删除产品:"+item.getProName()+"、产品key："+item.getProId());
                        // 删除对应的数据
                        if (!CheckUtil.isBlankOrNull(item.getRecordId())) {
                            DbtLog.logUtils(TAG,"解除供货关系");
                            InvoicingService service = new InvoicingService(context, null);
                            boolean isFlag=service.deleteSupply(item.getRecordId(), termId, visitId, item.getProId());
                            if(isFlag){
                                // 删除界面listView相应行
                                dataLst.remove(delPosition);
                                notifyDataSetChanged();
                                checkAdapter.setDelPosition(delPosition);
                                checkAdapter.notifyDataSetChanged();
                                ViewUtil.setListViewHeight(askGoodsLv);
                                ViewUtil.setListViewHeight(checkGoodsLv);
                            }else{
                                Toast.makeText(context, "删除产品失败!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            // 删除界面listView相应行
                            dataLst.remove(delPosition);
                            notifyDataSetChanged();
                            checkAdapter.setDelPosition(delPosition);
                            checkAdapter.notifyDataSetChanged();
                            ViewUtil.setListViewHeight(askGoodsLv);
                            ViewUtil.setListViewHeight(checkGoodsLv);
                        }
                        
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.dialog_bt_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DbtLog.logUtils(TAG,"删除取消");
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
    
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    	
    	boolean hasFocus2 = hasFocus;
    	
        EditText et = (EditText)v;
        int position = (Integer)v.getTag();
        if (position == delPosition) return;
        if (position > delPosition && delPosition != -1) {
            position = position -1;
        }
        if (position > -1) {
            InvoicingStc stc = dataLst.get(position);
            String content = et.getText().toString();
            switch (et.getId()) {
            case R.id.askgoods_et_channelprice:
            	// 渠道价-判断小数点
            	if(".".equals(content)){//
            		stc.setChannelPrice("0.0");
            		et.setText("0.0");// 页面
            	}
            	else if(content.length()>1&&content.endsWith(".")){
            		stc.setChannelPrice(content+"0");
            		et.setText(content+"0");// 页面
            	}
            	else if(content.length()>1&&content.startsWith(".")){
            		/*stc.setChannelPrice("0"+content);
            		et.setText("0"+content);// 页面*/     
            		// 以小数点开头,截取小数点后两位
            		Double d = Double.parseDouble("0"+content);
        			/*String num = new DecimalFormat("#0.00").format(d);
            		stc.setChannelPrice(num);
            		et.setText(num);*/
            		
            		/*d=d+0.000001;
        			String count = String.valueOf(d);
        			String[] split = count.split("\\.");*/
        			String[] split = String.valueOf(d+0.000001).split("\\.");
        			String num = split[0]+"."+split[1].substring(0, 2);
            		
                    stc.setChannelPrice(num);
            		et.setText(num);
                    
            	}
            	/*else if("".equals(content)){
            		stc.setChannelPrice("0");
            		et.setText("0");// 页面
            	}*/
            	else{
            		/*stc.setChannelPrice(content);
            		et.setText(content);// 页面
*/            		
            		
            		
            		if (content!=null&&(!"".equals(content))) {
            			
            			Double d = Double.parseDouble(content);
            			/*String num = new DecimalFormat("#0.00").format(d);
                		stc.setChannelPrice(num);
                		et.setText(num);*/
            			
            			//String count = String.valueOf(d+0.000001);
            			String[] split = String.valueOf(d+0.000001).split("\\.");
            			String num = split[0]+"."+split[1].substring(0, 2);
                		
                        stc.setChannelPrice(num);
                		et.setText(num);
                		
            		}else{
            			stc.setChannelPrice(content);
                		et.setText(content);
            		}
            		
            		
            	}
                break;
                
            case R.id.askgoods_et_sellproce:
            	
            	// 零售价-判断小数点
            	if(".".equals(content)){//
            		stc.setSellPrice("0.0");
            		et.setText("0.0");// 页面
            	}
            	else if(content.length()>1&&content.endsWith(".")){
            		stc.setSellPrice(content+"0");
            		et.setText(content+"0");// 页面
            	}
            	else if(content.length()>1&&content.startsWith(".")){
            		/*stc.setSellPrice("0"+content);
            		et.setText("0"+content);// 页面*/
            		// 以小数点开头,截取小数点后两位
            		Double d = Double.parseDouble("0"+content);
        			//String num = new DecimalFormat("#0.00").format(d);
        			
        			String[] split = String.valueOf(d+0.000001).split("\\.");
        			String num = split[0]+"."+split[1].substring(0, 2);
            		stc.setChannelPrice(num);
            		et.setText(num);
            	}
            	/*else if("".equals(content)){
            		stc.setSellPrice("0");
            		et.setText("0");// 页面
            	}*/
            	else{
            		/*stc.setSellPrice(content);
            		et.setText(content);// 页面
*/            		
            		if (content!=null&&(!"".equals(content))) {
            			
            			Double d = Double.parseDouble(content);
            			//String num = new DecimalFormat("#0.00").format(d);
            			String[] split = String.valueOf(d+0.000001).split("\\.");
            			String num = split[0]+"."+split[1].substring(0, 2);
                		stc.setChannelPrice(num);
                		et.setText(num);
            		}else{
            			stc.setChannelPrice(content);
                		et.setText(content);
            		}
            	}
            	
                
                break;
                
            default:
    
                break;
            }
        }
    }

    public int getDelPosition() {
        return delPosition;
    }

    public void setDelPosition(int delPosition) {
        this.delPosition = delPosition;
    }
}

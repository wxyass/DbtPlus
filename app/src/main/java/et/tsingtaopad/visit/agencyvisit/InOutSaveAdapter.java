package et.tsingtaopad.visit.agencyvisit;

import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.visit.agencyvisit.domain.InOutSaveStc;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：InOutSaveAdapter.java</br> 作者：@吴欣伟 </br>
 * 创建时间：2013/11/26</br> 功能描述: 进销存台账Adapter</br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
public class InOutSaveAdapter extends BaseAdapter implements OnFocusChangeListener {

    private Context context;
    private List<InOutSaveStc> dataLst;
    private int index = -1;
    private int editFocusViewID;
    
    public InOutSaveAdapter(Context context, List<InOutSaveStc> dataLst) {
        this.context = context;
       this.dataLst = dataLst;
    }
    
    @Override
    public int getCount() {

        if (CheckUtil.IsEmpty(this.dataLst)) {
            return 0;

        } else {
            return this.dataLst.size();
        }
    }

    @Override
    public Object getItem(int position) {

        if (CheckUtil.IsEmpty(this.dataLst)) {
            return null;

        } else {
            return this.dataLst.get(position);
        }
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.agencyvisit_inoutsave_lvitem, null);
            holder = new ViewHolder();
            holder.strProName = (TextView) convertView.findViewById(R.id.inoutsave_tv_lvitem_proname);//品种
            
            holder.pretorenum = (EditText) convertView.findViewById(R.id.inoutsave_et_lvitem_laststorenum);//初期库存
            holder.distribution = (EditText) convertView.findViewById(R.id.inoutsave_et_lvitem_indirectnum);//转入转出(分销量)
            holder.in = (EditText) convertView.findViewById(R.id.inoutsave_et_lvitem_innum);//进货量(到货确认)
            holder.direct = (EditText) convertView.findViewById(R.id.inoutsave_et_lvitem_directnum);//每日赠酒销量
            
            holder.selfsales = (EditText) convertView.findViewById(R.id.inoutsave_et_lvitem_selfsales);// 协议店销量
            holder.unselfsales = (EditText) convertView.findViewById(R.id.inoutsave_et_lvitem_unselfsales);// 非协议店销量
            holder.othersales = (EditText) convertView.findViewById(R.id.inoutsave_et_lvitem_othersales);// 其他销量
            holder.stock = (EditText) convertView.findViewById(R.id.inoutsave_et_lvitem_storenum);//期末库存
            
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        InOutSaveStc info = dataLst.get(position);

        holder.in.setTag(position);
        holder.direct.setTag(position);
        holder.distribution.setTag(position);
        holder.stock.setTag(position);
        
        holder.selfsales.setTag(position);
        holder.unselfsales.setTag(position);
        holder.othersales.setTag(position);

        //绑定事件
        holder.in.addTextChangedListener(new MyTextWathcer(R.id.inoutsave_et_lvitem_innum, holder.in,holder.stock));
        holder.direct.addTextChangedListener(new MyTextWathcer(R.id.inoutsave_et_lvitem_directnum, holder.direct,holder.stock));
        holder.distribution.addTextChangedListener(new MyTextWathcer(R.id.inoutsave_et_lvitem_indirectnum, holder.distribution,holder.stock));
        holder.stock.addTextChangedListener(new MyTextWathcer(R.id.inoutsave_et_lvitem_storenum, holder.stock,holder.stock));
        
        holder.selfsales.addTextChangedListener(new MyTextWathcer(R.id.inoutsave_et_lvitem_selfsales, holder.selfsales,holder.stock));
        holder.unselfsales.addTextChangedListener(new MyTextWathcer(R.id.inoutsave_et_lvitem_unselfsales, holder.unselfsales,holder.stock));
        holder.othersales.addTextChangedListener(new MyTextWathcer(R.id.inoutsave_et_lvitem_othersales, holder.othersales,holder.stock));
        

        holder.in.setOnFocusChangeListener(this);
        holder.direct.setOnFocusChangeListener(this);
        holder.distribution.setOnFocusChangeListener(this);
        holder.stock.setOnFocusChangeListener(this);
        
        holder.selfsales.setOnFocusChangeListener(this);
        holder.unselfsales.setOnFocusChangeListener(this);
        holder.othersales.setOnFocusChangeListener(this);

        //显示数据
        holder.strProName.setText(info.getProName());
        if(info.getInnum() == null) {
            info.setInnum(0.0);
        }
        if(info.getDirecout() == null) {
            info.setDirecout(0.0);
        }
        if(info.getIndirecout() == null) {
            info.setIndirecout(0.0);
        }
        if(info.getStorenum() == null) {//期初库存
            info.setStorenum(0.0);
        }
        if(info.getPrestorenum() == null){//期末库存
        	info.setPrestorenum(0.0);
        }
        
        if(info.getSelfsales() == null) {
            info.setSelfsales(0.0);
        }
        if(info.getUnselfsales() == null) {
            info.setUnselfsales(0.0);
        }
        if(info.getOthersales() == null) {
            info.setOthersales(0.0);
        }
        
        if((info.getInnum().intValue()) == 0 && info.getDirecout().intValue() == 0 && 
              info.getIndirecout().intValue() == 0 && 
            		  info.getSelfsales().intValue() == 0 && 
            				  info.getUnselfsales().intValue() == 0 && 
            						  info.getOthersales().intValue() == 0 && 
                                           info.getStorenum().intValue() == 0 ){
            holder.in.setText("");
            holder.direct.setText("");
            holder.distribution.setText("");
            holder.stock.setText("");
            
            holder.selfsales.setText("");
            holder.unselfsales.setText("");
            holder.othersales.setText("");
            
            if(info.getPrestorenum().intValue()  == 0){
                holder.pretorenum.setText("");
            }else{
                double fristStorenum = info.getPrestorenum();
                holder.pretorenum.setText(Integer.toString((int)fristStorenum));
            }
        }else {
            double in = info.getInnum();
            holder.in.setText(Integer.toString((int)in));
            double direct = info.getDirecout();
            holder.direct.setText(Integer.toString((int)direct));
            double distribution = info.getIndirecout();
            holder.distribution.setText(Integer.toString((int)distribution));
            double stock = info.getStorenum();
            holder.stock.setText(Integer.toString((int)stock));
            double fristStorenum = info.getPrestorenum();
            holder.pretorenum.setText(Integer.toString((int)fristStorenum));
            
            double selfsales = info.getSelfsales();
            holder.selfsales.setText(Integer.toString((int)selfsales));
            double unselfsales = info.getUnselfsales();
            holder.unselfsales.setText(Integer.toString((int)unselfsales));
            double othersales = info.getOthersales();
            holder.othersales.setText(Integer.toString((int)othersales));
        }
        
        //listview  上edittext 焦点还原
        if (index != position) {
            holder.in.clearFocus();
            holder.direct.clearFocus();
            holder.distribution.clearFocus();
            holder.stock.clearFocus();
            
            holder.selfsales.clearFocus();
            holder.unselfsales.clearFocus();
            holder.othersales.clearFocus();
            

        } else {

            switch (editFocusViewID) {
            case R.id.inoutsave_et_lvitem_innum:
                holder.in.requestFocus();
                break;
            case R.id.inoutsave_et_lvitem_directnum:
                holder.direct.requestFocus();

                break;
            case R.id.inoutsave_et_lvitem_indirectnum:
                holder.distribution.requestFocus();
                break;
            case R.id.inoutsave_et_lvitem_storenum:
                holder.stock.requestFocus();
                break;
                
            case R.id.inoutsave_et_lvitem_selfsales:
                holder.selfsales.requestFocus();
                break;
            case R.id.inoutsave_et_lvitem_unselfsales:
                holder.unselfsales.requestFocus();
                break;
            case R.id.inoutsave_et_lvitem_othersales:
                holder.othersales.requestFocus();
                break;

            default:
                break;
            }
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView strProName;
        private EditText in;
        private EditText direct;
        private EditText distribution;
        private EditText stock;
        private EditText pretorenum;
        
        private EditText selfsales;//协议店销量
        private EditText unselfsales;//非协议店销量
        private EditText othersales;//其他销量
    }

    private class MyTextWathcer implements TextWatcher {
        private EditText edittext;
        private EditText stockitem;
        private int viewID;

        public MyTextWathcer(int viewID, EditText edittext,EditText store) {
            this.edittext = edittext;
            this.stockitem = store;
            this.viewID = viewID;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String content = s.toString();
            if("".equals(content)||"0.0".equals(content)) {
                content = "0";
            }
            
            int shuzhi = FunUtil.doStoI(content);
            double contentNumber = (double)shuzhi;
            //Double contentNumber = Double.parseDouble(content);
            int position = (Integer) edittext.getTag();
            InOutSaveStc info = dataLst.get(position);
            double num = 0;
            
            switch (viewID) {
            //转入转出(分销量)
            case R.id.inoutsave_et_lvitem_indirectnum:
                info.setIndirecout(contentNumber);
                num = plusStorenum(info.getPrestorenum(), info.getIndirecout(), info.getInnum(), info.getDirecout(), 
                		info.getSelfsales() , info.getUnselfsales() , info.getOthersales());
                info.setStorenum(num);
                if(0 == num){
                	stockitem.setText("");
                }else{
                	stockitem.setText(Integer.toString((int)num));
                }
                break;
            //进货量(到货确认)
            case R.id.inoutsave_et_lvitem_innum:
                info.setInnum(contentNumber);
                num = plusStorenum(info.getPrestorenum(), info.getIndirecout(), info.getInnum(), info.getDirecout(), 
                		info.getSelfsales() , info.getUnselfsales() , info.getOthersales());
                
                info.setStorenum(num);
                if(0 == num){
                	stockitem.setText("");
                }else{
                	stockitem.setText(Integer.toString((int)num));
                }
                break;
            //每日赠酒销量
            case R.id.inoutsave_et_lvitem_directnum:
                info.setDirecout(contentNumber);
                num = plusStorenum(info.getPrestorenum(), info.getIndirecout(), info.getInnum(), info.getDirecout(), 
                		info.getSelfsales() , info.getUnselfsales() , info.getOthersales());
                info.setStorenum(num);
                if(0 == num){
                	stockitem.setText("");
                }else{
                	stockitem.setText(Integer.toString((int)num));
                }
                break;
            
                
                // 协议店销量
            case R.id.inoutsave_et_lvitem_selfsales:
            	info.setSelfsales(contentNumber);
            	num = plusStorenum(info.getPrestorenum(), info.getIndirecout(), info.getInnum(), info.getDirecout(), 
                		info.getSelfsales() , info.getUnselfsales() , info.getOthersales());
                info.setStorenum(num);
                if(0 == num){
                	stockitem.setText("");
                }else{
                	stockitem.setText(Integer.toString((int)num));
                }
            	break;
            	//非协议店销量
            case R.id.inoutsave_et_lvitem_unselfsales:
                info.setUnselfsales(contentNumber);
                num = plusStorenum(info.getPrestorenum(), info.getIndirecout(), info.getInnum(), info.getDirecout(), 
                		info.getSelfsales() , info.getUnselfsales() , info.getOthersales());
                info.setStorenum(num);
                if(0 == num){
                	stockitem.setText("");
                }else{
                	stockitem.setText(Integer.toString((int)num));
                }
                break;
                //其他销量
            case R.id.inoutsave_et_lvitem_othersales:
                info.setOthersales(contentNumber);
                num = plusStorenum(info.getPrestorenum(), info.getIndirecout(), info.getInnum(), info.getDirecout(), 
                		info.getSelfsales() , info.getUnselfsales() , info.getOthersales());
                info.setStorenum(num);
                if(0 == num){
                	stockitem.setText("");
                }else{
                	stockitem.setText(Integer.toString((int)num));
                }
                break;
                
              //期末库存
            case R.id.inoutsave_et_lvitem_storenum:
                //info.setStorenum(contentNumber);
                num = plusStorenum(info.getPrestorenum(), info.getIndirecout(), info.getInnum(), info.getDirecout(), 
                		info.getSelfsales() , info.getUnselfsales() , info.getOthersales());
                info.setStorenum(num);
                
                break;

            default:

                break;
            }

        }
    }
    
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            editFocusViewID = v.getId();
            index = (Integer) v.getTag();
            ((EditText) v).setSelection(0);
            ((EditText) v).isFocused();
        }
    }
    
    // 期末库存计算规则
    private double plusStorenum(double prestorenum,double indirecout,double innum,double direcout,double selfsales,double unselfsales,double othersales){
		
    	double storenum = FunUtil.isZeroSetZero(prestorenum)
		+FunUtil.isZeroSetZero(indirecout)
				+FunUtil.isZeroSetZero(innum)
		-FunUtil.isZeroSetZero(direcout) 
		-FunUtil.isZeroSetZero(selfsales)
		-FunUtil.isZeroSetZero(unselfsales)
		-FunUtil.isZeroSetZero(othersales);
    	return storenum;
    	
    }

}

package et.tsingtaopad.operation.workplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.operation.workplan.domain.VpLvItemStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.visit.shopvisit.term.TermListService;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;

/**
 * 
 * 项目名称：营销移动智能工作平台 </br> 文件名：MakePlanViewPagerAdapter.java</br> 作者：wangshiming
 * </br> 创建时间：2014年2月11日</br> 功能描述: viewpager中listview的adapter</br> 版本 V
 * 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人 修改版本</br>
 */
public class MakePlanViewPagerAdapter extends BaseAdapter
{
    private final String TAG = "MakePlanViewPagerAdaptera";
    private LayoutInflater inflater;
    private Context context;
    private PopupWindow pop;
    private String checkUse;
    private boolean isPromotion;
    private boolean isProductTerminal;
    private boolean isother;
    private boolean isDaoju;// 道具生动化
    private boolean isChanpinhua;// 产品生动化
    private boolean isBingdonghua;// 冰冻化
    private boolean isGezhonghua;
    private boolean isdingdan;
    private boolean isjingpintuijin;
    private List<String> lineKeys;
    private String checkkey; 
    private boolean isPreview_plan;
    private List<VpLvItemStc> list = new ArrayList<VpLvItemStc>();// 当前adapter的数据
    private List<MstTermListMStc> terminals = new ArrayList<MstTermListMStc>();
    private List<MstTermListMStc> terminals1 = new ArrayList<MstTermListMStc>();
    private List<String> blankDeleteIds = new ArrayList<String>();//保存需要删除的id，空白终端 编辑状态 可以删除 需要把之前保存在数据库的数据删除掉 
    private MyTextWatcher mWatcher;
    /***
     * key:listview下标
     * value:下标对应终端列表集合
     */
    private Map<String, List<MstTermListMStc>> promoTerminals = new HashMap<String, List<MstTermListMStc>>();//促销活动每个活动的活动终端（）
    /**
     * k-v
     * k  k 计划采集项主键，，之前用position作为主键 但是空白终端可编辑造成position变动 对应的回显list的数据出错
     * v List<String> 选择的终端的id
     */
    private Map<String, List<String>> selectTerminalsMap = new HashMap<String, List<String>>();// 记录每个item选择的List<String>

    /**
     * 
     * @param context
     * @param list
     *            当前adapter的数据
     * @param checkkey
     * @param lineKeys
     *            查询当前线路下的终端 弹出poplist用的数据
     * @param isPreview_plan
     * @param isPromotion
     */
    public MakePlanViewPagerAdapter(Context context, List<VpLvItemStc> list, String checkkey, List<String> lineKeys, boolean isPreview_plan, boolean isProductTerminal, boolean isPromotion,boolean isother,boolean isGezhonghua,boolean isjingpintuijin,boolean isDaoju,boolean isChanpinhua,boolean isBingdonghua,boolean isdingdan)
    {
        this.context = context;
        this.isPreview_plan = isPreview_plan;
        this.list = list;
        this.isPromotion = isPromotion;
        this.isother = isother;
        this.isDaoju = isDaoju;
        this.isChanpinhua = isChanpinhua;
        this.isBingdonghua = isBingdonghua;
        this.isGezhonghua = isGezhonghua;
        this.isjingpintuijin = isjingpintuijin;
        this.isdingdan = isdingdan;
        this.lineKeys = lineKeys;
        this.checkkey = checkkey;
        this.isProductTerminal = isProductTerminal;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        termListService = new TermListService(context);
        //if (!CheckUtil.IsEmpty(lineKeys) && !isPromotion && !isjingpintuijin&&!isChanpinhua&&!isBingdonghua)// 具体到每个产品需要重新获取terminals(比如:促销活动,竞品瓦解)
        if (!CheckUtil.IsEmpty(lineKeys) && !isPromotion &&!isChanpinhua&&!isBingdonghua)// 具体到每个产品需要重新获取terminals(比如:促销活动,竞品瓦解)
        {
            terminals = termListService.getTerMinalVisitResult(checkkey, lineKeys, null, isProductTerminal, isPromotion, null, isother,isjingpintuijin,isDaoju,isChanpinhua,isBingdonghua);// 采集计划用只要查询
            // 一次
            // 不需要用到活动主键只传null
        }
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int arg0)
    {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    ViewHolder holder = null;
    TerminalSelectAdapter popAdapter;
    private TermListService termListService;
    
    private Integer index = -1; 

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        if (convertView == null)
        {

            convertView = inflater.inflate(R.layout.operation_makeplan_viewpagger_lvitem, null);
            holder = new ViewHolder();
            holder.tv_product = (TextView) convertView.findViewById(R.id.vp_tv_product);// 产品名称
            holder.vp_tv_proname = (TextView) convertView.findViewById(R.id.vp_tv_proname);// 产品名称 
            holder.vp_v_deve = (View) convertView.findViewById(R.id.vp_v_deve);// 分割线
            holder.img_delete = (ImageView) convertView.findViewById(R.id.img_delete);
            holder.delete_devider = (View) convertView.findViewById(R.id.delete_devider);
            holder.tv_num = (TextView) convertView.findViewById(R.id.vp_tv_num);
            holder.et_num = (EditText) convertView.findViewById(R.id.vp_et_num);
            holder.tv_terminal = (TextView) convertView.findViewById(R.id.date_tv_terminal);
            holder.tv_terminalrl = (RelativeLayout) convertView.findViewById(R.id.rl);// 终端整体
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        final VpLvItemStc info = list.get(position);
        if (isProductTerminal)// 空白终端
        {
            holder.img_delete.setVisibility(View.VISIBLE);
            holder.delete_devider.setVisibility(View.VISIBLE);
        }
        if (isdingdan)// 订单目标
        {
        	holder.img_delete.setVisibility(View.VISIBLE);
        	holder.delete_devider.setVisibility(View.VISIBLE);
        	holder.tv_num.setVisibility(View.GONE);
        	//holder.tv_terminal.setVisibility(View.GONE);
        	holder.tv_terminalrl.setVisibility(View.GONE);
        	holder.et_num.setVisibility(View.VISIBLE);
        	
        }
        if (isGezhonghua) // 各种化
        {
        	holder.img_delete.setVisibility(View.VISIBLE);
        	holder.delete_devider.setVisibility(View.VISIBLE);
        	holder.vp_tv_proname.setVisibility(View.VISIBLE);
        	holder.vp_v_deve.setVisibility(View.VISIBLE);
        }
        if(isDaoju){// 道具生动化
        	holder.img_delete.setVisibility(View.GONE);
        	holder.delete_devider.setVisibility(View.GONE);
        	holder.vp_tv_proname.setVisibility(View.GONE);
        	holder.vp_v_deve.setVisibility(View.GONE);
        }
        if (isjingpintuijin) // 竞品推进计划
        {
        	holder.img_delete.setVisibility(View.VISIBLE);
        	holder.delete_devider.setVisibility(View.VISIBLE);
        	holder.vp_tv_proname.setVisibility(View.VISIBLE);
        	holder.vp_v_deve.setVisibility(View.VISIBLE);
        }
        
        // 数据绑定
        if (CheckUtil.isBlankOrNull(info.getTerm()))
        {
            holder.tv_terminal.setText(context.getString(R.string.makeplan_hint_terminal));
        }
        else
        {
            holder.tv_terminal.setText(info.getTerm());
        }

        holder.tv_product.setText(info.getName());
        holder.vp_tv_proname.setText(info.getProname());
        if (info.getNum().equals("0"))
        {
            holder.tv_num.setText(String.valueOf(0));
        }
        else
        {
            holder.tv_num.setText(info.getNum());
        }

        if (isPreview_plan)
        {
            holder.tv_terminal.setEnabled(false);
            holder.img_delete.setVisibility(View.GONE);
        }
        
        // edittext触摸时获取位置
        holder.et_num.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {  
					index=position;
	            }
				return false;
			}  
              
        });
        
        holder.et_num.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				//设置焦点监听，当获取到焦点的时候才给它设置内容变化监听解决卡的问题
				EditText et=(EditText) v;
	            if(mWatcher==null){
	                mWatcher=new MyTextWatcher(holder);
	            }
	            if(hasFocus){
	                et.addTextChangedListener(mWatcher);//设置edittext内容监听
	            }else {
	                et.removeTextChangedListener(mWatcher);
	            }
			}
        });
        
        holder.et_num.clearFocus();//防止点击以后弹出键盘，重新getview导致的焦点丢失
        
        if (index != -1 && index == position) {
            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
        	holder.et_num.requestFocus();
        }
        
        // 初始化订单目标数量 //这一定要放在clearFocus()之后，否则最后输入的内容在拉回来时会消失
        if (CheckUtil.isBlankOrNull(info.getDingdannum())||"null".equals(info.getDingdannum()))
        {
        	holder.et_num.setText("");
        }
        else
        {
        	holder.et_num.setText(list.get(position).getDingdannum());
        }
        
        //空白终端产品删除
        holder.img_delete.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Builder builder = new Builder(context);
                builder.setTitle("提示");
                builder.setMessage("是否删除" + '"' + info.getName() + '"');
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        
                        
                        	VpLvItemStc vpLvItemStc = list.get(position);

                            list.remove(position);
                            selectTerminalsMap.remove(info.getPcolitemkey());
                            promoTerminals.remove(info.getPcolitemkey());
                            blankDeleteIds.add(info.getPcolitemkey());//MstPlancollectionInfo的主键
                            MakePlanViewPagerAdapter.this.notifyDataSetChanged();
                            //将商品还原到队列
                        	MstProductM product = new MstProductM();
                            product.setProductkey(vpLvItemStc.getKey());
                            product.setProname(vpLvItemStc.getName());
                            ((MakePlanActivity) context).addProductList(product);
                        
                    }
                });

                builder.setNegativeButton("取消", null);
                builder.create().show();
                //builder.show();
            }
        });
        // 请选择终端
        holder.tv_terminal.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                final int currentPosition = position;
                final VpLvItemStc item = list.get(currentPosition);
                View popupView = inflater.inflate(R.layout.operation_popupwindow, null);
                pop = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
                pop.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.transparent));
                Button btn_back = (Button) popupView.findViewById(R.id.list_pop_btn_back);
                Button btn_ok = (Button) popupView.findViewById(R.id.list_pop_btn_ok);
                TextView tv_title = (TextView) popupView.findViewById(R.id.pop_tv_title);
                tv_title.setText(context.getString(R.string.makeplan_terminalselect));
                ListView listView = (ListView) popupView.findViewById(R.id.pop_lv);
                if (isPromotion || isProductTerminal)// 促销活动 空白终端 有效铺货 有效销售
                {
                    String promotKey = info.getKey();
                    List<MstTermListMStc> promoTerminal = promoTerminals.get(info.getPcolitemkey());
                    if (CheckUtil.IsEmpty(promoTerminal))
                    {
                        promoTerminal = new TermListService(context).getTerMinalVisitResult(checkkey, lineKeys, promotKey, isProductTerminal, isPromotion, info.getKey(), isother,isjingpintuijin,isDaoju,isChanpinhua,isBingdonghua);
                        promoTerminals.put(info.getPcolitemkey(), promoTerminal);
                    }

                    List<String> popwListViewSelect;
                    if (selectTerminalsMap.get(info.getPcolitemkey()) == null)
                    {
                        popwListViewSelect = new ArrayList<String>();
                    }
                    else
                    {
                        popwListViewSelect = new ArrayList<String>(selectTerminalsMap.get(info.getPcolitemkey()));
                    }

                    popAdapter = new TerminalSelectAdapter(context, promoTerminal, popwListViewSelect,0);
                    listView.setAdapter(popAdapter);
                }
                // 竞品瓦解
                else if(isjingpintuijin){
                	/*String promotKey = info.getKey();
                    List<MstTermListMStc> promoTerminal = promoTerminals.get(info.getPcolitemkey());
                    if (CheckUtil.IsEmpty(promoTerminal))
                    {
                        promoTerminal = new TermListService(context).getTerMinalVisitResult(checkkey, lineKeys, promotKey, isProductTerminal, isPromotion, info.getKey(), isother,isjingpintuijin);
                        promoTerminals.put(info.getPcolitemkey(), promoTerminal);
                    }*/
                	
                	String jingpinKey = info.getKey();
                	List<MstTermListMStc> jingpinTerminal = new TermListService(context).getTerMinalVisitResult(checkkey, lineKeys, jingpinKey, isProductTerminal, isPromotion, info.getPronamekey(), isother,isjingpintuijin,isDaoju,isChanpinhua,isBingdonghua);
                    

                    List<String> popwListViewSelect;
                    if (selectTerminalsMap.get(info.getPcolitemkey()) == null)
                    {
                        popwListViewSelect = new ArrayList<String>();
                    }
                    else
                    {
                        popwListViewSelect = new ArrayList<String>(selectTerminalsMap.get(info.getPcolitemkey()));
                    }

                    popAdapter = new TerminalSelectAdapter(context, jingpinTerminal, popwListViewSelect,0);
                    listView.setAdapter(popAdapter);
                }
                // 产品生动化,冰冻化
                else if(isChanpinhua||isBingdonghua){
                	/*String promotKey = info.getKey();
                    List<MstTermListMStc> promoTerminal = promoTerminals.get(info.getPcolitemkey());
                    if (CheckUtil.IsEmpty(promoTerminal))
                    {
                        promoTerminal = new TermListService(context).getTerMinalVisitResult(checkkey, lineKeys, promotKey, isProductTerminal, isPromotion, info.getKey(), isother,isjingpintuijin);
                        promoTerminals.put(info.getPcolitemkey(), promoTerminal);
                    }*/
                	
                	String jingpinKey = info.getKey();
                	List<MstTermListMStc> jingpinTerminal = new TermListService(context).getTerMinalVisitResult(checkkey, lineKeys, jingpinKey, isProductTerminal, isPromotion, info.getPronamekey(), isother,isjingpintuijin,isDaoju,isChanpinhua,isBingdonghua);
                	
                	
                	List<String> popwListViewSelect;
                	if (selectTerminalsMap.get(info.getPcolitemkey()) == null)
                	{
                		popwListViewSelect = new ArrayList<String>();
                	}
                	else
                	{
                		popwListViewSelect = new ArrayList<String>(selectTerminalsMap.get(info.getPcolitemkey()));
                	}
                	
                	popAdapter = new TerminalSelectAdapter(context, jingpinTerminal, popwListViewSelect,0);
                	listView.setAdapter(popAdapter);
                }
                else if(isother){
                	// 曾经选择的数据
                	List<String> popwListViewSelect;
                	//terminals1 = termListService.getTerMinalVisitResult(checkkey, lineKeys, null, isProductTerminal, isPromotion, null, isother);// 采集计划用只要查询
                	
                	if (selectTerminalsMap.get(info.getPcolitemkey()) == null)
                	{
                		popwListViewSelect = new ArrayList<String>();
                	}
                	else
                	{
                		popwListViewSelect = new ArrayList<String>(selectTerminalsMap.get(info.getPcolitemkey()));
                	}
                	popAdapter = new TerminalSelectAdapter(context, terminals, popwListViewSelect,1);
                	listView.setAdapter(popAdapter);
                }
                
                
                
                else
                {
                    // 曾经选择的数据
                    List<String> popwListViewSelect;
                    if (selectTerminalsMap.get(info.getPcolitemkey()) == null)
                    {
                        popwListViewSelect = new ArrayList<String>();
                    }
                    else
                    {
                        popwListViewSelect = new ArrayList<String>(selectTerminalsMap.get(info.getPcolitemkey()));
                    }
                    popAdapter = new TerminalSelectAdapter(context, terminals, popwListViewSelect,2);
                    listView.setAdapter(popAdapter);
                }

                // 返回清空list 关闭popwindow
                btn_back.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        pop.dismiss();
                    }
                });
                // 确定
                btn_ok.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {

                        List<String> popwListViewSelectS = popAdapter.getTerminalSelect();
                        //根据主键查询名称
                        selectTerminalsMap.put(item.getPcolitemkey(), popwListViewSelectS);
                        
                        // 问题可能出在这个判断上
                        if (!CheckUtil.IsEmpty(popwListViewSelectS))
                        {
                            String terminals = termListService.getTerminalNames(popwListViewSelectS);

                            holder.tv_terminal.setText(terminals);
                            holder.tv_num.setText(String.valueOf(popwListViewSelectS.size()));

                            item.setName(list.get(currentPosition).getName());
                            item.setNum(String.valueOf(popwListViewSelectS.size()));
                            item.setTerm(terminals);
                            item.setKey(list.get(currentPosition).getKey());

                        }
                        else
                        {
                        	String terminals = termListService.getTerminalNames(popwListViewSelectS);

                            holder.tv_terminal.setText(terminals);
                            holder.tv_num.setText(String.valueOf(popwListViewSelectS.size()));
                        	
                        	
                            VpLvItemStc item = list.get(currentPosition);
                            item.setNum("0");
                            item.setTerm("");
                        }
                        MakePlanViewPagerAdapter.this.notifyDataSetChanged();
                        pop.dismiss();
                    }

                });

                pop.showAtLocation(tv_title, Gravity.CENTER, 0, 0);

            }
        });

        return convertView;
    }

    class ViewHolder
    {
    	TextView vp_tv_proname;
    	View vp_v_deve;
        TextView tv_product;
        ImageView img_delete;
        View delete_devider;
        TextView tv_num;
        EditText et_num;
        TextView tv_terminal;
        RelativeLayout tv_terminalrl;
    }

    public Map<String, List<String>> getSelectTerminalsMap()
    {
        return selectTerminalsMap;
    }

    public List<String> getBlankDeleteIds()
    {
        return blankDeleteIds;
    }

    public void setBlankDeleteIds(List<String> blankDeleteIds)
    {
        this.blankDeleteIds = blankDeleteIds;
    }
    
    class MyTextWatcher implements TextWatcher {  
        public MyTextWatcher(ViewHolder holder) {  
            mHolder = holder;  
        }  

        private ViewHolder mHolder;  

        @Override  
        public void onTextChanged(CharSequence s, int start,  
                int before, int count) {  
        }  

        @Override  
        public void beforeTextChanged(CharSequence s, int start,  
                int count, int after) {  
        }  

        @Override  
        public void afterTextChanged(Editable s) {  
            if (s != null && !"".equals(s.toString())) {  
                //int position = (Integer) holder.et_num.getTag(); 
                //String ordernum = holder.et_num.getText().toString();
                VpLvItemStc info = list.get(index);
                info.setDingdannum(s.toString());
            }  
        }  
    }
    
}

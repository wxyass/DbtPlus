package et.tsingtaopad.visit.tomorrowworkrecord;

import java.util.List;

import android.content.Context;
import android.provider.ContactsContract.DataUsageFeedback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import et.tsingtaopad.visit.tomorrowworkrecord.domain.DayWorkDetailStc;


import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
/**
 * 项目名称：营销移动智能工作平台<br>
 * 文件名：TomorrowWorkRecordAdapter.java<br>
 * 作者：@沈潇<br>
 * 创建时间：2013/11/24<br>
 * 功能描述: 每日工作Adapter<br>
 * 版本 V 1.0<br>
 * 修改履历<br>
 * 日期 原因 BUG号 修改人 修改版本<br>
 */
public class TomorrowWorkRecordAdapter extends BaseAdapter {

	private Context context;
	private List<DayWorkDetailStc> dataLst;

	public TomorrowWorkRecordAdapter(Context context, List<DayWorkDetailStc> dataLst) {
		this.context = context;
		this.dataLst = dataLst;
	}

	@Override
	public int getCount() {
	    
		if(CheckUtil.IsEmpty(this.dataLst)) {
		    return 0;
		}else {
		    return dataLst.size();
		}
	}

	@Override
	public Object getItem(int arg0) {
        
        if (CheckUtil.IsEmpty(this.dataLst)) {
            return null;
            
        } else {
            return this.dataLst.get(arg0);
        }
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.visit_tomorrowworkrecord_lvitem, null);
            holder = new ViewHolder();
            holder.routeName = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_line);
            holder.terminalCode = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_terminalcode);
            holder.terminalName = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_terminalname);
            holder.minorchannel = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_minorchannel);
            holder.tlevel = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_tlevel);
            holder.contact = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_contact);
            holder.address = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_address);
            holder.visitdate = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_visitdate);
            holder.visittarget = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_visittarget);
            holder.cmptreaty = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_cmptreaty);
            holder.visituser = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_visituser);
            holder.visitrecord = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_visitrecord);
            holder.status = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_status);
            holder.exetreaty = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_exetreaty);
            holder.ishdistribution = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_ishdistribution);
            holder.iscmpcollapse = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_iscmpcollapse);
            holder.selfoccupancy = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_selfoccupancy);
            holder.proCode = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_procode);
            holder.proName = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_proname);
            holder.purcPrice = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_purcprice);
            holder.retailPrice = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_retailprice);
            holder.agencyName = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_agencyname);
            holder.innum = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_innum);
            holder.visitbSalenum = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_visitbsalenum);
            holder.visitSelfOccu = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_visitselfoccu);
            holder.shopState = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_shopstate);
            holder.proLively = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_prolively);
            holder.toolLively = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_toollively);
            holder.itemName = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_itemname);
            holder.resultNum = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_resultnum);
            holder.changeNum = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_changenum);
            holder.ifvlid = (TextView) convertView.findViewById(R.id.tomorrow_tv_lvitem_ifvlid);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        DayWorkDetailStc stc = dataLst.get(position);
        holder.routeName.setText(stc.getRoutename());
        holder.terminalCode.setText(stc.getTerminalcode());
        holder.terminalName.setText(stc.getTerminalname());
        holder.minorchannel.setText(stc.getMinorchannel());
        holder.tlevel.setText(stc.getTlevel());
        holder.contact.setText(stc.getContact());
        holder.address.setText(stc.getAddress());
        if(!CheckUtil.isBlankOrNull(stc.getVisitdate())) {
            holder.visitdate.setText( DateUtil.formatDate(DateUtil.parse(stc.getVisitdate(),"yyyyMMddHHmmss"),"yyyy-MM-dd HH:mm:ss"));
        }
        if((CheckUtil.isBlankOrNull(stc.getIsself()) || stc.getIsself().equals(ConstValues
                  .FLAG_0)) && ((CheckUtil.isBlankOrNull(stc.getIscmp()) || stc.getIscmp()
                                                          .equals(ConstValues.FLAG_0)))) {
            holder.visittarget.setText(R.string.tomorrowwork_msg_no);
        }else if(stc.getIsself().equals(ConstValues.FLAG_1) && stc.getIscmp().
                                                    equals(ConstValues.FLAG_1)){
            holder.visittarget.setText(R.string.tomorrowwork_msg_double); 
        }else {
            if(stc.getIsself().equals(ConstValues.FLAG_1)) {
                holder.visittarget.setText(R.string.tomorrowwork_msg_self);  
            }
            if(stc.getIscmp().equals(ConstValues.FLAG_1)) {
                holder.visittarget.setText(R.string.tomorrowwork_msg_cmp);  
            }
        }
        if((CheckUtil.isBlankOrNull(stc.getSelftreaty()) || stc.getSelftreaty().equals(ConstValues
                .FLAG_0)) && ((CheckUtil.isBlankOrNull(stc.getCmptreaty()) || stc.getCmptreaty()
                        .equals(ConstValues.FLAG_0)))) {
            holder.cmptreaty.setText(R.string.tomorrowwork_msg_no);
        }else if(stc.getSelftreaty().equals(ConstValues.FLAG_1) && stc.getCmptreaty().
                equals(ConstValues.FLAG_1)){
            holder.cmptreaty.setText(R.string.tomorrowwork_msg_double); 
        }else {
            if(stc.getSelftreaty().equals(ConstValues.FLAG_1)) {
                holder.cmptreaty.setText(R.string.tomorrowwork_msg_self);  
            }
            if(stc.getCmptreaty().equals(ConstValues.FLAG_1)) {
                holder.cmptreaty.setText(R.string.tomorrowwork_msg_cmp);  
            }
        }
        holder.visitrecord.setText(stc.getRemarks());
        holder.visituser.setText(stc.getVisituser());
        if(CheckUtil.isBlankOrNull(stc.getStatus()) || stc.getStatus().equals(ConstValues.FLAG_0)) {
            holder.status.setText(R.string.tomorrowwork_msg_n);
        }else {
            holder.status.setText(R.string.tomorrowwork_msg_y);
        }
        holder.exetreaty.setText(stc.getExetreaty());
        holder.ishdistribution.setText(stc.getIshdistribution());
        if (ConstValues.FLAG_0.equals(FunUtil.isBlankOrNullTo(stc.getIscmpcollapse(), "0"))) {
            holder.iscmpcollapse.setText(R.string.tomorrowwork_msg_n);
        } else {
            holder.iscmpcollapse.setText(R.string.tomorrowwork_msg_y);
        }
        holder.selfoccupancy.setText(stc.getSelfoccupancy());
        holder.proCode.setText(stc.getProcode());
        holder.proName.setText(stc.getProname());
        holder.purcPrice.setText(stc.getPurcprice());
        holder.retailPrice.setText(stc.getRetailprice());
        holder.agencyName.setText(stc.getAgencyname());
        if(CheckUtil.isBlankOrNull(stc.getPurcnum())) {
            holder.innum.setText("0");
        }else {
            holder.innum.setText(stc.getPurcnum());
        }
        if(CheckUtil.isBlankOrNull(stc.getSalenum())) {
            holder.visitbSalenum.setText("0");
        }else {
            holder.visitbSalenum.setText(stc.getSalenum());
        }
        if(CheckUtil.isBlankOrNull(stc.getCurrnum())) {
            holder.visitSelfOccu.setText("0");
        }else {
            holder.visitSelfOccu.setText(stc.getCurrnum());
        }
        
        holder.shopState.setText(stc.getShopstate());
        holder.proLively.setText(stc.getProlively());
        holder.toolLively.setText(stc.getToollively());
        holder.itemName.setText(stc.getColitemname());
        holder.ifvlid.setText(stc.getIfVlid());
        
        if(CheckUtil.isBlankOrNull(stc.getTotalcount())) {
            holder.resultNum.setText("");
        }else {
            holder.resultNum.setText(stc.getTotalcount());
        }
        if(CheckUtil.isBlankOrNull(stc.getAddcount())) {
            holder.changeNum.setText("");
        }else {
            holder.changeNum.setText(stc.getAddcount());
        }
        
        return convertView;
    }

	private class ViewHolder {

	    private TextView routeName;
	    private TextView terminalCode;
	    private TextView terminalName;
	    private TextView minorchannel;
	    private TextView tlevel;
	    private TextView contact;
	    private TextView address;
	    private TextView visitdate;
	    private TextView visittarget;
	    private TextView cmptreaty;
	    private TextView visituser;
	    private TextView visitrecord;
	    private TextView status;
	    private TextView exetreaty;
	    private TextView ishdistribution;
	    private TextView iscmpcollapse;
	    private TextView selfoccupancy;
	    private TextView proCode;
	    private TextView proName;
	    private TextView purcPrice;
	    private TextView retailPrice;
	    private TextView agencyName;
	    private TextView innum;
	    private TextView visitbSalenum;
	    private TextView visitSelfOccu;
	    private TextView shopState;
	    private TextView proLively;
	    private TextView toolLively;
	    private TextView itemName;
	    private TextView resultNum;
	    private TextView changeNum;
	    private TextView ifvlid;
	}
}

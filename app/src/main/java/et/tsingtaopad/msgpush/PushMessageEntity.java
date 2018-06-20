package et.tsingtaopad.msgpush;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：PushEntity.java</br>
 * 作者：dbt   </br>
 * 创建时间：2013-12-28</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class PushMessageEntity {

	private int messageType;
	
	private String messageTitle;
	
	private String messageContent;
	
	private int messageState;
	
	private String startDate;
	
	private String endDate;
	
	private Date creDate;
	
	private String creUser;
	
	private List<String> messageMainKey = new ArrayList<String>();

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getMessageMainKey() {
        return messageMainKey;
    }

    public void setMessageMainKey(List<String> messageMainKey) {
        this.messageMainKey = messageMainKey;
    }

    public Date getCreDate() {
        return creDate;
    }

    public void setCreDate(Date creDate) {
        this.creDate = creDate;
    }

    public String getCreUser() {
        return creUser;
    }

    public void setCreUser(String creUser) {
        this.creUser = creUser;
    }
}

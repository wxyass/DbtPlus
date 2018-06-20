package et.tsingtaopad.visit.shopvisit.sayhi.domain;

import et.tsingtaopad.db.tables.MstTerminalinfoM;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstTerminalInfoMStc.java</br>
 * 作者：hongen   </br>
 * 创建时间：2014-3-20</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class MstTerminalInfoMStc extends MstTerminalinfoM {
    
    private String provName;
    
    private String cityName;
    
    private String countryName;
    
    private String sellChannelName;
    
    private String mainChannelName;
    
    private String minorChannelName;

    public String getProvName() {
        return provName;
    }

    public void setProvName(String provName) {
        this.provName = provName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getSellChannelName() {
        return sellChannelName;
    }

    public void setSellChannelName(String sellChannelName) {
        this.sellChannelName = sellChannelName;
    }

    public String getMainChannelName() {
        return mainChannelName;
    }

    public void setMainChannelName(String mainChannelName) {
        this.mainChannelName = mainChannelName;
    }

    public String getMinorChannelName() {
        return minorChannelName;
    }

    public void setMinorChannelName(String minorChannelName) {
        this.minorChannelName = minorChannelName;
    }

}

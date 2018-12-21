package com.xx.chinetek.model.DN;

import android.os.Parcel;
import android.os.Parcelable;

import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.greendao.DNDetailModelDao;
import com.xx.chinetek.greendao.DNModelDao;
import com.xx.chinetek.greendao.DaoSession;
import com.xx.chinetek.model.Base.ParamaterModel;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import java.util.List;

/**
 * Created by GHOST on 2017/10/25.
 */

@Entity
public class LogModel implements Parcelable {

   private String equipmentID;

   private String agentNo;

   private String actionTime;

   private String actionName;

   private String actionContent;

   private String dnNo;

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getAgentNo() {
        return agentNo;
    }

    public void setAgentNo(String agentNo) {
        this.agentNo = agentNo;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionContent() {
        return actionContent;
    }

    public void setActionContent(String actionContent) {
        this.actionContent = actionContent;
    }

    public String getDnNo() {
        return dnNo;
    }

    public void setDnNo(String dnNo) {
        this.dnNo = dnNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.equipmentID);
        dest.writeString(this.agentNo);
        dest.writeString(this.actionTime);
        dest.writeString(this.actionName);
        dest.writeString(this.actionContent);
        dest.writeString(this.dnNo);
    }

    public LogModel() {
    }

    public LogModel(String  actionName,String actionContent,String dnNo) {
        this.equipmentID= ParamaterModel.SerialNo;
        this.agentNo=ParamaterModel.PartenerID;
        this.actionName=actionName;
        this.actionContent=actionContent;
        this.dnNo=dnNo;
        this.actionTime= CommonUtil.DateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
    }

    protected LogModel(Parcel in) {
        this.equipmentID = in.readString();
        this.agentNo = in.readString();
        this.actionTime = in.readString();
        this.actionName = in.readString();
        this.actionContent = in.readString();
        this.dnNo = in.readString();
    }

    @Generated(hash = 425876189)
    public LogModel(String equipmentID, String agentNo, String actionTime,
            String actionName, String actionContent, String dnNo) {
        this.equipmentID = equipmentID;
        this.agentNo = agentNo;
        this.actionTime = actionTime;
        this.actionName = actionName;
        this.actionContent = actionContent;
        this.dnNo = dnNo;
    }

    public static final Creator<LogModel> CREATOR = new Creator<LogModel>() {
        @Override
        public LogModel createFromParcel(Parcel source) {
            return new LogModel(source);
        }

        @Override
        public LogModel[] newArray(int size) {
            return new LogModel[size];
        }
    };
}

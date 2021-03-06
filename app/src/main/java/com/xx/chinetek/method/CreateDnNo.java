package com.xx.chinetek.method;

import android.content.Context;
import android.content.SharedPreferences;

import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNModel;

/**
 * Created by GHOST on 2017/11/19.
 */

public class CreateDnNo {
    /**
     * 创建自定义单据号
     * 自定义出库单设定：三菱单据号：DNN+年（最后1位）月（2位）日（2位）+PDA序列号（后7位）+顺序号（4位）
     *                客户单据号：起始字符+PDA序列号（7位）+ 年（最后1位）月（2位）日（2位）+顺序号 (起始字符+顺序号长度不超6位)
     * @return
     */
    public static void GetDnNo(Context context,DNModel dnModel){
        try {
            String startWord = ParamaterModel.baseparaModel.getCusDnnoRule().getStartWords();
            int len = ParamaterModel.baseparaModel.getCusDnnoRule().getIndexLength();
            ParamaterModel.SerialNo = ParamaterModel.SerialNo == null ? "0000000" : ParamaterModel.SerialNo;
            int SerialLen = ParamaterModel.SerialNo.length();
            String serial = SerialLen > 7 ?
                    ParamaterModel.SerialNo.substring(SerialLen - 7) : ParamaterModel.SerialNo;
            String date = CommonUtil.getSystemDate();
            String lastData = "";
            Integer index = 0;
            SharedPreferences sharedPreferences = context.getSharedPreferences("CusDN", Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                lastData = sharedPreferences.getString("time", "");
                index = sharedPreferences.getInt("index", 0);
            }
            if (!lastData.equals(date))
                index = 0;
            String selfd = date.substring(3, 4) + date.substring(5, 7) + date.substring(8, 10);
            String ShiftNum = String.format("%0" + len + "d", index + 1);
            String ShiftNumAn = String.format("%04d", (index + 1));
            String cusDnNo = startWord + serial + selfd + ShiftNum;
            String AGENT_DN_NO = "DNN" + selfd + serial + ShiftNumAn;
            dnModel.setCUS_DN_NO(cusDnNo);
            dnModel.setAGENT_DN_NO(AGENT_DN_NO);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("time", date);
            edit.putInt("index", index + 1);
            edit.apply();
        }catch (Exception ex){

        }
    }

}

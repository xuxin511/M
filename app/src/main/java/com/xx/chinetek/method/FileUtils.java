package com.xx.chinetek.method;

import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by GHOST on 2017/11/15.
 */

public class FileUtils {

    /**
     * 导出文件至本地
     * @param selectDnModels
     * @throws Exception
     */
    public static void ExportDNFile(ArrayList<DNModel> selectDnModels) throws Exception{
        for (DNModel dnModel:selectDnModels ) {
            String DnNo=dnModel.getAGENT_DN_NO();
            String level2No=dnModel.getLEVEL_2_AGENT_NO();
            String level2Name=dnModel.getLEVEL_2_AGENT_NAME();
            String custom=dnModel.getCUSTOM_NO();
            String customName=dnModel.getCUSTOM_NAME();
            String fileName="DDN"+DnNo+"_QR.csv";
            File file = new File(ParamaterModel.UpDirectory+File.separator+fileName);
            //第二个参数意义是说是否以append方式添加内容
            OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(file),"GBK");
            BufferedWriter bw = new BufferedWriter(writerStream);
            if(dnModel.getDETAILS()!=null && dnModel.getDETAILS().size()!=0){
                for (DNDetailModel dnDetailModel:dnModel.getDETAILS()) {
                    StringBuffer sBuff=new StringBuffer();
                    String lineNo=dnDetailModel.getLINE_NO().toString();
                    String sapMaterial=dnDetailModel.getITEM_NO();
                    String maktx=dnDetailModel.getITEM_NAME();
                    String golfaCode=dnDetailModel.getGOLFA_CODE();
                    sBuff.append(DnNo+","+lineNo+","+level2No+","+level2Name
                            +","+custom+","+customName+","+sapMaterial+","+maktx+","+golfaCode);
                    if(dnDetailModel.getSERIALS()!=null && dnDetailModel.getSERIALS().size()!=0){
                        for (DNScanModel dnScanModel:dnDetailModel.getSERIALS()) {
                            String Serial=dnScanModel.getSERIAL_NO();
                            String packingDate=dnScanModel.getPACKING_DATE();
                            String region=dnScanModel.getREGION();
                            String country=dnScanModel.getCOUNTRY();
                            String dealSaleDate= CommonUtil.DateToString(dnScanModel.getDEAL_SALE_DATE(),null);
                            String writeLine=sBuff.toString()+","+Serial+","+packingDate+","
                                    +region+","+country+","+dealSaleDate;
                            bw.write(writeLine);
                            bw.newLine();
                            bw.flush();
                        }
                    }
                }
            }
            bw.close();
        }
    }
}

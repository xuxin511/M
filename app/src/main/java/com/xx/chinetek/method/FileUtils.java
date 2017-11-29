package com.xx.chinetek.method;

import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
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
     * 删除文件夹内文件
     */
    public static void DeleteFiles(){
        try {
            File dirFile = new File(ParamaterModel.UpDirectory);
            if (dirFile.isDirectory()) {
                File[] Files = dirFile.listFiles();
                for (int i = 0; i < Files.length; i++) {
                    File f = Files[i];
                    f.delete();
                }
            }

        }catch (Exception ex){

        }
    }


    /**
     * 导出文件至本地
     * @param selectDnModels
     * @throws Exception
     */
    public static void ExportDNFile(ArrayList<DNModel> selectDnModels,Integer IsMaps) throws Exception{
        String Notmaps="DDN_NO,LINE_NO,Tnd_Dealer,Tnd_Dealer,End_User,End_User,SAP_MAT,MAKTX,Golfa_Code,Serial_No," +
                "Packing_Date,Region,Country,Deal_Sale_Date";
        String Maps="DDN_NO,LINE_NO,Tnd_Dealer,End_User,SAP_MAL,Golfa_Code,Serial_No," +
                "Pack_Date,Region,Country,Deal_Sale_Date";
        String Title=IsMaps==0?Maps:Notmaps;
        Boolean isCusBarcode=false;
        for (DNModel dnModel:selectDnModels ) {
            String DnNo=dnModel.getAGENT_DN_NO();
            if(IsMaps!=0) {
                Long size = DbDnInfo.getInstance().HasCusBarcode(DnNo);
                if (Integer.parseInt(size.toString()) != 0) {
                    Title += ",TYPE1,TYPE2,TYPE3,TYPE4,TYPE5,TYPE6";
                    isCusBarcode = true;
                }
            }
            String level2No=dnModel.getLEVEL_2_AGENT_NO()==null?"":dnModel.getLEVEL_2_AGENT_NO();
            String level2Name=dnModel.getLEVEL_2_AGENT_NAME()==null?"":dnModel.getLEVEL_2_AGENT_NAME();
            String custom=dnModel.getCUSTOM_NO()==null?"":dnModel.getCUSTOM_NO();
            String customName=dnModel.getCUSTOM_NAME()==null?"":dnModel.getCUSTOM_NAME();
            String fileName="DDN"+DnNo+"_QR.csv";
            File file = new File(ParamaterModel.UpDirectory+File.separator+fileName);
            //第二个参数意义是说是否以append方式添加内容
            OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(file),"GBK");
            BufferedWriter bw = new BufferedWriter(writerStream);
            bw.write(Title);
            bw.newLine();
            if(dnModel.getDETAILS()!=null && dnModel.getDETAILS().size()!=0){
                for (DNDetailModel dnDetailModel:dnModel.getDETAILS()) {
                    String sapMaterial=dnDetailModel.getITEM_NO();
                    String maktx=dnDetailModel.getITEM_NAME()==null?"":dnDetailModel.getITEM_NAME();
                    String golfaCode=dnDetailModel.getGOLFA_CODE();
                    if(dnDetailModel.getSERIALS()!=null && dnDetailModel.getSERIALS().size()!=0){
                        for (DNScanModel dnScanModel:dnDetailModel.getSERIALS()) {
                            String lineNo=dnScanModel.getLINE_NO().toString();
                            String Serial=dnScanModel.getSERIAL_NO();
                            String packingDate=dnScanModel.getPACKING_DATE()==null?"":dnScanModel.getPACKING_DATE();
                            String region=dnScanModel.getREGION()==null?"":dnScanModel.getREGION();
                            String country=dnScanModel.getCOUNTRY()==null?"":dnScanModel.getCOUNTRY();
                            String dealSaleDate= CommonUtil.DateToString(dnScanModel.getDEAL_SALE_DATE(),"yyyy/MM/dd");
                            String writeLine=IsMaps==0? DnNo+","+lineNo+","+level2No
                                    +","+custom+","+sapMaterial+","
                                    +golfaCode+","+Serial+","+packingDate+","
                                    +region+","+country+","+dealSaleDate
                                    :
                                    DnNo+","+lineNo+","+level2No+","+level2Name
                                    +","+custom+","+customName+","+sapMaterial+","+maktx+","
                                    +golfaCode+","+Serial+","+packingDate+","
                                    +region+","+country+","+dealSaleDate;
                            if(isCusBarcode){
                                String type1=dnScanModel.getEXTEND_FIELD1()==null?"":dnScanModel.getEXTEND_FIELD1();
                                String type2=dnScanModel.getEXTEND_FIELD2()==null?"":dnScanModel.getEXTEND_FIELD2();
                                String type3=dnScanModel.getEXTEND_FIELD3()==null?"":dnScanModel.getEXTEND_FIELD3();
                                String type4=dnScanModel.getEXTEND_FIELD4()==null?"":dnScanModel.getEXTEND_FIELD4();
                                String type5=dnScanModel.getEXTEND_FIELD5()==null?"":dnScanModel.getEXTEND_FIELD5();
                                String type6=dnScanModel.getEXTEND_FIELD6()==null?"":dnScanModel.getEXTEND_FIELD6();
                                writeLine+=","+type1+","+type2+","+type3+","+type4+","+type5+","+type6;
                            }
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

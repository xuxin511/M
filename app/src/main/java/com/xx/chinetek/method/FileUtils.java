package com.xx.chinetek.method;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GHOST on 2017/11/15.
 */

public class FileUtils {

    /**
     * 删除文件夹内文件
     */
    public static void DeleteFiles(int index){
        try {
            File dirFile = new File(GetDirectory(index));
            if (dirFile.isDirectory()) {
                File[] Files = dirFile.listFiles();
                for (int i = 0; i < Files.length; i++) {
                    File f = Files[i];
                    f.delete();
                    UpdateMediaDirectory(BaseApplication.context,f);
                }
            }

        }catch (Exception ex){

        }
    }
    public static void  ExportMaterialFile(List<MaterialModel> materialModels, int index) throws Exception{
        String fileName="Material_QR.csv";
        String dir=GetDirectory(index);
        String Title="SPA号,GolafCode,产品线,型号";
        File file = new File(dir+File.separator+fileName);
        OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(file),"GBK");
        BufferedWriter bw = new BufferedWriter(writerStream);
        bw.write(Title);
        bw.write("\r\n");
        if(materialModels!=null && materialModels.size()!=0){
            for (MaterialModel material:materialModels) {
                String sap=material.getMATNR();
                String golafcode=material.getBISMT();
                String itemline=material.getSPART();
                String itemName=material.getMAKTX();
                String writeLine=sap+","+golafcode+","+itemline+","+itemName;
                bw.write(writeLine);
                bw.write("\r\n");
                bw.flush();
            }
        }
        bw.close();
        UpdateMediaDirectory(BaseApplication.context,file);

    }

    /**
     * 导出文件至本地
     * @param selectDnModels
     * @throws Exception
     */
    public static void ExportDNFile(ArrayList<DNModel> selectDnModels,int index) throws Exception{
        String Notmaps="DDN_NO,LINE_NO,Tnd_Dealer,Tnd_Dealer,End_User,End_User,SAP_MAT,MAKTX,Golfa_Code,Serial_No," +
                "Packing_Date,Region,Country,Deal_Sale_Date";
        String Maps="DDN_NO\tLINE_NO\tTnd_Dealer\tEnd_User\tSAP_MAL\tGolfa_Code\tSerial_No\t" +
                "Pack_Date\tRegion\tCountry\tDeal_Sale_Date";
        Boolean isCusBarcode=false;
        for (DNModel dnModel:selectDnModels ) {
            String DnNo=dnModel.getDN_SOURCE()==3?dnModel.getCUS_DN_NO():dnModel.getAGENT_DN_NO();
                Long size = DbDnInfo.getInstance().HasCusBarcode(dnModel.getAGENT_DN_NO());
                if (Integer.parseInt(size.toString()) != 0) {
                    Notmaps += ",TYPE1,TYPE2,TYPE3,TYPE4,TYPE5,TYPE6";
                    isCusBarcode = true;
                }

            String level2No=dnModel.getLEVEL_2_AGENT_NO()==null?"":dnModel.getLEVEL_2_AGENT_NO();
            String level2Name=dnModel.getLEVEL_2_AGENT_NAME()==null?"":dnModel.getLEVEL_2_AGENT_NAME();
            String custom=dnModel.getCUSTOM_NO()==null?"":dnModel.getCUSTOM_NO();
            String customName=dnModel.getCUSTOM_NAME()==null?"":dnModel.getCUSTOM_NAME();
            String fileName="DDN_"+DnNo+"_QR.csv";
            String fileNameMaps="DDN_"+DnNo+"_QR.txt";
            String dir=GetDirectory(index);
            File file = new File(dir+File.separator+fileName);
            File fileMaps = new File(dir+File.separator+fileNameMaps);
            //第二个参数意义是说是否以append方式添加内容
            OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(file),"GBK");
            OutputStreamWriter writerStreamMaps = new OutputStreamWriter(new FileOutputStream(fileMaps),"GBK");
            BufferedWriter bw = new BufferedWriter(writerStream);
            BufferedWriter bwMaps = new BufferedWriter(writerStreamMaps);
            bw.write(Notmaps);
            bwMaps.write(Maps);
            bw.write("\r\n");
            bwMaps.write("\r\n");
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
                            String dealSaleDate= dnScanModel.getDEAL_SALE_DATE();
                            String writeLine=DnNo+","+lineNo+","+level2No+","+level2Name
                                    +","+custom+","+customName+","+sapMaterial+","+maktx+","
                                    +golfaCode+","+Serial+","+packingDate+","
                                    +region+","+country+","+dealSaleDate;
                            String writeLineMaps= DnNo+"\t"+lineNo+"\t"+level2No
                                    +"\t"+(custom.equals("")?customName:custom)+"\t"+sapMaterial+"\t"
                                    +golfaCode+"\t"+Serial+"\t"+packingDate+"\t"
                                    +region+"\t"+country+"\t"+dealSaleDate;
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
                            bwMaps.write(writeLineMaps);
                            bw.write("\r\n");
                            bwMaps.write("\r\n");
                            bw.flush();
                            bwMaps.flush();
                        }
                    }
                }
            }
            bw.close();
            bwMaps.close();
            UpdateMediaDirectory(BaseApplication.context,file);
            UpdateMediaDirectory(BaseApplication.context,fileMaps);
        }
    }

   static void UpdateMediaDirectory(Context mContext, File file){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 判断SDK版本是不是4.4或者高于4.4
//            String[] paths = new String[]{file.getAbsolutePath()};
//            MediaScannerConnection.scanFile(mContext, paths, null, null);
//        } else {
            final Intent intent;
            if (file.isDirectory()) {
                intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
                intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            } else {
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
            }
            mContext.sendBroadcast(intent);
       // }
    }

    public static String GetDirectory(int index){
       String dir= ParamaterModel.UpDirectory;
       switch (index){
           case 0:
               dir= ParamaterModel.MailDirectory;
               break;
           case 1:
               dir= ParamaterModel.FTPDirectory;
               break;
       }
       return dir;
    }
}

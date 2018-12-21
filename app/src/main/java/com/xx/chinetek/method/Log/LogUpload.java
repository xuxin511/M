package com.xx.chinetek.method.Log;

import com.xx.chinetek.chineteklib.model.Paramater;
import com.xx.chinetek.method.DB.DbLogManager;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.URLModel;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.annotation.HttpResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by GHOST on 2018/12/19.
 */

public class LogUpload {

    public static void toUploadFile() throws FileNotFoundException {
        //封装参数信息
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("AgentNo", ParamaterModel.PartenerID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String path = ParamaterModel.DBDirectory+File.separator+ DbLogManager.DB_NAME;
        byte[] bytes = null;
        InputStream is;
        File myfile = new File(path);
        try {
            is = new FileInputStream(path);
            bytes = new byte[(int) myfile.length()];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                is.read(bytes);
            }
            is.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] updata = GpsImagePackage.getPacket(jsonObject.toString(), bytes);
        try {

            URL httpUrl = new URL(URLModel.GetURL().LogUpload);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setReadTimeout(5000);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(updata);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }

            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

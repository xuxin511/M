package com.xx.chinetek.chineteklib.util.function;

import java.io.File;
import java.util.List;

/**
 * Created by GHOST on 2017/11/6.
 */

public class FileUtil {
    public static Boolean FileExits(String strFilePath) {
        File file = new File(strFilePath);
        if (file.exists())
            return true;
        else
            return false;
    }

    public static Boolean CreateFile(List<String> strFilePaths) {
        Boolean isCreate=true;
        if(strFilePaths!=null && strFilePaths.size()!=0){
            for(String path :strFilePaths){
                File destDir = new File(path);
                if (!destDir.exists()) {
                    if(!destDir.mkdirs()) {
                        isCreate=false;
                        break;
                    }
                }
            }
        }
        return isCreate;

    }
}

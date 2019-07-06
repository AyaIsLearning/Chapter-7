package com.bytedance.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

public class OpenFileUtil {
    private static OpenFileUtil instance = null;

    private OpenFileUtil() {
    }

    public static OpenFileUtil getInstance() {
        synchronized (OpenFileUtil.class) {
            if (instance == null) {
                instance = new OpenFileUtil();
            }
        }

        return instance;
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    private String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end.equals("")) return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    /**
     * 打开文件
     *
     * @param file
     */
    public void openFile(Context mContext, File file) {
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(mContext, "com.bytedance.videoplayer.provider",file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, getMIMEType(file));
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //建立一个MIME类型与文件后缀名的匹配表
    private final String[][] MIME_MapTable = {
            {".mp4", "video/mp4"},
            {".flv", "application/octet-stream"}
    };
}

package com.asa.safety.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.asa.safety.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.MODE_PRIVATE;

public class Utils {


    public static File getFile(Context context, String fileName) {
        String devicePath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 优先保存到SD卡中
            devicePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "HelmetTag" + File.separator + fileName;
        } else {
            // 如果SD卡不存在，就保存到本应用的目录下
            devicePath = context.getFilesDir().getAbsolutePath() + File.separator + "HelmetTag" + File.separator + fileName;
        }
        File deviceListFile = new File(devicePath);
        if (!deviceListFile.exists()) {
            try {
                File parent = deviceListFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                deviceListFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return deviceListFile;
    }

    /**
     * @Date 2017/4/6
     * @Author wenzheng.liu
     * @Description 发送邮件
     */
    public static void sendEmail(Context context, String address, String body, String subject, String tips, File... files) {
        if (files.length == 0) {
            return;
        }
        Intent intent;
        if (files.length == 1) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(files[0]));
            intent.putExtra(Intent.EXTRA_TEXT, body);
        } else {
            ArrayList<Uri> uris = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                uris.add(Uri.fromFile(files[i]));
            }
            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            ArrayList<CharSequence> charSequences = new ArrayList<>();
            charSequences.add(body);
            intent.putExtra(Intent.EXTRA_TEXT, charSequences);
        }
        String[] addresses = {address};
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.setType("message/rfc822");
        Intent.createChooser(intent, tips);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static String getVersionInfo(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packInfo != null) {
            String version = packInfo.versionName;
            return String.format("%s", version);
        }
        return "";
    }

    /**
     * @Date 2018/1/22
     * @Author wenzheng.liu
     * @Description 加密
     */
    public static byte[] encrypt(byte[] value, byte[] password) {
        try {
            SecretKeySpec key = new SecretKeySpec(password, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器
            byte[] result = cipher.doFinal(value);// 加密
            byte[] data = Arrays.copyOf(result, 16);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || "null".equals(str))
            return true;
        else
            return false;
    }

    public static List<String> jsonArrayToStringArrayList(JSONArray jsonArray) throws JSONException {
        List<String> resultList = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            resultList.add(jsonArray.get(i).toString());
        }
        return resultList;
    }

    public static SharedPreferences getSharePreference(Activity activity) {
        return activity.getSharedPreferences(activity.getResources().getString(R.string.share_preference), MODE_PRIVATE);
    }

    public static String getMacFromSharedPreference(Activity activity) {
        return Utils.getSharePreference(activity).getString("mac", "N/A");
    }

    public static String getCurrentDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return sdf.format(new Date());
    }

    public static void saveInTxt(String content) {
        try {
            File path = new File(Environment.getExternalStorageDirectory()+"/infosmart");
            if (!path.exists()) {
                path.mkdirs();
            }

            File file = new File(Environment.getExternalStorageDirectory()+"/infosmart"+"/"+"Record.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            if (file.exists()) {
                FileOutputStream fOut = new FileOutputStream(file, true);
                OutputStreamWriter writer = new OutputStreamWriter(fOut);
                writer.append(content);
                writer.close();
            }
        } catch (Exception e) {
            Log.d("Files", e.toString());
        }
    }
}

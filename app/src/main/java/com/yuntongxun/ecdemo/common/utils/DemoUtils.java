package com.yuntongxun.ecdemo.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.view.PhotoBitmapDrawable;
import com.yuntongxun.ecdemo.pojo.Friend;
import com.yuntongxun.ecdemo.pojo.FriendMessage;
import com.yuntongxun.ecsdk.ECDeviceType;
import com.yuntongxun.ecsdk.ECNetworkType;
import com.yuntongxun.ecsdk.PersonInfo;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jorstin on 2015/3/18.
 */
public class DemoUtils {
    public static final String TAG = "ECDemo.DemoUtils";
    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;
    public static boolean inNativeAllocAccessError = false;
    /**
     * 当前SDK版本号
     */
    private static int mSdkint = -1;

    /**
     * 计算语音文件的时间长度
     *
     * @param file
     * @return
     */
    public static int calculateVoiceTime(String file) {
        File _file = new File(file);
        if (!_file.exists()) {
            return 0;
        }
        // 650个字节就是1s
        int duration = (int) Math.ceil(_file.length() / 650);
        if (duration > 60) {
            return 60;
        }
        if (duration < 1) {
            return 1;
        }
        return duration;
    }


    public static boolean checkAlertWindowsPermission(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1));
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }


    public static String arr2String(String[] a){

        return null;

    }


    public static String splite(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        String[] array = s.split("\\#");
        if (array != null && array.length == 2) {
            return array[1];
        }
        return "";
    }

    public static boolean isValidNormalAccount(String account) {
        if (account == null) {
            return false;
        }

        if (((account = account.trim()).length() < 1)
                || (account.length() > 64)) {
            return false;
        }

        return isValidAccount(account) || account.trim().matches("^[A-Za-z0-9\\w_\\-\\.]+$");
    }

    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isValidAccount(String account) {
        if (account == null) {
            return false;
        }
        if (((account = account.trim()).length() < 6)
                || (account.length() > 20)) {
            return false;
        }
        if (!(isAlpha(account.charAt(0)))) {
            return false;
        }
        for (int i = 0; i < account.length(); ++i) {
            char c = account.charAt(i);
            if ((!(isAlpha(c))) && (!(isNum(c)))
                    && (c != '-') && (c != '_')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlpha(char alpha) {
        return ((((alpha < 'a') || (alpha > 'z'))) && (((alpha < 'A') || (alpha > 'Z'))));
    }

    /**
     * 判断是否是数字 [0 - 9]
     *
     * @param number 字符
     * @return 是否是数字
     */
    public static boolean isNum(char number) {
        return ((number < '0') || (number > '9'));
    }


    public static int getRandRes(int[] arr) {

        return arr[new Random().nextInt(8)];
    }

    public static boolean isTrue(String s) {

        if (TextUtils.isEmpty(s)) {
            return false;
        }
        try {
            JSONObject o = new JSONObject(s);
            if (o != null && o.has("statusCode")) {
                String code = o.getString("statusCode");
                return "200".equalsIgnoreCase(code) || "0".equalsIgnoreCase(code) || "000000".equalsIgnoreCase(code);
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }


    public static String getErrMsg(String s) {

        if (TextUtils.isEmpty(s)) {
            return "";
        }
        try {
            JSONObject o = new JSONObject(s);
            if (o != null && o.has("statusMsg")) {
                String statusMsg = o.getString("statusMsg");
                return statusMsg;
            }
        } catch (JSONException e) {
            return "";
        }
        return "";
    }



    public static ArrayList<Friend> getFriends(String s) {

        ArrayList<Friend> list = new ArrayList<Friend>();

        try {
            JSONObject object = new JSONObject(s);
            JSONArray array = object.getJSONArray("friendsList");

            for (int i = 0; i < array.length(); i++) {

                JSONObject o = (JSONObject) array.get(i);

                Friend f = new Friend();
                if (o.has("useracc")) {
                    f.setUseracc(o.getString("useracc"));
                }
                if (o.has("nickName")) {
                    f.setNickName(o.getString("nickName"));
                }
                if (o.has("avatar")) {
                    f.setAvatar(o.getString("avatar"));
                }
                if (o.has("friendState")) {
                    f.setFriendState(o.getString("friendState"));
                }

                if (o.has("remarkName")) {
                    f.setRemarkName(o.getString("remarkName"));
                }
                list.add(f);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;

    }

    public static Friend getFriendInfo(String s) {
        Friend f = new Friend();
        try {
            JSONObject o = new JSONObject(s);
            if (o.has("useracc")) {
                f.setUseracc(o.getString("useracc"));
            }
            if (o.has("nickName")) {
                f.setNickName(o.getString("nickName"));
            }
            if (o.has("sign")) {
                f.setSign(o.getString("sign"));
            }
            if (o.has("avatar")) {
                f.setAvatar(o.getString("avatar"));
            }
            if (o.has("remarkName")) {
                f.setRemarkName(o.getString("remarkName"));
            }
            if (o.has("birthDay")) {
                f.setAge(o.getString("birthDay"));
            }
            if (o.has("state")) {
                f.setFriendState(o.getString("state"));
            }
            if (o.has("sex")) {
                String sex = o.getString("sex");
                f.setSex("1".equalsIgnoreCase(sex) ? PersonInfo.Sex.MALE : PersonInfo.Sex.FEMALE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static ArrayList<FriendMessage> getFriendsMessage(String s) {

        ArrayList<FriendMessage> list = new ArrayList<FriendMessage>();

        try {
            JSONObject object = new JSONObject(s);
            JSONArray array = object.getJSONArray("inviteList");

            for (int i = 0; i < array.length(); i++) {

                JSONObject o = (JSONObject) array.get(i);

                FriendMessage f = new FriendMessage();
                if (o.has("friendUseracc")) {
                    f.setFriendUseracc(o.getString("friendUseracc"));
                }
                if (o.has("message")) {
                    f.setMessage(o.getString("message"));
                }
                if (o.has("source")) {
                    f.setSource(o.getString("source"));
                }
                if (o.has("isInvited")) {
                    f.setIsInvited(o.getString("isInvited"));
                }
                if (o.has("dealState")) {
                    f.setDealState(o.getString("dealState"));
                }
                list.add(f);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;

    }

    public static ArrayList<String> getDisturb(String s) {

        ArrayList<String> list = new ArrayList<String>();

        try {
            JSONObject object = new JSONObject(s);
            JSONArray array = object.getJSONArray("result");

            for (int i = 0; i < array.length(); i++) {

                JSONObject o = (JSONObject) array.get(i);

//                list.add(f);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;

    }



    public static String buildSubDevice() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(Build.BRAND);
        sb.append("};");

        sb.append("{");
        sb.append(Build.PRODUCT);
        sb.append("};");

        sb.append("{");
        sb.append(Build.VERSION.RELEASE);
        sb.append("}");
        return sb.toString();
    }

    public static String getMacAddress(Context context) {
        // start get mac address
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiMan != null) {
            WifiInfo wifiInf = wifiMan.getConnectionInfo();
            if (wifiInf != null && wifiInf.getMacAddress() != null) {
                // 48位，如FA:34:7C:6D:E4:D7
                return wifiInf.getMacAddress();
            }
        }
        return null;
    }

    private static MessageDigest md = null;

    public static String md5(final String c) {
        if (md == null) {
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        if (md != null) {
            md.update(c.getBytes());
            return byte2hex(md.digest());
        }
        return "";
    }

    public static String byte2hex(byte b[]) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) {
                hs = (new StringBuffer(String.valueOf(hs))).toString();
            }
        }

        return hs.toUpperCase();
    }

    /**
     * 将集合转换成字符串，用特殊字符做分隔符
     *
     * @param srcList   转换前集合
     * @param separator 分隔符
     * @return
     */
    public static String listToString(List<String> srcList, String separator) {
        if (srcList == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < srcList.size(); ++i) {
            if (i == srcList.size() - 1) {
                sb.append(((String) srcList.get(i)).trim());
            } else {
                sb.append(((String) srcList.get(i)).trim() + separator);
            }
        }
        return sb.toString();
    }

    /**
     * SDK版本号
     *
     * @return
     */
    public static int getSdkint() {
        if (mSdkint < 0) {
            mSdkint = Build.VERSION.SDK_INT;
        }
        return mSdkint;
    }

    /**
     * Java文件操作 获取文件扩展名 Get the file extension, if no extension or file name
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 返回文件名
     *
     * @param pathName
     * @return
     */
    public static String getFilename(String pathName) {
        File file = new File(pathName);
        if (!file.exists()) {
            return "";
        }
        return file.getName();
    }

    /**
     * 过滤字符串为空
     *
     * @param str
     * @return
     */
    public static String nullAsNil(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    /**
     * 将字符串转换成整型，如果为空则返回默认值
     *
     * @param str 字符串
     * @param def 默认值
     * @return
     */
    public static int getInt(String str, int def) {
        try {
            if (str == null) {
                return def;
            }
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return def;
    }

    /**
     * 删除号码中的所有非数字
     *
     * @param str
     * @return
     */
    public static String filterUnNumber(String str) {
        if (str == null) {
            return null;
        }
        if (str.startsWith("+86")) {
            str = str.substring(3, str.length());
        }

//		if (str.contains("#")) {
//
//			return str.replaceAll("#", "@");
//		}

        // 只允数字
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        // 替换与模式匹配的所有字符（即非数字的字符将被""替换）
        // 对voip造成的负数号码，做处理
        if (str.startsWith("-")) {
            return "-" + m.replaceAll("").trim();
        } else {
            return m.replaceAll("").trim();
        }

    }

    /**
     * @param c
     * @return
     */
    public static boolean characterChinese(char c) {
        Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(c);
        return ((unicodeBlock != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                && (unicodeBlock != Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
                && (unicodeBlock != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
                && (unicodeBlock != Character.UnicodeBlock.GENERAL_PUNCTUATION)
                && (unicodeBlock != Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) && (unicodeBlock != Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS));
    }

    public static String getGroupShortId(String id) {
        /*
         * if(TextUtils.isEmpty(id) || !id.startsWith("G")) { return id; }
		 * return id.substring(id.length() - 6 , id.length());
		 */
        return id;
    }

    public static final String PHONE_PREFIX = "+86";

    /**
     * 去除+86
     *
     * @param phoneNumber
     * @return
     */
    public static String formatPhone(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }
        if (phoneNumber.startsWith(PHONE_PREFIX)) {
            return phoneNumber.substring(PHONE_PREFIX.length()).trim();
        }
        return phoneNumber.trim();
    }

    /**
     * @param userData
     * @return
     */
    public static String getFileNameFormUserdata(String userData) {
//        if (TextUtils.isEmpty(userData) || "null".equals(userData)) {
//            return "";
//        }
//        String[] split = userData.split("=");
//
//        return  split[1];
        int index = userData.indexOf("fileName=");
        if (index != -1 && index + "fileName=".length() <= userData.length()) {
            return userData.substring(
                    userData.indexOf("fileName=") + "fileName=".length(),
                    userData.length());
        } else {
            return "";
        }

    }

//    public static String getFileNameFormUserdata(String userData) {
//        if (TextUtils.isEmpty(userData) || "null".equals(userData)) {
//            return "";
//        }
//        return userData.substring(
//                userData.indexOf("fileName=") + "fileName=".length(),
//                userData.length());
//    }

    static MediaPlayer mediaPlayer = null;

    public static void playNotifycationMusic(Context context, String voicePath)
            throws IOException {
        // paly music ...
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(
                voicePath);
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                fileDescriptor.getStartOffset(), fileDescriptor.getLength());
        mediaPlayer.prepare();
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
    }

    /**
     * @param context
     * @param intent
     * @param appPath
     * @return
     */
    public static String resolvePhotoFromIntent(Context context, Intent intent,
                                                String appPath) {
        if (context == null || intent == null || appPath == null) {
            LogUtil.e(LogUtil.getLogUtilsTag(DemoUtils.class),
                    "resolvePhotoFromIntent fail, invalid argument");
            return null;
        }
        Uri uri = Uri.parse(intent.toURI());
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);
        try {

            String pathFromUri = null;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int columnIndex = cursor
                        .getColumnIndex(MediaStore.MediaColumns.DATA);
                // if it is a picasa image on newer devices with OS 3.0 and up
                if (uri.toString().startsWith(
                        "content://com.google.android.gallery3d")) {
                    // Do this in a background thread, since we are fetching a
                    // large image from the web
                    pathFromUri = saveBitmapToLocal(appPath,
                            createChattingImageByUri(intent.getData()));
                } else {
                    // it is a regular local image file
                    pathFromUri = cursor.getString(columnIndex);
                }
                cursor.close();
                LogUtil.d(TAG, "photo from resolver, path: " + pathFromUri);
                return pathFromUri;
            } else {

                if (intent.getData() != null) {
                    pathFromUri = intent.getData().getPath();
                    if (new File(pathFromUri).exists()) {
                        LogUtil.d(TAG, "photo from resolver, path: "
                                + pathFromUri);
                        return pathFromUri;
                    }
                }

                // some devices (OS versions return an URI of com.android
                // instead of com.google.android
                if ((intent.getAction() != null)
                        && (!(intent.getAction().equals("inline-data")))) {
                    // use the com.google provider, not the com.android
                    // provider.
                    // Uri.parse(intent.getData().toString().replace("com.android.gallery3d","com.google.android.gallery3d"));
                    pathFromUri = saveBitmapToLocal(appPath, (Bitmap) intent
                            .getExtras().get("data"));
                    LogUtil.d(TAG, "photo from resolver, path: " + pathFromUri);
                    return pathFromUri;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        LogUtil.e(TAG, "resolve photo from intent failed ");
        return null;
    }

    /**
     * @param uri
     * @return
     */
    public static Bitmap createChattingImageByUri(Uri uri) {
        return createChattingImage(0, null, null, uri, 0.0F, 400, 800);
    }

    /**
     * @param resource
     * @param path
     * @param b
     * @param uri
     * @param dip
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createChattingImage(int resource, String path,
                                             byte[] b, Uri uri, float dip, int width, int height) {
        if (width <= 0 || height <= 0) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        int outWidth = 0;
        int outHeight = 0;
        int sampleSize = 0;
        try {

            do {
                if (dip != 0.0F) {
                    options.inDensity = (int) (160.0F * dip);
                }
                options.inJustDecodeBounds = true;
                decodeMuilt(options, b, path, uri, resource);
                //
                outWidth = options.outWidth;
                outHeight = options.outHeight;

                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                if (outWidth <= width || outHeight <= height) {
                    sampleSize = 0;
                    setInNativeAlloc(options);
                    Bitmap decodeMuiltBitmap = decodeMuilt(options, b, path,
                            uri, resource);
                    return decodeMuiltBitmap;
                } else {
                    options.inSampleSize = (int) Math.max(outWidth / width,
                            outHeight / height);
                    sampleSize = options.inSampleSize;
                }
            } while (sampleSize != 0);

        } catch (IncompatibleClassChangeError e) {
            e.printStackTrace();
            throw ((IncompatibleClassChangeError) new IncompatibleClassChangeError(
                    "May cause dvmFindCatchBlock crash!").initCause(e));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            BitmapFactory.Options catchOptions = new BitmapFactory.Options();
            if (dip != 0.0F) {
                catchOptions.inDensity = (int) (160.0F * dip);
            }
            catchOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            if (sampleSize != 0) {
                catchOptions.inSampleSize = sampleSize;
            }
            setInNativeAlloc(catchOptions);
            try {
                return decodeMuilt(options, b, path, uri, resource);
            } catch (IncompatibleClassChangeError twoE) {
                twoE.printStackTrace();
                throw ((IncompatibleClassChangeError) new IncompatibleClassChangeError(
                        "May cause dvmFindCatchBlock crash!").initCause(twoE));
            } catch (Throwable twoThrowable) {
                twoThrowable.printStackTrace();
            }
        }

        return null;
    }

    /**
     * save image from uri
     *
     * @param outPath
     * @param bitmap
     * @return
     */
    public static String saveBitmapToLocal(String outPath, Bitmap bitmap) {
        try {
            String imagePath = outPath
                    + DemoUtils.md5(DateFormat.format("yyyy-MM-dd-HH-mm-ss",
                    System.currentTimeMillis()).toString()) + ".jpg";
            File file = new File(imagePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    bufferedOutputStream);
            bufferedOutputStream.close();
            LogUtil.d(TAG, "photo image from data, path:" + imagePath);
            return imagePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveBitmapToLocal(File file, Bitmap bitmap) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    bufferedOutputStream);
            bufferedOutputStream.close();
            LogUtil.d(TAG,
                    "photo image from data, path:" + file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param options
     * @param data
     * @param path
     * @param uri
     * @param resource
     * @return
     */
    public static Bitmap decodeMuilt(BitmapFactory.Options options,
                                     byte[] data, String path, Uri uri, int resource) {
        try {

            if (!checkByteArray(data) && TextUtils.isEmpty(path) && uri == null
                    && resource <= 0) {
                return null;
            }

            if (checkByteArray(data)) {
                return BitmapFactory.decodeByteArray(data, 0, data.length,
                        options);
            }

            if (uri != null) {
                InputStream inputStream = CCPAppManager.getContext()
                        .getContentResolver().openInputStream(uri);
                Bitmap localBitmap = BitmapFactory.decodeStream(inputStream,
                        null, options);
                inputStream.close();
                return localBitmap;
            }

            if (resource > 0) {
                return BitmapFactory.decodeResource(CCPAppManager.getContext()
                        .getResources(), resource, options);
            }
            return BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setInNativeAlloc(BitmapFactory.Options options) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && !inNativeAllocAccessError) {
            try {
                BitmapFactory.Options.class.getField("inNativeAlloc")
                        .setBoolean(options, true);
                return;
            } catch (Exception e) {
                inNativeAllocAccessError = true;
            }
        }
    }

    public static boolean checkByteArray(byte[] b) {
        return b != null && b.length > 0;
    }

    public static Bitmap getSuitableBitmap(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            LogUtil.e(TAG, "filepath is null or nil");
            return null;
        }

        if (!new File(filePath).exists()) {
            LogUtil.e(TAG,
                    "getSuitableBmp fail, file does not exist, filePath = "
                            + filePath);
            return null;
        }
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap decodeFile = BitmapFactory.decodeFile(filePath, options);
            if (decodeFile != null) {
                decodeFile.recycle();
            }

            if ((options.outWidth <= 0) || (options.outHeight <= 0)) {
                LogUtil.e(TAG, "get bitmap fail, file is not a image file = "
                        + filePath);
                return null;
            }

            int maxWidth = 960;
            int width = 0;
            int height = 0;
            if ((options.outWidth <= options.outHeight * 2.0D)
                    && options.outWidth > 480) {
                height = maxWidth;
                width = options.outWidth;
            }
            if ((options.outHeight <= options.outWidth * 2.0D)
                    || options.outHeight <= 480) {
                width = maxWidth;
                height = options.outHeight;
            }

            Bitmap bitmap = extractThumbNail(filePath, width, height, false);
            if (bitmap == null) {
                LogUtil.e(TAG,
                        "getSuitableBmp fail, temBmp is null, filePath = "
                                + filePath);
                return null;
            }
            int degree = readPictureDegree(filePath);
            if (degree != 0) {
                bitmap = degreeBitmap(bitmap, degree);
            }
            return bitmap;
        } catch (Exception e) {
            LogUtil.e(TAG, "decode bitmap err: " + e.getMessage());
            return null;
        }
    }

    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1) {
            return (returnValue + "MB");
        }
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * @param src
     * @param degree
     * @return
     */
    public static Bitmap degreeBitmap(Bitmap src, float degree) {
        if (degree == 0.0F) {
            return src;
        }
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree, src.getWidth() / 2, src.getHeight() / 2);
        Bitmap resultBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
        boolean filter = true;
        if (resultBitmap == null) {
            LogUtil.e(TAG, "resultBmp is null: ");
            filter = true;
        } else {
            filter = false;
        }

        if (resultBitmap != src) {
            src.recycle();
        }
        LogUtil.d(TAG, "filter: " + filter + "  degree:" + degree);
        return resultBitmap;
    }

    /**
     * 得到指定路径图片的options
     *
     * @param srcPath
     * @return Options {@link android.graphics.BitmapFactory.Options}
     */
    public final static BitmapFactory.Options getBitmapOptions(String srcPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);
        return options;
    }

    /**
     * 压缩发送到服务器的图片
     *
     * @param origPath     原始图片路径
     * @param widthLimit   图片宽度限制
     * @param heightLimit  图片高度限制
     * @param format       图片格式
     * @param quality      图片压缩率
     * @param authorityDir 图片目录
     * @param outPath      图片详细目录
     * @return
     */
    public static boolean createThumbnailFromOrig(String origPath,
                                                  int widthLimit, int heightLimit, Bitmap.CompressFormat format,
                                                  int quality, String authorityDir, String outPath) {
        Bitmap bitmapThumbNail = extractThumbNail(origPath, widthLimit,
                heightLimit, false);
        if (bitmapThumbNail == null) {
            return false;
        }

        try {
            saveImageFile(bitmapThumbNail, quality, format, authorityDir,
                    outPath);
            return true;
        } catch (IOException e) {
            LogUtil.e(TAG, "create thumbnail from orig failed: " + outPath);
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Bitmap extractThumbNail(final String path, final int width,
                                          final int height, final boolean crop) {
        Assert.assertTrue(path != null && !path.equals("") && height > 0
                && width > 0);

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            LogUtil.d(TAG, "extractThumbNail: round=" + width + "x" + height
                    + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            LogUtil.d(TAG, "extractThumbNail: extract beX = " + beX
                    + ", beY = " + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY)
                    : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                options.inMutable = true;
            }
            LogUtil.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight
                    + ", orig=" + options.outWidth + "x" + options.outHeight
                    + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            setInNativeAlloc(options);
            if (bm == null) {
                Log.e(TAG, "bitmap decode failed");
                return null;
            }

            LogUtil.i(
                    TAG,
                    "bitmap decoded size=" + bm.getWidth() + "x"
                            + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth,
                    newHeight, true);
            if (scale != null) {
                bm.recycle();
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm,
                        (bm.getWidth() - width) >> 1,
                        (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                LogUtil.i(
                        TAG,
                        "bitmap croped size=" + bm.getWidth() + "x"
                                + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            LogUtil.e(TAG, "decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }

    public static void saveImageFile(Bitmap bitmap, int quality,
                                     Bitmap.CompressFormat format, String authorityDir, String outPath)
            throws IOException {
        if (!TextUtils.isEmpty(authorityDir) && !TextUtils.isEmpty(outPath)) {
            LogUtil.d(TAG, "saving to " + authorityDir + outPath);
            File file = new File(authorityDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            File outfile = new File(file, outPath);
            outfile.createNewFile();

            try {
                FileOutputStream outputStream = new FileOutputStream(outfile);
                bitmap.compress(format, quality, outputStream);
                outputStream.flush();
            } catch (Exception e) {
                LogUtil.e(TAG, "saveImageFile fil=" + e.getMessage());
            }
        }
    }

    private static int mScreenWidth;

    public static int getImageMinWidth(Context context) {
        if (mScreenWidth <= 0) {
            mScreenWidth = DensityUtil.getImageWeidth(context, 1.0F)
                    - DensityUtil.getDisplayMetrics(context, 40F);
            mScreenWidth = mScreenWidth / 4;
        }
        return mScreenWidth;
    }

    public static int getImageMinWidth2(Context context) {
        if (mScreenWidth <= 0) {
            mScreenWidth = DensityUtil.getImageWeidth(context, 1.0F)
                    - DensityUtil.getDisplayMetrics(context, 40F);
            mScreenWidth = mScreenWidth / 4;
        }
        return mScreenWidth;
    }

    /**
     * 获取图片被旋转的角度
     *
     * @param filePath
     * @return
     */
    public static int getBitmapDegrees(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            LogUtil.d(TAG, "filePath is null or nil");
            return 0;
        }
        if (!new File(filePath).exists()) {
            LogUtil.d(TAG, "file not exist:" + filePath);
            return 0;
        }
        ExifInterface exifInterface = null;
        try {

            if (Integer.valueOf(Build.VERSION.SDK).intValue() >= 5) {
                exifInterface = new ExifInterface(filePath);
                int attributeInt = -1;
                if (exifInterface != null) {
                    attributeInt = exifInterface.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, -1);
                }

                if (attributeInt != -1) {
                    switch (attributeInt) {
                        case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                        case ExifInterface.ORIENTATION_TRANSPOSE:
                        case ExifInterface.ORIENTATION_TRANSVERSE:
                            return 0;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            return 180;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            return 90;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            return 270;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            LogUtil.e(TAG, "cannot read exif :" + e.getMessage());
        } finally {
            exifInterface = null;
        }
        return 0;
    }

    /**
     * 旋转图片
     *
     * @param srcPath
     * @param degrees
     * @param format
     * @param root
     * @param fileName
     * @return
     */
    public static boolean rotateCreateBitmap(String srcPath, int degrees,
                                             Bitmap.CompressFormat format, String root, String fileName) {
        Bitmap decodeFile = BitmapFactory.decodeFile(srcPath);
        if (decodeFile == null) {
            LogUtil.e(TAG, "rotate: create bitmap fialed");
            return false;
        }
        int width = decodeFile.getWidth();
        int height = decodeFile.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, width / 2.0F, height / 2.0F);
        Bitmap createBitmap = Bitmap.createBitmap(decodeFile, 0, 0, width,
                height, matrix, true);
        decodeFile.recycle();
        try {
            saveImageFile(createBitmap, 60, format, root, fileName);
            return true;
        } catch (Exception e) {
            LogUtil.e(TAG, "create thumbnail from orig failed: " + fileName);
        }
        return false;
    }

    /**
     * 生成一张缩略图
     *
     * @param bitmap
     * @param paramFloat
     * @return
     */
    public static Bitmap processBitmap(Bitmap bitmap, float paramFloat) {
        Assert.assertNotNull(bitmap);
        Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
        Rect localRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(localRect);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-4144960);
        canvas.drawRoundRect(rectF, paramFloat, paramFloat, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, localRect, localRect, paint);
        bitmap.recycle();
        return resultBitmap;
    }

    /**
     * @param stream
     * @param dip
     * @return
     */
    public static Bitmap decodeStream(InputStream stream, float dip) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (dip != 0.0F) {
            options.inDensity = (int) (160.0F * dip);
        }
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        setInNativeAlloc(options);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
            return bitmap;
        } catch (OutOfMemoryError e) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            setInNativeAlloc(options);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(stream, null,
                        options);
                return bitmap;
            } catch (OutOfMemoryError e2) {
            }
        }
        return null;
    }

    public static String getLastText(String text) {
        if (text == null) {
            return null;
        }
        for (int i = text.length() - 1; i >= 0; --i) {
            int j = text.charAt(i);
            if ((j >= 19968) && (j <= 40869)) {
                return String.valueOf(j);
            }
        }
        return null;
    }

    public static boolean isOffice(String path){
        if(TextUtils.isEmpty(path)){
            return false;
        }
        String prefix=path.substring(path.lastIndexOf(".")+1);

        return "txt".equalsIgnoreCase(prefix)||"pdf".equalsIgnoreCase(prefix)||"doc".equalsIgnoreCase(prefix)
                ||"ppt".equalsIgnoreCase(prefix)||"xls".equalsIgnoreCase(prefix)||"docx".equalsIgnoreCase(prefix)
                ||"pptx".equalsIgnoreCase(prefix)||"xlsx".equalsIgnoreCase(prefix);
    }



    /**
     * @return
     */
    public static Paint newPaint() {
        Paint paint = new Paint(1);
        paint.setFilterBitmap(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        return paint;
    }

    public static Drawable getDrawable(Context context, int resid,
                                       Bitmap defaultMask) {
        return getDrawable(((BitmapDrawable) context.getResources()
                .getDrawable(resid)).getBitmap(), defaultMask, newPaint());
    }

    public static Drawable getDrawable(Bitmap bitmap, Bitmap defaultMask) {
        return getDrawable(bitmap, defaultMask, newPaint(), true);
    }

    public static Drawable getDrawable(Bitmap photo, Bitmap mask,
                                       Paint paramPaint) {
        return getDrawable(photo, mask, paramPaint, true);
    }

    /*
     *
     */
    public static Bitmap newBitmap(int width, int height, Bitmap.Config config) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);
            return bitmap;
        } catch (Throwable localThrowable) {
            LogUtil.e(TAG, localThrowable.getMessage());
        }
        return null;
    }

    public static Drawable getDrawable(Bitmap src, Bitmap mask, Paint paint,
                                       boolean stroke) {
        if (src == null) {
            return null;
        }
        if ((stroke) && (src.getHeight() != src.getWidth())) {
            try {
                int maxSize = Math.max(src.getWidth(), src.getHeight());
                Bitmap bitmap = newBitmap(maxSize, maxSize,
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(-1);
                canvas.drawBitmap(src, (maxSize - src.getWidth()) / 2,
                        (maxSize - src.getHeight()) / 2, new Paint());
                PhotoBitmapDrawable photo = new PhotoBitmapDrawable(bitmap,
                        mask, paint);
                return photo;
            } catch (Exception e) {
                e.printStackTrace();
                return new PhotoBitmapDrawable(src, mask, paint);
            }
        }
        return new PhotoBitmapDrawable(src, mask, paint);
    }

    public static boolean checkUpdater(String serVer) {
        String version = CCPAppManager.getVersion();
        return compareVersion(version, serVer) == -1;
    }

    public static int compareVersion(String curVer, String serVer) {
        if (curVer.equals(serVer) || serVer == null) {
            return 0;
        }
        String[] version1Array = curVer.split("\\.");
        String[] version2Array = serVer.split("\\.");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;
        while (index < minLen
                && (diff = Long.parseLong(getStringNum(version1Array[index]))
                - Long.parseLong(getStringNum(version2Array[index]))) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (i >= 4 || Integer.parseInt(version1Array[i]) > 0) {
                    // 没有新版本
                    return 1;
                }
            }
            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    // 有新版本更新
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    private static String getStringNum(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static boolean saveImage(String url) {
        return saveImage(url, ".jpg");
    }

    public static boolean saveImage(String url, String ext) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String filePath = url;
        File dir = new File(FileAccessor.APPS_ROOT_DIR, "ECDemo_IM");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long timeMillis = System.currentTimeMillis();
        int result = FileUtils.copyFile(
                dir.getAbsolutePath(),
                "ecexport" + timeMillis,
                ext,
                FileUtils.readFlieToByte(filePath, 0,
                        FileUtils.decodeFileLength(filePath)));
        if (result == 0) {
            ExportImgUtil.refreshingMediaScanner(CCPAppManager.getContext(),
                    "ecexport" + timeMillis + ext);
            ToastUtil.showMessage("图片已保存至" + dir.getAbsolutePath(),
                    Toast.LENGTH_LONG);
            return false;
        }
        ToastUtil.showMessage("图片保存失败");
        return true;
    }

    public static int getScreenWidth(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * @param context
     * @param id
     * @return
     */
    public static Drawable getDrawables(Context context, int id) {
        Drawable drawable = getResources(context).getDrawable(id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());

        return drawable;
    }

    /**
     * @param context
     * @return Resources
     * @Title: getResource
     * @Description: TODO
     */
    public static Resources getResources(Context context) {
        return context.getResources();
    }

    public static DisplayImageOptions getChatDisplayImageOptions() {
        return mChatDiaplayOptions;
    }

    static DisplayImageOptions mChatDiaplayOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
            .considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
            .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
            .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
            .build();

    public static DisplayImageOptions.Builder getChatDisplayImageOptionsBuilder() {
        return new DisplayImageOptions.Builder().cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
                .resetViewBeforeLoading(true);// 设置图片在下载前是否重置，复位;
    }

    private static final long[] SHAKE_PATTERN = {300L, 200L, 300L, 200L};
    private static final long[] SHAKE_MIC_PATTERN = {300L, 200L};

    public static String getLastwords(String srcText, String p) {
        try {
            String[] array = TextUtils.split(srcText, p);
            int index = (array.length - 1 < 0) ? 0 : array.length - 1;
            return array[index];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * msg shake
     *
     * @param context
     * @param isShake
     */
    public static void shake(Context context, boolean isShake) {
        Vibrator vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null) {
            return;
        }
        if (isShake) {
            vibrator.vibrate(SHAKE_PATTERN, -1);
            return;
        }
        vibrator.cancel();
    }

    /**
     * msg shake
     *
     * @param context
     * @param isShake
     */
    public static void shakeControlMic(Context context, boolean isShake) {
        Vibrator vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null) {
            return;
        }
        if (isShake) {
            vibrator.vibrate(SHAKE_MIC_PATTERN, -1);
            return;
        }
        vibrator.cancel();
    }

    public static String getDeviceWithType(ECDeviceType deviceType) {
        if (deviceType == null) {
            return "未知";
        }
        switch (deviceType) {
            case ANDROID_PHONE:
                return "Android手机";

            case IPHONE:
                return "iPhone手机";

            case IPAD:
                return "iPad平板";

            case ANDROID_PAD:
                return "Android平板";

            case PC:
                return "PC";

            case WEB:
                return "Web";

            default:
                return "未知";
        }
    }

    public static String getNetWorkWithType(ECNetworkType type) {
        if (type == null) {
            return "未知";
        }
        switch (type) {
            case ECNetworkType_WIFI:
                return "wifi";

            case ECNetworkType_4G:
                return "4G";

            case ECNetworkType_3G:
                return "3G";

            case ECNetworkType_GPRS:
                return "GRPS";

            case ECNetworkType_LAN:
                return "Internet";
            default:
                return "其他";
        }
    }

    /**
     * 仅能输入中英文、ASCII码范围内的值。
     * <p>名字不能空并且仅能输入中英文、ASCII码范围内的值,
     * 可以空或者仅能输入中英文、ASCII码范围内的值
     *
     * @return 是否验证通过
     */
    public static boolean isGroupNameDescValid(String name) {
        return !isNullOrNil(name) && isValidASCIIChineseEnglish(name);

    }


    public static boolean isDeclareValid(String declare) {
        return (isNullOrNil(declare) || isValidASCIIChineseEnglish(declare));

    }

    public static boolean isNullOrNil(String value) {
        return value == null || value.length() <= 0;
    }

    public static boolean isValidASCIIChineseEnglish(String str) {
        return isNullOrNil(str) ? false : str.trim().matches("^[\\u3002\\uff1b\\uff0c\\uff1a\\u201c\\u201d\\uff08\\uff09\\u3001\\uff1f\\u300a\\u300b \\x00-\\x7F\\u4e00-\\u9fa5\\uFE30-\\uFFA0a-zA-Z、]+");
    }


    /**
     * 中国电信号码格式验证 手机段： 133,153,180,181,189,177,1700,173
     * **/
    private static final String CHINA_TELECOM_PATTERN = "(^1(33|53|7[37]|8[019])\\d{8}$)|(^1700\\d{7}$)";

    /**
     * 中国联通号码格式验证 手机段：130,131,132,155,156,185,186,145,176,1707,1708,1709
     * **/
    private static final String CHINA_UNICOM_PATTERN = "(^1(3[0-2]|4[5]|5[56]|7[6]|8[56])\\d{8}$)|(^170[7-9]\\d{7}$)";

    /**
     * 中国移动号码格式验证
     * 手机段：134,135,136,137,138,139,150,151,152,157,158,159,182,183,184
     * ,187,188,147,178,1705
     *
     **/
    private static final String CHINA_MOBILE_PATTERN = "(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478])\\d{8}$)|(^1705\\d{7}$)";
    /**
     * 仅手机号格式校验
     */
    private static final String PHONE_PATTERN=new StringBuilder(300).append(CHINA_MOBILE_PATTERN)
            .append("|")
            .append(CHINA_TELECOM_PATTERN)
            .append("|")
            .append(CHINA_UNICOM_PATTERN)
            .toString();

    /**
     * 仅手机号码校验
     * @param input
     * @return
     */
    public static boolean isPhone(String input){
        return match(PHONE_PATTERN, input);
    }

    /**
     * 匹配函数
     * @param regex
     * @param input
     * @return
     */
    private static boolean match(String regex, String input) {
        return Pattern.matches(regex, input);
    }


}
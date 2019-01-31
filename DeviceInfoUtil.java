package com.mario.devicemanager.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.MalformedParametersException;

/*
* 工具类，第三方app会有一些接口有权限限制
*/
public class Util {

    /**
     * @param context
     * @return int[2], 0: width; 1,height
     */
    public static void getScreenWidthHeight(Context context, int[] result) {
        if (result == null || result.length != 2){
            throw new IllegalArgumentException("no support");
        }
        int ver = Build.VERSION.SDK_INT;
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        int realHeight = 0;
        int realWidth = dm.widthPixels;
        if (ver < 13) {
            realHeight = dm.heightPixels;
        } else if (ver == 13) {
            try {
                realHeight = ((Integer) display.getClass().getMethod("getRealHeight", new Class[0]).invoke(display, new Object[0])).intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ver > 13 && ver < 17) {
            try {
                realHeight = ((Integer) display.getClass().getMethod("getRawHeight", new Class[0]).invoke(display, new Object[0])).intValue();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                realWidth = ((Integer) display.getClass().getMethod("getRealWidth", new Class[0]).invoke(display, new Object[0])).intValue();
            } catch (Exception e22) {
                e22.printStackTrace();
            }
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        } else if (ver >= 17) {
            display.getRealMetrics(dm);
        }
        int resultWidth = realWidth > dm.widthPixels ? realWidth : dm.widthPixels;
        int resultHeight = realHeight > dm.heightPixels ? realHeight : dm.heightPixels;
        result[0] = resultWidth;
        result[1] = resultHeight;
    }


    /**
     * change the 'pointer location' true or false in SettingProvider
     */
    public static void setPointerLocationEnable(Context context, boolean enable){
        try {
            Settings.System.putInt(context.getContentResolver(), "pointer_location", enable ? 1 : 0);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static void saveScreenBrightness(Context context, int brightness) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
            DisplayManager displayManager = context.getSystemService(DisplayManager.class);
            displayManager.setTemporaryBrightness(brightness);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static int getScreenBrightness(Context context) {
        int brightness = 0;
        try {
            return Settings.System.getInt(context.getContentResolver(), "screen_brightness");
        } catch (Settings.SettingNotFoundException e) {
            return brightness;
        }
    }

    /**
     * 得到当前应用版本名称的方法
     *
     * @param context
     *            :上下文
     * @throws Exception
     */
    public static String getVersionName(Context context) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }


    /**
     * 获取运行内存信息，单位为byte
     * @param context android context
     * @param info [avail, total], place the result, length is 2, in case of null, new array will be allocated.
     * @return result array.
     */
    public static long[] getMemoryInfo(Context context, long[] info){
        long[] result = (info == null ? new long[2] : info);
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        result[0] = memoryInfo.availMem;
        result[2] = memoryInfo.totalMem;
        return result;
    }

    /**
     * 获取Rom内存的信息
     * @param info [avail, total], place the result, length is 2, in case of null, new array will be allocated.
     * @return result array.
     */
    public static  long[] getRomSize(long[] info){
        long[] result = (info == null ? new long[2] : info);
        final StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long totalCounts = statFs.getBlockCountLong();
        long availableCounts = statFs.getAvailableBlocksLong();
        long blockSize = statFs.getBlockSizeLong();
        long availableSize = availableCounts * blockSize;
        long totalSize = totalCounts * blockSize;
        result[0] = availableSize;
        result[1] = totalSize;
        return result;
    }




}

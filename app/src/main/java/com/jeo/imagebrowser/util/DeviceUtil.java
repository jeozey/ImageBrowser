package com.jeo.imagebrowser.util;

import java.lang.reflect.Field;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

public class DeviceUtil {
	private static final String TAG = "DeviceUtil";
	/**
	 * 得到全局唯一UUID
	 */
	public static String getUUID(Context context) {
		String uuid = "";
		SharedPreferences mShare = context.getSharedPreferences("sysCacheMap",
				Context.MODE_PRIVATE);
		if (mShare != null) {
			uuid = mShare.getString("uuid", "");
		}

		if (TextUtils.isEmpty(uuid)) {
			uuid = UUID.randomUUID().toString();
			Editor editor = mShare.edit();
			editor.putString("uuid", uuid);
			editor.commit();
		}

		Log.e("getUUID", "getUUID : " + uuid);
		return uuid;
	}

	public static int getVersionCode(Context context)// 获取版本号(内部识别号)
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	
	public static int getStateBarHeight(Context context){
		try {
			Class<?> c = null;
			Object obj = null;
			Field field = null;
			int x = 0, sbar = 0;
			try {
			    c = Class.forName("com.android.internal.R$dimen");
			    obj = c.newInstance();
			    field = c.getField("status_bar_height");
			    x = Integer.parseInt(field.get(obj).toString());
			    sbar = context.getResources().getDimensionPixelSize(x);
			    return sbar;
			} catch(Exception e1) {
			    Log.e(TAG,"get status bar height fail");
			    e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}

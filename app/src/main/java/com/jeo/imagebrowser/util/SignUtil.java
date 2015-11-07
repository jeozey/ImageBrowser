package com.jeo.imagebrowser.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

//获取应用签名
public class SignUtil {
	private static final String TAG = "SignUtil";
	private static Signature[] getRawSignature(Context paramContext, String paramString) {
		if ((paramString == null) || (paramString.length() == 0)) {
			Log.e(TAG,"getSignature, packageName is null");
			return null;
		}
		PackageManager localPackageManager = paramContext.getPackageManager();
		PackageInfo localPackageInfo;
		try {
			localPackageInfo = localPackageManager.getPackageInfo(paramString,
					64);
			if (localPackageInfo == null) {
				Log.e(TAG,"info is null, packageName = " + paramString);
				return null;
			}
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			Log.e(TAG,"NameNotFoundException");
			return null;
		}
		return localPackageInfo.signatures;
	}

	public static String getSign(Context context,String paramString) {
		StringBuffer sb = new StringBuffer();
		Signature[] arrayOfSignature = getRawSignature(context, paramString);
		if ((arrayOfSignature == null) || (arrayOfSignature.length == 0))
			Log.e(TAG,"signs is null");
		else{
			int i = arrayOfSignature.length;
			for (int j = 0; j < i; j++){
				sb.append(MD5.getMessageDigest(arrayOfSignature[j].toByteArray()));
			}
		}
		return sb.toString();
	}
}

package com.weige.smart.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CacheUtils {
	public static final String CACHE_FILE_NAME = "weige_config";
	private static SharedPreferences mSharedPreferences;
	
	/**
	 * 使用SharedPreferences存储boolean类型数据
	 */
	public static void cacheBooleanData(Context context, String key, boolean value) {
		if(mSharedPreferences == null){
			mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
		}
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}
	/**
	 * 取出缓存的boolean类型数据
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBooleanData(Context context, String key, boolean defValue){
		if(mSharedPreferences == null){
			mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
		}
		return mSharedPreferences.getBoolean(key, defValue);
		
	}
	
	public static void cacheStringData(Context context, String key, String value) {
		if(mSharedPreferences == null){
			mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
		}
		mSharedPreferences.edit().putString(key, value).commit();
	}
	
	public static String getStringData(Context context, String key, String defValue) {
		if(mSharedPreferences == null){
			mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
		}
		return mSharedPreferences.getString(key, defValue);
	}

}

package com.jarvis.mvvm.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jarvis.mvvm.AppApplication;
import java.util.Map;


public class PreferenceUtil {
	private static SharedPreferences sharedPreferences;

	/**
	 * 默认。用于存储一些配置信息。一般不清除里面的数据
	 * @return
	 */
	public static SharedPreferences getSharedPreferences() {
		if(sharedPreferences == null)
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppApplication.getContext());
		return sharedPreferences;
	}

	/**
	 * 用于缓存一些网络资源信息，可能会被清除（如用户退出登录时）
	 * @return
	 */
	public static SharedPreferences getCacheSharedPreferences() {
		SharedPreferences cacheSharedPreferences = AppApplication.getContext().getSharedPreferences("cache",Activity.MODE_PRIVATE);
		return cacheSharedPreferences;
	}

	/**
	 * 用于缓存一些网络资源信息，可能会被清除（如用户退出登录时）
	 * @return
	 */
	public static Boolean setPreferenceToCache(String key, String value) {
		SharedPreferences.Editor editor = getCacheSharedPreferences().edit();
		editor.putString(key,value);
		return editor.commit();
	}

	public static String getPreferenceFromCache(String key, String defaultValue) {
		return getCacheSharedPreferences().getString(key, defaultValue);
	}

	/**
	 * 从默认配置文件中取值
	 */
	public static String getPreference(String key, String defaultValue) {
		return getSharedPreferences().getString(key, defaultValue);
	}

	/**
	 * 从默认配置文件中取值
	 */
	public static Boolean getPreference(String key, boolean defaultValue) {
		return getSharedPreferences().getBoolean(key, defaultValue);
	}

	/**
	 * 向默认配置文件中存值
	 */
	public static Boolean setPreference(Map<String, String> preferences) {
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		for(String key : preferences.keySet())
			editor.putString(key, preferences.get(key));
		return editor.commit();
	}

	public static int getPreference(String key, int defaultValue) {
		return getSharedPreferences().getInt(key, defaultValue);
	}

	public static Boolean setPreference(String key, int value ){
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putInt(key,value);
		return editor.commit();
	}

	public static Boolean setPreference(String key, String value) {
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putString(key,value);
		return editor.commit();
	}
}

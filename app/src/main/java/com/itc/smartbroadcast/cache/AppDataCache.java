package com.itc.smartbroadcast.cache;

import android.content.Context;

import com.itc.smartbroadcast.application.SmartBroadcastApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 应用程序数据缓存
 */
public class AppDataCache extends BaseDataCache {
	private static AppDataCache sDataCache;
	private final static String CACHE_NAME = "AppDataCache";

	public ArrayList<String> saveServiceIP = new ArrayList<>();
	public void saveDataToList(String ipStr){
		saveServiceIP = AppDataCache.getInstance().getList("saveServiceIP");
		if(saveServiceIP.size() < 4){
			if(!saveServiceIP.contains(ipStr)){
				saveServiceIP.add(ipStr);
				Collections.reverse(saveServiceIP); // 倒序
			}else {
				for(int i = 0; i < saveServiceIP.size(); i++){
					if(saveServiceIP.get(i).equals(ipStr)){
						Collections.swap(saveServiceIP,i,0); // 互换位置
					}
				}
			}
		}else {
			saveServiceIP.remove(saveServiceIP.size() - 1);
			saveServiceIP.add(ipStr);
			Collections.reverse(saveServiceIP); // 倒序
		}
		AppDataCache.getInstance().putList("saveServiceIP", saveServiceIP);
	}

	public ArrayList<String> saveTcpServiceIP = new ArrayList<>();
	public void saveTcpDataToList(String tcpIpStr){
		saveTcpServiceIP = AppDataCache.getInstance().getList("saveTcpServiceIP");
		if(saveTcpServiceIP.size() < 4){
			if(!saveTcpServiceIP.contains(tcpIpStr)){
				saveTcpServiceIP.add(tcpIpStr);
				Collections.reverse(saveTcpServiceIP); // 倒序
			}else {
				for(int i = 0; i < saveTcpServiceIP.size(); i++){
					if(saveTcpServiceIP.get(i).equals(tcpIpStr)){
						Collections.swap(saveTcpServiceIP,i,0); // 互换位置
					}
				}
			}
		}else {
			saveTcpServiceIP.remove(saveTcpServiceIP.size() - 1);
			saveTcpServiceIP.add(tcpIpStr);
			Collections.reverse(saveTcpServiceIP); // 倒序
		}
		AppDataCache.getInstance().putList("saveTcpServiceIP", saveTcpServiceIP);
	}

	public static synchronized AppDataCache getInstance() {
		if (sDataCache == null) {
			Context context = SmartBroadcastApplication.getInstance();
			if (context == null) {
				throw new IllegalArgumentException("context is null!");
			}
			sDataCache = new AppDataCache(context, CACHE_NAME);
		}
		return sDataCache;
	}

	/**
	 * @param appContext
	 */
	public AppDataCache(Context appContext) {
		super(appContext);
	}

	/**
	 * @param appContext
	 * @param cacheName
	 */
	public AppDataCache(Context appContext, String cacheName) {
		super(appContext, cacheName);
	}
	
	@Override
	public void setPreferences(String tinydbname) {
		super.setPreferences(tinydbname);
	}

	@Override
	public void putString(String key, String value) {
		super.putString(key, value);
	}

	@Override
	public String getString(String key) {
		return super.getString(key);
	}

	@Override
	public ArrayList<String> getList(String key) {
		return super.getList(key);
	}

	@Override
	public void putList(String key, ArrayList<String> mArray) {
		super.putList(key, mArray);
	}

//	@Override
//	public <T> void setDataList(String tag, List<T> datalist) {
//		super.setDataList(tag, datalist);
//	}
//
//	@Override
//	public List<List<MiddleControlModel.DataBeanX.DataBean>> getMiddleControlList(String tag) {
//		return super.getMiddleControlList(tag);
//	}
}
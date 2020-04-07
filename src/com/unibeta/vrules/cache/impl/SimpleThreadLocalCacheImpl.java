package com.unibeta.vrules.cache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.unibeta.vrules.cache.CacheManager;

/**
 * CloudTestContext is of thread-safe for cloud test context data management.
 */
public class SimpleThreadLocalCacheImpl implements CacheManager {

	private static Map<String, ThreadLocal<Map<String, Object>>> localThreadMap = Collections
			.synchronizedMap(new HashMap<String, ThreadLocal<Map<String, Object>>>());

	public void put(String contextType, String key, Object value) {

		getlocalThread(contextType).get().put(key, value);
	}

	public Object get(String contextType, String key) {

		return getlocalThread(contextType).get().get(key);
	}

	public Object remove(String contextType, String key) {

		return getlocalThread(contextType).get().remove(key);

	}

	public void clear() {

		List<String> set = keySet(CACHE_TYPE_TASKS_QUEUE);
		// System.out.println(Thread.currentThread().getName() +
		// " task queue is: "+ set.size()+"\n" + ObjectDigester.toXML(set));

		for (ThreadLocal<Map<String, Object>> t : localThreadMap.values()) {

			if (null != t && t.get() != null && (set == null || set.size() == 0)) {
				t.get().clear();
			}
		}
	}

	public List<String> keySet(String contextType) {

		List<String> list = new ArrayList<String>();
		Set<String> keySet = getlocalThread(contextType).get().keySet();

		for (String s : keySet) {
			list.add(s);
		}

		return list;

	}

	private static ThreadLocal<Map<String, Object>> getlocalThread(String key) {

		ThreadLocal<Map<String, Object>> local = localThreadMap.get(key);

		if (null == local) {
			local = new ThreadLocal<Map<String, Object>>();
			localThreadMap.put(key, local);
		}

		if (null == localThreadMap.get(key).get()) {
			localThreadMap.get(key).set(Collections.synchronizedMap(new HashMap<String, Object>()));
		}

		return localThreadMap.get(key);
	}

	@Override
	public boolean isThreadLocalCache() {
		// always not support threadlocal mode.
		return false;
	}
}

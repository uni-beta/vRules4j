package com.unibeta.vrules.cache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.unibeta.vrules.cache.CacheManager;

public class SimpleGlobalCacheImpl implements CacheManager {

	private static Map<String, Map<String, Object>> globalCacheMap = Collections
			.synchronizedMap(new HashMap<String, Map<String, Object>>());

	public void put(String contextType, String key, Object value) {

		getlocalThread(contextType).put(key, value);
	}

	public Object get(String contextType, String key) {

		return getlocalThread(contextType).get(key);
	}

	public Object remove(String contextType, String key) {

		return getlocalThread(contextType).remove(key);

	}

	public void clear() {

		List<String> set = keySet(CACHE_TYPE_TASKS_QUEUE);
		// System.out.println(Thread.currentThread().getName() +
		// " task queue is: "+ set.size()+"\n" + ObjectDigester.toXML(set));

		for (String t : globalCacheMap.keySet()) {

			if (null != t && (set == null || set.size() == 0)) {
				globalCacheMap.clear();
			}
		}
	}

	public List<String> keySet(String contextType) {

		List<String> list = new ArrayList<String>();
		Set<String> keySet = getlocalThread(contextType).keySet();

		for (String s : keySet) {
			list.add(s);
		}

		return list;

	}

	private static Map<String, Object> getlocalThread(String key) {

		Map<String, Object> local = globalCacheMap.get(key);

		if (null == local) {
			local = new HashMap<String, Object>();
			globalCacheMap.put(key, local);
		}

		if (null == globalCacheMap.get(key)) {
			globalCacheMap.put(key, Collections.synchronizedMap(new HashMap<String, Object>()));
		}

		return globalCacheMap.get(key);
	}

	@Override
	public boolean isThreadLocalCache() {
		// always in global cache mode.
		return true;
	}
}

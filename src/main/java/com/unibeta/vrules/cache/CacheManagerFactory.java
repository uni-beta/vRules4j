package com.unibeta.vrules.cache;

import com.unibeta.vrules.cache.impl.SimpleGlobalCacheImpl;
import com.unibeta.vrules.cache.impl.SimpleThreadLocalCacheImpl;

public class CacheManagerFactory {

	private static CacheManager threadLocalCacheManager = null;
	private static CacheManager globalCacheManager = null;

	public static CacheManager getThreadLocalInstance() {

		if (null != threadLocalCacheManager) {
			return threadLocalCacheManager;
		} else {
			threadLocalCacheManager = new SimpleThreadLocalCacheImpl();
			return threadLocalCacheManager;
		}
	}

	public static CacheManager getGlobalCacheInstance() {

		if (null != globalCacheManager) {
			return globalCacheManager;
		} else {
			globalCacheManager = new SimpleGlobalCacheImpl();
			return globalCacheManager;
		}
	}

}

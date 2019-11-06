package com.unibeta.vrules.engines;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface RatingEngine {

	public boolean compile(List<LinkedHashMap<String, String>> ratingDefineDatas);

	public Object lookup(String ratingName, Map<String, String> inputData);

}

package com.lindzh.hasting.cluster;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON对象转换工具
 * @author lindezhi
 *
 */
public class JSONUtils {
	
	private static Logger logger = Logger.getLogger(JSONUtils.class);
	
	private static ObjectMapper objectMapper;
	static {
		objectMapper = new ObjectMapper();
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
	};
	
	public static ObjectMapper getJsonMapper() {
		return objectMapper;
	}

	public static String toJSON(Object obj) {
		try {
			return getJsonMapper().writeValueAsString(obj);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	public static <T> T fromJSON(String json, Class<T> clz) {
		try {
			return getJsonMapper().readValue(json, clz);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	public static <T> T fromJSON(String json, TypeReference<T> typeReference) {
		try {
			return getJsonMapper().readValue(json, typeReference);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

}

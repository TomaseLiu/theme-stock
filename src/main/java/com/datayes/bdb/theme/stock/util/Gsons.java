package com.datayes.bdb.theme.stock.util;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;


public class Gsons {
	static Gson gson = new GsonBuilder().serializeNulls()
			.registerTypeAdapter(Double.class, new DoubleTypeAdapter())
			.create();
	static Gson gsonPretty = new GsonBuilder().serializeNulls()
			.setPrettyPrinting()
			.registerTypeAdapter(Double.class, new DoubleTypeAdapter())
			.create();

	static Gson gsonDate = new GsonBuilder().serializeNulls()
			.registerTypeAdapter(Double.class, new DoubleTypeAdapter())
			.setDateFormat("yyyy-MM-dd").create();
	static Gson gsonPrettyDate = new GsonBuilder().serializeNulls()
			.setDateFormat("yyyy-MM-dd").setPrettyPrinting()
			.registerTypeAdapter(Double.class, new DoubleTypeAdapter())
			.create();

	static JsonParser parser = new JsonParser();

	// builder.;

	public static String toJson(Object obj, boolean pretty) {
		return pretty ? gsonPretty.toJson(obj) : gson.toJson(obj);
	}

	public static String toJson_OnlyDate(Object obj, boolean pretty) {
		return pretty ? gsonPrettyDate.toJson(obj) : gsonDate.toJson(obj);
	}

	public static Gson getGson() {
		return gson;
	}

	public static JsonParser getParser() {
		return parser;
	}

	/**
	 * TypeToken<List<Foo>> list = new TypeToken<List<Foo>>() { };
	 * 
	 * @param json
	 * @param typeToken
	 * @return
	 */
	public static <T> List<T> fromJson(String json, TypeToken<List<T>> typeToken) {
		Type type = typeToken.getType();

		List<T> ret = gson.fromJson(json, type);

		return ret;
	}

	public static <T> T fromJson(String json, Class<T> classOfT) {
		return gson.fromJson(json, classOfT);
	}

	public static String pretty(String json) {
		if (json == null)
			return null;

		JsonElement jsonElement = parser.parse(json);

		return gsonPretty.toJson(jsonElement);
	}

	static class DoubleTypeAdapter implements JsonSerializer<Double> {

		@Override
		public JsonElement serialize(Double d, Type type,
				JsonSerializationContext context) {
//			DecimalFormat format = new DecimalFormat("##0.00");
//			String temp = format.format(d);
			
			JsonPrimitive pri = new JsonPrimitive(FormatUtil.scale(d, 6));
			return pri;
		}

	}

}

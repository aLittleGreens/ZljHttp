package com.klaus.zljhttplib.utils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Description : JSON工具类（目前暂添加了Gson解析，后续再逐渐接入其它第三方解析）
 **/
public class JsonUtils {
    /**
     * map转化为JSONObject
     * @param map
     * @return
     */
    public static JSONObject toJsonObject(Map<String, String> map) {
        try {
            return new JSONObject(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    /**
     * Pojo to json
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        try {
            Gson gson = new Gson();
            return gson.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Pojo to json 空字符串也会局序列化
     * @param obj
     * @return
     */
    public static String toJsonWithNullString(Object obj){
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(String.class, new StringNullAdapter());
            return gsonBuilder.create().toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 转为一维数组jsonArray字符串
     * @param obj
     * @return
     */
    public static String toJsonArray(Object obj) {
        String arrJson = "";
        try {
            Gson gson = new Gson();
            JSONObject json = new JSONObject(gson.toJson(obj));
            arrJson = new JSONArray().put(json).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrJson;
    }

    /**
     * json to pojo
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json to pojo
     *
     * @param json
     * @param typeOfT
     * @return
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(JsonElement json, Type typeOfT) {
        Gson gson = new Gson();
        return gson.fromJson(json, typeOfT);
    }

    /**
     * json字符串数组转化为List集合
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> parseToList(String json, Class<T> clazz) {
        List<T> list = null;
        try {
            Type type = new ParameterizedTypeImpl(clazz);
            list =  new Gson().fromJson(json, type);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * json字符串转化为treeset
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> TreeSet<T> paseToSet(String json,Class<T> clazz) {
        TreeSet<T> ts = null;
        try {
            Type type = new ParameterizedTypeImpl(clazz);
            ts =  new Gson().fromJson(json, type);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ts;
    }

    public static class StringNullAdapter extends TypeAdapter<String> {


        @Override
        public void write(JsonWriter jsonWriter, String s) throws IOException {
            if (s == null) {//序列化使用的是adapter的write方法
                //jsonWriter.nullValue();//这个方法是错的，而是应该将null转成""
                jsonWriter.value("");
                return;
            }
            jsonWriter.value(s);
//            if (s == null) {
//                jsonWriter.nullValue();
//                return;
//            }
//            jsonWriter.value(s);
        }

        @Override
        public String read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {//反序列化使用的是read方法
                jsonReader.nextNull();
                return "";
            }
            return jsonReader.nextString();
        }
    }

    public static class ParameterizedTypeImpl implements ParameterizedType {
        private Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        /**
         * 返回实际类型组成的数据，即new Type[]{String.class,Integer.class}
         * @return
         */
        @NonNull
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        /**
         * getRawType 返回原生类型，即 HashMap
         * @return
         */
        @NonNull
        @Override
        public Type getRawType() {
            return List.class;
        }

        /**
         * 返回 Type 对象，表示此类型是其成员之一的类型。例如，如果此类型为 O<T>.I<S>，则返回 O<T> 的表示形式。
         * 如果此类型为顶层类型，则返回 null。这里就直接返回null就行了。
         * @return
         */
        @Override
        public Type getOwnerType() {
            return null;
        }
    }


}
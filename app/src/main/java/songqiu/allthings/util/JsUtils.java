package songqiu.allthings.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cc on 2018/5/14.
 */

public class JsUtils {

    /**
     * 将js字符串转换为jsobject
     *
     * @param js
     * @return
     */
    public static JSONObject getJsobjectByJsString(String js) {
        if (StringUtil.isEmpty(js)) {
            return null;
        }

        try {
            JSONObject object = new JSONObject(js);
            return object;
        } catch (JSONException e) {
            return null;
        }

    }

    /**
     * 将奇数个转义字符变为偶数个
     * @param s
     * @return
     */
    public static String getDecodeJSONStr(String s){
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
                case '\\':
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
    /**
     * 根据给定的positon 解析jsarray中的jsobject.
     *
     * @param jsonArray
     * @param position
     * @return
     */
    public static JSONObject getJsobjectByPosition(JSONArray jsonArray, int position) {
        if (jsonArray == null || position < 0) {
            return null;
        }

        try {
            JSONObject object = jsonArray.getJSONObject(position);
            return object;
        } catch (JSONException e) {
            return null;
        }

    }

    /**
     * 根据给定的positon 解析jsarray中的jsobject.
     *
     * @param jsonArray
     * @param position
     * @return
     */
    public static JSONArray getJsonArrayByPosition(JSONArray jsonArray, int position) {
        if (jsonArray == null || position < 0) {
            return null;
        }
        try {
            JSONArray object = jsonArray.getJSONArray(position);
            return object;
        } catch (JSONException e) {
            return null;
        }
    }


    /**
     * 根据给定的名字 object中的jsobject.
     *
     * @param name
     * @param object
     * @return
     */
    public static JSONObject getJsobjectByName(String name, JSONObject object) {
        if (object == null || StringUtil.isEmpty(name)) {
            return null;
        }

        try {
            JSONObject object1 = object.getJSONObject(name);
            return object1;
        } catch (JSONException e) {
            return null;
        }

    }


    /**
     * 根据给定的名字充jsobject中解析出对应的值
     *
     * @param name
     * @param object
     * @return
     */
    public static String getValueByName(String name, JSONObject object) {
        if (StringUtil.isEmpty(name) || object == null) {
            return null;
        }
        try {
            String value = object.getString(name);
            return value;
        } catch (JSONException e) {
            return null;
        }
    }


    /**
     * 根据给定的名字充jsobject中解析出对应的int值
     *
     * @param name
     * @param object
     * @return
     */
    public static int getIntByName(String name, JSONObject object) {
        if (StringUtil.isEmpty(name) || object == null) {
            return -1;
        }
        try {
            int value = object.getInt(name);
            return value;
        } catch (JSONException e) {
            return -1;
        }
    }


    /**
     * 根据给定的jsarray返回其长度。如果jsarray为null就返回0；
     *
     * @param jsonArray
     * @return
     */
    public static int getSizeFromJsarray(JSONArray jsonArray) {

        if (jsonArray == null) {
            return 0;
        }

        return jsonArray.length();

    }


    /**
     * 该方法用于向jsobject中添加字符串的数据
     *
     * @param object
     * @param name
     * @param value
     */
    public static void putJsobjectString(JSONObject object, String name, String value) {
        if (object == null || StringUtil.isEmpty(name) || StringUtil.isEmpty(value)) {
            return;
        }

        try {
            object.put(name, value);
        } catch (JSONException e) {
            return;
        }

    }


    /**
     * 该方法用于向jsobject中添加字符串的数据
     *
     * @param object
     * @param name
     * @param value
     */
    public static void putJsobjectLong(JSONObject object, String name, long value) {
        if (object == null || StringUtil.isEmpty(name)) {
            return;
        }

        try {
            object.put(name, value);
        } catch (JSONException e) {
            return;
        }

    }



    /**
     * 根据输入的类型获取到result
     *
     * @param s    js字符串
     * @param type result的类型  jsobject 为 ob  jsarray 为 arr string 为 s
     */
    public static Object getResult(String s, String type) {
        if (StringUtil.isEmpty(s) ||StringUtil.isEmpty(type)) {
            return null;
        }
        JSONObject object = JsUtils.getJsobjectByJsString(s);
        if (object == null) {
            return null;
        }

        int code = JsUtils.getIntByName("statu", object);
        if (code < 0) {
            return null;
        }
        if (code > 0) {
            return null;
        }
        switch (type) {
            case "ob":
                return JsUtils.getJsobjectByName("result", object);

            case "arr":
                return JsUtils.getjsonArrayByName("result", object);

            case "s":
                return JsUtils.getValueByName("result", object);

            default:
                break;
        }

        return null;
    }


    /**
     * 充jsobject中解析出对应名字的jsarray
     *
     * @param name   jsarray的名字
     * @param object jsarray所在的jaonobject
     * @return
     */
    public static JSONArray getjsonArrayByName(String name, JSONObject object) {
        if (StringUtil.isEmpty(name) || object == null) {
            return null;
        }

        try {
            JSONArray array = object.getJSONArray(name);
            return array;
        } catch (JSONException e) {
            return null;
        }

    }


    /**
     * 根据下标得到对应的字符串
     *
     * @param array
     * @param index
     * @return
     */
    public static String getStringByJsArryPosition(JSONArray array, int index) {
        if (array == null || index < 0) {
            return null;
        }

        if (index >= array.length()) {

            return null;
        }
        String path = null;

        try {
            path = (String) array.get(index);
        } catch (JSONException e) {
            return path;
        }

        return path;

    }


    /**
     * 根据传递的S返回用于保存到数据库的jsarray。
     *
     * @param s
     * @return
     */
    public static JSONArray getWebJsarrayByString(String url, String s) {
        JSONArray jsonArray = new JSONArray();
        JSONObject object = new JSONObject();
        try {
            object.put(url, s);
            object.put("time", System.currentTimeMillis());
            jsonArray.put(object);
        } catch (JSONException e) {
            return null;
        }

        return jsonArray;

    }

    /**
     * 把通过接口中的得到的jsonArray，全部添加到 List<JSONObject> list 中，准备进行后续的数据添加到布局中
     *
     * @param jsonArray
     * @return
     */
    public static List<JSONObject> changJsArrayToList(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = JsUtils.getJsobjectByPosition(jsonArray, i);
            if (object != null) {
                list.add(object);
            }
        }

        return list;
    }


    /**
     * 得到long型数据
     *
     * @param name
     * @param jsonObject
     * @return
     */
    public static Long getLongByName(String name, JSONObject jsonObject) {
        if (jsonObject == null) {
            return -1l;
        }
        try {

            long l = jsonObject.getLong(name);

            return l;

        } catch (JSONException e) {

            return -1l;

        }
    }

    /**
     * 根据传递的数据提取出正常的数据
     *
     * @param jsonArray
     * @return
     */
    public static JSONArray getNormalArray(String[] par, JSONArray jsonArray) {
        JSONArray array = new JSONArray();
        if (par == null || jsonArray == null) {
            return array;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = JsUtils.getJsobjectByPosition(jsonArray, i);
            if (object == null) {
                continue;
            }

            for (int j = 0; j < par.length; j++) {

                String values = JsUtils.getValueByName(par[j], object);
                if (StringUtil.isEmpty(values)) {
                    object = null;
                    break;
                }

            }

            if (object != null) {
                array.put(object);
            }

        }

        return array;
    }

    /**
     * 将JSonArray转为字符串
     *
     * @param jsonArray
     * @return
     */
    public static String JSonArrayToString(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        if (jsonArray.length() == 0 || jsonArray.length() < 1) {
            return null;
        }
        String data = jsonArray.toString();
        data = data.substring(1, data.length() - 1);
        return data;
    }

    /**
     * object转json字符串
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /**
     * 将json转为实体类
     *
     * @param json 数据
     * @param obj  实体类
     * @param <T>  泛类型
     * @return
     */
    public static <T> T jsonToEntity(String json, T obj) {
        Gson gson = new Gson();
        return (T) gson.fromJson(json, obj.getClass());
    }

    public static <T> T jsonToEntityList(Object object, Class<T> classOfT) {
        Gson gson = new Gson();
        String data = gson.toJson(object);
        Type type1 = new TypeToken<List<T>>() {
        }.getType();
        ArrayList<T> list = new ArrayList<>();
        list = gson.fromJson(data, type1);
        return (T) list;
    }

    public static void putObject(JSONObject jsonObject, String keyName, Object object) {
        if (jsonObject == null) {
            return;
        }
        try {
            jsonObject.put(keyName, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void  putArray(JSONObject jsonObject, String keyName, JSONArray array){
        if (jsonObject == null) {
            return;
        }
        try {
            jsonObject.put(keyName, array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public  static String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}

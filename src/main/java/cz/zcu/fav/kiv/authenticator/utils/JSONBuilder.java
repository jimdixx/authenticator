package cz.zcu.fav.kiv.authenticator.utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Version 1.0 - only simple json can be built
 * @version 1.0
 * @author Vaclav Hrabik, Jiri Trefil
 */
public class JSONBuilder {

    /*
    public static String buildJson(HashMap<String, String> map){
        if(map == null) {
            return "";
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObject = mapper.createObjectNode();
        for (String key : map.keySet()) {
            jsonObject.put(key,map.get(key));
        }
        return jsonObject.toString();
    }
     */

/**
 * Method transforms map into string representation of JSON object
 * @param json key value pair that will be generated as json line
 * @return String representation of JSON object
 * */

    public static String buildJSON(Map<String,Object> json){

        JSONObject jsonObject = new JSONObject();
        for(String key : json.keySet()){
            jsonObject.put(key,parseJSONValue(json.get(key)));
        }
        String jsonString = jsonObject.toJSONString();

        return jsonString;
    }

    private static Object parseJSONValue(Object value){

        if(value instanceof HashMap<?,?>){
            Map<String,Object> map = null;
            try{
                map = (HashMap<String,Object>) value;
            }
            catch (ClassCastException e){
                throw new RuntimeException("Provided object of HashMap is not <String,Object> typed!");
            }
            JSONObject jsonObject = new JSONObject();
            for(String key : map.keySet())
                jsonObject.put(key,parseJSONValue(map.get(key)));
            return jsonObject;


        }
        if (value instanceof ArrayList){
            JSONArray jsonArray = new JSONArray();
            ArrayList<Object> list;
            list = (ArrayList<Object>) value;
            for(int i = 0,n=list.size(); i < n; i++)
                jsonArray.add(parseJSONValue(list.get(i)));
            return jsonArray;
        }


        //some simple data type without any indentation, we can just return it
        return value;
    }
}

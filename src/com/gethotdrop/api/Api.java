package com.gethotdrop.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Api {
        
		//GET + SET DROPS
        final static private String SERVER_URL = "http://google.com/api/";
        
        //final private String HOTDROP_GET_RADIUS = "hotdrop/getRadius";
        //final private String USER_GET_ID = "user/getId";
        final static private String HOTDROP_GET = "hotdrop/get";
        final static private String HOTDROP_SET = "hotdrop/set";
        
        //device id method store in service (singleton)
        //intent in worker to update things
        //post through the worker and then the worker makes the api call
        //worker makes api call because it's on it's own thread
        //with extras being the message stuff
        
        final static private String CHARSET = "UTF-8";

        public static Map<Integer, Drop> getHotdrops(double latitude_current, double longitude_current) throws IOException, JSONException {
                String url = SERVER_URL + HOTDROP_GET;
                String query;
                query = String.format("latitude=%f&longitude=%f"
                                , latitude_current
                                , longitude_current);

                Map<Integer, Drop> hotdrops = new HashMap<Integer, Drop>();
                
                JSONObject json = new JSONObject(getRequest(url, query));
                JSONObject jsonHotdrops = json.getJSONObject("hotdrops");
                
                Iterator<String> i = jsonHotdrops.keys();
                
                while (i.hasNext()) {
                        Object key = i.next();
                        JSONObject jsonHotdrop = jsonHotdrops.getJSONObject(key.toString());
                        int id = jsonHotdrop.getInt("id");
                        //int userId = jsonHotdrop.getInt("user_id");
                        double latitude = jsonHotdrop.getDouble("latitude");
                        double longitude = jsonHotdrop.getDouble("longitude");
                        String message = jsonHotdrop.getString("message");
                        String createdAt = Long.toString(jsonHotdrop.getLong("created_at") * 1000); 
                        Date updatedAt = new Date(jsonHotdrop.getLong("updated_at") * 1000);
                        Drop hotdrop = new Drop(id, latitude, longitude, message, createdAt, updatedAt, 1);
                        hotdrops.put(id, hotdrop);
                }

                return hotdrops;
        }
        
        public static Drop setHotdrop(double latitude, double longitude, String message) throws IOException, JSONException {
                String url = SERVER_URL + HOTDROP_SET;
                String query;
                query = String.format("latitude=%f&longitude=%f&message=%s"
                                , latitude
                                , longitude
                                , URLEncoder.encode(message, CHARSET));
                System.out.println(query);
                JSONObject json = new JSONObject(postRequest(url, query));
                boolean success = json.getBoolean("success");
                if (success) {
                        JSONObject jsonHotdrop = json.getJSONObject("hotdrop");
                        int id = jsonHotdrop.getInt("id");
                        String createdAt = Long.toString(jsonHotdrop.getLong("created_at")); //Choose String or Date
                        Date updatedAt = new Date(jsonHotdrop.getLong("updated_at"));
                        Drop hotdrop = new Drop(id, latitude, longitude, message, createdAt, updatedAt, 1);
                        return hotdrop;
                } else
                        return null;
        }
        
        private static String getRequest(String url, String query) throws IOException {
                URLConnection connection = new URL(url + "?" + query).openConnection();
                connection.setRequestProperty("Accept-Charset", CHARSET);
                //connection.setRequestProperty("accept", "application/json");
                InputStream response = connection.getInputStream();
                
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                        reader = new BufferedReader(new InputStreamReader(response));
                        while ((line = reader.readLine()) != null) {
                                sb.append(line);
                        }
                }
                finally {
                        if (reader != null) {
                                reader.close();
                        }
                }
        
                return sb.toString();
        }
        
        private static String postRequest(String url, String query) throws IOException { //wrap in an async task
                URLConnection connection = new URL(url).openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept-Charset", CHARSET);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
                OutputStream output = null;
                try {
                     output = connection.getOutputStream(); 
                     output.write(query.getBytes(CHARSET));
                } finally {
                     if (output != null) output.close();
                }
                InputStream response = connection.getInputStream();
                
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                        reader = new BufferedReader(new InputStreamReader(response));
                        while ((line = reader.readLine()) != null) {
                                sb.append(line);
                        }
                }
                finally {
                        if (reader != null) {
                                reader.close();
                        }
                }
                return sb.toString();
        }
}

//private String deviceId;
//
//public Api(String deviceId) {
//      this.deviceId = deviceId;
//}

//public User getUser() throws IOException, JSONException {
//String url = SERVER_URL + USER_GET_ID;
//String query;
//query = String.format("device_id=%s", URLEncoder.encode(deviceId, CHARSET));
//
//JSONObject json = new JSONObject(getRequest(url, query));
//
//boolean success = json.getBoolean("success");
//if (success) {
//      int userId = json.getInt("user_id");
//      return new User(userId);
//}
//else
//      return null;
//}

//public double getRadius() throws IOException, JSONException {
//String url = SERVER_URL + HOTDROP_GET_RADIUS;
//String query;
//query = String.format("device_id=%s", URLEncoder.encode(deviceId, CHARSET));
//
//JSONObject json = new JSONObject(getRequest(url, query));
//
//boolean success = json.getBoolean("success");
//if (success) {
//      double radius = json.getDouble("radius");
//      return radius;
//}
//else
//      return -1;
//}

//public boolean isPrize(int drop_id) throws IOException, JSONException {
//String url = SERVER_URL + "hotdrop/isPrize";
//String query = String.format("drop_id=%s", drop_id);
//
//JSONObject json = new JSONObject(getRequest(url, query));
//return json.getBoolean("prize");
//}
//
//public boolean claimPrize(String email, int drop_id) throws IOException, JSONException {
//String url = SERVER_URL + "hotdrop/isPrize";
//String query = String.format("email=%s&drop_id=%s", drop_id);
//
//JSONObject json = new JSONObject(getRequest(url, query));
//return json.getBoolean("success");
//}
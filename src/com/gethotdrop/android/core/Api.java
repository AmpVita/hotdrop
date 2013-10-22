package com.gethotdrop.android.core;

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
        
        final private String SERVER_URL = "http://hotdrop-env-pz2qpriirq.elasticbeanstalk.com/api/";
        
        final private String USER_GET_ID = "user/getId";
        final private String HOTDROP_GET = "hotdrop/get";
        final private String HOTDROP_GET_RADIUS = "hotdrop/getRadius";
        final private String HOTDROP_SET = "hotdrop/set";
        
        final private String CHARSET = "UTF-8";
        
        private String deviceId;
        
        public Api(String deviceId) {
                this.deviceId = deviceId;
        }
        
        public User getUser() throws IOException, JSONException {
                String url = SERVER_URL + USER_GET_ID;
                String query;
                query = String.format("device_id=%s", URLEncoder.encode(deviceId, CHARSET));
                
                JSONObject json = new JSONObject(getRequest(url, query));

                boolean success = json.getBoolean("success");
                if (success) {
                        int userId = json.getInt("user_id");
                        return new User(userId);
                }
                else
                        return null;
        }
        
        public double getRadius() throws IOException, JSONException {
                String url = SERVER_URL + HOTDROP_GET_RADIUS;
                String query;
                query = String.format("device_id=%s", URLEncoder.encode(deviceId, CHARSET));
                
                JSONObject json = new JSONObject(getRequest(url, query));
                
                boolean success = json.getBoolean("success");
                if (success) {
                        double radius = json.getDouble("radius");
                        return radius;
                }
                else
                        return -1;
        }

        public Map<Integer, Drop> getHotdrops(double latitude_current, double longitude_current, double radius) throws IOException, JSONException {
                String url = SERVER_URL + HOTDROP_GET;
                String query;
                query = String.format("device_id=%s&latitude=%f&longitude=%f&radius=%f"
                                , URLEncoder.encode(deviceId, CHARSET)
                                , latitude_current
                                , longitude_current
                                , radius);

                Map<Integer, Drop> hotdrops = new HashMap<Integer, Drop>();
                
                JSONObject json = new JSONObject(getRequest(url, query));
                JSONObject jsonHotdrops = json.getJSONObject("hotdrops");
                
                Iterator<String> i = jsonHotdrops.keys();
                
                while (i.hasNext()) {
                        Object key = i.next();
                        JSONObject jsonHotdrop = jsonHotdrops.getJSONObject(key.toString());
                        int id = jsonHotdrop.getInt("id");
                        int userId = jsonHotdrop.getInt("user_id");
                        double latitude = jsonHotdrop.getDouble("latitude");
                        double longitude = jsonHotdrop.getDouble("longitude");
                        String message = jsonHotdrop.getString("message");
                        Date createdAt = new Date(jsonHotdrop.getLong("created_at") * 1000); 
                        Date updatedAt = new Date(jsonHotdrop.getLong("updated_at") * 1000);
                        Drop hotdrop = new Drop(id, userId, latitude, longitude, message, createdAt, updatedAt);
                        hotdrops.put(id, hotdrop);
                }

                return hotdrops;
        }
        
        public boolean isPrize(int drop_id) throws IOException, JSONException {
                String url = SERVER_URL + "hotdrop/isPrize";
                String query = String.format("drop_id=%s", drop_id);
                
                JSONObject json = new JSONObject(getRequest(url, query));
                return json.getBoolean("prize");
        }
        
        public boolean claimPrize(String email, int drop_id) throws IOException, JSONException {
                String url = SERVER_URL + "hotdrop/isPrize";
                String query = String.format("email=%s&drop_id=%s", drop_id);
                
                JSONObject json = new JSONObject(getRequest(url, query));
                return json.getBoolean("success");
        }
        
        public Drop setHotdrop(double latitude, double longitude, String message) throws IOException, JSONException {
                String url = SERVER_URL + HOTDROP_SET;
                String query;
                query = String.format("device_id=%s&latitude=%f&longitude=%f&message=%s"
                                , URLEncoder.encode(deviceId, CHARSET)
                                , latitude
                                , longitude
                                , URLEncoder.encode(message, CHARSET));
                System.out.println(query);
                JSONObject json = new JSONObject(postRequest(url, query));
                boolean success = json.getBoolean("success");
                if (success) {
                        JSONObject jsonHotdrop = json.getJSONObject("hotdrop");
                        int id = jsonHotdrop.getInt("id");
                        int userId = jsonHotdrop.getInt("user_id");
                        Date createdAt = new Date(jsonHotdrop.getLong("created_at")); 
                        Date updatedAt = new Date(jsonHotdrop.getLong("updated_at"));
                        Drop hotdrop = new Drop(id, userId, latitude, longitude, message, createdAt, updatedAt);
                        return hotdrop;
                } else
                        return null;
        }
        
        private String getRequest(String url, String query) throws IOException {
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
        
        private String postRequest(String url, String query) throws IOException {
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
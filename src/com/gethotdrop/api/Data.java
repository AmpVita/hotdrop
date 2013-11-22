package com.gethotdrop.api;

import java.util.Map;

import org.json.JSONObject;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class Data {
	final static private String BASE = "ec2-54-234-23-75.compute-1.amazonaws.com:8000/api/";
	
	
	final static private String GET_DROPS = "drops/";
	final static private String SET_DROP = "drops/create";

	private static Data instance;
	Context context;
	RequestQueue queue = Volley.newRequestQueue(context);

	public static Map<Integer, Drop> getDrops(double latitude_cur, double longitude_cur) {
		
		return null;
	
	}
	private Data(Service is) {
		context = is;
	}
	
	public static Data getData(IntentService is) {
		if (instance == null) {
			instance = new Data(is);
		}
		return instance;
	}
}

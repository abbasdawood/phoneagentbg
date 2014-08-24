package com.listener.bgservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import com.red_folder.phonegap.plugin.backgroundservice.BackgroundService;

public class BackgroundListener extends BackgroundService {

	@Override
	protected JSONObject initialiseLatestResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected JSONObject doWork() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
		String now = df.format(new Date(System.currentTimeMillis()));
		JSONObject r = new JSONObject();
		try {
			r.put("Timestamp", now.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}

	@Override
	protected JSONObject getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setConfig(JSONObject config) {
		// TODO Auto-generated method stub
		
	}

}

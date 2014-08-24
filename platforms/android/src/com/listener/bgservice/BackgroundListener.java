package com.listener.bgservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.util.Log;
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
		// TODO Auto-generated method stub
		JSONObject result = new JSONObject();
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String now = df.format(new Date(System.currentTimeMillis()));
		String msg = "Hello world, time now:"+now;
		Log.d("BGService",msg);
		try {
			result.put("Message", msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
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

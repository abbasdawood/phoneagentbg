package com.listener.bgservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.red_folder.phonegap.plugin.backgroundservice.BackgroundService;

public class BackgroundListener extends BackgroundService {

	TelephonyManager Tel;
	GsmCellLocation cellLocation;
	public static String imei;
	public static String operator;
	private static List<NeighboringCellInfo> NeighboringList;
	public static int cellID;
	public static int lac;
	ConnectivityManager manager;

	public static final String WIFI = "wifi";
	public static final String MOBILE = "mobile";

	@Override
	protected JSONObject initialiseLatestResult() {
		// TODO Auto-generated method stub
		this.Tel = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		this.cellLocation = (GsmCellLocation) Tel.getCellLocation();

		this.manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		return null;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected JSONObject doWork() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String now = df.format(new Date(System.currentTimeMillis()));
		JSONObject r = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			r.put("Timestamp", now.toString());
			r.put("imei", this.Tel.getDeviceId());
			r.put("imsi", this.Tel.getSubscriberId());
			r.put("operator", this.Tel.getNetworkOperatorName());
			r.put("cellID", this.cellLocation.getCid());
			r.put("lac", this.cellLocation.getLac());
			r.put("currentSignal", this.getCurrentStrength());
			r.put("networkType", this.networkType());
			r.put("activeNetwork", this.getActiveConnectionInfo());

			r.put("neighbors", this.getNeighbours());

			data.put("sent", TrafficStats.getTotalTxBytes());
			data.put("recd", TrafficStats.getTotalRxBytes());
			r.put("data", data);


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}

	private Object getWifiState() {
		// TODO Auto-generated method stub
		return null;
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

	@SuppressLint("NewApi")
	public int getCurrentStrength() {
		CellInfoGsm cellInfoGsm =  (CellInfoGsm) this.Tel.getAllCellInfo().get(0);
		CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
		return cellSignalStrengthGsm.getDbm();
	}

	public JSONObject getNeighbours() {
		this.setNeighboringList(this.Tel.getNeighboringCellInfo());
		JSONObject neighborsArray = new JSONObject();
		for (int i = 0; i < getNeighboringList().size(); i++) {
			JSONObject neighbors = new JSONObject();
			String dbM = null, cid = null, lac = null;
			cid = String.valueOf(getNeighboringList().get(i).getCid());
			lac = String.valueOf(getNeighboringList().get(i).getLac());
			int rssi = getNeighboringList().get(i).getRssi();
			if (rssi == NeighboringCellInfo.UNKNOWN_RSSI)
				dbM = "Unknown RSSI";
			else
				dbM = String.valueOf(-113 + 2 * rssi) + " dBm";
			try {

				if (getNeighboringList().get(i).getCid() != -1
						&& getNeighboringList().get(i).getCid() != 65535
						&& rssi != 99 && getNeighboringList().get(i).getLac() != 0) {
					neighbors.put("rssi", dbM);
					neighbors.put("cid", cid);
					neighbors.put("lac", lac);
				}

				neighborsArray.put(String.valueOf(i), neighbors);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return neighborsArray;
	}

	public static List<NeighboringCellInfo> getNeighboringList() {
		return NeighboringList;
	}

	public void setNeighboringList(List<NeighboringCellInfo> neighboringList) {
		NeighboringList = neighboringList;
	}

	public String networkType(){
		int type = this.Tel.getNetworkType();
		switch (type) {
		case TelephonyManager.NETWORK_TYPE_1xRTT: return "1xRTT";
		case TelephonyManager.NETWORK_TYPE_CDMA: return "CDMA";
		case TelephonyManager.NETWORK_TYPE_EDGE: return "EDGE";
		case TelephonyManager.NETWORK_TYPE_EHRPD: return "eHRPD";
		case TelephonyManager.NETWORK_TYPE_EVDO_0: return "EVDO rev. 0";
		case TelephonyManager.NETWORK_TYPE_EVDO_A: return "EVDO rev. A";
		case TelephonyManager.NETWORK_TYPE_EVDO_B: return "EVDO rev. B";
		case TelephonyManager.NETWORK_TYPE_GPRS: return "GPRS";
		case TelephonyManager.NETWORK_TYPE_HSDPA: return "HSDPA";
		case TelephonyManager.NETWORK_TYPE_HSPA: return "HSPA";
		case TelephonyManager.NETWORK_TYPE_HSPAP: return "HSPA+";
		case TelephonyManager.NETWORK_TYPE_HSUPA: return "HSUPA";
		case TelephonyManager.NETWORK_TYPE_IDEN: return "iDen";
		case TelephonyManager.NETWORK_TYPE_LTE: return "LTE";
		case TelephonyManager.NETWORK_TYPE_UMTS: return "UMTS";
		case TelephonyManager.NETWORK_TYPE_UNKNOWN: return "Unknown";
		}
		return null;
	}

	public String getActiveConnectionInfo(){
		NetworkInfo info = this.manager.getActiveNetworkInfo();
		String type = info.getTypeName();
		if(type.toLowerCase().equals(WIFI))
		{return WIFI;}
		else
		{return MOBILE;}
	}

}

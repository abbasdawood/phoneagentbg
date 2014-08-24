package com.abbasdawood.cordova.signal;

import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class Signal extends CordovaPlugin {

	TelephonyManager Tel;
	GsmCellLocation cellLocation;
	MyPhoneStateListener MyListener;
	public static String imei;
	public static String operator;
	private static List<NeighboringCellInfo> NeighboringList;
	public static int cellID;
	public static int lac;

	public Signal() {
		
	}

	/**
	 * Sets the context of the Command. This can then be used to do things like
	 * get file paths associated with the Activity.
	 *
	 * @param cordova
	 *            The context of the main Activity.
	 * @param webView
	 *            The CordovaWebView Cordova is running in.
	 */
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		/* Get the telephony service from the native code, the context will be the cordova application
		 * that is invoking the service
		 */
		this.Tel = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		this.cellLocation = (GsmCellLocation) Tel.getCellLocation();
	}
	
	/**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getSignalInfo")) {
            JSONObject r = new JSONObject();
            r.put("imei", this.getMobileIdentifier());
            r.put("operator", this.getOperator());
            r.put("cellID", this.getCellID());
            r.put("lac", this.getLac());
            r.put("neighbors", this.getNeighbours());
            callbackContext.success(r);
        }
        else {
            return false;
        }
        return true;
    }
	
    /*All the local methods to give results back to cordova app
     * 1. getMobileIdentifier()
     * 2. getOperator()
     * 3. getCellID()
     * 4. getLac()
     * 5. getNeighbours()
     */
    
    public String getMobileIdentifier(){
    	return this.Tel.getDeviceId();
    }
    
    public String getOperator(){
    	return this.Tel.getNetworkOperatorName();
    }
    
    public int getCellID(){
    	return this.cellLocation.getCid();
    }
    
    public int getLac(){
    	return this.cellLocation.getLac();
    }
    
    public JSONArray getNeighbours(){
		this.setNeighboringList(this.Tel.getNeighboringCellInfo());
    	JSONArray neighborsArray = new JSONArray();
    	for(int i=0; i< getNeighboringList().size(); i++){
    		JSONObject neighbors = new JSONObject();
    		String dbM = null,
    				cid = null,
    				lac = null;
    		cid = String.valueOf(getNeighboringList().get(i).getCid());
    		lac = String.valueOf(getNeighboringList().get(i).getLac());
    		int rssi = getNeighboringList().get(i).getRssi();
    		if(rssi == NeighboringCellInfo.UNKNOWN_RSSI)
    			dbM = "Unknown RSSI";
    		else
    			dbM = String.valueOf(-113 + 2 * rssi) + " dBm";
    		try {
				neighbors.put("rssi", dbM);
				neighbors.put("cid", cid);
	    		neighbors.put("lac", lac);
	    		neighborsArray.put(neighbors);
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


	private class MyPhoneStateListener extends PhoneStateListener {

	}
}

var app = {

    backgroundService: null,
    myFirebaseRef: null,
    location: {},
    accelerometer: {},
    device: {},
    neighbors: {},
    advanced: {},
    battery: {},
    network: null,

    // Application Constructor
    initialize: function() {
        this.bindEvents();
        app.myFirebaseRef = new Firebase("https://boiling-heat-3431.firebaseio.com/")
        app.myFirebaseRef.push();
    },

    // Bind Event Listeners
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },

    onDeviceReady: function() {
        var serviceName = 'com.listener.bgservice.BackgroundListener';
        var factory = cordova.require('com.red_folder.phonegap.plugin.backgroundservice.BackgroundService');
        app.backgroundService = factory.create(serviceName);
        console.log('calling senddata');

        window.addEventListener('watchingnetwork', app.getNetworkInfo, false);
        window.addEventListener('batterystatus', app.getBatteryInfo, false);
        navigator.accelerometer.getCurrentAcceleration(app.onAccSuccess, app.onError);
        app.checkConnection();
        app.getDeviceName();
        app.getLocationInfo();

        window.setTimeout(app.sendData(), 5000);

    },

    //Function to send - If background service is running, start the service
    sendData: function() {
        console.log('starting senddata');
        app.backgroundService.getStatus(app.startService, app.displayError);
    },

    //Get the status of the background service
    getStatus: function() {
        app.backgroundService.getStatus(function(r) {
            alert("Service running status:" + r.ServiceRunning);
        }, app.displayError);
    },

    //Start the service if the service is running, else start the service
    startService: function(data) {
        if (data.ServiceRunning) {
            enableTimer(data);
        } else {
            app.backgroundService.startService(app.enableTimer, app.displayError);
        }
    },

    enableTimer: function(data) {
        if (data.TimerEnabled) {
            app.registerForUpdates(data);
        } else {
            var frequency = app.location.speed > 0 ? 1200000 : 600000;
            console.log("Frequency:" + frequency);
            app.backgroundService.enableTimer(frequency, app.registerForUpdates, app.displayError);
        }
    },

    registerForUpdates: function(data) {
        if (!data.RegisteredForUpdates) {
            app.backgroundService.registerForUpdates(app.updateHandler, app.displayError);
        }
    },


    //Do the main work here: 1. Update the view & 2. Sync the data with the data store
    updateHandler: function(data) {
        if (data.LatestResult != null) {
            //Call all the functions here
            var parent = document.getElementById('parent');
            parent.innerHTML = "<core-item icon='device:access-time' label='Last Sync at: " +
                data.LatestResult.Timestamp + "'></core-item>";

            console.log("Data incoming:" + JSON.stringify(data));
            //Commit it to the database
            app.myFirebaseRef.push({
                "timestamp": data.LatestResult.Timestamp,
                "accelerometer": app.accelerometer,
                "neighbors": app.neighbors,
                "network": app.network,
                "battery": {
                    "level": app.battery.level,
                    "isPlugged": app.battery.isPlugged
                },
                "networkdata": {
                    "imei": app.advanced.imei,
                    "operator": app.advanced.operator,
                    "cellId": app.advanced.cellID,
                    "lac": app.advanced.lac
                },
                "device": app.device,
                "location": {
                    "latitude": app.location.latitude,
                    "longitude": app.location.longitude,
                    "altitude": app.location.altitude,
                    "accuracy": app.location.accuracy,
                    "altitude-accuracy": app.location.altitudeAccuracy,
                    "heading": app.location.heading.toString(),
                    "speed": app.location.speed
                }
            });


        }
    },

    //Error handling
    displayError: function(error) {
        console.log("Error:" + JSON.stringify(error));
    },

    /*Geo location functions*/
    /* Geolocation function */
    getLocationInfo: function() {
        navigator.geolocation.getCurrentPosition(app.geolocationSuccess, app.geolocationError, {
            timeout: 30000,
            maximumAge: 0,
            enableHighAccuracy: false
        });
    },

    geolocationSuccess: function(position) {
        app.location = position.coords;
        console.log('Geolocation success callback');
    },

    geolocationError: function(error) {
        alert('Geolocation error code:' + error.code + ' reason:' + error.message);
    },

    /*Advanced Parameters*/
    getNetworkInfo: function(info) {
        app.advanced = info;
        app.neighbors = info.neighbors;
    },

    /*Accelerometer */
    onAccSuccess: function(acceleration) {
        app.accelerometer = acceleration;
    },

    /*Battery Info*/
    getBatteryInfo: function(info) {
        app.battery = info;
    },

    /*Get device info*/
    getDeviceName: function() {
        app.device = device;
    },

    /*Check connection info*/
    checkConnection: function() {
        var networkState = navigator.connection.type;

        var states = {};
        states[Connection.UNKNOWN] = 'Unknown ';
        states[Connection.ETHERNET] = 'Ethernet ';
        states[Connection.WIFI] = 'WiFi ';
        states[Connection.CELL_2G] = 'Cellular - 2G ';
        states[Connection.CELL_3G] = 'Cellular - 3G ';
        states[Connection.CELL_4G] = 'Cellular - 4G ';
        states[Connection.CELL] = 'Cellular ';
        states[Connection.NONE] = 'Not Connected ';

        app.network = states[networkState];
    }
};
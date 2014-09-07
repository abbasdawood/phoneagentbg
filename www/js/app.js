var app = {

    backgroundService: null,
    myFirebaseRef: null,
    location: {},
    device: {},
    neighbors: {},
    battery: {},

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
        window.addEventListener('batterystatus', app.getBatteryInfo, false);
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
            var frequency = app.location.speed > 0 ? 10000 : 60000;
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
            var pushed = app.myFirebaseRef.push({
                "timestamp": data.LatestResult.Timestamp,
                "data": data.LatestResult.data,
                "neighbors": data.LatestResult.neighbors,
                "network": data.LatestResult.networkType,
                "activeNwk": data.LatestResult.activeNetwork,
                "battery": {
                    "level": app.battery.level,
                    "isPlugged": app.battery.isPlugged
                },
                "networkdata": {
                    "imei": data.LatestResult.imei,
                    "operator": data.LatestResult.operator,
                    "cellId": data.LatestResult.cellID,
                    "lac": data.LatestResult.lac,
                    "imsi": data.LatestResult.imsi,
                    "currentSignal": data.LatestResult.currentSignal
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

            console.log('Firebase returns:' + pushed.toString());

            if (pushed != null) {
                app.myFirebaseRef.on('child_added', function(snapshot) {
                    var e = document.createElement('div');
                    e.innerHTML = '<core-item icon="device:access-time" label="Pushed at ' + snapshot.timestamp + '"></core-item>';
                    parent.appendChild(d);
                    console.log("Child Added: " + e.toString());
                });
            } else {
                var d = document.createElement('div');
                d.innerHTML = '<core-item icon="error" label="Error pushing at ' + data.LatestResult.Timestamp + '"></core-item>';
                parent.appendChild(d);
                console.log("Child Added: " + d.toString());
            }

        }
    },

    //Error handling
    displayError: function(error) {
        console.log("Error:" + JSON.stringify(error));
    },

    /*Geo location functions*/
    /* Geolocation function */
    getLocationInfo: function() {
        var watch = navigator.geolocation.watchPosition(app.geolocationSuccess, app.geolocationError, {
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
        console.log('Geolocation error' + error.toString());
    },

    /*Battery Info*/
    getBatteryInfo: function(info) {
        app.battery = info;
    },

    /*Get device info*/
    getDeviceName: function() {
        app.device = device;
    }
};
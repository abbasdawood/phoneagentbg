var app = {

    backgroundService: null,
    myFirebaseRef: null,

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
        app.sendData();
        //app.getStatus();
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
            app.backgroundService.enableTimer(10000, app.registerForUpdates, app.displayError);
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
            var parent = document.getElementById('parent');
            parent.innerHTML = data.LatestResult.Message;
            alert(JSON.stringify(data.LatestResult.Message));
            //create the JSON Object

            //Commit it to the database
            /*app.myFirebaseRef.push({
                "Message": data.LatestResult.Message
            });*/

        }
    },

    //Error handling
    displayError: function(error) {
        console.log("Error:" + JSON.stringify(error));
    }
};
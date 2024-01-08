# RideSharingApp

A Simple Ride Sharing App.

## How to run our app

### 1. Create a Firebase project

Follow [this tutorial](https://firebase.google.com/docs/android/setup) to create a firebase project and add both the passenger-side and driver-side app to the project. Download the `google-service.json` file and add it to both [driversideapp](/driversideapp/) and [passengersideapp](/passengersideapp/) folders.

### 2. Create a Google Cloud project and get a Google Maps API key

Follow [this tutorial](https://developers.google.com/maps/documentation/android-sdk/cloud-setup) to create a Google Cloud project and follow [this](https://developers.google.com/maps/documentation/android-sdk/get-api-key) to get a Google Maps API key. After that, open [local.properties](/local.properties) and add this line at the end of it:

    MAPS_API_KEY=YOUR_API_KEY

replace YOUR_API_KEY with the Maps API key you just obtained from the previous step.

### 3. Create a Stream Chat project

Go to [getstream.io](https://getstream.io/) and click `START CODING FREE`. Follow their instruction to create a stream account and enable Chat and Livestream service. Next, in the dashboard, click on your app name and copy the App Access Key. Open [local.properties](/local.properties) and add the line:

    STREAM_API_KEY=YOUR_API_KEY

paste the App Access Key in place of YOUR_API_KEY.

After you have done step 2 and 3, your [local.properties](/local.properties) file should look like the [local.defaults.properties](/local.defaults.properties) file. That's all setup you'd need to run this app.

### 4. Build and run app

Open Android Studio and build both driver-side app and passenger-side app. Note that, you need to run each app on a different device or android emulator. If you run both of them on the same device, the apps won't be able to connect to a ride.

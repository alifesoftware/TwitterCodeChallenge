# Twitter Code Challenge

Twitter code challenge involved developing **Tweather** app for which requirements were provided. 


# Design
The app is designed using two modules:
1. tweathersdk - SDK that wraps all weather related functionality
2. app - Lightweight app that uses the SDK to fetch and display weather data

For code design choices and reasons, **please read comments that are in the code** as well as additional (high level) information below.

## tweathersdk

I decided to take a SDK approach to wrap all weather related APIs and network code in a separate module as a good design practice. Usually it is better to make self contained modules for specific functionality (which is getting weather data in this case) as it not only makes code more organized, it also means other apps can be developed using the module.

In this case there was only one weather data provider, but hypothetically if there were more weather providers, this SDK would be an even better choice as it would implement multiple weather providers with a common interface and a Factory pattern. So this SDK was designed by thinking big and forward, like it was a real world project.

SDK response events are published on EventBus, so the code between the SDK and App is loosely coupled. The other option was to use callbacks using interfaces, but having a Pub-Sub/EventBus based design makes the code more robust and clean.

## app
App module is a lightweight application that uses tweathersdk  to display weather data. This app is developed using a MVP pattern. 

At launch, this app fires a current weather request to the SDK via the Presenter and then updates the View once data (Model) is received from the SDK. This brings forward an interesting use case - what if the user is offline when app is launched? To handle this case, I added a network connectivity BroadcastReceiver. When network transition is made from offline state to online state, it fires a SDK request to get current weather data. There are some pros and cons using this approach, so please read comments in file **NetworkStateReceiver.java** and **NetworkStateReceiver.onReceive()** method to better understand my design choice.

To show the cloud icon in the app, I decided to use a local drawable asset. To me making another network call to get a static icon didn't appear right. Had there been more images to use in the app, I would have used Picasso library to lazy load images from cloud to the app.

## Open Source / 3rd Party Libraries Used

1. GSON by Google - GSON is used to convert JSON data directly to Java Object.
2. OkHttp by Square - OkHttp is popular library to make network requests. Another alternative was to use Retrofit.
3. EventBus by GreenRobot - EventBus is used a classic Pub-Sub mechanism to communicate between the SDK and the App.
4. JsonSchemaToPojo (http://www.jsonschema2pojo.org/) - To generate classes for Weather data POJO using response JSON.

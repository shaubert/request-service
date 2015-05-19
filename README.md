# Request Service

Android service for executing requests. Provides core classes for parcelable Request and Response, Service for execution with in-memory queue for synchronous requests, and base class RSEvent if you supporting eventbus in your app.  

## Gradle
    
    repositories {
        maven{url "https://github.com/shaubert/maven-repo/raw/master/releases"}
    }
    dependencies {
        compile 'com.shaubert.network.service:library:1.0.3'
    }

## Requirements

Android API >= 11

## Basic How-to

1.  In your `Application.onCreate()` you have to setup `ServiceConfig` with `newBuilder()` call. You can provide your implementations of following parameters (defaults are Void* objects except executor, which is `DefaultMainThreadExecutor`):
  *  RSBus for eventbus;
  *  RSCache for caching responses;
  *  RSInjector for injecting objects in request before execution;
  *  RSExecutor executor for requests;
  *  RSTracker tracker to log request errors and execution times;
  *  RSTimeTable request execution timetable. To limit the frequency of requests. You can use provided `ExecutionTimeTable` class.
2.  Add service declaration to your `AndroidManifest.xml`:

        <service android:name="com.shaubert.network.service.RequestService" android:exported="false"/>

3.  Extend from `Request` and `Response`. In `produceEvent` method you can return `DefaultEvent` if you are not working with eventbus.
4.  Call `Request.startOnService()`.

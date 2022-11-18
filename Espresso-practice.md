## Espresso setup instructions

To avoid flakiness, we highly recommend that you turn off system animations on the virtual or physical devices used for testing. 
On your device, under `Settings > Developer options`, disable the following 3 settings:

- Window animation scale

- Transition animation scale

- Animator duration scale

### Run tests

**From the command line**

Execute the following Gradle command:

```
./gradlew connectedAndroidTest
```

## Example idling resource implementations

The following list describes several example implementations of idling resources that you can integrate into your app:

- CountingIdlingResource

>Maintains a counter of active tasks. When the counter is zero, the associated resource is considered idle. 
This functionality closely resembles that of a Semaphore. 
In most cases, this implementation is sufficient for managing your app's asynchronous work during testing.

- UriIdlingResource
>Similar to CountingIdlingResource, but the counter needs to be zero for a specific period of time before the resource is considered idle. 
This additional waiting period takes consecutive network requests into account, 
where an app in your thread might make a new request immediately after receiving a response to a previous request.

- IdlingThreadPoolExecutor
>A custom implementation of ThreadPoolExecutor that keeps track of the total number of running tasks within the created thread pools. 
This class uses a CountingIdlingResource to maintain the counter of active tasks.

- IdlingScheduledThreadPoolExecutor
>A custom implementation of ScheduledThreadPoolExecutor. 
It provides the same functionality and capabilities as the IdlingThreadPoolExecutor class, 
but it can also keep track of tasks that are scheduled for the future or are scheduled to execute periodically.
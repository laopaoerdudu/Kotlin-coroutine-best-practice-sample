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
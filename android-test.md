## ActivityScenarioRule

>To access the given activity in your test logic, provide a callback runnable to ActivityScenarioRule.getScenario().onActivity().

```

@RunWith(AndroidJUnit4::class.java)
@LargeTest
class MyClassTest {
  @get:Rule
  val activityRule = ActivityScenarioRule(MyClass::class.java)

  @Test fun myClassMethod_ReturnsTrue() {
    activityRule.scenario.onActivity { … } // Optionally, access the activity.
   }
}
```

Ref: https://developer.android.com/guide/components/activities/testing

## FragmentScenario

>in order to test fragments in isolation, you can use the FragmentScenario class

Ref: https://developer.android.com/guide/fragments/test

## ServiceTestRule

>This rule doesn't support `IntentService`. 
> This is because the service is destroyed when `IntentService.onHandleIntent(Intent)` finishes all outstanding commands, 
>so there is no guarantee to establish a successful connection in a timely manner.

```
@RunWith(AndroidJUnit4::class.java)
@MediumTest
class MyServiceTest {
  @get:Rule
  val serviceRule = ServiceTestRule()

  @Test fun testWithStartedService() {
    serviceRule.startService(
      Intent(ApplicationProvider.getApplicationContext<Context>(),
      MyService::class.java))
    // Add your test code here.
  }

  @Test fun testWithBoundService() {
    val binder = serviceRule.bindService(
      Intent(ApplicationProvider.getApplicationContext(),
      MyService::class.java))
    val service = (binder as MyService.LocalBinder).service
    assertThat(service.doSomethingToReturnTrue()).isTrue()
  }
}
```

## AndroidJUnitRunner

>lets you run instrumented JUnit 4 tests on Android devices, including those using the `Espresso`, `UI Automator`, and Compose testing frameworks.

**This test runner supports several common testing tasks, including the following:**

- Writing JUnit tests

```
@Test 
fun changeText_sameActivity() {
	 // GIVEN: type text
	 onView(withId(R.id.editTextUserInput))
	 .perform(typeText(stringToBeTyped), closeSoftKeyboard())
	 
	 // WHEN: press the button
	 onView(withId(R.id.changeTextBt)).perform(click())

	 // THEN: check that the text was changed.
	 onView(withId(R.id.textToBeChanged))
	 .check(matches(withText(stringToBeTyped)))
 }
```

- Accessing the app's context

When you use `AndroidJUnitRunner` to run your tests, you can access the context for the app under test by calling the static 
`ApplicationProvider.getApplicationContext()` method.
If you've created a custom subclass of Application in your app, this method returns your custom subclass's context.
If you're a tools implementer, you can access low-level testing APIs using the `InstrumentationRegistry` class. 
This class includes the Instrumentation object, the target app Context object, the test app Context object, 
and the command line arguments passed into your test.

- Filtering tests

`@RequiresDevice`, `@SDKSuppress(minSdkVersion=23)`, `@SmallTest`, `@MediumTest`, and `@LargeTest`

- Sharding tests

>The test runner supports splitting a single test suite into multiple shards to make them run faster
>Each shard is identified by an index number. 
>use the `-e numShards` option to specify the number of separate shards
>the `-e shardIndex` option to specify which shard to run.

```
adb shell am instrument -w -e numShards 10 -e shardIndex 2
```

**Note: If you don't know your target instrumentation, you can look it up by running the following command:**

```
adb shell pm list instrumentation
```






































## 使用 `suspendCancellableCoroutine` 创建一个挂起函数

`suspendCancellableCoroutine` 它非常适合将基于回调的 API 转换为协程。

它有另一个 不可取消 版本 (`suspendCoroutine`)，但最好还是一律选择 `suspendCancellableCoroutine` 来处理协程作用域的取消操作，或从底层 API 传播取消操作。

```
suspend fun FusedLocationProviderClient.awaitLastLocation(): Location =
   suspendCancellableCoroutine<Location> { continuation ->
       lastLocation.addOnSuccessListener { location ->
           continuation.resume(location)
       }.addOnFailureListener { e ->
           continuation.resumeWithException(e)
       }
   }
```

如需启动协程，我们通常会使用 `CoroutineScope.launch`，对于该函数，我们需要指定协程作用域。
幸运的是，Android KTX 库附带适用于 `Activity`、`Fragment` 和 `ViewModel` 等常用生命周期对象的若干预定义作用域。

我们建议使用 `viewModelScope` 运行协程，然后使用 `LiveData` 公开界面所需的数据搭配 `ViewModel` 使用。

## callbackFlow：一种流构建器，适用于基于回调的 API

```
fun FusedLocationProviderClient.locationFlow() = callbackFlow<Location> {
	// Emit updates on location changes
    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            for (location in result.locations) {
                offer(location) // emit location into the Flow using ProducerScope.offer
            }
        }
    }

    // Register a location listener
    requestLocationUpdates(
       createLocationRequest(),
       callback,
       Looper.getMainLooper()
    ).addOnFailureListener { e ->
       close(e) // in case of error, close the Flow
    }

    // Clean up listener when finished
    awaitClose {
       removeLocationUpdates(callback) // clean up when Flow collection ends
    }
}
```

### 收集流

`Flow.collect()` 是一个终端运算符，可以启动 Flow 的实际操作。
在该函数中，我们将收到由 `callbackFlow` 构建器发出的所有位置信息更新。
`collect` 是一个挂起函数，因此必须在协程内运行，我们将使用 `lifecycleScope` 启动该函数。

```
private fun startUpdatingLocation() {
    lifecycleScope.launch {
        fusedLocationClient.locationFlow()
        .conflate()
        .catch { e ->
            findAndSetText(R.id.textView, "Unable to get location.")
            Log.d(TAG, "Unable to get location", e)
        }
        .collect { location ->
            showLocation(R.id.textView, location)
            Log.d(TAG, location.toString())
        }
    }
}
```

`conflate()` 将流合并意味着：当发出更新的速度快于收集器处理更新的速度时，我们只希望接收最新更新。
该函数非常适合我们的示例，因为我们只希望在界面中显示最新位置。

`catch` 可让您处理上游抛出的所有异常，在本示例中，是在 `locationFlow` 构建器中进行处理。您可以将“上游”想象成在当前操作之前采取的操作。

**上面的代码段存在什么问题？**

虽然它不会导致应用崩溃，并且会在 Activity 销毁后妥善进行清理（这要归功于 lifecycleScope），
但它并没有考虑 Activity 停止时（例如，当 Activity 不可见时）的情形。 这意味着，我们不仅会在非必要时更新界面，
而且流会使位置数据订阅持续处于活跃状态，这会浪费电池电量和 CPU 周期！

解决此问题的一种方法是，使用 LiveData KTX 库中的 `Flow.asLiveData` 扩展将流转换为 LiveData。
LiveData 知道何时观察以及何时暂停订阅，并会根据需要重启底层流。

```
private fun startUpdatingLocation() {
    fusedLocationClient.locationFlow()
        .conflate()
        .catch { e ->
            findAndSetText(R.id.textView, "Unable to get location.")
            Log.d(TAG, "Unable to get location", e)
        }
        .asLiveData()
        .observe(this, Observer { location ->
            showLocation(R.id.textView, location)
            Log.d(TAG, location.toString())
        })
}
```

## How to do POC test?

现在，您可以运行自己的应用，检查它对旋转屏幕、按主屏幕按钮以及按返回按钮有何反应。
检查 logcat，看看该应用在后台时是否会在屏幕上显示新的位置。如果实现正确，在您按主屏幕按钮后再返回该应用时，该应用应正确暂停并重启流收集。









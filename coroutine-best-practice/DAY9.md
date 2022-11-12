
Kotlin introduced structured concurrency. when followed, help you keep track of all work running in coroutines.

On Android, we can use structured concurrency to do three things:

- Cancel work when it is no longer needed.

- Keep track of work while it’s running.

- Signal errors when a coroutine fails.

A CoroutineScope keeps track of all your coroutines, and it can cancel all of the coroutines started in it.

Structured concurrency guarantees when a scope cancels, all of its coroutines cancel.

coroutineScope and supervisorScope will wait for child coroutines to complete.

### supervisorScope vs. coroutineScope. 

The main difference is that a coroutineScope will cancel whenever any of its children fail. 
So, if one network request fails, all of the other requests are cancelled immediately. 
If instead you want to continue the other requests even when one fails, you can use a supervisorScope. 
A supervisorScope won’t cancel other children when one of them fails.

Since the coroutineScope will wait for all children to complete, it can also get notified when they fail. 
If a coroutine started by coroutineScope throws an exception, coroutineScope can throw it to the caller. 
Since we’re using coroutineScope instead of supervisorScope, 
it would also immediately cancel all other children when the exception is thrown.

---

There are three basic patterns that you can use for a one shot request to ensure that exactly one request runs at a time.

- Cancel previous work before starting more.

- Queue the next work and wait for the previous requests to complete before starting another one.

- Join previous work if there’s already a request running just return that one instead of starting another request.


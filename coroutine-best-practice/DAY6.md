## Exceptions in coroutines

In this article, we’ll explain how exceptions are propagated in coroutines and how you can always be in control, 
including the different ways to handle them.

When a coroutine fails with an exception, what will happen?

it will propagate said exception up to its parent!

Then, the parent will 

1) cancel the rest of its children,
2) cancel itself
3) propagate the exception up to its parent.

### SupervisorJob to the rescue

With a `SupervisorJob`, the failure of a child does’t affect other children. 
A `SupervisorJob` won’t cancel itself or the rest of its children. 
Moreover, `SupervisorJob` won’t propagate the exception either, and will let the child coroutine handle it.

You can create a `CoroutineScope` like this using `supervisorScope` or `CoroutineScope(SupervisorJob())`.

```
// In this case, if child#1 fails, neither scope nor child#2 will be cancelled.
val scope = CoroutineScope(SupervisorJob())
scope.launch {
    // Child 1
}
scope.launch {
    // Child 2
}
```

Remember that a `SupervisorJob` only works as described when **it’s part of a scope**: 
either created using `supervisorScope` or `CoroutineScope(SupervisorJob())`

### Async catch exception

**When `async` is used as a root coroutine, exceptions are thrown when you call `.await` .**

```
supervisorScope {
    val deferred = async {
        codeThatCanThrowExceptions()
    }
    try {
        deferred.await()
    } catch(e: Exception) {
        // Handle exception thrown in async
    }
}
```

### CoroutineExceptionHandler

The `CoroutineExceptionHandler` is an optional element of a `CoroutineContext` allowing you to handle uncaught exceptions.

```
val handler = CoroutineExceptionHandler { context, exception ->
    println("Caught $exception")
}
```

**Exceptions will be caught if these requirements are met:**

- When ⏰: The exception is thrown by a coroutine that automatically **throws exceptions** (works with `launch`, not with `async`).

- Where 🌍: If it’s in the CoroutineContext of a CoroutineScope or a root coroutine (direct child of CoroutineScope or a supervisorScope).

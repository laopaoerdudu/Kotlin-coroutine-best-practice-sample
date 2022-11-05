
In this article, we’ll explain how exceptions are propagated in coroutines and how you can always be in control, 
including the different ways to handle them.

When a coroutine fails with an exception, what will happen?

it will propagate said exception up to its parent!

Then, the parent will 

1) cancel the rest of its children,
2) cancel itself
3) propagate the exception up to its parent.






## Ref:

https://medium.com/androiddevelopers/easy-coroutines-in-android-viewmodelscope-25bffb605471

https://medium.com/androiddevelopers/the-suspend-modifier-under-the-hood-b7ce46af624f

https://medium.com/androiddevelopers/suspending-over-views-19de9ebd7020

https://medium.com/androiddevelopers/coroutines-first-things-first-e6187bf3bb21

https://www.youtube.com/watch?v=w0kfnydnFWI
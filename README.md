## ViewBinding 和 DataBinding

### ViewBinding：

- 仅仅支持绑定 View

- 不需要在布局文件中添加 layout 标签

- 需要在模块级 build.gradle 文件中添加 viewBinding = true 即可使用

- 效率高于 DataBinding，因为避免了与数据绑定相关的开销和性能问题

- 相比于 kotlin-android-extensions 插件避免了空异常

### DataBinding：

- 包含了 ViewBinding 所有的功能

- 需要在模块级 build.gradle 文件内添加 dataBinding = true 并且需要在布局文件中添加 layout 标签才可以使用

- 支持 data 和 view 双向绑定

- 效率低于 ViewBinding，因为注释处理器会影响数据绑定的构建时间。

ViewBinding 可以实现的， DataBinding 都可以实现，但是 DataBinding 的性能低于 ViewBinding，
DataBinding 和 ViewBinding 会为每个 XML 文件生成绑定类。

```
// Android Studio 4.0
android {
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}
```

Kotlin 将 `Parcelable` 相关的功能，移到了新的插件  `kotlin-parcelize`
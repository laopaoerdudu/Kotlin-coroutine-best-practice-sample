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

不想为某个布局生成 binding 类，将下面属性添加到布局文件的根视图中：

```
<LinearLayout tools:viewBindingIgnore="true" >
</LinearLayout>



```

### include 标签的使用

1，include 标签不带 merge 标签，需要给 include 标签添加 id, 直接使用 id 即可，用法如下所示：

```
<include
    android:id="@+id/include"
    layout="@layout/layout_include_item" />
    
val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
binding.include.includeTvTitle.setText("使用 include 布局中的控件, 不包含 merge")
```

2，include 标签带 merge 标签，不需要给 include 标签添加 id，ViewBinding 的用法如下所示：

```
<include layout="@layout/layout_merge_item" />

val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
val mergeItemBinding = LayoutMergeItemBinding.bind(binding.root)
mergeItemBinding.mergeTvTitle.setText("使用 include 布局中的控件, 包含 merge")
```

### ViewStub 标签的使用

https://stackoverflow.com/questions/66703789/how-to-inflate-a-viewstub-using-viewbinding-in-android

### DataBinding 的使用

#### 需要给布局文件添加 layout 标签

```
<layout 
    xmlns:android="http://schemas.android.com/apk/res/android">
    
    <LinearLayout...>
    ...
    </LinearLayout
    
</layout>
```

```
// 在 Activity 中使用
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    binding.lifecycleOwner = this
    setContentView(binding.root)
}

// 在 Fragment 中使用
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val binding = FragmentViewBindBinding.inflate(inflater, container, false)
    binding.lifecycleOwner = this
    return binding.root
}

// 在 Adapter 中的使用
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
    val bidning: RecycleItemProductBinding = DataBindingUtil.bind(view) 
}

// 在 Dialog 中使用
override fun onCreate(savedInstanceState: Bundle?) {
    binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_data_binding, null, false)
    setContentView(binding.root)
}
```

Ref: 
https://mp.weixin.qq.com/s?__biz=MzAwNDgwMzU4Mw==&mid=2247484806&idx=1&sn=e625b551167fdde90c347d5b4b84ef31&chksm=9b271b16ac509200b6fc1b0cb206aafedfbf6f64added05ec5c01a40ea5f3b082b9f88213a4e&scene=178&cur_album_id=1535736585781035010#rd

































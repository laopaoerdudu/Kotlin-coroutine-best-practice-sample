## Alter type with `typealias`

If you’re working with a long class name, you could use `typealias` to shorten it:

```
typealias AVD = AnimatedVectorDrawable
```

But for this use case, a better fit would be using an import alias:

```
import android.graphics.drawable.AnimatedVectorDrawable as AVD
```

But, import aliases become especially useful when you need to disambiguate between classes with the same name, coming from different packages:

```
import io.plaidapp.R as appR
import io.plaidapp.about.R
```




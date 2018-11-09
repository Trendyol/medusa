# 🐍medusa🐍 [UNDER DEVELOPMENT]
Let the medusa fight with fragment stack for you.

Basic start stop mechanism for multiple fragment stack app.

Medusa is smart enough to handle fragment attachment/detachment or hiding/showing fragment.  No more memory handling. No more fragment stack logic.


## Basic Usage ## 

* Create your root fragment list
```kotlin
private val rootFragmentList = arrayOf(home, search, settings)
```

* Initialize navigator in your activity
```kotlin
navigator = MultipleStackNavigator(
            fragmentManager, 
            R.id.fragmentContainer,
            rootFragmentList,
            navigatorListener)
```

* Listen for changes to update UI
```kotlin
private val navigatorListener = object : NavigatorListener{
    override fun onTabChanged(tabIndex: Int) {
        //Update your UI. Medusa will notify you when tab is changed.
    }
}
```

* Override onBackPressed()
```kotlin
override fun onBackPressed() {
    if (multipleStackNavigator.canGoBack()) {
        multipleStackNavigator.goBack()
    } else {
        super.onBackPressed()
    }
}
```

* Start fragment

```kotlin
navigator.start(newFragment) 
```

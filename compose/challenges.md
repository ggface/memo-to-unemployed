# Задачки по compose

### Анти-тротлинг для кликов
```kotlin
@Composable
fun rememberThrottleClick(
  throttleDelay: Long = 500L,
  onClick: () -> Unit
): () -> Unit {
  val lastClickTime = remember { mutableStateOf(0L) }
  return {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastClickTime.value > throttleDelay) {
      lastClickTime.value = currentTime
      onClick()
    }
  }
}

// Использование:
val throttledClick = rememberThrottleClick {
  navController.navigate("screen")
}
```
# Kotlin and Android Best Practices

## Code Style and Formatting

### General Guidelines
- Follow the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
- Use 4-space indentation (no tabs).
- Keep lines under 120 characters where possible.
- Use meaningful variable, function, and class names.
- Prefer `val` over `var` — immutability is preferred unless mutation is required.
- Use trailing lambdas when the last parameter is a lambda.
- Avoid using `!!` (not-null assertion) — handle nullability explicitly.

### Naming Conventions

**Classes and Interfaces:**
- Use `PascalCase` for class, object, and interface names.
- Example: `GroceriesListFragment`, `ItemRepository`, `BarcodeScanner`

**Functions and Variables:**
- Use `camelCase` for functions, properties, and local variables.
- Example: `fetchGroceries()`, `itemName`, `isScanning`

**Constants:**
- Use `SCREAMING_SNAKE_CASE` for compile-time constants (`const val`).
- Example: `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT_MS`

**Resources:**
- Prefix layout files with their component type: `fragment_`, `activity_`, `item_`, `dialog_`
- Prefix drawable files with their usage type: `ic_` (icon), `bg_` (background), `img_` (image)
- Prefix string resource keys with the screen/feature name: `groceries_title`, `inventory_empty_message`
- Example: `fragment_groceries_list.xml`, `ic_grocery_cart.xml`, `strings.xml`

**Packages:**
- Use lowercase, dot-separated package names.
- Organise by feature, not by type.
- Example: `com.github.caracal.jarvis.groceries`, `com.github.caracal.jarvis.scanner`

---

## Architecture

### MVVM (Model-View-ViewModel)
All screens must follow the MVVM pattern:

- **Model** — Data classes, repositories, and data sources.
- **View** — Fragments and Activities. They observe LiveData/StateFlow and dispatch events only. No business logic.
- **ViewModel** — Holds UI state, business logic, and communicates with the repository layer.

```kotlin
// ViewModel example
class GroceriesViewModel(private val repository: GroceriesRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<GroceryItem>>(emptyList())
    val items: StateFlow<List<GroceryItem>> = _items.asStateFlow()

    fun loadItems() {
        viewModelScope.launch {
            _items.value = repository.getItems()
        }
    }
}
```

### Repository Pattern
- All data access must go through a repository.
- The repository abstracts the data source (local database, network, preferences).
- Fragments and Activities must never access data sources directly.

### Dependency Injection
- Use **Hilt** for dependency injection.
- Annotate ViewModels with `@HiltViewModel`.
- Annotate modules with `@Module` and `@InstallIn`.

---

## Resource Management

### All Values Must Be in Resource Files
- **Strings** → `res/values/strings.xml`
- **Colors** → `res/values/colors.xml`
- **Dimensions** → `res/values/dimens.xml`
- **Styles and Themes** → `res/values/themes.xml`, `res/values/styles.xml`
- **Drawables** → `res/drawable/` or `res/mipmap/` (for launcher icons)
- **Navigation** → `res/navigation/`

**No hardcoded strings, colors, or dimensions in code or layout files.**

```xml
<!-- Wrong -->
<TextView android:text="Groceries" android:textColor="#FF0000" android:textSize="16sp" />

<!-- Correct -->
<TextView
    android:text="@string/groceries_title"
    android:textColor="@color/iron_man_red"
    android:textSize="@dimen/text_size_medium" />
```

---

## Null Safety

- Use Kotlin's type system to express nullability explicitly.
- Prefer `?.let { }`, `?:`, `if (value != null)` over `!!`.
- Return `null` only when it is a meaningful value; otherwise, use sealed classes or `Result<T>`.

```kotlin
// Avoid
val name = user!!.name

// Prefer
val name = user?.name ?: "Unknown"
```

---

## Coroutines and Asynchronous Code

- Use `viewModelScope.launch` in ViewModels for UI-bound coroutines.
- Use `lifecycleScope.launch` in Fragments/Activities only when necessary.
- Use `Dispatchers.IO` for database and network operations.
- Always handle exceptions in coroutines.

```kotlin
viewModelScope.launch {
    try {
        val result = withContext(Dispatchers.IO) { repository.fetchData() }
        _uiState.value = UiState.Success(result)
    } catch (e: Exception) {
        _uiState.value = UiState.Error(e.message ?: "Unknown error")
    }
}
```

---

## Error Handling

- Never swallow exceptions silently.
- Use `Result<T>` or sealed classes to represent success/failure states.
- Log errors with a meaningful message and context.

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

---

## Documentation

- Write **KDoc** comments for all public classes, functions, and properties.
- KDoc comments start with `/**` and use `@param`, `@return`, and `@throws` tags.

```kotlin
/**
 * Fetches the list of grocery items from the repository.
 *
 * @param forceRefresh If true, bypasses the cache and fetches fresh data.
 * @return A list of [GroceryItem] objects.
 */
suspend fun fetchGroceries(forceRefresh: Boolean = false): List<GroceryItem>
```

---

## Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/com/github/caracal/jarvis/
│       │   ├── MainActivity.kt
│       │   ├── groceries/
│       │   │   ├── GroceriesListFragment.kt
│       │   │   ├── GroceriesViewModel.kt
│       │   │   ├── GroceriesRepository.kt
│       │   │   └── GroceryItem.kt
│       │   ├── baseitems/
│       │   │   ├── BaseItemsFragment.kt
│       │   │   └── BaseItemsViewModel.kt
│       │   ├── scanner/
│       │   │   └── BarcodeScannerFragment.kt
│       │   └── di/
│       │       └── AppModule.kt
│       └── res/
│           ├── drawable/
│           ├── layout/
│           ├── mipmap/
│           ├── navigation/
│           └── values/
│               ├── colors.xml
│               ├── dimens.xml
│               ├── strings.xml
│               └── themes.xml
```

- Organise by **feature** (e.g., `groceries/`, `scanner/`), not by type.
- Keep related classes (Fragment, ViewModel, Repository, Model) together in the same feature package.

---

## Testing

- Write unit tests for all ViewModels and Repositories.
- Write integration tests for critical user flows.
- Place unit tests in `src/test/` and instrumentation tests in `src/androidTest/`.
- Use `JUnit4` and `Mockito` or `MockK` for unit testing.
- Use `Espresso` for UI/instrumentation tests.
- Aim for >80% coverage on business logic.

```kotlin
@Test
fun `loadItems emits success state when repository returns data`() = runTest {
    val fakeItems = listOf(GroceryItem(id = 1, name = "Milk"))
    whenever(repository.getItems()).thenReturn(fakeItems)

    viewModel.loadItems()

    assertEquals(UiState.Success(fakeItems), viewModel.uiState.value)
}
```

---

## Performance

- Avoid doing heavy work on the main thread.
- Use `RecyclerView` with `DiffUtil` for efficient list rendering.
- Use `Glide` or `Coil` for image loading — never load images on the main thread.
- Use `ViewBinding` — avoid `findViewById`.
- Profile with Android Studio Profiler when investigating performance issues.

---

## Dependencies

- Keep dependencies minimal and current.
- Use the version catalog (`libs.versions.toml`) for all dependency declarations.
- Run Gradle sync after every dependency change.
- Avoid snapshot or beta versions in production builds.
- Check for dependency updates regularly.

---

## Security

- Never hardcode API keys, secrets, or credentials in source code.
- Store sensitive values in `local.properties` or a secure vault, and access them via `BuildConfig`.
- Validate all user inputs before processing.
- Request only the Android permissions that are strictly necessary.
- Use `EncryptedSharedPreferences` for sensitive local storage.

---

## Tools and Commands

- **Build project:** `./gradlew assembleDebug`
- **Run unit tests:** `./gradlew test`
- **Run instrumentation tests:** `./gradlew connectedAndroidTest`
- **Lint:** `./gradlew lint`
- **Check dependencies for updates:** `./gradlew dependencyUpdates` (with Ben Manes plugin)
- **Clean build:** `./gradlew clean`

---

## Resources

- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
- [Effective Kotlin](https://kt.academy/book/effectivekotlin)
- [Android Developers — Best Practices](https://developer.android.com/guide/practices)
- [Material Design 3](https://m3.material.io/)


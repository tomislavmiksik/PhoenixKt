# Interface-Based Dependency Injection Guide

This guide explains the interface-based DI pattern used in the PhoenixKt project, inspired by the Bitwarden Android architecture.

## Why Interface-Based DI?

1. **Testability**: Easy to mock and fake implementations in tests
2. **Flexibility**: Can swap implementations without changing consumers
3. **Encapsulation**: Implementation details are hidden behind interfaces
4. **Prevents Accidental Injection**: Only interfaces can be injected, not concrete classes

## Pattern Structure

Every data layer class follows this structure:

```
Interface (e.g., ExampleRepository)
    ↓
Implementation (e.g., ExampleRepositoryImpl)
    ↓
Hilt Module provides Interface → constructs Implementation
```

## Implementation Guidelines

### 1. Data Sources

Data sources are the lowest level of the data layer and interact with raw data (APIs, databases, SharedPreferences, etc.).

#### Example: Disk Data Source

```kotlin
// Interface - Exposes behavior
interface AuthDiskSource {
    suspend fun getUserToken(): Result<String?>
    suspend fun saveUserToken(token: String): Result<Unit>
    fun getUserTokenFlow(): Flow<String?>
}

// Implementation - Hidden from external consumers
class AuthDiskSourceImpl(
    private val sharedPreferences: SharedPreferences
) : AuthDiskSource {

    override suspend fun getUserToken(): Result<String?> {
        return try {
            val token = sharedPreferences.getString(KEY_USER_TOKEN, null)
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveUserToken(token: String): Result<Unit> {
        return try {
            sharedPreferences.edit().putString(KEY_USER_TOKEN, token).apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserTokenFlow(): Flow<String?> {
        // Implementation for observing token changes
        return TODO()
    }

    companion object {
        private const val KEY_USER_TOKEN = "user_token"
    }
}
```

#### Example: Network Data Source

```kotlin
// API service interface (Retrofit)
interface WorkoutApiService {
    @GET("workouts")
    suspend fun getWorkouts(): List<WorkoutDto>

    @POST("workouts")
    suspend fun createWorkout(@Body workout: WorkoutDto): WorkoutDto
}

// Network data source interface
interface WorkoutNetworkSource {
    suspend fun getWorkouts(): Result<List<WorkoutDto>>
    suspend fun createWorkout(workout: WorkoutDto): Result<WorkoutDto>
}

// Implementation wraps Retrofit and handles exceptions
class WorkoutNetworkSourceImpl(
    private val api: WorkoutApiService
) : WorkoutNetworkSource {

    override suspend fun getWorkouts(): Result<List<WorkoutDto>> {
        return try {
            val workouts = api.getWorkouts()
            Result.success(workouts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createWorkout(workout: WorkoutDto): Result<WorkoutDto> {
        return try {
            val created = api.createWorkout(workout)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 2. Managers

Managers have single, discrete responsibilities and can depend on data sources or other managers.

```kotlin
// Interface
interface VaultLockManager {
    fun isVaultLocked(userId: String): Boolean
    suspend fun lockVault(userId: String)
    suspend fun unlockVault(userId: String)
    fun vaultLockStateFlow(userId: String): StateFlow<Boolean>
}

// Implementation
class VaultLockManagerImpl(
    private val authDiskSource: AuthDiskSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : VaultLockManager {

    private val lockStates = mutableMapOf<String, MutableStateFlow<Boolean>>()

    override fun isVaultLocked(userId: String): Boolean {
        return lockStates[userId]?.value ?: true
    }

    override suspend fun lockVault(userId: String) = withContext(dispatcher) {
        lockStates.getOrPut(userId) { MutableStateFlow(true) }.value = true
        // Additional lock logic
    }

    override suspend fun unlockVault(userId: String) = withContext(dispatcher) {
        lockStates.getOrPut(userId) { MutableStateFlow(true) }.value = false
        // Additional unlock logic
    }

    override fun vaultLockStateFlow(userId: String): StateFlow<Boolean> {
        return lockStates.getOrPut(userId) { MutableStateFlow(true) }.asStateFlow()
    }
}
```

### 3. Repositories

Repositories are the highest level of the data layer and orchestrate data from multiple sources.

```kotlin
// Sealed class for repository results (preferred over raw Result)
sealed class WorkoutResult {
    data class Success(val workouts: List<Workout>) : WorkoutResult()
    data class Error(val message: String) : WorkoutResult()
    data object NetworkError : WorkoutResult()
}

// Interface
interface WorkoutRepository {
    suspend fun getWorkouts(): WorkoutResult
    suspend fun createWorkout(workout: Workout): WorkoutResult
    fun getWorkoutsFlow(): StateFlow<DataState<List<Workout>>>
}

// Implementation
class WorkoutRepositoryImpl(
    private val workoutNetworkSource: WorkoutNetworkSource,
    private val workoutDao: WorkoutDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : WorkoutRepository {

    private val _workoutsFlow = MutableStateFlow<DataState<List<Workout>>>(DataState.Loading)

    override suspend fun getWorkouts(): WorkoutResult = withContext(dispatcher) {
        // Try network first
        workoutNetworkSource.getWorkouts().fold(
            onSuccess = { dtos ->
                val workouts = dtos.map { it.toDomainModel() }
                // Cache locally
                workoutDao.insertAll(workouts.map { it.toEntity() })
                WorkoutResult.Success(workouts)
            },
            onFailure = { error ->
                // Fallback to local cache
                val cached = workoutDao.getAll().map { it.toDomainModel() }
                if (cached.isNotEmpty()) {
                    WorkoutResult.Success(cached)
                } else {
                    WorkoutResult.NetworkError
                }
            }
        )
    }

    override suspend fun createWorkout(workout: Workout): WorkoutResult = withContext(dispatcher) {
        workoutNetworkSource.createWorkout(workout.toDto()).fold(
            onSuccess = { dto ->
                val created = dto.toDomainModel()
                workoutDao.insert(created.toEntity())
                WorkoutResult.Success(listOf(created))
            },
            onFailure = { error ->
                WorkoutResult.Error(error.message ?: "Unknown error")
            }
        )
    }

    override fun getWorkoutsFlow(): StateFlow<DataState<List<Workout>>> {
        return _workoutsFlow.asStateFlow()
    }
}
```

### 4. Hilt Modules

Each domain should have its own Hilt module that provides the interfaces.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object WorkoutDataModule {

    // Network
    @Provides
    @Singleton
    fun provideWorkoutApiService(retrofit: Retrofit): WorkoutApiService {
        return retrofit.create(WorkoutApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkoutNetworkSource(
        api: WorkoutApiService
    ): WorkoutNetworkSource {
        return WorkoutNetworkSourceImpl(api)
    }

    // Manager
    @Provides
    @Singleton
    fun provideVaultLockManager(
        authDiskSource: AuthDiskSource
    ): VaultLockManager {
        return VaultLockManagerImpl(authDiskSource)
    }

    // Repository
    @Provides
    @Singleton
    fun provideWorkoutRepository(
        workoutNetworkSource: WorkoutNetworkSource,
        workoutDao: WorkoutDao
    ): WorkoutRepository {
        return WorkoutRepositoryImpl(
            workoutNetworkSource = workoutNetworkSource,
            workoutDao = workoutDao
        )
    }
}
```

## Error Handling Philosophy

**Important**: Functions should NOT throw exceptions.

### Data Sources
- Return `Result<T>` types
- Wrap third-party libraries that throw (Retrofit, Room, etc.)

```kotlin
override suspend fun fetchData(): Result<Data> {
    return try {
        val data = api.getData()  // Retrofit throws
        Result.success(data)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Repositories
- Return custom sealed classes representing success/error states
- Avoid returning raw `Throwable`/`Exception` instances

```kotlin
sealed class FetchResult {
    data class Success(val data: Data) : FetchResult()
    data class Error(val message: String) : FetchResult()  // Processed error
    data object NetworkError : FetchResult()
    data object CacheError : FetchResult()
}
```

## Testing Benefits

With interface-based DI, testing becomes straightforward:

```kotlin
class FakeWorkoutRepository : WorkoutRepository {
    var shouldReturnError = false
    var cachedWorkouts = listOf<Workout>()

    override suspend fun getWorkouts(): WorkoutResult {
        return if (shouldReturnError) {
            WorkoutResult.NetworkError
        } else {
            WorkoutResult.Success(cachedWorkouts)
        }
    }

    // ... other overrides
}

// In tests
@Test
fun `test workout loading`() = runTest {
    val fakeRepository = FakeWorkoutRepository()
    fakeRepository.cachedWorkouts = listOf(testWorkout)

    val viewModel = WorkoutViewModel(fakeRepository)

    // Test with fake data
    assertEquals(1, viewModel.state.value.workouts.size)
}
```

## Key Rules

1. ✅ **DO** create an interface for every data layer class
2. ✅ **DO** name implementations with `...Impl` suffix
3. ✅ **DO** provide only interfaces via Hilt modules
4. ✅ **DO** manually construct `...Impl` classes in modules
5. ✅ **DO** inject interfaces, never implementations
6. ✅ **DO** return `Result<T>` from data sources
7. ✅ **DO** return custom sealed classes from repositories
8. ❌ **DON'T** throw exceptions from data layer functions
9. ❌ **DON'T** inject `...Impl` classes directly
10. ❌ **DON'T** return raw exceptions in repository results

## Existing Implementation

- `AppConfig` / `AppConfigImpl` - Already implemented following this pattern
- See `core/di/AppModule.kt` for the Hilt provider

## Next Steps

When creating new data layer classes:
1. Create the interface first
2. Create the `...Impl` class
3. Add the provider to the appropriate Hilt module
4. Inject the interface in consumers
5. Create fake implementations for testing

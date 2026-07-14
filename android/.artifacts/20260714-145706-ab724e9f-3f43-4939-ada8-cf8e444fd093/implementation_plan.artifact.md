# Fix 401 Unauthorized Error in Inventory Management

The 401 Unauthorized error in the inventory management system is caused by `ProductViewModel` attempting to load products during its initialization at app startup. At this point, the user is not authenticated, and no JWT token is available, leading to a failed API request. Subsequent navigations to inventory-related screens display this initial error state without attempting to reload the data.

## Proposed Changes

### ViewModel Layer

#### [ProductViewModel.kt](file:///C:/Users/Admin/Desktop/replica/android/app/src/main/java/com/example/myapplication/ui/viewmodel/ProductViewModel.kt)

- Remove the `init` block that calls `loadProducts()`. This prevents the unauthorized API call at startup.

### UI Layer

#### [InventoryScreens.kt](file:///C:/Users/Admin/Desktop/replica/android/app/src/main/java/com/example/myapplication/ui/screens/InventoryScreens.kt)

- Add `LaunchedEffect(Unit) { viewModel.loadProducts() }` to the `InventoryManagementScreen` composable to ensure data is loaded when the user enters the screen.

#### [ProductListScreen.kt](file:///C:/Users/Admin/Desktop/replica/android/app/src/main/java/com/example/myapplication/ui/screens/ProductListScreen.kt)

- Add `LaunchedEffect(Unit) { viewModel.loadProducts() }` to the `ProductListScreen` composable to ensure data is loaded when the user enters the screen.

---

## Verification Plan

### Automated Tests
- I will run a build to ensure no syntax errors were introduced:
  `gradlew :app:assembleDebug`

### Manual Verification
1.  **Launch the app**: Verify that no unauthorized API calls are logged or displayed on the login screen (if there were any).
2.  **Log in as Admin/Stocker**: Navigate to the "Gestión de Inventario" screen.
3.  **Verify Data Load**: Confirm that the product list loads successfully without showing a 401 error.
4.  **Log in as Client**: Navigate to the "Catálogo de Productos" screen.
5.  **Verify Data Load**: Confirm that the product list loads successfully.
6.  **Verify Token Persistence**: Close the app and reopen it. If the session is persisted, navigate to inventory and verify it still works (token should be retrieved from `DataStore`).

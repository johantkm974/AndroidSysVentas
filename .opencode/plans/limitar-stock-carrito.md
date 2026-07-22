# Plan: Limitar cantidad en carrito según stock disponible

## Problema
El backend devuelve error 400 si se intenta crear un pedido con cantidad > stock disponible, pero el frontend permite agregar cualquier cantidad al carrito sin validar.

## Cambios necesarios

### 1. `CartViewModel.kt` — Validar stock antes de incrementar

**Función `addToCart(product)`** (línea 78):
- Leer la cantidad actual en carrito para ese producto (0 si no existe)
- Si `currentQty >= product.stock`, hacer `return` sin modificar nada
- Solo incrementar si hay stock disponible

```kotlin
fun addToCart(product: ProductoResponse) {
    val currentList = _cartItems.value.toMutableList()
    val index = currentList.indexOfFirst { it.product.idProducto == product.idProducto }
    val currentQty = if (index >= 0) currentList[index].quantity else 0
    if (currentQty >= product.stock) return
    if (index >= 0) {
        currentList[index] = currentList[index].copy(quantity = currentQty + 1)
    } else {
        currentList.add(CartItem(product, 1))
    }
    _cartItems.value = currentList
}
```

**Función `increaseQuantity(productId)`** (línea 89):
- Si `item.quantity >= item.product.stock`, hacer `return`
- Solo incrementar si hay stock disponible

```kotlin
fun increaseQuantity(productId: Long) {
    val currentList = _cartItems.value.toMutableList()
    val index = currentList.indexOfFirst { it.product.idProducto == productId }
    if (index >= 0) {
        val item = currentList[index]
        if (item.quantity >= item.product.stock) return
        currentList[index] = item.copy(quantity = item.quantity + 1)
        _cartItems.value = currentList
    }
}
```

### 2. `CartScreen.kt` — Deshabilitar botón `[+]` al alcanzar stock máximo

Buscar el `FilledIconButton` con `Icons.Default.Add` (aprox. línea 273) y agregar `enabled = item.quantity < item.product.stock`:

```kotlin
FilledIconButton(
    onClick = { viewModel.increaseQuantity(item.product.idProducto) },
    enabled = item.quantity < item.product.stock,
    modifier = Modifier.size(32.dp),
    shape = RoundedCornerShape(8.dp)
) {
    Icon(Icons.Default.Add, contentDescription = "Aumentar", modifier = Modifier.size(16.dp))
}
```

## Resultado esperado
- No se puede agregar al carrito más unidades del stock disponible
- El botón `[+]` se deshabilita visualmente al alcanzar el límite
- El error 400 del backend ya no aparece por exceder stock

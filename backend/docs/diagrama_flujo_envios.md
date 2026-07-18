# Diagrama General - Gestión de Envíos

## Flujo Actual (Simulación automática)

> **Contexto:** El sistema actual usa una simulación donde todo se genera automáticamente al crear un pedido. La venta usa el método de pago "SIMULADO" y se marca como PAGADA de inmediato, sin esperar un pago real ni el proceso logístico.

```mermaid
flowchart TD
    subkey["
        Leyenda:
        ⚡ Automático (lo hace el sistema)
        👤 Manual (lo hace el usuario)
    "]

    CLIENTE["👤 Cliente"] -->|"POST /api/pedidos"| CREAR

    subgraph CREAR["CREAR PEDIDO (⚡ automático)"]
        direction TB
        P1[Pedido → CONFIRMADO ⚡]
        P2[Venta → PAGADA ⚡<br>Método: SIMULADO]
        P3[Stock descontado ⚡]
        P4[Envío → PENDIENTE ⚡<br>Dirección: 'Por asignar']
        P5[Seguimiento: 'Envío generado automáticamente' ⚡]

        P1 --- P2 --- P3 --- P4 --- P5
    end

    CREAR --> LISTO[Pedido listo en CONFIRMADO<br>Venta ya PAGADA<br>Stock ya descontado]

    LISTO --> ADMIN["👤 ADMIN"] -->|"PATCH /api/pedidos/{id}/estado/{idEstado}"| CAMBIO

    subgraph CAMBIO["CAMBIO DE ESTADO (manual)"]
        direction TB
        C1{¿A qué estado cambia?}
        C1 -->|ENTREGADO| C2[Envío → ENTREGADO ⚡<br>Fecha entrega registrada ⚡<br>Pedido → ENTREGADO ⚡<br>Venta sigue PAGADA ⚡]
        C1 -->|CANCELADO| C3[Envío → CANCELADO ⚡<br>Pedido → CANCELADO ⚡<br>Venta → ANULADA ⚡<br>Stock restaurado ⚡]
    end

    style key fill:#eee,stroke:#999
    style CREAR fill:#fff3cd,stroke:#ffc107
    style CAMBIO fill:#fff3cd,stroke:#ffc107
```

### ¿Qué pasa realmente?

```mermaid
flowchart LR
    subgraph Lo_que_deberia_pasar["Lo que DEBERÍA pasar"]
        direction TB
        D1[1. Cliente pide] --> D2[2. Venta PENDIENTE]
        D2 --> D3[3. Cliente paga] --> D4[4. Venta PAGADA]
        D4 --> D5[5. Almacén prepara] --> D6[6. Envío EN_RUTA]
        D6 --> D7[7. Cliente recibe] --> D8[8. Envío ENTREGADO<br>Todo completo]
    end

    subgraph Lo_que_pasa_realmente["Lo que PASA REALMENTE (actual)"]
        direction TB
        R1[1. Cliente pide ⚡] --> R2[2. Todo se genera automático ⚡]
        R2 --> R3["3. Venta ya PAGADA (SIMULADO) ⚡"]
        R3 --> R4["4. Stock ya descontado ⚡"]
        R4 --> R5["5. Envío PENDIENTE sin datos ⚡"]
        R5 --> R6["6. Admin marca ENTREGADO 👤"]
        R6 --> R7[7. Fin - sin proceso real]
    end

    style Lo_que_deberia_pasar fill:#d4edda,stroke:#28a745
    style Lo_que_pasa_realmente fill:#f8d7da,stroke:#dc3545
```

### Glosario del problema

| Término | Cómo debería ser | Cómo es ahora (simulación) |
|---------|-----------------|---------------------------|
| **Venta** | El cliente paga → PAGADA | Se crea automáticamente como PAGADA con método "SIMULADO". No hay pago real. |
| **Stock** | Se descuenta cuando el almacén prepara el pedido | Se descuenta al crear el pedido, antes de cualquier proceso logístico |
| **Envío** | El vendedor lo crea con datos reales y pasa por EN_RUTA | Se crea automaticamente con "Por asignar" y puede saltar directo a ENTREGADO |
| **ENTREGADO** | El cliente recibe físicamente el producto | Solo es un click del admin que cambia estados, no hay entrega real |
| **Método SIMULADO** | Solo para pruebas/desarrollo | Se usa como método real de pago, no hay transacción bancaria |

### Resumen visual del flujo actual vs flujo real

| Paso | Actor | Sistema actual | Sistema real (propuesto) |
|:----:|:-----:|:-------------:|:----------------------:|
| 1 | Cliente | Crea pedido | Crea pedido |
| 2 | Sistema | Pedido → CONFIRMADO ⚡ | Pedido → PENDIENTE |
| 3 | Sistema | Venta → PAGADA ⚡ | Venta → PENDIENTE |
| 4 | Sistema | Stock descontado ⚡ | Stock intacto |
| 5 | Sistema | Envío → PENDIENTE ⚡ | — |
| 6 | Admin | — | Cambia pedido → CONFIRMADO |
| 7 | Vendedor | — | Crea envío con datos reales |
| 8 | Admin/Vendedor | Cambia pedido → ENTREGADO | Cambia envío → EN_RUTA |
| 9 | Admin/Vendedor | — | Cambia envío → ENTREGADO |
| 10 | Sistema | — | Venta → PAGADA, Pedido → ENTREGADO |

> ⚡ = automático

## Actores y sus permisos

```mermaid
flowchart LR
    subgraph Usuarios["Usuarios del Sistema"]
        A[ADMIN]
        V[VENDEDOR]
        C[CLIENTE]
    end

    subgraph API["API /api/envios"]
        GET1[GET /]
        GET2[GET /{id}]
        GET3[GET /pedido/{idPedido}]
        GET4[GET /{id}/tracking]
        POST[POST /]
        PATCH[PATCH /{id}/estado]
    end

    A -->|Todo| API
    V -->|Listar, crear, actualizar| GET1
    V -->|Listar, crear, actualizar| GET2
    V -->|Listar, crear, actualizar| POST
    V -->|Listar, crear, actualizar| PATCH
    V -->|Consultar| GET3
    V -->|Consultar| GET4
    C -->|Solo consulta| GET3
    C -->|Solo consulta| GET4
```

## Diagrama de flujo con usuarios

```mermaid
flowchart TD
    %% ACTORES
    ADMIN[\ADMIN/]
    VENDEDOR[\VENDEDOR/]
    CLIENTE[\CLIENTE/]

    %% CREAR ENVÍO
    subgraph Crear["Crear Envío"]
        direction TB
        C1[Verificar pedido existe]
        C2{Pedido ya tiene envío?}
        C3[Error: envío duplicado]
        C4[Asignar estado PENDIENTE]
        C5[Registrar seguimiento: Envío creado]
        C6[✓ Envío creado]
        C1 --> C2
        C2 -->|Sí| C3
        C2 -->|No| C4 --> C5 --> C6
    end

    %% ACTUALIZAR ESTADO
    subgraph Actualizar["Actualizar Estado"]
        direction TB
        A1[Buscar envío por ID]
        A2[Buscar nuevo estado]
        A1 --> A2

        A2 --> A3{EN_RUTA}
        A2 --> A4{ENTREGADO}
        A2 --> A5{CANCELADO}

        A3 --> A6[Actualizar fecha_envio]
        A6 --> A7[Pedido → ENVIADO]

        A4 --> A8[Actualizar fecha_entrega]
        A8 --> A9[Pedido → ENTREGADO]
        A9 --> A10{Venta asociada?}
        A10 -->|Sí| A11[Venta → PAGADA]
        A10 -->|No| A12

        A5 --> A13[Pedido → CANCELADO]
        A13 --> A14{Venta existe y no ANULADA?}
        A14 -->|Sí| A15[Restaurar stock]
        A15 --> A16[Registrar movimiento inventario]
        A16 --> A17[Venta → ANULADA]
        A14 -->|No| A12

        A7 --> A12[Registrar seguimiento]
        A11 --> A12
        A17 --> A12
        A12 --> A18[✓ Estado actualizado]
    end

    %% CONSULTAS
    subgraph Consultar["Consultar"]
        CT1[Listar todos los envíos]
        CT2[Obtener envío por ID]
        CT3[Obtener envío por pedido]
        CT4[Ver historial tracking]
    end

    %% FLUJO PRINCIPAL
    ADMIN -->|Crear| Crear
    VENDEDOR -->|Crear| Crear

    ADMIN -->|Actualizar| Actualizar
    VENDEDOR -->|Actualizar| Actualizar

    ADMIN -->|Consultar| Consultar
    VENDEDOR -->|Consultar| Consultar
    CLIENTE -->|Solo tracking y pedido| Consultar

    Consultar -.->|GET| CT3
    Consultar -.->|GET| CT4
    Consultar -.->|GET| CT1
    Consultar -.->|GET| CT2

    %% ESTILOS
    classDef admin fill:#9C27B0,color:#fff
    classDef vendedor fill:#2196F3,color:#fff
    classDef cliente fill:#4CAF50,color:#fff
    classDef proceso fill:#FF9800,color:#fff
    classDef decision fill:#f44336,color:#fff
    classDef exito fill:#4CAF50,color:#fff

    class ADMIN admin
    class VENDEDOR vendedor
    class CLIENTE cliente
    class C1,C4,C5,A6,A7,A8,A9,A11,A15,A16,A17,A12 proceso
    class C2,C3,A10,A14 decision
    class C6,A18 exito
```

## Diagrama de estados del envío

```mermaid
stateDiagram-v2
    [*] --> PENDIENTE
    PENDIENTE --> EN_RUTA: ADMIN / VENDEDOR
    PENDIENTE --> CANCELADO: ADMIN / VENDEDOR
    EN_RUTA --> ENTREGADO: ADMIN / VENDEDOR
    EN_RUTA --> CANCELADO: ADMIN / VENDEDOR
    ENTREGADO --> [*]
    CANCELADO --> [*]

    note right of PENDIENTE
        Creado por ADMIN o VENDEDOR
        Pedido debe existir
        Sin envío previo
    end note

    note right of EN_RUTA
        Fecha de envío se registra
        Pedido → ENVIADO
    end note

    note right of ENTREGADO
        Fecha de entrega se registra
        Pedido → ENTREGADO
        Venta → PAGADA
    end note

    note right of CANCELADO
        Pedido → CANCELADO
        Venta → ANULADA
        Stock se restaura
        Movimiento inventario registrado
    end note
```

## Modelo de datos

```mermaid
erDiagram
    ENVIOS ||--o| PEDIDOS : contiene
    ENVIOS ||--o{ SEGUIMIENTO_ENVIO : tiene
    ENVIOS }o--|| ESTADOS_ENVIO : pertenece
    SEGUIMIENTO_ENVIO }o--|| ESTADOS_ENVIO : registra

    ENVIOS {
        bigint id_envio PK
        bigint id_pedido FK
        varchar direccion
        varchar distrito
        varchar referencia
        bigint id_estado_envio FK
        datetime fecha_envio
        datetime fecha_entrega
        datetime created_at
        datetime updated_at
    }

    SEGUIMIENTO_ENVIO {
        bigint id_seguimiento PK
        bigint id_envio FK
        bigint id_estado_envio FK
        varchar observacion
        datetime created_at
    }

    ESTADOS_ENVIO {
        bigint id_estado_envio PK
        varchar nombre
        varchar descripcion
    }
```

## Permisos por rol

| Endpoint | ADMIN | VENDEDOR | CLIENTE |
|----------|:-----:|:--------:|:-------:|
| `GET /api/envios` | ✓ | ✓ | ✗ |
| `GET /api/envios/{id}` | ✓ | ✓ | ✗ |
| `GET /api/envios/pedido/{idPedido}` | ✓ | ✓ | ✓ |
| `GET /api/envios/{id}/tracking` | ✓ | ✓ | ✓ |
| `POST /api/envios` | ✓ | ✓ | ✗ |
| `PATCH /api/envios/{id}/estado` | ✓ | ✓ | ✗ |

## Estados del envío

| Estado | ¿Quién lo asigna? | Efecto en Pedido | Efecto en Venta |
|--------|:-----------------:|-----------------|-----------------|
| PENDIENTE | ADMIN, VENDEDOR | — | — |
| EN_RUTA | ADMIN, VENDEDOR | → ENVIADO | — |
| ENTREGADO | ADMIN, VENDEDOR | → ENTREGADO | → PAGADA |
| CANCELADO | ADMIN, VENDEDOR | → CANCELADO | → ANULADA + restaura stock |

## Flujo general (texto)

```text
ADMIN / VENDEDOR
      │
      ├── CREAR ENVÍO ──────────────► PEDIDO existe? ──Sí──► ¿Ya tiene envío? ──No──► PENDIENTE + seguimiento
      │                                    │                      │
      │                                    No                     Sí
      │                                    │                      │
      │                                    ▼                      ▼
      │                                 ERROR               ERROR duplicado
      │
      ├── ACTUALIZAR ESTADO ──────────► EN_RUTA   ──► Pedido → ENVIADO
      │                                    │
      │                                    ├── ENTREGADO ──► Pedido → ENTREGADO
      │                                    │                    Venta → PAGADA
      │                                    │
      │                                    └── CANCELADO ──► Pedido → CANCELADO
      │                                                          Venta → ANULADA
      │                                                          Stock → restaurar
      │
      └── CONSULTAR ──────────────────► Listar todos
                                            Ver por ID
                                            Ver por pedido  ◄── CLIENTE también
                                            Ver tracking    ◄── CLIENTE también
```

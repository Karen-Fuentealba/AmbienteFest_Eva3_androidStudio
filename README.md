# Descripcion del proyecto
ambienteFestambienteFest es una aplicación para Android diseñada para la gestión y reserva de servicios para eventos y fiestas. Permite a los usuarios explorar diferentes servicios, reservar horarios, realizar pagos y consultar el detalle de sus compras. 

# Funcionalidades Principales
- Autenticación de Usuarios: Registro e inicio de sesión seguro para clientes.
- Catálogo de Servicios: Muestra una lista detallada de los servicios disponibles para eventos (ej. DJ, catering, decoración, etc.).
- Consulta de Disponibilidad: Permite a los usuarios ver los horarios y fechas disponibles para cada servicio.
- Sistema de Reservas: Funcionalidad para que los usuarios puedan agendar y confirmar la reserva de un servicio en un horario específico y añadir al carrito de compra.
- Flujo de Pago: Permite a los usuarios realizar la compra y el pago de los servicios reservados.
- Detalle de Compra: Los usuarios pueden ver un resumen detallado de sus compras y reservas realizadas, incluyendo costos y fechas.



# Documentación de API Externa - Proyecto

Este proyecto utiliza **APIs externas de Xano** para manejar recursos , carritos, usuarios, pagos, servicios, etc.  

A continuación se describe cada endpoint, con método, URL y descripción 

## URLs Base

| Propósito | URL Base |
|------------|----------|
| Base de datos / recursos | `https://x8ki-letl-twmt.n7.xano.io/api:OdHOEeXs` |
| Autenticación / token | `https://x8ki-letl-twmt.n7.xano.io/api:KBcldO_7` |
---

## Cart

| Método | URL | Descripción |
|--------|-----|------------|
| DELETE | /cart/{cart_id} | Elimina un carrito |
| GET | /cart/{cart_id} | Obtiene un carrito por ID |
| PATCH | /cart/{cart_id} | Edita un carrito |
| GET | /cart | Consulta todos los carritos |
| POST | /cart | Agrega un carrito |

---

## Cart Detail

| Método | URL | Descripción |
|--------|-----|------------|
| DELETE | /cart_detail/{cart_detail_id} | Elimina un detalle de carrito |
| PATCH | /cart_detail/{cart_detail_id} | Edita un detalle de carrito |
| GET | /cart_detail | Consulta todos los detalles de carrito |
| POST | /cart_detail | Agrega un detalle de carrito |

---

## Clear Cart

| Método | URL | Descripción |
|--------|-----|------------|
| DELETE | /clear_cart | Limpia todo el carrito |

---

## Payment

| Método | URL | Descripción |
|--------|-----|------------|
| DELETE | /payment/{payment_id} | Elimina un pago |
| GET | /payment/{payment_id} | Obtiene un pago por ID |
| PATCH | /payment/{payment_id} | Edita un pago |
| PUT | /payment/{payment_id} | Actualiza un pago |
| GET | /payment | Consulta todos los pagos |
| POST | /payment | Agrega un pago |

---

## Role

| Método | URL | Descripción |
|--------|-----|------------|
| DELETE | /role/{role_id} | Elimina un rol |
| GET | /role/{role_id} | Obtiene un rol por ID |
| PATCH | /role/{role_id} | Edita un rol |
| GET | /role | Consulta todos los roles |
| POST | /role | Agrega un rol |

---

##  Service

| Método | URL | Descripción |
|--------|-----|------------|
| DELETE | /service/{service_id} | Elimina un servicio |
| GET | /service/{service_id} | Obtiene un servicio por ID |
| PATCH | /service/{service_id} | Edita un servicio |
| PUT | /service/{service_id} | Actualiza un servicio |
| GET | /service | Consulta todos los servicios |
| POST | /service | Agrega un servicio |

---

## Service Category

| Método | URL | Descripción |
|--------|-----|------------|
| DELETE | /service_category/{service_category_id} | Elimina una categoría de servicio |
| GET | /service_category/{service_category_id} | Obtiene una categoría por ID |
| PATCH | /service_category/{service_category_id} | Edita una categoría |
| PUT | /service_category/{service_category_id} | Actualiza una categoría |
| GET | /service_category | Consulta todas las categorías |
| POST | /service_category | Agrega una categoría |

---

##  Service Reservation

| Método | URL | Descripción |
|--------|-----|------------|
| DELETE | /service_reservation/{service_reservation_id} | Elimina una reserva |
| GET | /service_reservation/{service_reservation_id} | Obtiene una reserva por ID |
| PATCH | /service_reservation/{service_reservation_id} | Edita una reserva |
| PUT | /service_reservation/{service_reservation_id} | Actualiza una reserva |
| GET | /service_reservation | Consulta todas las reservas |
| POST | /service_reservation | Agrega una reserva |

---

## Service Time Slot

| Método | URL | Descripción |
|--------|-----|------------|
| DELETE | /service_time_slot/{service_time_slot_id} | Elimina un horario de servicio |
| GET | /service_time_slot/{service_time_slot_id} | Obtiene un horario por ID |
| PATCH | /service_time_slot/{service_time_slot_id} | Edita un horario |
| PUT | /service_time_slot/{service_time_slot_id} | Actualiza un horario |
| GET | /service_time_slot | Consulta todos los horarios |
| POST | /service_time_slot | Agrega un horario |

---

##  Upload

| Método | URL | Descripción |
|--------|-----|------------|
| POST | /upload/image | Subir una imagen |

---

##  User

| Método | URL | Descripción |
|--------|-----|------------|
| DELETE | /user/{user_id} | Elimina un usuario |
| GET | /user/{user_id} | Obtiene un usuario por ID |
| PATCH | /user/{user_id} | Edita un usuario |
| PUT | /user/{user_id} | Actualiza un usuario |
| GET | /user | Consulta todos los usuarios |
| POST | /user | Agrega un usuario |

---

## Auth

| Método | URL | Descripción |
|--------|-----|------------|
| GET | /auth/me | Obtiene el usuario asociado al token de autenticación |
| POST | /auth/signup | Registro y obtención de token de autenticación |

---

## Login

| Método | URL | Descripción |
|--------|-----|------------|
| POST | /login | Inicio de sesión y obtención de token |

---

###  Notas Finales

- Todos los endpoints devuelven **JSON**.  
- Los endpoints con `{id}` requieren que pases un ID válido en la URL.  
- Para operaciones POST o PUT, enviar los parámetros requeridos en **JSON**.  
- Probar todos los endpoints con **Postman**.
  
### Instrucciones para ejecutar el proyecto
- Clona el repositorio.
- Abre el proyecto en Android Studio.
- Asegúrate de tener instalado el JDK 17.
- Sincroniza el proyecto con los archivos de Gradle.
- Ejecuta la aplicación en un emulador o dispositivo físico
  
### Herramientas usadas para los tests en este proyecto:
- JUnit (tests unitarios en app/src/test).
- kotlinx-coroutines-test (declarado).
- AndroidX Test + Espresso (para tests instrumentados; declarados).
- Jetpack Compose UI Test (androidx.compose.ui.test.junit4, declarado).

 ### tests principales :
CartScreenTest — calcula y formatea total (subtotal - descuento + IVA).
CartScreenTest — habilitación lógica del botón de checkout.
CartViewModelTest — suma precios e IVA en ViewModel.
CartItemCardTest — subtotal por item, ajustar cantidad, validar URL e imagen.
CartTest — modelo del carrito: suma, descuentos, vacío y expiración. 

### Este proyecto fue desarrollado por Antonella Aedo y Karen Fuentealba.

# ⭐⭐⭐ Nivel 3 - Ejercicio CRUD con MongoDB

## 🍎 API de Pedidos de Frutas - MongoDB CRUD
Este proyecto es una API REST para gestionar pedidos de frutas, desarrollado como parte del Nivel 3 del itinerario Java de Itacademy. El objetivo principal es practicar la persistencia en MongoDB utilizando documentos embebidos y validación de datos.

## 📖 Descripción del Proyecto

La API permite realizar el ciclo completo de CRUD (Create, Read, Update, Delete) para pedidos de frutas. Cada pedido se guarda como un único documento en una colección de MongoDB, incluyendo una lista de ítems (frutas y cantidades) como subdocumentos.

## 🧩 Historias de Usuario Implementadas

Crear pedido: Validación del nombre del cliente, lista de frutas (cantidad > 0) y fecha de entrega (mínimo mañana).

Consultar todos: Listar todos los pedidos registrados.

Consultar por ID: Obtener detalles de un pedido específico o 404 si no existe.

Modificar: Actualizar los datos de un pedido existente.

Eliminar: Borrar pedidos cancelados con retorno 204 No Content.

## 🛠️ Tecnologías Utilizadas

Java 21 (LTS)

Spring Boot 3.x

Spring Data MongoDB (Persistencia NoSQL)

Spring Validation (Jakarta Validation)

JUnit 5 & Mockito (Test-Driven Development)

Maven (Gestión de dependencias)

## ⚙️ Configuración e Instalación
Requisitos Previos

Tener instalado Java 21.

Tener una instancia de MongoDB activa (local o Docker) en el puerto 27017.

Pasos para ejecutar

Clonar el repositorio.
```
Configurar la conexión a MongoDB en src/main/resources/application.properties:

spring.data.mongodb.uri=mongodb://localhost:27017/order_system
```

Ejecutar la aplicación:
```
mvn spring-boot:run
```
## 🌐 Endpoints de la API
Método	Endpoint	Descripción	Status Ok	Status Error
POST	/orders	Crear un nuevo pedido	201 Created	400 Bad Request
GET	/orders	Listar todos los pedidos	200 OK	-
GET	/orders/{id}	Detalle de un pedido	200 OK	404 Not Found
PUT	/orders/{id}	Actualizar pedido	200 OK	400 / 404
DELETE	/orders/{id}	Eliminar pedido	204 No Content	404 Not Found
Ejemplo de JSON para POST/PUT
```
{
"clientName": "Rong",
"fruitList": [
{
"name": "Apple",
"number": 10
}
],
"deliveryDate": "2026-01-20"
}
```
## 🧪 Testing (TDD)

El proyecto se ha desarrollado siguiendo la metodología TDD. Se han implementado:

Unit Tests: Para la lógica de negocio en OrderService.

Integration Tests: Utilizando MockMvc para validar los controladores y los códigos de estado HTTP.

Para ejecutar los tests:
```
mvn test
```
## 📂 Estructura del Proyecto
```
src/main/java/cat/itacademy/s04/t02/n03/fruit/
├── controller/        # Endpoints REST
├── DTO/               # Objetos Request y Response (Records)
├── entities/          # Documentos de MongoDB (Order, Fruit)
├── exception/         # Excepciones Personalizadas y Global Handler
├── repository/        # Interfaces MongoRepository
└── service/           # Lógica de negocio (Interfaces e Impl)
```
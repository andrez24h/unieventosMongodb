# UniEventos – Backend (Spring Boot & MongoDB)

Backend del sistema **UniEventos**, una plataforma para la venta de entradas a conciertos y eventos en distintas ciudades de Colombia.  
Este repositorio corresponde **exclusivamente al backend**, desarrollado como parte de un proyecto académico y utilizado como **portafolio profesional backend**.

##  Objetivo del proyecto
Demostrar habilidades en:
- Diseño de lógica de negocio
- Arquitectura backend con Spring Boot
- Uso de MongoDB (modelo no relacional)
- Manejo de DTOs, servicios y repositorios
- Validaciones, estados, cupones y flujos reales de negocio

>  El frontend **no está incluido** en este repositorio.

---

##  Tecnologías utilizadas
- Java 17
- Spring Boot
- Spring Data MongoDB
- Gradle
- MongoDB
- Lombok
- JUnit

---

##  Dominio del sistema

**UniEventos** permite a los clientes registrarse, autenticarse y comprar entradas para eventos, aplicando cupones de descuento y recibiendo notificaciones por correo electrónico.

El sistema maneja dos tipos de usuarios:
- **Administrador**
- **Cliente**

---

##  Servicios implementados

###  Servicio de Cuenta
- Registro y activación de cuentas
- Manejo de estados de cuenta
- Gestión de roles
- Recuperación de contraseña mediante código
- Validaciones de seguridad

###  Servicio de Cupón
- Cupones por registro
- Cupones por primera compra
- Redención y reversión de cupones
- Control de estado, tipo y vencimiento
- Cupones de código único e individual

###  Servicio de Email
- Envío de correos electrónicos
- Envío de códigos de activación
- Envío de cupones
- Notificaciones del sistema

---

##  Arquitectura backend
- Arquitectura por capas
- Uso de DTOs para comunicación
- Repositorios con Spring Data MongoDB
- Servicios con interfaces e implementaciones
- Manejo centralizado de excepciones
- Uso de enums para estados y tipos

---

##  Pruebas
El proyecto incluye **pruebas unitarias** para los servicios principales utilizando **JUnit**.

---

##  Ejecución del proyecto
1. Configurar MongoDB (local o mediante Docker)
2. Ajustar el archivo `application.properties`
3. Ejecutar el proyecto desde IntelliJ IDEA o terminal

---

##  Evolución del proyecto
Este backend tendrá **dos versiones independientes**:

-  Versión actual: **MongoDB (NoSQL)**
-  Próxima versión: **MySQL (SQL / Relacional)**

El objetivo es demostrar dominio tanto de:
- Modelos **no relacionales**
- Modelos **relacionales**
- Adaptación de la lógica de negocio a distintos motores de base de datos

---

##  Autor
**Andrés Mauricio**  
Backend Developer  
Java · Spring Boot · MongoDB · MySQL

# 🍔 QuickBite - Food Ordering Backend System

QuickBite is a scalable backend application for a food ordering platform built using Spring Boot.  
The project focuses on real-world backend development concepts including authentication, authorization, REST APIs, role-based access control, exception handling, DTO mapping, Swagger documentation, and layered architecture.

---

# 🚀 Features

## 🔐 Authentication & Security
- JWT-based Authentication
- Spring Security Integration
- Role-Based Authorization
- Secure Password Encryption using BCrypt

## 👤 User Features
- User Registration & Login
- Browse Restaurants
- Browse Menu Items
- Add Items to Cart
- Place Orders
- View Order History

## 🏪 Restaurant Features
- Create Restaurant
- Update Restaurant Details
- Upload Restaurant Images
- Manage Menu Items
- View Restaurant Orders

## 🛒 Cart & Order Management
- Add/Remove Cart Items
- Dynamic Cart Total Calculation
- Place Orders from Cart
- Order Status Tracking

## 📘 API Documentation
- Swagger/OpenAPI Integration
- API Testing using Swagger UI

## ⚙️ Backend Engineering Concepts
- DTO Pattern
- Global Exception Handling
- Layered Architecture
- Repository-Service-Controller Structure
- Validation Handling
- Response Wrappers
- Clean REST API Design

---

# 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Java 17 | Programming Language |
| Spring Boot | Backend Framework |
| Spring Security | Authentication & Authorization |
| JWT | Token-Based Authentication |
| Spring Data JPA | Database Operations |
| Hibernate | ORM |
| MySQL | Database |
| Maven | Dependency Management |
| Swagger/OpenAPI | API Documentation |
| Lombok | Boilerplate Reduction |

---

# 📂 Project Structure

```bash
src/main/java/com/quickbite
│
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
├── service
└── util
```

---

# 🔑 API Modules

## Authentication APIs
- Register User
- Login User
- JWT Token Generation

## Restaurant APIs
- Create Restaurant
- Get Restaurants
- Update Restaurant
- Delete Restaurant

## Menu APIs
- Add Menu Item
- Update Menu Item
- Delete Menu Item
- Get Restaurant Menu

## Cart APIs
- Add to Cart
- Remove from Cart
- View Cart

## Order APIs
- Place Order
- View Orders
- Order Status Updates

---

# 📸 Swagger UI

After running the application:

```bash
http://localhost:8080/swagger-ui/index.html
```

---

# ⚡ Getting Started

## Clone Repository

```bash
git clone https://github.com/Atharva5500/FoodOrderingApplication-QuickBite-.git
```

## Navigate to Project

```bash
cd FoodOrderingApplication-QuickBite-
```

## Configure Database

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quickbite
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

---

# ▶️ Run Application

Using Maven:

```bash
mvn spring-boot:run
```

Or run the main class directly from IntelliJ IDEA.

---

# 🔒 Authentication Flow

1. User logs in
2. JWT token is generated
3. Token is sent in Authorization Header
4. Protected APIs validate JWT token
5. Access granted based on roles

Example:

```http
Authorization: Bearer YOUR_JWT_TOKEN
```

---

# 📌 Future Improvements

- Refresh Token Implementation
- Docker Deployment
- Redis Caching
- Payment Gateway Integration
- Email Notifications
- Live Order Tracking using WebSockets
- Unit & Integration Testing
- CI/CD Pipeline

---

# 👨‍💻 Author

Atharva Deshmukh

GitHub:
https://github.com/Atharva5500

---

# ⭐ Project Goal

This project was built to practice and demonstrate production-level backend development concepts using Spring Boot and modern REST API design principles.

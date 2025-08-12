# Inventory Management System

A comprehensive inventory management solution built with Spring Boot that helps businesses track, manage, and optimize their inventory operations.

![Inventory Management System](https://via.placeholder.com/800x400?text=Inventory+Management+System)

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Installation and Setup](#installation-and-setup)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)

## Overview

The Inventory Management System is a robust solution designed to streamline inventory operations for businesses of all sizes. It provides tools for product management, stock tracking, warehouse management, and reporting to optimize inventory levels and prevent stockouts or overstocking.

## Features

- **User Authentication & Authorization**
    - Secure JWT-based authentication
    - Role-based access control

- **Product Management**
    - Complete product lifecycle management
    - SKU and barcode tracking
    - Product categorization

- **Inventory Control**
    - Real-time inventory tracking
    - Multi-warehouse inventory management
    - Batch and expiry date tracking
    - Stock adjustment and transfer capabilities

- **Warehouse Management**
    - Multiple warehouse support
    - Location tracking within warehouses

- **Supplier Management**
    - Supplier information management
    - Default supplier assignment for products

- **Purchase Order Processing**
    - Create and manage purchase orders
    - Receive inventory against purchase orders

- **Reporting & Analytics**
    - Low stock alerts
    - Expiring inventory reports
    - Stock level history

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2
- **Database**: PostgreSQL
- **Security**: Spring Security, JWT Authentication
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven

## System Architecture

The application follows a layered architecture pattern:

```
┌─────────────────────────────────────────────────┐
│                   Controllers                    │
├─────────────────────────────────────────────────┤
│                   Services                       │
├─────────────────────────────────────────────────┤
│                Repositories                      │
├─────────────────────────────────────────────────┤
│                Database                          │
└─────────────────────────────────────────────────┘
```

- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic
- **Repositories**: Interface with the database
- **DTOs**: Transfer data between layers
- **Entities**: Map to database tables

## Installation and Setup

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven 3.6+
- PostgreSQL
- Git

### Steps

1. **Clone the repository**

```bash
git clone https://github.com/yourusername/inventory-management-system.git
cd inventory-management-system
```

2. **Configure database**

Create a PostgreSQL database and update `application.properties` or `application.yml` file with your database credentials.

```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **Update JWT Secret**

Configure a secure JWT secret in your application properties:

```properties
jwt.secret=YourSecureJwtSecretKeyHereShouldBeLongEnoughForHS512Algorithm
jwt.expirationMs=86400000
```

4. **Build the application**

```bash
mvn clean install
```

5. **Run the application**

```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## API Documentation

The application provides RESTful APIs for all functionality. Below is a summary of available endpoints:

### Authentication APIs

- `POST /api/auth/login` - Authenticate user
- `POST /api/auth/register` - Register new user

### User Management APIs

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Product APIs

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product
- `GET /api/products/sku/{sku}` - Get product by SKU
- `GET /api/products/search` - Search products
- `GET /api/products/low-stock` - Get products below min stock level

### Inventory APIs

- `GET /api/inventory` - Get all inventory
- `GET /api/inventory/{id}` - Get inventory by ID
- `POST /api/inventory` - Add inventory
- `PUT /api/inventory/{id}` - Update inventory
- `DELETE /api/inventory/{id}` - Delete inventory
- `GET /api/inventory/product/{productId}` - Get inventory by product
- `GET /api/inventory/warehouse/{warehouseId}` - Get inventory by warehouse
- `POST /api/inventory/{id}/adjust` - Adjust inventory quantity
- `POST /api/inventory/transfer` - Transfer inventory between warehouses
- `GET /api/inventory/expiring/{days}` - Get expiring inventory

### Category APIs

- `GET /api/categories` - Get all categories
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Create category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Supplier APIs

- `GET /api/suppliers` - Get all suppliers
- `GET /api/suppliers/{id}` - Get supplier by ID
- `POST /api/suppliers` - Create supplier
- `PUT /api/suppliers/{id}` - Update supplier
- `DELETE /api/suppliers/{id}` - Delete supplier

### Warehouse APIs

- `GET /api/warehouses` - Get all warehouses
- `GET /api/warehouses/{id}` - Get warehouse by ID
- `POST /api/warehouses` - Create warehouse
- `PUT /api/warehouses/{id}` - Update warehouse
- `DELETE /api/warehouses/{id}` - Delete warehouse

### Purchase Order APIs

- `GET /api/purchase-orders` - Get all purchase orders
- `GET /api/purchase-orders/{id}` - Get purchase order by ID
- `POST /api/purchase-orders` - Create purchase order
- `PUT /api/purchase-orders/{id}/status` - Update purchase order status
- `POST /api/purchase-orders/{id}/receive` - Receive purchase order

## Database Schema

The system uses the following core entities:

- **Users**: Store user information and credentials
- **Products**: Store product details
- **Categories**: Product categorization
- **Inventory**: Track product quantities across warehouses
- **Warehouses**: Store location and capacity information
- **Suppliers**: Store supplier information
- **Purchase Orders**: Track orders placed to suppliers
- **Inventory Transactions**: Log all inventory movements

## Security

The application implements robust security measures:

- **Authentication**: JWT-based authentication
- **Password Security**: BCrypt password encoding
- **Authorization**: Role-based access control
- **API Security**: Secured endpoints with proper authorization
- **Input Validation**: Validation on all input data

## Testing

The system includes comprehensive test coverage:

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test component interactions
- **API Tests**: Test REST endpoints

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProductServiceTest

# Run only service tests
mvn test -Dtest=*ServiceTest
```

## Deployment

### Production Configuration

For production deployment, update the following configurations:

```properties
# application-prod.properties
spring.profiles.active=prod
spring.datasource.url=jdbc:postgresql://production-db-server:5432/inventory_db
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=WARN
```

### Deployment Options

1. **JAR Deployment**:
   ```bash
   java -jar target/inventory-management-system.jar --spring.profiles.active=prod
   ```

2. **Docker Deployment**:
   ```bash
   docker build -t inventory-system .
   docker run -p 8080:8080 inventory-system
   ```

3. **Cloud Deployment**:
   The application can be deployed to AWS, Azure, or Google Cloud Platform.

## Contributing

We welcome contributions to enhance the Inventory Management System:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Coding Standards

- Follow Java coding conventions
- Include unit tests for new features
- Update documentation for API changes
- Follow commit message conventions

---

*Last updated: 2025-08-12 16:38:01*
*Developed by dnyaneshagale*
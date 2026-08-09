# 🌐 POS Billing System (Point Of Sale)
#  [![Live Frontend](https://img.shields.io/badge/LIVE-Frontend-B?style=for-the-badge)](https://pos-billing-soft.netlify.app) [![Live Demo](https://img.shields.io/badge/LIVE-Swagger_APIs-blue?style=for-the-badge)](http://localhost:8080/api/v1.0/swagger-ui/index.html)



A professional-grade point-of-sale and billing web application. This project features a Spring Boot 3.5.10 backend and a React frontend, integrating AWS S3 for cloud storage and Razorpay for secure payments.

## 🏗️ System Architecture

* **Frontend:** React.js (Vite) running on` https://pos-billing-soft.netlify.app`
* **Backend:** Java 17+, Spring Boot 3.5.10
* **Security:** JWT-based stateless authentication with Role-Based Access Control (RBAC).
* **Database:** **MySQL** for **ACID compliance** and strict data integrity for financial transactions and inventory management.
* **Cloud:** AWS SDK for S3 bucket management.
* **Payments:** Razorpay Java SDK for order creation and signature verification.

## 🗄️ Why MySQL over MongoDB?
For this POS Billing system, I intentionally chose a **Relational Database (SQL)** over MongoDB for the following reasons:

* **Data Integrity (ACID Compliance):** Billing systems require strict consistency. SQL ensures that transactions (like updating inventory and processing payments) are completed fully or not at all, preventing data corruption.
* **Structured Relationships:** The project relies heavily on complex relationships between **Users**, **Categories**, **Items**, and **Orders**. SQL handles these predefined schemas and relational joins much more efficiently than a document-based store.
* **Complex Querying:** Calculating sales analytics, GST totals, and inventory reports is more performant using SQL's powerful aggregation and join capabilities.

## 🧩 Key Features

* **JWT Authentication:** Secure login flow providing email, token, and user roles (ADMIN/USER).
* **Inventory Management:** Full CRUD for Categories and Items, featuring multipart file uploads to AWS S3.
* **Automated Billing:** Logic to calculate subtotal, tax, and grand totals for customer orders.
* **Payment Gateway:** Integration with Razorpay for UPI/Online payments and a CASH option.
* **Live Dashboard:** Real-time sales metrics including "Today's Sale," "Order Count," and a list of the 5 most recent transactions

## ⛳ Setup & Installation
### Backend Configuration
Add your credentials to `src/main/resources/application.properties`:

```properties
# AWS Configuration
aws.access.key=YOUR_ACCESS_KEY
aws.secret.key=YOUR_SECRET_KEY
aws.region=YOUR_REGION
aws.bucket.name=YOUR_BUCKET_NAME

# Razorpay Keys
razorpay.key-id=YOUR_RAZORPAY_ID
razorpay.key.secret=YOUR_RAZORPAY_SECRET

# JWT Security
jwt.secret.key=YOUR_BASE64_SECRET
```
## 🏃 Run Locally
1. Backend:
```java
mvn clean install
mvn spring-boot:run
```
2. Frontend:
```React
 npm install
 npm run dev
```
## 🔌 API Reference
```APIS
# Auth & User
* POST /login: Authenticates user and returns JWT.
* POST /admin/register: (Admin Only) Register new users.
```
```APIS
# Categories & Items
* POST /admin/categories: Upload category with image to S3.
* POST /admin/items: Add items to a specific category.
* GET /items: Fetch all available products.
```
```APIS
# Orders & Payments
* POST /orders: Place a new order.
* POST /payments/create-order: Initialize a Razorpay payment.
* POST /payments/verify: Verify payment signatures after transaction
```

## 🛡 Security & CORS
The backend is secured using a JwtRequestFilter.
* **Public:** /login, /encode.
* **Admin Only:** Any path under /admin/**.
* **CORS:** Pre-configured to allow requests from `https://pos-billing-soft.netlify.app`.
## 👥 Contribution & Collaboration
- **Backend Development:** Engineered the core RESTful API architecture using Spring Boot 3.5.10.
- **Frontend Integration:** Collaborated closely with the Frontend Developer to define API contracts, handle CORS policies, and integrate JWT-based security flows.
- **API Documentation:** Implemented **Swagger/OpenAPI** to provide interactive documentation for seamless testing and frontend-to-backend handshakes.

## 🔮 Future Enhancements (Under Development)
* **Smart AI Insights:** Implementation of RAG (Retrieval-Augmented Generation) to create an AI-based POS assistant for business predictions and insights.

* **Enhanced Security:** Migrating to HMAC-SHA256 for advanced payment signature verification.

* **Transaction Management:** Adding multi-step transactional integrity for complex inventory updates.

## 📖 API Documentation (Swagger)
Once the application is running, you can access the interactive API documentation at:
`http://localhost:8080/api/v1.0/swagger-ui/index.html`

## 🧾 Author
* 👤  **Vandesh Ghodke**
* 🎯 **Java Backend Developer** | B.Tech Automation & Robotic 2025
* 📧 **vandesghodke2003@gmail.com**
* 🔗 **GitHub – 2003Vandu**
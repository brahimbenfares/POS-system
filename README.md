# POS-system
# 🧾 HB Store – AI-Powered POS System (Java + MySQL)

HB Store is a comprehensive, offline Point-of-Sale (POS) system built with Java and MySQL. It empowers retail and wholesale businesses to manage sales, inventory, suppliers, and customers while using intelligent AI tools to forecast demand, suggest pricing, and optimize stock management.

## 🔧 Key Features

### Core System
- Modular Java backend using **DAO → Service → Controller** architecture
- Full CRUD support for:
  - Products
  - Orders
  - Suppliers
  - Wholesale Customers
  - Admins (with role-based permissions)
- Dual-format receipts: **A4 invoice style** and **thermal printer layout**
- Secure admin login & session management
- Inventory alerts for low stock items
- Integrated frontend using static **HTML/CSS/JS**

### 🤖 AI-Powered Modules
- `GET /api/ai/predict-sales/{productId}/{daysAhead}`  
  → Predict future demand based on historical sales  
- `GET /api/ai/price-suggestions`  
  → Generate dynamic pricing based on trends and stock levels  
- `GET /api/ai/reorder-recommendations`  
  → AI-suggested stock replenishment  
- `GET /api/ai/customer-trends`  
  → Detect behavior trends in customer orders  
- `GET /api/ai/personalized-promotions`  
  → Auto-generate targeted discount strategies  
- `GET /api/ai/stock-optimization`  
  → Recommend ideal inventory distribution  
- `GET /api/ai/sales-report`  
  → Auto-generated AI-enhanced sales summary  

## 🗂️ Project Structure




📁 hbstore-backend/
├── App.java # Initializes app & routes
├── Main.java # Entry point
├── controllers/
│ ├── ProductController.java
│ ├── OrderController.java
│ ├── AdminController.java
│ └── AISalesForecastController.java
├── dao/
│ ├── ProductDAO.java
│ ├── OrderDAO.java
│ └── ForecastDAO.java
├── models/
│ └── Product.java, Order.java, etc.
├── services/
│ ├── ProductService.java
│ ├── ForecastService.java
│ └── AuthService.java
├── static/
│ └── index.html, dashboard.html, etc.
├── database/
│ └── db_setup.sql (MySQL schema)


## 🧠 Technologies Used

- Java 17
- Javalin (REST API routing)
- MySQL (via JDBC)
- Tailwind CSS (for UI)
- AI logic via Java-based prediction models
- Modular MVC-style architecture

## 📊 Target Use Case

HB Store is designed for **offline or semi-offline small businesses** that want modern POS functionality with the power of AI forecasting — without relying on cloud platforms.

## 🔐 Note

This repository does not contain any production credentials or API keys. All database connections use local development settings. Please configure `db_config.properties` before running the app.

---

## 🔗 Demo Links (to be added after push)
- Forecasting logic: `/services/ForecastService.java`
- AI endpoints: `/controllers/AISalesForecastController.java`
- SQL structure: `/database/db_setup.sql`


# turbine-webstore

A lightweight, customizable shopping cart and inventory manager for small businesses built with Apache Turbine and Torque.

## 🛍️ Overview

**turbine-webstore** is a complete e-commerce and inventory management system tailored for small businesses. Built using the [Apache Turbine](https://turbine.apache.org/) framework (v6.0) and [Apache Torque](https://db.apache.org/torque/) ORM, the application provides out-of-the-box functionality for product management, order tracking, PayPal payments, and customizable branding — all backed by a flexible, database-driven architecture.

---

## 🚀 Features

- 🔧 Inventory and product catalog management
- 🛒 Shopping cart and order workflow
- 💳 PayPal integration with sandbox/test mode support
- 📦 Order tracking and shipping label printing
- 📄 PDF invoice generation with logo branding
- 🧑‍💼 Admin panel with customizable homepage, credentials, and configuration
- 🔒 Secure login and role-based access
- 🧩 Easily extendable via Maven modules

---

## 🔎 Live Demo

You can see a the application running live at:

👉 **[https://stuffedchickens.com](https://stuffedchickens.com)**


---

## ⚙️ Technology Stack

- **Framework:** Apache Turbine v6.0
- **ORM Layer:** Apache Torque
- **Database:** MySQL (default) — configurable to other databases via `pom.xml`
- **Build Tool:** Maven
- **Deployment:** Jetty / Tomcat compatible
- **Language:** Java 11+

---

## 🔧 Setup & Build Instructions

### 📦 Prerequisites

- Java JDK 8 or later
- Maven 3.x
- MySQL (or any Torque-compatible database)

### 🖥️ Option 1: Import into Eclipse

This project is ready to import directly as a **Maven Project**:

1. Open Eclipse.
2. Navigate to **File > Import > Maven > Existing Maven Projects**.
3. Select the root directory of this repository.
4. Finish the import process.

> Make sure to configure your database credentials before running.

### 🧪 Option 2: Run via Command Line

1. Create a MySQL database called `webstore`.
2. Update the following files with your database **username** and **password**:
   - `pom.xml`
   - `src/main/webapp/WEB-INF/jetty-env.xml`
   - `src/main/webapp/META-INF/context.xml`

3. From the project root, run:

```bash
mvn clean install
```

This will generate all required tables and initialize the default schema using Torque.

---


## 🔐 Default Admin Login

Once the application is deployed, you can log into the admin panel using the default credentials:

- **Username:** `admin`  
- **Password:** `password`

You should change these credentials immediately after setup for security.

## 💳 PayPal Integration

WebStore uses the PayPal API for transaction processing. You’ll need to:

- Create your own PayPal Developer account
- Generate API keys (client ID and secret)
- Configure them in the **Admin Panel** once the app is running

These keys are securely stored in the `COMPANY_CONFIG` table as:
- `PAYPAL_CLIENT_ID`
- `PAYPAL_CLIENT_SECRET`

To run in **sandbox mode**, enable the appropriate toggle in the Admin Panel.

> **Note:** It's critical that the `SERVER_URL` matches your deployed domain to ensure PayPal callbacks are received correctly.

## 🎨 Customization

Once deployed, you can:
- Customize the homepage landing screen
- Upload your company’s logo (used in PDF invoices)
- Print shipping labels directly from the admin order management page

## 📜 License

This project is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0). It is provided without any restrictions, warranties, or guarantees.

---

© 2025 JiveCast – Built with ❤️ for small business owners.

# turbine-webstore
A lightweight, customizable shopping cart and inventory manager for small businesses built with Apache Turbine and Torque.

**WebStore** is a lightweight shopping cart and inventory management application designed specifically for small businesses. Built with the [Apache Turbine](https://turbine.apache.org/) framework (v6.0) and powered by [Apache Torque](https://db.apache.org/torque/) for ORM/database operations, WebStore offers a customizable and scalable foundation for e-commerce applications.

## 🚀 Features

- Complete product and inventory management
- Shopping cart and order processing system
- PayPal integration with sandbox/test mode support
- Admin panel for configuration, branding, and reporting
- PDF invoice generation and shipping label printing
- Custom homepage and logo configuration

## ⚙️ Technology Stack

- **Backend Framework:** Apache Turbine v6.0
- **ORM/DB Layer:** Apache Torque
- **Database:** MySQL (default, configurable)
- **Build System:** Maven
- **Deployment:** Jetty / Tomcat compatible

## 🔧 Configuration

To run the application locally, follow these steps:

1. **Create a MySQL database** called `webstore`.

2. **Update the following configuration files** with your own database username and password:
   - `pom.xml`
   - `src/main/webapp/WEB-INF/jetty-env.xml`
   - `src/main/webapp/META-INF/context.xml`

3. **Update PayPal callback URL:**
   - In `src/main/java/com/jivecast/webstore/paypal/PaymentServices.java`, set the `SERVER_URL` to the correct base URL where your app will be hosted.

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

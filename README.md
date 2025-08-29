# SwiftCart

## Description
A full-stack e-commerce application built with **Spring Boot (backend)** and **React (frontend)**.  
It allows users to browse products, manage their cart, and securely complete purchases.  
Designed as a Single Page Application (SPA) with a responsive layout for both desktop and mobile.

---

## Tech Stack
- **Backend:** Spring Boot, REST APIs, Spring Data JPA, Spring Security
- **Frontend:** ReactJS, React Router  
- **Database:** SQL
- **Authentication:** JWT
- **Other Tools:** Git, GitHub, Maven, NPM, Vite

---

## Features
- User signup & login with JWT authentication
- Access & Refresh token mechanism (auto refreshes expired access token)
- Product catalog with search & filter
- Cart & checkout simulation
- Delivery Addresses management & selection
- Seller panel for managing products and orders
- REST API backend with Spring Boot
- Razorpay Payment Gateway Integration (test mode)
- Organized project structure (separate backend & frontend)
- Easy to deploy (can be hosted on Render / Railway / Vercel)

---

## Working GIF
Check out the app in action!<br /><br />
<img src="screenshots/demo.gif" alt="App Demo" width="600" />

## Live Demo
[Click here to view the app](https://swiftcart-omega.vercel.app)

## Screenshots
- ### Home Page
<img src="screenshots/home.png" alt="Home Page" width="400" />

- ### Cart Page
<img src="screenshots/cart.png" alt="Cart Page" width="400" />

- ### Login Page
<img src="screenshots/login.png" alt="Login Page" width="400" />

- ### Manage Addresses Page
<img src="screenshots/addresses.png" alt="Manage Addresses Page" width="400" />

- ### Select Address Page
<img src="screenshots/select-address.png" alt="Select Address Page" width="400" />

- ### Profile Page
<img src="screenshots/profile.png" alt="Profile Page" width="400" />

- ### Checkout Page
<img src="screenshots/checkout.png" alt="Checkout Page" width="400" />

- ### Payment Page
<img src="screenshots/payment.png" alt="Payment Page" width="400" />

## Run

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm start
```

## API Documentation

This project includes **OpenAPI (Swagger UI)** for exploring and testing REST APIs.  

<img src="screenshots/swagger-ui.png" alt="Swagger UI" width="600" />
# E-Commerce Backend System – Phase 1

Backend E-Commerce system cho local brand **Hung Hypebeast**, tập trung giải quyết bài toán **overselling** thông qua **Inventory Locking + Reservation**.
---

## Overview

- **Project**: E-Commerce Backend – Hung Hypebeast
- **Author**: Tungts17 
- **Tech Stack**: Spring Boot 4.0.1 · Java 21 · MySQL
- **Version**: 1.0.0
- **Status**: Phase 1 – 90% Complete

---

## Problem Statement

Trong mô hình bán hàng online, khi nhiều khách cùng mua **sản phẩm còn 1 item**, hệ thống dễ gặp:

- Race condition
- Overselling
- Order sai lệch tồn kho

Dự án này được thiết kế để **ngăn overselling** và đảm bảo **first-come-first-served**.

---

## Key Features
### Yêu cầu khách hàng

Catalog: Quản lý variants (Size/Màu), phân trang, lọc giá
Cart: Guest + Customer, check tồn kho
Inventory Locking (CRITICAL): Giữ hàng 10-15 phút khi checkout
Payment: COD + SePay (defer Phase 2)
Order Tracking: Email link, không cần đăng nhập
Admin: Xem đơn + đổi status

### Must-Have (Completed)
- Product Catalog + Sku (Size / Color)
- Customer Cart
- Inventory Locking (Pessimistic Lock)
- Reservation-based Checkout (15 minutes)
- Order Management
- Public Order Tracking
- Email Notifications 

### Phase 2 (Planned)

- SePay Integration
- Async Email Queue
- Redis Cache
- Message Queue (RabbitMQ / Kafka)

---

## System Design

1. Entity Relationship Diagram
<img width="1585" height="934" alt="E-commerceSystem" src="https://github.com/user-attachments/assets/db3f2594-418d-4e89-8b17-5a307ab24b4c" />


Hybrid ID Strategy: Auto-increment (users, products) vs UUID (orders, carts, reservations)

Product Variants: Separate table để track stock cho từng Size/Màu

Inventory Reservations: Bảng riêng giữ hàng 15 phút, không lock trực tiếp product_variants

2. Tech Stack
   
Backend Framework: Spring Boot 4.0.1

Language: Java 21 (LTS)

Database: mysql-8.0.36

Security: Spring Security 6 + JWT

Validation: Jakarta Validation 3.0

Email: JavaMailSender + Thymeleaf

Build Tool: Maven 3.9+

Testing: Postman Collection

## Technique
1. Inventory Locking - Giải quyết Race Condition
Problem: 2 users cùng mua "last item" → overselling

Solution: 3-Layer Protection

Layer 1: Database Lock

@Lock(LockModeType.PESSIMISTIC_WRITE)
Sku findByIdWithLock(Long id);

Layer 2: Soft Reservation

int available = stock - SUM(active_reservations);

Layer 3: Transaction Isolation

@Transactional(isolation = Isolation.READ_COMMITTED)
Sequence Diagram: ./images/Sequence_Diagram_Inventory_Locking.png

Luồng xử lý:

Customer A → POST /checkout/prepare
START TRANSACTION + SELECT ... FOR UPDATE (lock row)
Calculate: Available = Stock (1) - Active Reservations (0) = 1
INSERT reservation (expires_at = NOW + 15m)
COMMIT (release lock)
Customer B bị block → tính lại Available = 0 → REJECT
Cleanup: Scheduled task chạy mỗi 5 phút để expire old reservations

2. Public Order Tracking
Challenge: Khách track order mà không cần login

Solution: Email Verification + Content Negotiation

GET /api/v1/public/orders/{uuid}?email=customer@example.com
Accept: text/html → HTML view
Accept: application/json → JSON API
Security:

UUID order ID (không đoán được)
Email verification (chỉ người có email)
Rate limiting ready

3. Email System
Challenge: Order confirmation emails không làm crash order creation

Solution: Strategy Pattern + Conditional Bean

// Production
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "true")
public class EmailServiceImpl { }

// Development (no-op)
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "false")
public class NoOpEmailService { }
Result:

Non-blocking (order creation NEVER fails)
Dev-friendly (no SMTP required locally)
Professional HTML templates (Thymeleaf)

## CÀI ĐẶT & CHẠY ỨNG DỤNG

1. Yêu Cầu Hệ Thống
Component	Version	Note
Java	21 (LTS)	Required
Maven	3.9+	Wrapper included
Docker	Latest	Recommended for DB
Mysql
Postman	Latest	For testing

2. Clone Repository
git clone 
cd e-commerce

3. Configuration
File src/main/resources/application.yaml:

# Database Configuration
spring:
  application:
    name: ecommerce
  datasource:
    url: "jdbc:mysql://localhost:3306/ecommerce"
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# Email Configuration (Gmail SMTP)
  mail:
    enabled: true
    host: smtp.gmail.com
    port: 587
    username: your_email
    password: your_app_password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000

# Order Tracking URL (for email links)
app.order.tracking.base-url=http://localhost:8080/api/v1/public/orders

# Server Configuration

server.port=8080
Cấu hình Email (Required cho email notifications):

Bước 1: Tạo Gmail App Password

Vào Google Account Security

Bật 2-Step Verification
Vào App passwords → Generate new password
Chọn Mail + Other (Custom name) → Nhập "E-Commerce API"
Copy 16-digit password (vd: abcd efgh ijkl mnop)

Bước 2: Update application.properties

spring.mail.username=your-email@gmail.com
spring.mail.password=abcd efgh ijkl mnop

4. Build & Run Application

# Build project
./mvnw clean package

# Run application
./mvnw spring-boot:run
Alternative: Run compiled JAR

java -jar target/e-commerce-0.0.1-SNAPSHOT.jar
5. Verify Application is Running

# Check health
curl http://localhost:8080/actuator/health

# Expected response:
{"status":"UP"}

# Run entire collection

newman run E-Commerce-API.postman_collection.json \
  -e E-Commerce.postman_environment.json \
  --reporters cli,html \
  --reporter-html-export test-results.html

Architecture & Design

Tech Stack

Layer	Technology

Backend Framework	Spring Boot 4.0.1

Language	Java 21 (LTS)

Database	MySQL

ORM	Spring Data JPA + Hibernate

Build Tool	Maven 3.9+

Database Schema (ERD)

        Cart ─────< (N) CartItem >───── (1) ProductVariant
                                                  │
         Order ────< (N) OrderItem >──────────────┘
                                                  │
         InventoryReservation >───────────────────┤
                                                  │
Category (1) ────< (N) Product ─────< (N) Sku ────┘
                                          
# API Endpoints Summary
```
Endpoint	Method	Auth	Description

/api/products	GET	Public	Browse products (filters)

/api/products/{id}	GET	Public	Product details

/api/cart	GET	Customer	View cart

/api/cart/addItems	POST	Customer	Add to cart

/api/cart/updateItems	PUT	Customer	Update quantity

/api/cart/items/{id}	DELETE	Customer	Remove item

/api/checkout/checkout	POST	Customer	Reserve stock

/api/checkout/verify/{id}	GET	Customer	Verify reservation

/api/orders	POST	Guest/Customer	Create order

/api/orders	GET	Customer	My orders

/api/orders/{id}	GET	Customer	Order details

/api/public/orders/{id}	GET	Public	Track order (HTML/JSON)

/api/orders	GET		All orders

/api/orders/{id}	GET		Any order details

/api/orders/{id}/status	PATCH		Update status

Order Tracking (Content Negotiation)
Strategy Pattern: Cùng 1 endpoint nhưng trả về HTML hoặc JSON tùy client

Endpoint: GET /api/v1/public/orders/{orderId}?email={email}
```
Browser (HTML View)
# Open in browser
http://localhost:8080/api/v1/public/orders/faecce20-9ca3-4126-8c35-cc136344a474?email=customer@example.com

# Returns: Professional HTML page with order details

 - Customer info (name, email, phone, address)
 - Order status badge (color-coded)
 - Product items table
 - Payment information
 - Responsive design (mobile-friendly)
   
Features:

Professional UI (Segoe UI font, clean layout)

Status badges (color-coded: PENDING=yellow, CONFIRMED=blue, PROCESSING=cyan, SHIPPED=green, DELIVERED=dark green)

Formatted currency (4,500,000 đ)

Responsive design (grid layout for mobile)

No authentication required (email verification)

 API Client (JSON Response)
# Postman or curl
curl -H "Accept: application/json"
  "http://localhost:8080/api/v1/public/orders/faecce20...?email=customer@example.com"

# Returns JSON:
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": "faecce20-9ca3-4126-8c35-cc136344a474",
    "customerName": "Guest Test User",
    "status": "PENDING",
    "totalAmount": 4500000.00,
    "items": [...]
  }
}
Architecture:

// Strategy 1: HTML View
@Controller
class PublicOrderViewController {
    @GetMapping("/api/v1/public/orders/{orderId}")
    String trackOrder(..., Model model) {
        return "order-tracking"; // Thymeleaf template
    }
}

// Strategy 2: JSON Response
@RestController
class PublicOrderController {
    @GetMapping(value = "/{orderId}", produces = "application/json")
    ResponseEntity<ApiResponse<OrderDTO>> trackOrder(...) {
        return ResponseEntity.ok(...);
    }
}
Spring MVC tự động chọn controller dựa trên:

Browser request → Accept: text/html → HTML view
API request → Accept: application/json → JSON response
Product Filter API 
Endpoint: GET /api/v1/products

Filter Parameters:

- Filter by category
GET /api/products?categoryId=1

- Filter by price range
GET /api/products?minPrice=3000000&maxPrice=5000000

Key Design Decisions
1. Stock Reservation (Pessimistic Locking)
// InventoryReservation Entity
- reservedQuantity: int
- expiresAt: LocalDateTime (15 minutes)
- status: ACTIVE | COMPLETED | EXPIRED

// Scheduler runs every 5 minutes
@Scheduled(fixedRate = 300000)
void cleanupExpiredReservations()
Benefits:

Prevents overselling
Fairness (first-come-first-served)
Auto-cleanup expired reservations
2. Guest vs Customer Cart
// Guest: X-Guest-ID header (UUID)
// Customer: JWT token (@AuthenticationPrincipal)

// CartService resolves cart by:
if (userId != null) {
    return cartRepository.findByUserId(userId);
} else {
    return cartRepository.findBySessionId(sessionId);
}
3. Email Templates (Thymeleaf)
Location: src/main/resources/templates/email/

<!-- order-confirmation.html -->

- Professional dark theme (gray #1a1a1a + blue #0066cc) - Order summary (ID,
total amount, payment method) - Items table (product name, SKU, size/color,
quantity, price) - Tracking link button (→ HTML view) - Customer info (shipping
address) - Footer with brand info

<!-- order-status-update.html -->
- Status change visualization (OLD → NEW with arrow) - Color-coded status badges
- Tracking link - Professional footer
Email Triggers:

Event	Template	Recipient	Trigger Point
Order Created	order-confirmation.html	Customer email	After order creation
Status Updated	order-status-update.html	Customer email	Admin updates status


Project Structure

```
e-commerce/
├── src/main/java/com.example.e_commerce
│   ├── config/               # SessionConfig
│   ├── Enums/                # Enums
│   ├── controllers/          # REST API Controllers
│   ├── dtos/                 # Request/Response DTOs
│   ├── entities/             # JPA Entities
│   ├── exceptions/           # Custom Exceptions
│   ├── repositories/         # Spring Data JPA Repositories
│   ├── schedulers/           # Scheduled Tasks
│   ├── services/             # Business Logic
│
├── src/main/resources/
│   ├── application.yaml
│   └── templates/email/      # Email templates
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── E-Commerce-API.postman_collection.json
├── E-Commerce.postman_environment.json
└── README.md (this file)
```

Contact & Support
Developer: TungTS17
Email: TungTS17@fpt.edu.vn
GitHub: https://github.com/Trantung03/Ecommerce

Instructor: Anh Hùng (HungHypeBeast)
Course: Backend Development - Phase 1

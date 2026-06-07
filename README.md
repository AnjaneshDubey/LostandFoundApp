# FindStuff - Lost & Found Item Tracker - College Management System

<div align="center">
    
*Enterprise Java console application with MySQL integration for campus lost item recovery*

[![Built on - Java](https://img.shields.io/badge/Backend-Java%20%7C%20JDBC-blue)](#)
[![Database - MySQL](https://img.shields.io/badge/Database-MySQL%20%7C%20InnoDB-orange)](#)
![Architecture](https://img.shields.io/badge/Architecture-MVC%20Pattern-8A2BE2)
![License](https://img.shields.io/badge/License-MIT-2F2F2F)

</div>

---

## 🚀 Overview

College Lost & Found Tracker is a comprehensive console-based management system designed to streamline campus lost item recovery:
- **Secure authentication** with SHA-256 password hashing and salt-based encryption
- **Role-based access control** with Admin and Student privilege levels
- **Auto-generated tracking numbers** (Format: LF-YYYYMMDD-XXXX) for efficient item identification
- **Advanced search functionality** with multi-criteria filtering by category, location, and status
- **Business intelligence dashboard** with comprehensive admin reports and analytics
- **JDBC optimization** with prepared statements, connection pooling, and ResultSet management

Built with pure Java, MySQL database, and enterprise design patterns for scalability and reliability.

---

## 🌟 Key Features

| Feature                          | Description                                                                                                                              |
| -------------------------------- | -----------------------------------------------------------------------------------------------------------------------------------------|
| 🔐 **Secure Authentication**    | SHA-256 password hashing with custom salt generation, failed login tracking, and account activation management                          |
| 👥 **Role-Based Access**        | Dual privilege system: Admin (full CRUD operations) and Student (personal item management and search)                                   |
| 📊 **Item Tracking System**     | Auto-generated unique tracking numbers, multi-status workflow (LOST/FOUND/CLAIMED/RETURNED), and timestamp tracking                    |
| 🔍 **Advanced Search Engine**   | Keyword search across multiple fields, category/location/status filters, and advanced multi-criteria search                             |
| 📈 **Admin Dashboard**          | User management, item oversight, system reports (status/category/activity), and bulk operations                                         |
| 🎨 **Console UI Framework**     | Custom-built ConsoleUI with formatted tables, input validation, menu navigation, and cross-platform screen clearing                    |

---

## 📁 Project Structure
```bash
📁 lost-and-found-tracker/
├── 📁 backend/
│ ├── 📄 DatabaseConnection.java # Singleton DB connection with pooling
│ ├── 📄 PasswordUtil.java # SHA-256 hashing utilities
│ ├── 📄 ValidationUtil.java # Input validation engine
│ ├── 📄 User.java # User model/POJO
│ ├── 📄 Item.java # Item model/POJO
│ ├── 📄 UserOperations.java # User CRUD operations
│ ├── 📄 ItemOperations.java # Item CRUD operations
│ └── 📄 SearchOperations.java # Advanced search functionality
├── 📁 frontend/
│ ├── 📄 ConsoleUI.java # Reusable UI components
│ ├── 📄 LoginView.java # Authentication screens
│ ├── 📄 ItemView.java # Item management views
│ ├── 📄 SearchView.java # Search interface
│ ├── 📄 StudentMenu.java # Student dashboard
│ ├── 📄 AdminMenu.java # Admin dashboard
│ └── 📄 Main.java # Application entry point
├── 📄 schema.sql # Database schema
├── 📄 database.properties # DB configuration
```


---

## 🚀 **Core Capabilities**

---

### Database Architecture 🗄️

- **Schema Design**: 3-table normalized structure (users, items, status_updates) with foreign key constraints
- **User Table**: Secure credential storage, role management, login tracking, account activation flags
- **Item Table**: Comprehensive metadata (tracking_number, category, location, dates), status workflow, soft-delete support
- **Status Updates**: Complete audit trail with timestamp logging and user attribution
- **Indexes**: Optimized queries with indexes on tracking_number, username, status, category

---

### Security Implementation 🔒

>**Password Security:** SHA-256 hashing → Custom salt generation → Secure verification → Account lockout after failed attempts  
>**SQL Injection Prevention:** PreparedStatement usage → Parameter binding → Input sanitization → Validation layer  
>**Access Control:** Role-based permissions → Session management → Admin privilege checks → User data isolation  
>**Data Integrity:** Foreign key constraints → Transaction support → Soft-delete pattern → Audit logging

---

### Search & Analytics 📊

- **Search Capabilities**: Keyword search with LIKE patterns, Category filtering, Location-based search, Status tracking
- **Admin Reports**: Items by status (LOST/FOUND/CLAIMED/RETURNED), Category distribution analysis, User activity metrics, System performance statistics
- **Data Export**: SQL query results, Business insights, User reports, Item tracking history

---

### Technical Implementation 🔧

**MVC Architecture:** Clear separation of concerns → Backend services → Frontend views → Modular design  
**JDBC Optimization:** Connection pooling → PreparedStatements → ResultSet management → Resource cleanup  
**Console UI Framework:** Formatted tables → Input validation → Menu navigation → Cross-platform support  
**Error Handling:** Try-catch-finally patterns → User-friendly messages → Graceful degradation → Exception logging

---

## 🚀 Quick Start

### Prerequisites

```bash
Required Softwares:

JDK 8 or higher

MySQL Server 5.7+

MySQL Connector/J JDBC Driver (mysql-connector-j-8.0.33.jar)

Command Prompt / Terminal
```
### Note:
```bash
add your actual local mysql password in- database.properties file - "db.password=  //your device mysql password
" in this line
```
### Installation
```bash
1. Clone repository
git clone https://github.com/vishnupriyanpr/lost-and-found-tracker.git
cd lost-and-found-tracker

2. Setup MySQL Database
mysql -u root -p < schema.sql

3. Configure database.properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/lostfound_db
db.username=root
db.password=your_password

4. Download MySQL JDBC Driver
Place mysql-connector-j-8.0.33.jar in project root
Download: https://dev.mysql.com/downloads/connector/j/

5. Compile Java files

javac -cp ".;mysql-connector-j-8.0.33.jar" backend/.java frontend/.java

6. Run Application

java -cp ".;mysql-connector-j-8.0.33.jar" frontend.Main
```

### First Run
```bash
Register Admin Account

Choose "Register New Account"
Enter username: admin
Enter email: admin@college.edu
Enter phone: 1234567890
Enter password: admin123
Select role: ADMIN

Login and Explore

Username: admin
Password: admin123
```

---

## 💡 Usage Examples

### Student Operations
```bash
Report Lost Item
Login → Report Lost Item
Item Name: Blue Backpack
Category: PERSONAL
Location: Library 2nd Floor
Date Lost: 2025-10-24
Get Tracking Number: LF-20251024-0001

Search Items
Login → Search Items
Choose filter: Category
Select: ELECTRONICS
View results in formatted table
```

### Admin Operations
```bash
View All Users
Login as Admin → View All Users
See user table with ID, username, email, role, status

Generate Reports
Login as Admin → Generate Reports
Choose: Items by Status Report
View: LOST (15), FOUND (12), CLAIMED (5), RETURNED (3)

Delete Item
Login as Admin → Delete Item
Enter Tracking Number: LF-20251024-0001
Confirm deletion
```

---

## 🗃️ Database Schema

```bash
-- Users Table
CREATE TABLE users (
user_id INT PRIMARY KEY AUTO_INCREMENT,
username VARCHAR(50) UNIQUE NOT NULL,
hashed_password VARCHAR(64) NOT NULL,
email VARCHAR(100) UNIQUE NOT NULL,
phone VARCHAR(15),
role ENUM('STUDENT', 'ADMIN') DEFAULT 'STUDENT',
is_active BOOLEAN DEFAULT TRUE,
failed_login_attempts INT DEFAULT 0,
last_login TIMESTAMP NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Items Table
CREATE TABLE items (
item_id INT PRIMARY KEY AUTO_INCREMENT,
tracking_number VARCHAR(20) UNIQUE NOT NULL,
user_id INT NOT NULL,
item_name VARCHAR(100) NOT NULL,
description TEXT,
category ENUM('ELECTRONICS','BOOKS','PERSONAL','DOCUMENTS','ACCESSORIES','OTHER'),
location VARCHAR(100),
date_lost DATE,
date_found DATE,
status ENUM('LOST','FOUND','CLAIMED','RETURNED') DEFAULT 'LOST',
found_by INT,
is_active BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (user_id) REFERENCES users(user_id),
FOREIGN KEY (found_by) REFERENCES users(user_id)
);
```

---

## 🎯 Features Roadmap

### Phase 1 (Current) ✅
- ✅ User authentication and registration
- ✅ Lost/Found item reporting
- ✅ Advanced search functionality
- ✅ Admin dashboard and reports
- ✅ Item tracking system

### Phase 2 (Planned) 🚧
- 🔄 Email notifications for matches
- 🔄 Image upload support
- 🔄 SMS alerts integration
- 🔄 QR code generation for items
- 🔄 Mobile app development

### Phase 3 (Future) 🎯
- 📊 AI-powered item matching
- 🌐 Web-based interface
- 📱 Mobile push notifications
- 🔐 Two-factor authentication
- 📈 Advanced analytics dashboard

---

## 🤝 Contributing

Contributions are welcome! Development guidelines:

1. **Fork Repository** → Create feature branch (`feature/AmazingFeature`)
2. **Code Standards** → Follow Java conventions → Comment complex logic → Write clean code
3. **Testing** → Test all CRUD operations → Verify SQL queries → Check error handling
4. **Documentation** → Update README → Add code comments → Document new features
5. **Submit PR** → Detailed description → Link related issues → Request review

**Areas for Contribution:**
- **Backend Enhancement** - API development - Cloud integration - Performance optimization - Security hardening
- **Frontend Development** - GUI with JavaFX/Swing - Web interface - Mobile app - Dashboard improvements
- **Database Optimization** - Query optimization - Caching layer - Database migrations - Backup automation

---

## 📜 License

MIT License (see LICENSE file)

---

## 🙌 Acknowledgments & Core Team

This project is crafted with precision and innovation by **Vishnupriyan P R**.

<table align="center">
  <tr>
    <td align="center">
      <a href="https://github.com/vishnupriyanpr">
        <img src="https://github.com/vishnupriyanpr.png?size=120" width="120px;" alt="Vishnupriyan P R"/>
        <br />
        <sub><b>Vishnupriyan P R</b></sub>
      </a>
      <br />
      <br />
      <sub>JDBC Optimization • Security Implementation • Enterprise Architecture</sub>
    </td>
  </tr>
</table>

### Special Thanks
- **MySQL Community** - Database engine and JDBC driver
- **Java Community** - Platform and libraries
- **GitHub** - Version control and collaboration platform

---

## 📞 Support & Contact

- **Issues**: Report bugs via [GitHub Issues](https://github.com/vishnupriyanpr/lost-and-found-tracker/issues)
- **Discussions**: Join [GitHub Discussions](https://github.com/vishnupriyanpr/lost-and-found-tracker/discussions)
- **Email**: priyanv783@example.com
- **LinkedIn**: [Vishnupriyan P R](https://linkedin.com/in/vishnupriyan-p-r)

---

<div align="center">
  <p><i>🔍 Connecting Lost Items with Their Owners Through Smart Technology 🎯</i></p>
  <p><b>Made with ❤️ for College Communities</b></p>
</div>

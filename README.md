# 🎓 EduBoost

> **A comprehensive student productivity application with calendar management, study methods, and academic planning tools**

![EduBoost](client/images/Logo.png)

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Electron](https://img.shields.io/badge/Electron-Latest-blue.svg)](https://www.electronjs.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Table of Contents

- [✨ Features](#-features)
- [🏗️ Architecture](#️-architecture)
- [🚀 Getting Started](#-getting-started)
- [📦 Installation](#-installation)
- [🔧 Configuration](#-configuration)
- [💻 Usage](#-usage)
- [🔒 Authentication](#-authentication)
- [📚 API Documentation](#-api-documentation)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)

## ✨ Features

### 🎯 Core Features

- **📅 Smart Calendar Management**
  - Create, edit, and manage events with reminders
  - Color-coded event categories
  - Print-friendly schedule views
  - Todo items integration

- **📖 Study Methods Library**
  - Browse and discover proven study techniques
  - Favorite your preferred methods
  - Detailed guides with step-by-step instructions
  - Admin-curated content management

- **👤 User Management**
  - Secure JWT-based authentication
  - Email verification system
  - Role-based access control (User/Admin)
  - Profile management with avatar support

- **📊 Admin Dashboard**
  - User account management
  - Study method content moderation
  - System analytics and monitoring

### 🔧 Technical Features

- **🔐 Security**
  - JWT token authentication
  - BCrypt password encryption
  - CORS configuration
  - Input validation and sanitization

- **📧 Email Integration**
  - Account verification emails
  - Event reminder notifications
  - Password reset functionality

- **🎨 Modern UI/UX**
  - Responsive design with Bootstrap
  - Interactive notifications with Toastr
  - Dark/Light theme support
  - Print-optimized layouts

## 🏗️ Architecture

EduBoost follows a **client-server architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────┐
│                Client (Electron)             │
├─────────────────────────────────────────────┤
│ • HTML/CSS/JavaScript Frontend              │
│ • Electron Desktop Application             │
│ • Bootstrap UI Framework                   │
│ • Toastr Notifications                     │
└─────────────────────────────────────────────┘
                        │
                   HTTP/REST API
                        │
┌─────────────────────────────────────────────┐
│              Server (Spring Boot)           │
├─────────────────────────────────────────────┤
│ • RESTful API Endpoints                    │
│ • JWT Authentication                       │
│ • Business Logic Services                 │
│ • Data Validation                          │
└─────────────────────────────────────────────┘
                        │
                    JPA/Hibernate
                        │
┌─────────────────────────────────────────────┐
│               Database (MariaDB)            │
├─────────────────────────────────────────────┤
│ • User Management                          │
│ • Events & Schedules                       │
│ • Study Methods                            │
│ • Authentication Tokens                    │
└─────────────────────────────────────────────┘
```

### 🏛️ Server Architecture

- **Controller Layer**: REST endpoints handling HTTP requests
- **Service Layer**: Business logic and data processing
- **Repository Layer**: Data access with Spring Data JPA
- **Model Layer**: JPA entities representing database structure
- **Configuration Layer**: Security, CORS, and application settings

### 🖥️ Client Architecture

- **Electron Main Process**: Application lifecycle and window management
- **Renderer Process**: Web-based UI with HTML/CSS/JavaScript
- **Global Utilities**: API communication and shared functions
- **Modular JavaScript**: Feature-specific modules for each page

## 🚀 Getting Started

### 📋 Prerequisites

- **Java 21** or higher
- **Node.js 16** or higher
- **MariaDB 10.6** or higher
- **Maven 3.8** or higher
- **Git** for version control

### 🔧 System Requirements

- **OS**: Windows 10/11, macOS 10.15+, or Linux Ubuntu 18.04+
- **RAM**: Minimum 4GB (8GB recommended)
- **Storage**: 500MB free space
- **Network**: Internet connection for initial setup and email features

## 📦 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/edu_boost.git
cd edu_boost
```

### 2. Database Setup

```sql
-- Create database
CREATE DATABASE eduboost;

-- Create user (optional)
CREATE USER 'eduboost_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON eduboost.* TO 'eduboost_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Server Setup

```bash
cd server

# Configure database connection in application.properties
# (See Configuration section below)

# Install dependencies and run
mvn clean install
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

### 4. Client Setup

```bash
cd client

# Install dependencies
npm install

# Start the Electron application
npm start
```

### 5. Build for Production

**Server:**
```bash
cd server
mvn clean package
java -jar target/server-0.0.1-SNAPSHOT.jar
```

**Client:**
```bash
cd client
npm run build
npm run dist
```

## 🔧 Configuration

### 📊 Database Configuration

Create `server/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mariadb://localhost:3306/eduboost
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# JWT Configuration
jwt.secret=your-256-bit-secret-key-here
jwt.expiration=86400000

# Email Configuration (Gmail example)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Application Configuration
app.base-url=http://localhost:3000
server.port=8080
```

### 🖥️ Client Configuration

Update `client/global.js` if needed:

```javascript
// API Base URL
const API_BASE_URL = 'http://localhost:8080';

// Application Settings
const APP_CONFIG = {
    name: 'EduBoost',
    version: '1.0.0',
    defaultTimeout: 30000
};
```

## 💻 Usage

### 🚀 First Run

1. **Start the server** (Spring Boot application)
2. **Launch the client** (Electron application)
3. **Register a new account** or use the default admin credentials
4. **Verify your email** (check spam folder if needed)
5. **Start organizing your academic life!**

### 👨‍💼 Admin Functions

- **User Management**: View, edit, or delete user accounts
- **Content Moderation**: Manage study methods and educational content
- **System Monitoring**: Track application usage and performance

### 👤 User Functions

- **Schedule Management**: Create events, set reminders, view calendar
- **Study Planning**: Browse study methods, save favorites
- **Profile Management**: Update personal information, change passwords

## 🔒 Authentication

EduBoost uses **JWT (JSON Web Tokens)** for secure authentication:

### 🔑 Authentication Flow

1. **Registration**: User creates account with email verification
2. **Login**: Credentials validated, JWT token issued
3. **Authorization**: Token included in API requests
4. **Refresh**: Automatic token refresh for active sessions

### 🛡️ Security Features

- **Password Hashing**: BCrypt encryption
- **Token Expiration**: Configurable session timeouts
- **Role-Based Access**: User and Admin role separation
- **CORS Protection**: Cross-origin request filtering

## 📚 API Documentation

### 🔗 Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/verify` | Email verification |
| POST | `/api/auth/forgot-password` | Password reset request |

### 📅 Schedule Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/events` | Get user events |
| POST | `/api/events` | Create new event |
| PUT | `/api/events/{id}` | Update event |
| DELETE | `/api/events/{id}` | Delete event |

### 📖 Study Methods Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/study-methods` | Get all study methods |
| GET | `/api/study-methods/{id}` | Get specific method |
| POST | `/api/study-methods/favorite` | Add to favorites |
| DELETE | `/api/study-methods/favorite/{id}` | Remove from favorites |

### 👤 User Management Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/profile` | Get user profile |
| PUT | `/api/users/profile` | Update profile |
| POST | `/api/users/change-password` | Change password |

## 🏗️ Development

### 🛠️ Tech Stack

**Backend:**
- **Framework**: Spring Boot 3.4.1
- **Security**: Spring Security + JWT
- **Database**: Spring Data JPA + MariaDB
- **Validation**: Hibernate Validator
- **Documentation**: OpenAPI 3
- **Email**: Spring Mail
- **Build Tool**: Maven

**Frontend:**
- **Runtime**: Electron
- **UI Framework**: Bootstrap 5
- **Styling**: Custom CSS
- **Notifications**: Toastr
- **Icons**: Font Awesome + Feather Icons
- **Build Tool**: npm

### 📁 Project Structure

```
edu_boost/
├── client/                 # Electron frontend application
│   ├── html/              # HTML pages
│   ├── css/               # Custom stylesheets
│   ├── js/                # JavaScript modules
│   ├── images/            # Application images
│   ├── assets/            # Bootstrap & library assets
│   ├── main.js            # Electron main process
│   ├── global.js          # Global utilities
│   └── package.json       # Node.js dependencies
├── server/                # Spring Boot backend
│   ├── src/main/java/quochung/server/
│   │   ├── controller/    # REST controllers
│   │   ├── service/       # Business logic
│   │   ├── model/         # JPA entities
│   │   ├── repository/    # Data repositories
│   │   ├── config/        # Configuration classes
│   │   └── util/          # Utility classes
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml            # Maven dependencies
└── README.md              # This file
```

### 🔄 Development Workflow

1. **Backend Development**: Make changes in `server/` directory
2. **Frontend Development**: Update files in `client/` directory  
3. **Testing**: Run unit tests with `mvn test`
4. **Building**: Use `mvn package` for server, `npm run build` for client
5. **Deployment**: Package with `npm run dist` for distribution

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### 📝 Contribution Guidelines

- Follow existing code style and conventions
- Add tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot** team for the excellent framework
- **Electron** community for desktop app capabilities
- **Bootstrap** for responsive UI components
- **MariaDB** for reliable database performance

## 📞 Support

For support, email support@eduboost.com or create an issue on GitHub.

---

<p align="center">
  <strong>Made with ❤️ for students everywhere</strong>
</p>

<p align="center">
  <img src="client/images/Logo.png" alt="EduBoost Logo" width="100">
</p>

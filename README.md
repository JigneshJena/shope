# 🧵 Tailoring Shop Management System

A complete Android Native application for managing tailoring shop operations with three role-based dashboards (Owner, Employee, Customer).

## 📱 Features

### **Three Role-Based Dashboards:**

#### 👔 Owner Dashboard
- Employee Management (Add, Edit, Activate/Deactivate)
- School Management (Manage school uniform orders)
- Business Analytics & Reports
- Profit & Loss Analysis
- Shop Settings

#### 👨‍💼 Employee Dashboard
- Customer Management
- Order Creation (Alteration, New Stitching, Readymade, School Uniform)
- Inventory Management
- Billing & Invoicing
- Measurement Recording
- Payment Processing

#### 🛍️ Customer Dashboard
- Browse Products
- Shopping Cart
- Place Orders
- Track Orders
- View Measurements
- Profile Management

## 🏗️ Technical Stack

- **Platform:** Android Native
- **Language:** Kotlin
- **UI:** XML Layouts (Material Design 3)
- **Architecture:** MVVM
- **Backend:** Firebase
  - Firebase Authentication (Email/Password + Google Sign-In)
  - Cloud Firestore
  - Firebase Realtime Database
- **Image Storage:** Base64 strings (direct in Firestore)
- **Libraries:**
  - AndroidX Libraries
  - Material Design 3
  - Glide (Image Loading)
  - Coroutines (Async Operations)
  - LiveData & ViewModel

## 🚀 Getting Started

### Prerequisites

1. Android Studio (Latest version)
2. Firebase Project
3. Min SDK: 24 (Android 7.0)
4. Target SDK: 34 (Android 14)

### Firebase Setup

1. **Create Firebase Project:**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project
   - Add an Android app with package name: `com.example.shope`
   - Download `google-services.json` and place in `app/` directory (already done)

2. **Enable Authentication:**
   - Go to Authentication > Sign-in method
   - Enable "Email/Password"
   - Enable "Google" and configure
   - Copy Web client ID

3. **Create Firestore Database:**
   - Go to Firestore Database
   - Create database in production mode (or test mode)
   - Deploy security rules (see `DEVELOPMENT_PROGRESS.md`)

4. **Enable Firestore Persistence:**
   - Already configured in code

5. **Configure Google Sign-In:**
   - Open `app/src/main/res/values/google_signin.xml`
   - Replace `YOUR_WEB_CLIENT_ID_HERE` with your actual Web Client ID from Firebase

### Installation

1. Clone the repository
2. Open project in Android Studio
3. Sync Gradle files
4. Configure Firebase (see above)
5. Build and run

```bash
./gradlew build
```

## 📂 Project Structure

```
com.example.shope/
├── data/
│   ├── models/          # Data models (User, Order, Customer, etc.)
│   └── repository/      # Data layer (Firestore operations)
├── ui/
│   ├── auth/            # Login, Signup, Forgot Password
│   ├── owner/           # Owner Dashboard
│   ├── employee/        # Employee Dashboard
│   └── customer/        # Customer Dashboard
├── viewmodel/           # ViewModels for MVVM
└── utils/               # Utility classes
```

## 🔑 User Roles & Access

| Role | Email Pattern | Access Level |
|------|--------------|--------------|
| Owner | Contains "@owner" | Full access, employee & school management |
| Employee | role="employee" | Customer, order, inventory, billing management |
| Customer | role="customer" | Browse, cart, orders, profile |

### Default Accounts (After Implementation)

Create your first owner account by signing up with an email containing "@owner".

## 🐛 Bug Fixes Implemented

✅ **Customer Not Saving** - Fixed with proper Firestore write and validation  
✅ **Order Not Creating** - Fixed with inventory updates and stats tracking  
✅ **Fake Statistics** - Removed, using real Firestore queries only  
✅ **Navigation Issues** - Proper role-based routing implemented  

## 📊 Database Collections

- `users` - All user accounts
- `employees` - Employee details
- `customers` - Customer information
- `schools` - School uniform orders
- `inventory` - Stock items
- `orders` - All orders
- `payments` - Billing & invoices
- `measurements` - Customer measurements
- `carts` - Shopping carts

## 🔒 Security

- Firebase Authentication for user identity
- Firestore Security Rules for data access
- Role-based authorization
- Input validation
- Secure password reset

## 📱 Screens (Current Status)

### ✅ Completed:
- [x] Login Screen
- [x] Signup Screen
- [x] Forgot Password Screen
- [x] Role-based Dashboard Routing

### 🚧 In Progress:
- [ ] Owner Dashboard (5 tabs)
- [ ] Employee Dashboard (6 tabs)
- [ ] Customer Dashboard (4 tabs)

## 🛠️ Development Status

See `DEVELOPMENT_PROGRESS.md` for detailed progress tracking.

**Current Phase:** Phase 2 Complete (Authentication)  
**Next Phase:** Phase 3 (Owner Dashboard Implementation)

## 📝 TODO

See `.agent/implementation_plan.md` for complete implementation roadmap.

## 🤝 Contributing

This is a complete tailoring shop management system. Future enhancements:
- [ ] Push notifications
- [ ] SMS integration
- [ ] WhatsApp integration
- [ ] Print receipt functionality
- [ ] Barcode scanning
- [ ] Analytics dashboard
- [ ] Multi-shop support

## 📄 License

This project is for educational and commercial use.

## 📞 Support

For issues or questions, refer to the Firebase documentation and Android developer guides.

---

**Built with ❤️ using Kotlin, Firebase, and Material Design 3**

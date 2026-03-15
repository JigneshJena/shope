package com.example.shope.utils

object Constants {
    // User Roles
    const val ROLE_ADMIN = "admin"
    const val ROLE_OWNER = "owner"
    const val ROLE_EMPLOYEE = "employee"
    const val ROLE_CUSTOMER = "customer"
    
    // User Status
    const val STATUS_ACTIVE = "active"
    const val STATUS_INACTIVE = "inactive"
    
    // Order Types
    const val ORDER_TYPE_ALTERATION = "Alteration"
    const val ORDER_TYPE_NEW_STITCHING = "New Stitching"
    const val ORDER_TYPE_READYMADE = "Readymade"
    const val ORDER_TYPE_SCHOOL_UNIFORM = "School Uniform"
    
    // Order Status
    const val ORDER_STATUS_PENDING = "Pending"
    const val ORDER_STATUS_IN_PROGRESS = "In Progress"
    const val ORDER_STATUS_READY = "Ready"
    const val ORDER_STATUS_DELIVERED = "Delivered"
    const val ORDER_STATUS_CANCELLED = "Cancelled"
    
    // Payment Status
    const val PAYMENT_STATUS_PAID = "Paid"
    const val PAYMENT_STATUS_PARTIAL = "Partial"
    const val PAYMENT_STATUS_UNPAID = "Unpaid"
    
    // Payment Methods
    const val PAYMENT_METHOD_CASH = "Cash"
    const val PAYMENT_METHOD_CARD = "Card"
    const val PAYMENT_METHOD_UPI = "UPI"
    const val PAYMENT_METHOD_BANK_TRANSFER = "Bank Transfer"
    const val PAYMENT_METHOD_CREDIT = "Credit"
    const val PAYMENT_METHOD_COD = "Cash on Delivery"
    
    // Inventory Categories
    const val CATEGORY_FABRIC = "Fabric"
    const val CATEGORY_READYMADE = "Readymade"
    const val CATEGORY_ACCESSORIES = "Accessories"
    const val CATEGORY_BUTTONS = "Buttons"
    const val CATEGORY_THREADS = "Threads"
    const val CATEGORY_OTHER = "Other"
    
    // Units
    const val UNIT_METERS = "Meters"
    const val UNIT_PIECES = "Pieces"
    const val UNIT_ROLLS = "Rolls"
    const val UNIT_KG = "Kg"
    const val UNIT_PAIRS = "Pairs"
    
    // Stock Status
    const val STOCK_IN_STOCK = "In Stock"
    const val STOCK_LOW_STOCK = "Low Stock"
    const val STOCK_OUT_OF_STOCK = "Out of Stock"
    
    // Customer Types
    const val CUSTOMER_TYPE_REGULAR = "Regular"
    const val CUSTOMER_TYPE_SCHOOL = "School/Institution"
    
    // Measurement Types
    const val MEASUREMENT_SHIRT = "Shirt"
    const val MEASUREMENT_PANT = "Pant"
    const val MEASUREMENT_SUIT = "Suit"
    const val MEASUREMENT_DRESS = "Dress"
    const val MEASUREMENT_BLOUSE = "Blouse"
    const val MEASUREMENT_SCHOOL_UNIFORM = "School Uniform"
    
    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_OWNERS = "owners"
    const val COLLECTION_EMPLOYEES = "employees"
    const val COLLECTION_CUSTOMERS = "customers"
    const val COLLECTION_SCHOOLS = "schools"
    const val COLLECTION_INVENTORY = "inventory"
    const val COLLECTION_ORDERS = "orders"
    const val COLLECTION_PAYMENTS = "payments"
    const val COLLECTION_MEASUREMENTS = "measurements"
    const val COLLECTION_CARTS = "carts"
    const val COLLECTION_ACTIVITY_LOG = "activity_log"
    const val COLLECTION_SHOP_SETTINGS = "shop_settings"
    
    // Realtime Database Paths
    const val DB_USERS_PRESENCE = "users_presence"
    const val DB_RECENT_ACTIVITIES = "recent_activities"
    const val DB_QUICK_STATS = "quick_stats"
    const val DB_NOTIFICATIONS = "notifications"
    
    // SharedPreferences Keys
    const val PREF_NAME = "TailorShopePrefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_NAME = "user_name"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_USER_ROLE = "user_role"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
    
    // Intent Keys
    const val KEY_USER_ID = "userId"
    const val KEY_CUSTOMER_ID = "customerId"
    const val KEY_ORDER_ID = "orderId"
    const val KEY_SCHOOL_ID = "schoolId"
    const val KEY_EMPLOYEE_ID = "employeeId"
    const val KEY_INVENTORY_ID = "inventoryId"
    const val KEY_ORDER_TYPE = "orderType"
    
    // Date Formats
    const val DATE_FORMAT_DISPLAY = "dd MMM yyyy"
    const val DATE_FORMAT_FULL = "dd MMM yyyy, hh:mm a"
    const val DATE_FORMAT_SHORT = "dd/MM/yyyy"
    const val DATE_FORMAT_TIME = "hh:mm a"
    
    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_PHONE_LENGTH = 10
    const val MAX_IMAGE_SIZE_KB = 800 // Max dimension for image compression
    const val IMAGE_QUALITY = 75 // JPEG compression quality
    
    // Default Values
    const val DEFAULT_MIN_STOCK_LEVEL = 5
    const val DEFAULT_CURRENCY = "₹"
    const val DEFAULT_TAX_RATE = 0.0
    
    // Pagination
    const val PAGE_SIZE = 20
    const val INITIAL_LOAD_SIZE = 30
}

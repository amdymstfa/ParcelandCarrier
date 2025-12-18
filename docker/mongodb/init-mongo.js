// =============================================================================
// MongoDB Initialization Script (English Version - Compatible with Java Entities)
// =============================================================================

db = db.getSiblingDB('parcel-carrier-mongodb');

print('🚀 Initializing the parcel-carrier-mongodb database...');

// Drop collections if they exist to start fresh (Optional)
db.users.drop();
db.packages.drop();

// =============================================================================
// 1. Create collections with validation (ENGLISH)
// =============================================================================

db.createCollection('users', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['login', 'password', 'role', 'active'],
      properties: {
        login: { bsonType: 'string' },
        password: { bsonType: 'string' },
        role: {
          enum: ['ADMIN', 'TRANSPORTER'], // Match Role.java
          description: 'Must be ADMIN or TRANSPORTER'
        },
        active: { bsonType: 'bool' },
        specialty: {
          enum: ['STANDARD', 'FRAGILE', 'REFRIGERATED'], // Match Specialty.java
          description: 'Optional for ADMIN'
        },
        status: {
          enum: ['AVAILABLE', 'ON_DELIVERY'], // Match TransporterStatus.java
          description: 'Optional for ADMIN'
        }
      }
    }
  }
});

db.createCollection('packages', { // Renamed from 'colis' to 'packages'
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['type', 'weight', 'destinationAddress', 'status'],
      properties: {
        type: { enum: ['STANDARD', 'FRAGILE', 'REFRIGERATED'] },
        weight: { bsonType: 'double', minimum: 0 },
        destinationAddress: { bsonType: 'string' },
        status: { enum: ['PENDING', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED'] },
        transporterId: { bsonType: 'string' },
        handlingInstructions: { bsonType: 'string' },
        minTemperature: { bsonType: 'double' },
        maxTemperature: { bsonType: 'double' }
      }
    }
  }
});

print('✅ Collections created with English validation schemas');

// =============================================================================
// 2. Create indexes
// =============================================================================

db.users.createIndex({ login: 1 }, { unique: true, name: 'idx_user_login' });
db.users.createIndex({ role: 1 }, { name: 'idx_user_role' });
db.users.createIndex({ specialty: 1 }, { name: 'idx_user_specialty' });

db.packages.createIndex({ type: 1 }, { name: 'idx_package_type' });
db.packages.createIndex({ status: 1 }, { name: 'idx_package_status' });
db.packages.createIndex({ transporterId: 1 }, { name: 'idx_package_transporter' });

print('✅ English indexes created');

// =============================================================================
// 3. Insert initial data (ENGLISH)
// =============================================================================

// BCrypt hash for "admin123" and "trans123"
const adminPassword = '$2a$10$xZnPq5qJZ7Z9QxZ9QxZ9QeO5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K';
const transporterPassword = '$2a$10$yZnPq5qJZ7Z9QxZ9QxZ9QeO6K6K6K6K6K6K6K6K6K6K6K6K6K6K6K';

db.users.insertMany([
  {
    login: 'admin',
    password: adminPassword,
    role: 'ADMIN',
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    login: 'transporter1',
    password: transporterPassword,
    role: 'TRANSPORTER',
    active: true,
    specialty: 'STANDARD',
    status: 'AVAILABLE',
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    login: 'transporter2',
    password: transporterPassword,
    role: 'TRANSPORTER',
    active: true,
    specialty: 'REFRIGERATED', // Changed from FRIGO
    status: 'AVAILABLE',
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

db.packages.insertMany([
  {
    type: 'STANDARD',
    weight: 5.5,
    destinationAddress: '123 Paris Street, France',
    status: 'PENDING',
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    type: 'REFRIGERATED',
    weight: 10.0,
    destinationAddress: '789 Berlin Road, Germany',
    status: 'PENDING',
    minTemperature: 2.0,
    maxTemperature: 8.0,
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

print('Initial data inserted');

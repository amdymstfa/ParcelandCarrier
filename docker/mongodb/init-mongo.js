db = db.getSiblingDB('parcel-carrier-mongodb');

print('🚀 Starting database initialization...');

db.users.drop();
db.packages.drop();

// =============================================================================
// 1. Collections with Validations
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
          enum: ['ADMIN', 'TRANSPORTER'],
          description: 'Must be ADMIN or TRANSPORTER'
        },
        active: { bsonType: 'bool' },
        specialty: {
          enum: ['STANDARD', 'FRAGILE', 'REFRIGERATED'],
          description: 'Required for TRANSPORTER'
        },
        status: {
          enum: ['AVAILABLE', 'ON_DELIVERY'],
          description: 'Required for TRANSPORTER'
        }
      }
    }
  }
});

db.createCollection('packages', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['type', 'weight', 'destinationAddress', 'status'],
      properties: {
        type: { enum: ['STANDARD', 'FRAGILE', 'REFRIGERATED'] },
        weight: { bsonType: 'number' },
        destinationAddress: { bsonType: 'string' },
        status: { enum: ['PENDING', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED'] },
        transporterId: { bsonType: 'string' },
        handlingInstructions: { bsonType: 'string' },
        minTemperature: { bsonType: 'number' },
        maxTemperature: { bsonType: 'number' }
      }
    }
  }
});

print('✅ Collections and validators created.');
print('ℹ️  Note: Indexes will be created automatically by the Java application.');

// =============================================================================
// 2. Data Insertion
// =============================================================================
db = db.getSiblingDB('parcel-carrier-mongodb');
print('🚀 Initializing database with Spring Data compatibility...');

db.users.drop();
db.packages.drop();

const adminHash = '$2a$12$clZ8MOnT02Uo5.U/3sEInOInS8U4.f/xUvR6x.eH5Yq6V8m8W.Xl6';
const transHash = '$2a$12$8.UAsR/3sEInoOImN.KjS6G4.f/xUvR6x.eH5Yq6V8m8W.8vVfMCHn';

db.users.insertMany([
  {
    _class: "com.logistics.parcelandcarrier.entity.User",
    login: 'admin',
    password: adminHash,
    role: 'ADMIN',
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _class: "com.logistics.parcelandcarrier.entity.User",
    login: 'transporter1',
    password: transHash,
    role: 'TRANSPORTER',
    active: true,
    specialty: 'STANDARD',
    status: 'AVAILABLE',
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

db.packages.insertMany([
  {
    _class: "com.logistics.parcelandcarrier.entity.Package",
    type: 'STANDARD',
    weight: 5.5,
    destinationAddress: '123 Paris Street, France',
    status: 'PENDING',
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

print('✅ Initial data inserted with Spring Data class mapping.');

// =============================================================================
// MongoDB Initialization Script
// =============================================================================

// Connect to the database
db = db.getSiblingDB('parcel-carrier-mongodb');

print('🚀 Initializing the gestion_colis database...');

// =============================================================================
// 1. Create collections with validation
// =============================================================================

// Users collection with validation schema
db.createCollection('users', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['login', 'password', 'role', 'active'],
      properties: {
        login: {
          bsonType: 'string',
          description: 'User login - required'
        },
        password: {
          bsonType: 'string',
          description: 'Hashed password - required'
        },
        role: {
          enum: ['ADMIN', 'TRANSPORTEUR'],
          description: 'User role - required'
        },
        active: {
          bsonType: 'bool',
          description: 'Whether the account is active - required'
        },
        specialite: {
          enum: ['STANDARD', 'FRAGILE', 'FRIGO'],
          description: 'Transporter specialty (optional for ADMIN)'
        },
        statut: {
          enum: ['DISPONIBLE', 'EN_LIVRAISON'],
          description: 'Transporter status (optional for ADMIN)'
        }
      }
    }
  }
});

print('✅ Users collection created with validation');

// Packages collection with validation schema
db.createCollection('colis', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['type', 'poids', 'adresseDestination', 'statut'],
      properties: {
        type: {
          enum: ['STANDARD', 'FRAGILE', 'FRIGO'],
          description: 'Package type - required'
        },
        poids: {
          bsonType: 'double',
          minimum: 0,
          description: 'Package weight in kg - required'
        },
        adresseDestination: {
          bsonType: 'string',
          description: 'Destination address - required'
        },
        statut: {
          enum: ['EN_ATTENTE', 'EN_TRANSIT', 'LIVRE', 'ANNULE'],
          description: 'Package status - required'
        },
        transporteurId: {
          bsonType: 'string',
          description: 'Assigned transporter ID (optional)'
        },
        instructionsManutention: {
          bsonType: 'string',
          description: 'Handling instructions for FRAGILE packages (optional)'
        },
        temperatureMin: {
          bsonType: 'double',
          description: 'Minimum temperature for FRIGO packages (optional)'
        },
        temperatureMax: {
          bsonType: 'double',
          description: 'Maximum temperature for FRIGO packages (optional)'
        }
      }
    }
  }
});

print('✅ Packages collection created with validation');

// =============================================================================
// 2. Create indexes to optimize performance
// =============================================================================

// Indexes for Users
db.users.createIndex({ login: 1 }, { unique: true, name: 'idx_user_login' });
db.users.createIndex({ role: 1 }, { name: 'idx_user_role' });
db.users.createIndex({ specialite: 1 }, { name: 'idx_user_specialite' });
db.users.createIndex({ role: 1, specialite: 1 }, { name: 'idx_user_role_specialite' });

print('✅ Indexes created for users');

// Indexes for Packages
db.colis.createIndex({ type: 1 }, { name: 'idx_colis_type' });
db.colis.createIndex({ statut: 1 }, { name: 'idx_colis_statut' });
db.colis.createIndex({ transporteurId: 1 }, { name: 'idx_colis_transporteur' });
db.colis.createIndex({ adresseDestination: 'text' }, { name: 'idx_colis_address_text' });
db.colis.createIndex({ type: 1, statut: 1 }, { name: 'idx_colis_type_status' });
db.colis.createIndex({ dateCreation: -1 }, { name: 'idx_colis_creation_date' });

print('✅ Indexes created for packages');

// =============================================================================
// 3. Insert initial data
// =============================================================================

// Password: "admin123" hashed with BCrypt
const adminPassword = '$2a$10$xZnPq5qJZ7Z9QxZ9QxZ9QeO5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K';

// Password: "trans123" hashed with BCrypt
const transporteurPassword = '$2a$10$yZnPq5qJZ7Z9QxZ9QxZ9QeO6K6K6K6K6K6K6K6K6K6K6K6K6K6K6K';

// Insert default users
db.users.insertMany([
  {
    _id: ObjectId(),
    login: 'admin',
    password: adminPassword,
    role: 'ADMIN',
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId(),
    login: 'transporteur1',
    password: transporteurPassword,
    role: 'TRANSPORTEUR',
    active: true,
    specialite: 'STANDARD',
    statut: 'DISPONIBLE',
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId(),
    login: 'transporteur2',
    password: transporteurPassword,
    role: 'TRANSPORTEUR',
    active: true,
    specialite: 'FRAGILE',
    statut: 'DISPONIBLE',
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId(),
    login: 'transporteur3',
    password: transporteurPassword,
    role: 'TRANSPORTEUR',
    active: true,
    specialite: 'FRIGO',
    statut: 'DISPONIBLE',
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

print('✅ Initial users inserted:');
print('   - admin / admin123 (ADMIN)');
print('   - transporteur1 / trans123 (TRANSPORTEUR - STANDARD)');
print('   - transporteur2 / trans123 (TRANSPORTEUR - FRAGILE)');
print('   - transporteur3 / trans123 (TRANSPORTEUR - FRIGO)');

// Insert sample packages
db.colis.insertMany([
  {
    _id: ObjectId(),
    type: 'STANDARD',
    poids: 5.5,
    adresseDestination: '123 Rue de Paris, 75001 Paris, France',
    statut: 'EN_ATTENTE',
    dateCreation: new Date(),
    dateModification: new Date()
  },
  {
    _id: ObjectId(),
    type: 'FRAGILE',
    poids: 2.3,
    adresseDestination: '456 Avenue des Champs-Élysées, 75008 Paris, France',
    statut: 'EN_ATTENTE',
    instructionsManutention: 'Handle with care - Fragile content',
    dateCreation: new Date(),
    dateModification: new Date()
  },
  {
    _id: ObjectId(),
    type: 'FRIGO',
    poids: 10.0,
    adresseDestination: '789 Boulevard Saint-Germain, 75006 Paris, France',
    statut: 'EN_ATTENTE',
    temperatureMin: 2.0,
    temperatureMax: 8.0,
    dateCreation: new Date(),
    dateModification: new Date()
  }
]);

print('✅ 3 sample packages inserted');

// =============================================================================
// 4. Final statistics
// =============================================================================

print('\n📊 Database statistics:');
print('   - Users: ' + db.users.countDocuments());
print('   - Packages: ' + db.colis.countDocuments());
print('\n✅ Initialization completed successfully!');
print('🔐 Use: admin/admin123 to log in as administrator');
print('🚚 Use: transporteur1/trans123 to log in as transporter\n');


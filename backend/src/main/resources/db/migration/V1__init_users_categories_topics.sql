CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255),
  full_name VARCHAR(150) NOT NULL,
  auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
  role VARCHAR(20) NOT NULL DEFAULT 'LEARNER',
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  experience_level VARCHAR(20),
  learning_goals VARCHAR(2000),
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX idx_users_email ON users(email);
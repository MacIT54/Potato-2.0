GRANT ALL PRIVILEGES ON DATABASE wallet to wallet;

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE wallets (
                         wallet_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         wallet_number BIGINT NOT NULL,
                         wallet_balance BIGINT NOT NULL,
                         last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
                       user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       wallet_id UUID REFERENCES wallets(wallet_id) ON DELETE CASCADE,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       birth_date DATE NOT NULL,
                       phone BIGINT NOT NULL,
                       password VARCHAR(512) NOT NULL,
                       registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CHECK (EXTRACT(YEAR FROM AGE(birth_date)) BETWEEN 18 AND 100),
                       FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);

CREATE TABLE banned_tokens (
                               token TEXT PRIMARY KEY
);

CREATE TABLE sessions (
                          id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                          user_id UUID NOT NULL,
                          token VARCHAR(255) NOT NULL UNIQUE,
                          expiration_time TIMESTAMP NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE transfers (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              amount BIGINT NOT NULL,
                              transaction_date TIMESTAMP NOT NULL,
                              type VARCHAR(255) NOT NULL,
                              receiver_phone BIGINT,
                              maintenance_number BIGINT,
                              status VARCHAR(255) NOT NULL,
                              sender_wallet_id UUID,
                              receiver_wallet_id UUID,
                              FOREIGN KEY (sender_wallet_id) REFERENCES wallets(wallet_id),
                              FOREIGN KEY (receiver_wallet_id) REFERENCES wallets(wallet_id)
);

CREATE TABLE invoices (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          amount BIGINT NOT NULL,
                          status VARCHAR(255) NOT NULL,
                          transaction_date DATE NOT NULL,
                          comment TEXT,
                          sender_id UUID,
                          receiver_id UUID,
                          FOREIGN KEY (sender_id) REFERENCES users(user_id),
                          FOREIGN KEY (receiver_id) REFERENCES users(user_id)
);
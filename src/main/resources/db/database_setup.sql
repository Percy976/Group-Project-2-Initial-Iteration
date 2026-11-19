-- Local Services Marketplace
-- Group Project 2 database setup

CREATE DATABASE IF NOT EXISTS local_services_marketplace
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE local_services_marketplace;

-- Create users first because other tables reference it.

CREATE TABLE IF NOT EXISTS users (
    user_id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) DEFAULT NULL,
    bio TEXT,
    is_admin TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id),
    UNIQUE KEY username (username),
    UNIQUE KEY email (email)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS listings (
    listing_id INT NOT NULL AUTO_INCREMENT,
    provider_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) DEFAULT NULL,
    price_type ENUM('FIXED', 'HOURLY', 'NEGOTIABLE')
        NOT NULL DEFAULT 'NEGOTIABLE',
    status ENUM('ACTIVE', 'INACTIVE')
        NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (listing_id),
    KEY fk_listings_provider (provider_id),

    CONSTRAINT fk_listings_provider
        FOREIGN KEY (provider_id)
        REFERENCES users (user_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS service_requests (
    request_id INT NOT NULL AUTO_INCREMENT,
    listing_id INT NOT NULL,
    customer_id INT NOT NULL,
    message TEXT NOT NULL,
    status ENUM(
        'PENDING',
        'ACCEPTED',
        'DECLINED',
        'CANCELLED',
        'COMPLETED'
    ) NOT NULL DEFAULT 'PENDING',
    provider_response TEXT,
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (request_id),
    KEY fk_requests_listing (listing_id),
    KEY fk_requests_customer (customer_id),

    CONSTRAINT fk_requests_customer
        FOREIGN KEY (customer_id)
        REFERENCES users (user_id),

    CONSTRAINT fk_requests_listing
        FOREIGN KEY (listing_id)
        REFERENCES listings (listing_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reviews (
    review_id INT NOT NULL AUTO_INCREMENT,
    request_id INT NOT NULL,
    listing_id INT NOT NULL,
    customer_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    rating TINYINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (review_id),
    UNIQUE KEY request_id (request_id),
    KEY fk_reviews_listing (listing_id),
    KEY fk_reviews_customer (customer_id),

    CONSTRAINT fk_reviews_request
        FOREIGN KEY (request_id)
        REFERENCES service_requests (request_id),

    CONSTRAINT fk_reviews_listing
        FOREIGN KEY (listing_id)
        REFERENCES listings (listing_id),

    CONSTRAINT fk_reviews_customer
        FOREIGN KEY (customer_id)
        REFERENCES users (user_id),

    CONSTRAINT chk_review_rating
        CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reports (
    report_id INT NOT NULL AUTO_INCREMENT,
    reporter_id INT NOT NULL,
    reported_user_id INT DEFAULT NULL,
    listing_id INT DEFAULT NULL,
    reason VARCHAR(255) NOT NULL,
    details TEXT,
    status ENUM('OPEN', 'REVIEWED', 'RESOLVED', 'DISMISSED')
        NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (report_id),
    KEY fk_reports_reporter (reporter_id),
    KEY fk_reports_reported_user (reported_user_id),
    KEY fk_reports_listing (listing_id),

    CONSTRAINT fk_reports_listing
        FOREIGN KEY (listing_id)
        REFERENCES listings (listing_id),

    CONSTRAINT fk_reports_reported_user
        FOREIGN KEY (reported_user_id)
        REFERENCES users (user_id),

    CONSTRAINT fk_reports_reporter
        FOREIGN KEY (reporter_id)
        REFERENCES users (user_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- Verify the selected database and its tables.

SELECT DATABASE() AS selected_database;

SHOW TABLES;
-- Demo reviews for Local Services Marketplace (MySQL 8+).
-- Run AFTER database_setup.sql and marketplace_seed_listings.sql.
-- Uses five demo customer accounts and completed demo service requests.
-- Each demo listing gets a stable, pseudo-random number of reviews (0 through 5).
-- Rerunnable: existing demo requests and reviews are not duplicated.
-- DEMO/DEVELOPMENT ONLY. Do not use fabricated reviews in production.

USE local_services_marketplace;

-- These demo accounts use the same sample PBKDF2 password hash as the
-- provider demo accounts in marketplace_seed_listings.sql.
INSERT INTO users (username, email, password_hash, first_name, last_name, bio)
SELECT s.username, s.email,
       'PBKDF2WithHmacSHA256$600000$6YiGoRxz4ss5xCWeUPYRJg==$zaFj+jcf6FrjPkl3HGFqdTQt3XS6VhmsiMSzwLpZtDY=',
       s.first_name, s.last_name, 'Demo customer for seeded reviews.'
FROM (
    SELECT 'demo_review_customer_01' AS username, 'demo_review_customer_01@marketplace.invalid' AS email, 'Jamie' AS first_name, 'Taylor' AS last_name
    UNION ALL SELECT 'demo_review_customer_02', 'demo_review_customer_02@marketplace.invalid', 'Morgan', 'Lee'
    UNION ALL SELECT 'demo_review_customer_03', 'demo_review_customer_03@marketplace.invalid', 'Alex', 'Rivera'
    UNION ALL SELECT 'demo_review_customer_04', 'demo_review_customer_04@marketplace.invalid', 'Casey', 'Wong'
    UNION ALL SELECT 'demo_review_customer_05', 'demo_review_customer_05@marketplace.invalid', 'Jordan', 'Patel'
) AS s
WHERE NOT EXISTS (
    SELECT 1 FROM users u WHERE u.username = s.username
);

-- Materialize exactly the (listing, reviewer) pairs to seed.
CREATE TEMPORARY TABLE temp_demo_review_targets (
    listing_id INT NOT NULL,
    customer_id INT NOT NULL,
    review_number INT NOT NULL,
    PRIMARY KEY (listing_id, review_number)
);

INSERT INTO temp_demo_review_targets (listing_id, customer_id, review_number)
SELECT l.listing_id, customer.user_id, slots.review_number
FROM listings l
JOIN users provider ON provider.user_id = l.provider_id
JOIN (
    SELECT 1 AS review_number
    UNION ALL SELECT 2
    UNION ALL SELECT 3
    UNION ALL SELECT 4
    UNION ALL SELECT 5
) AS slots
JOIN users customer ON customer.username = CONCAT('demo_review_customer_0', slots.review_number)
WHERE provider.username LIKE 'demo_provider_%'
  AND slots.review_number <= MOD(CRC32(CONCAT('review-count:', l.listing_id)), 6);

-- Reviews can only reference actual service requests, so create completed
-- demo requests first. A distinctive message identifies these seed records.
INSERT INTO service_requests (listing_id, customer_id, message, status, provider_response)
SELECT t.listing_id, t.customer_id,
       CONCAT('Demo completed request for review #', t.review_number),
       'COMPLETED',
       'Demo service completed.'
FROM temp_demo_review_targets t
WHERE NOT EXISTS (
    SELECT 1
    FROM service_requests sr
    WHERE sr.listing_id = t.listing_id
      AND sr.customer_id = t.customer_id
      AND sr.message = CONCAT('Demo completed request for review #', t.review_number)
);

-- Add a short review for each generated completed request.
-- One review per request is enforced by the unique key on reviews.request_id.
INSERT INTO reviews (request_id, listing_id, customer_id, title, description, rating)
SELECT sr.request_id, t.listing_id, t.customer_id,
       CASE MOD(CRC32(CONCAT('title:', t.listing_id, ':', t.review_number)), 8)
           WHEN 0 THEN 'Great service'
           WHEN 1 THEN 'Very helpful'
           WHEN 2 THEN 'Good experience'
           WHEN 3 THEN 'Would recommend'
           WHEN 4 THEN 'Job well done'
           WHEN 5 THEN 'Reliable provider'
           WHEN 6 THEN 'Happy with the result'
           ELSE 'Solid experience'
       END,
       CASE MOD(CRC32(CONCAT('body:', t.listing_id, ':', t.review_number)), 10)
           WHEN 0 THEN 'Arrived on time and did a great job.'
           WHEN 1 THEN 'Friendly and easy to work with.'
           WHEN 2 THEN 'Everything was completed as discussed.'
           WHEN 3 THEN 'Good communication throughout.'
           WHEN 4 THEN 'The service met my expectations.'
           WHEN 5 THEN 'Professional and efficient.'
           WHEN 6 THEN 'Quick turnaround and tidy work.'
           WHEN 7 THEN 'Helpful, courteous, and reliable.'
           WHEN 8 THEN 'Overall a positive experience.'
           ELSE 'Would be happy to book again.'
       END,
       3 + MOD(CRC32(CONCAT('rating:', t.listing_id, ':', t.review_number)), 3)
FROM temp_demo_review_targets t
JOIN service_requests sr
  ON sr.listing_id = t.listing_id
 AND sr.customer_id = t.customer_id
 AND sr.message = CONCAT('Demo completed request for review #', t.review_number)
WHERE sr.status = 'COMPLETED'
  AND NOT EXISTS (
      SELECT 1 FROM reviews r WHERE r.request_id = sr.request_id
  );

DROP TEMPORARY TABLE temp_demo_review_targets;

-- Verification: 0-5 seeded reviews per demo listing (including zero).
SELECT l.listing_id, l.title, COUNT(r.review_id) AS demo_review_count
FROM listings l
JOIN users p ON p.user_id = l.provider_id
LEFT JOIN service_requests sr
  ON sr.listing_id = l.listing_id
 AND sr.message LIKE 'Demo completed request for review #%'
LEFT JOIN reviews r ON r.request_id = sr.request_id
WHERE p.username LIKE 'demo_provider_%'
GROUP BY l.listing_id, l.title
ORDER BY l.listing_id;

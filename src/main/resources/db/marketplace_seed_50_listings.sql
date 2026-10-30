-- Development/demo data for Local Services Marketplace (MySQL 8+)
-- Run after database_setup.sql has created the tables.
-- Creates a dedicated demo provider and 50 service listings automatically.
-- Safe to rerun: the demo provider and its existing listing titles are not duplicated.
-- Demo data only. Do not enable this seed in production.
-- Demo login: username marketplace_demo_provider / password DemoProvider2026!

USE local_services_marketplace;

-- A provider is required because listings.provider_id references users.user_id.
-- Password hash uses the same PBKDF2WithHmacSHA256 format and 600000 iterations
-- as com.marketplace.util.PasswordUtil (with a randomly generated salt).
INSERT INTO users (username, email, password_hash, first_name, last_name, bio)
SELECT 'marketplace_demo_provider', 'demo-provider@marketplace.invalid',
       'PBKDF2WithHmacSHA256$600000$6YiGoRxz4ss5xCWeUPYRJg==$zaFj+jcf6FrjPkl3HGFqdTQt3XS6VhmsiMSzwLpZtDY=', 'Demo', 'Provider',
       'Sample provider account for development listings.'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'marketplace_demo_provider'
);

SET @seed_provider_id = (
    SELECT user_id FROM users WHERE username = 'marketplace_demo_provider'
);

CREATE TEMPORARY TABLE temp_seed_listings (
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10,2),
    price_type ENUM('FIXED', 'HOURLY', 'NEGOTIABLE') NOT NULL
);

INSERT INTO temp_seed_listings
    (title, description, category, price, price_type)
VALUES
    ('Lawn Mowing and Edging', 'Lawn cutting, trimming, and tidy edges for residential yards.', 'Home & Garden', 40.00, 'FIXED'),
    ('Spring Garden Cleanup', 'Remove leaves, branches, and winter debris to refresh your garden.', 'Home & Garden', 85.00, 'FIXED'),
    ('Hedge and Shrub Trimming', 'Shape hedges and shrubs and remove clippings after service.', 'Home & Garden', 55.00, 'HOURLY'),
    ('Flower Bed Planting', 'Plan and plant seasonal flowers suited to your garden.', 'Home & Garden', 70.00, 'FIXED'),
    ('Weeding and Mulching', 'Weed garden beds and apply fresh mulch for a neat finish.', 'Home & Garden', 45.00, 'HOURLY'),
    ('Gutter Cleaning', 'Clear reachable gutters and downspouts of leaves and debris.', 'Home Maintenance', 110.00, 'FIXED'),
    ('Furniture Assembly', 'Assemble flat-pack furniture, desks, and shelving.', 'Home Maintenance', 50.00, 'HOURLY'),
    ('Drywall Patch Repair', 'Repair small holes and dents in interior drywall.', 'Home Maintenance', 95.00, 'FIXED'),
    ('Interior Painting', 'Paint walls and trim for a single room or larger project.', 'Home Maintenance', 250.00, 'NEGOTIABLE'),
    ('Door and Cabinet Adjustments', 'Adjust hinges, handles, and sticking cabinet doors.', 'Home Maintenance', 65.00, 'HOURLY'),
    ('Standard House Cleaning', 'Routine cleaning of kitchens, bathrooms, and living areas.', 'Cleaning', 38.00, 'HOURLY'),
    ('Deep Kitchen Cleaning', 'Detailed cleaning of counters, appliances, cabinets, and floors.', 'Cleaning', 135.00, 'FIXED'),
    ('Move-Out Cleaning', 'Thorough cleaning to prepare a home for new occupants.', 'Cleaning', 220.00, 'FIXED'),
    ('Office Cleaning', 'After-hours cleaning of small offices and workspaces.', 'Cleaning', 45.00, 'HOURLY'),
    ('Window Washing', 'Interior and exterior cleaning of accessible windows.', 'Cleaning', 95.00, 'FIXED'),
    ('Math Tutoring Grades 7-12', 'One-on-one tutoring for algebra, geometry, and functions.', 'Tutoring', 40.00, 'HOURLY'),
    ('Introductory Physics Tutoring', 'Help with high school physics concepts and problem solving.', 'Tutoring', 45.00, 'HOURLY'),
    ('English Essay Coaching', 'Support with essay outlines, structure, and editing skills.', 'Tutoring', 35.00, 'HOURLY'),
    ('Beginner French Lessons', 'Conversational French and grammar lessons for beginners.', 'Tutoring', 35.00, 'HOURLY'),
    ('Programming Tutoring', 'Introductory Java, Python, and coding fundamentals.', 'Tutoring', 50.00, 'HOURLY'),
    ('Guitar Lessons', 'Beginner acoustic and electric guitar instruction.', 'Lessons', 42.00, 'HOURLY'),
    ('Piano Lessons', 'Personalized piano lessons for children and adults.', 'Lessons', 48.00, 'HOURLY'),
    ('Portrait Photography', 'Outdoor and studio-style personal portrait sessions.', 'Photography', 150.00, 'FIXED'),
    ('Event Photography', 'Photography for birthdays, celebrations, and local events.', 'Photography', 100.00, 'HOURLY'),
    ('Product Photography', 'Clear product photos for small businesses and online shops.', 'Photography', 120.00, 'FIXED'),
    ('Dog Walking', 'Neighborhood walks tailored to your dog and schedule.', 'Pet Care', 25.00, 'FIXED'),
    ('Cat Sitting Visits', 'Feeding, litter care, and check-ins while you are away.', 'Pet Care', 28.00, 'FIXED'),
    ('Pet Grooming', 'Bathing, brushing, and basic coat care for dogs.', 'Pet Care', 65.00, 'FIXED'),
    ('Aquarium Maintenance', 'Tank cleaning and basic freshwater aquarium upkeep.', 'Pet Care', 70.00, 'FIXED'),
    ('Pet Photography', 'Relaxed photo session featuring your pet.', 'Pet Care', 95.00, 'FIXED'),
    ('Computer Setup and Updates', 'Set up computers, install updates, and configure user accounts.', 'Technology', 60.00, 'HOURLY'),
    ('Wi-Fi Troubleshooting', 'Diagnose home wireless coverage and connectivity problems.', 'Technology', 70.00, 'HOURLY'),
    ('Printer Setup', 'Connect printers to home computers and wireless networks.', 'Technology', 55.00, 'FIXED'),
    ('Phone and Tablet Lessons', 'Learn device settings, apps, and everyday functions.', 'Technology', 35.00, 'HOURLY'),
    ('Data Backup Setup', 'Configure local or cloud backups for personal files.', 'Technology', 85.00, 'FIXED'),
    ('Resume Formatting', 'Improve resume formatting, readability, and organization.', 'Professional Services', 60.00, 'FIXED'),
    ('Basic Bookkeeping', 'Organize invoices, receipts, and routine records.', 'Professional Services', 45.00, 'HOURLY'),
    ('Social Media Graphics', 'Create simple branded graphics for online posts.', 'Professional Services', 75.00, 'FIXED'),
    ('Website Content Editing', 'Revise headings, descriptions, and page text for clarity.', 'Professional Services', 50.00, 'HOURLY'),
    ('Small Business Logo Design', 'Create an initial logo concept and basic revisions.', 'Professional Services', 180.00, 'FIXED'),
    ('Moving Help', 'Help load, unload, and carry furniture and boxes.', 'Moving & Delivery', 45.00, 'HOURLY'),
    ('Furniture Delivery', 'Local pickup and delivery of small furniture items.', 'Moving & Delivery', 80.00, 'FIXED'),
    ('Packing Assistance', 'Pack household items and label boxes for a move.', 'Moving & Delivery', 35.00, 'HOURLY'),
    ('Garage Organization', 'Sort and organize tools, storage bins, and garage items.', 'Organization', 50.00, 'HOURLY'),
    ('Closet Organization', 'Declutter and reorganize clothing and storage areas.', 'Organization', 45.00, 'HOURLY'),
    ('Meal Prep Assistance', 'Help prepare ingredients and meals for the upcoming week.', 'Food Services', 40.00, 'HOURLY'),
    ('Birthday Cake Decorating', 'Custom cake decorating for small celebrations.', 'Food Services', 90.00, 'FIXED'),
    ('Personal Fitness Coaching', 'Individual sessions focused on basic fitness technique.', 'Fitness', 55.00, 'HOURLY'),
    ('Beginner Yoga Instruction', 'Private yoga session tailored to beginner ability.', 'Fitness', 50.00, 'HOURLY'),
    ('Bicycle Tune-Up', 'Basic adjustments, chain lubrication, and safety checks.', 'Repairs', 75.00, 'FIXED');

INSERT INTO listings (provider_id, title, description, category, price, price_type, status)
SELECT @seed_provider_id, s.title, s.description, s.category,
       s.price, s.price_type, 'ACTIVE'
FROM temp_seed_listings s
WHERE NOT EXISTS (
    SELECT 1 FROM listings l
    WHERE l.provider_id = @seed_provider_id
      AND l.title = s.title
);

DROP TEMPORARY TABLE temp_seed_listings;

-- Check results:
SELECT COUNT(*) AS total_listings FROM listings;
SELECT listing_id, title, category, price, price_type
FROM listings ORDER BY listing_id DESC LIMIT 10;

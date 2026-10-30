-- Development/demo data for Local Services Marketplace (MySQL 8+).
-- Execute after database_setup.sql on the SAME JDBC connection.
-- Creates 100 unique demo providers (50 individuals and 50 businesses) and 100 listings.
-- Rerunnable: usernames and listing assignments are stable. Existing prior demo listings
-- owned by marketplace_demo_provider are reassigned to the appropriate unique providers.
-- Requires schema with users(first_name,last_name) and listings(provider_id,...).
-- Company names are split between first_name / last_name because the current schema
-- has no display_name or account_type column; the JSP CONCAT renders the full brand.
-- Demo-only credentials: each demo_provider_### uses password DemoProvider2026!
-- Never deploy this sample dataset or shared demo credentials to production.

USE local_services_marketplace;

CREATE TEMPORARY TABLE temp_seed_marketplace_100 (
    username VARCHAR(50) NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    bio TEXT,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10,2),
    price_type ENUM('FIXED', 'HOURLY', 'NEGOTIABLE') NOT NULL
);

INSERT INTO temp_seed_marketplace_100
    (username, email, first_name, last_name, bio, title, description, category, price, price_type)
VALUES
    ('demo_provider_001', 'demo_provider_001@marketplace.invalid', 'Olivia', 'Bennett', 'Demo individual provider for home & garden services.', 'Lawn Mowing and Edging', 'Lawn cutting, trimming, and tidy edges for residential yards.', 'Home & Garden', 40.00, 'FIXED'),
    ('demo_provider_002', 'demo_provider_002@marketplace.invalid', 'Evergreen', 'Lawn Care', 'Demo business provider for home & garden services.', 'Spring Garden Cleanup', 'Remove leaves, branches, and winter debris to refresh your garden.', 'Home & Garden', 85.00, 'FIXED'),
    ('demo_provider_003', 'demo_provider_003@marketplace.invalid', 'Noah', 'Patel', 'Demo individual provider for home & garden services.', 'Hedge and Shrub Trimming', 'Shape hedges and shrubs and remove clippings after service.', 'Home & Garden', 55.00, 'HOURLY'),
    ('demo_provider_004', 'demo_provider_004@marketplace.invalid', 'Northstar', 'Cleaning Co.', 'Demo business provider for home & garden services.', 'Flower Bed Planting', 'Plan and plant seasonal flowers suited to your garden.', 'Home & Garden', 70.00, 'FIXED'),
    ('demo_provider_005', 'demo_provider_005@marketplace.invalid', 'Maya', 'Chen', 'Demo individual provider for home & garden services.', 'Weeding and Mulching', 'Weed garden beds and apply fresh mulch for a neat finish.', 'Home & Garden', 45.00, 'HOURLY'),
    ('demo_provider_006', 'demo_provider_006@marketplace.invalid', 'Maple Ridge', 'Home Services', 'Demo business provider for home maintenance services.', 'Gutter Cleaning', 'Clear reachable gutters and downspouts of leaves and debris.', 'Home Maintenance', 110.00, 'FIXED'),
    ('demo_provider_007', 'demo_provider_007@marketplace.invalid', 'Ethan', 'Brooks', 'Demo individual provider for home maintenance services.', 'Furniture Assembly', 'Assemble flat-pack furniture, desks, and shelving.', 'Home Maintenance', 50.00, 'HOURLY'),
    ('demo_provider_008', 'demo_provider_008@marketplace.invalid', 'Brightline', 'Tutoring', 'Demo business provider for home maintenance services.', 'Drywall Patch Repair', 'Repair small holes and dents in interior drywall.', 'Home Maintenance', 95.00, 'FIXED'),
    ('demo_provider_009', 'demo_provider_009@marketplace.invalid', 'Sofia', 'Martinez', 'Demo individual provider for home maintenance services.', 'Interior Painting', 'Paint walls and trim for a single room or larger project.', 'Home Maintenance', 250.00, 'NEGOTIABLE'),
    ('demo_provider_010', 'demo_provider_010@marketplace.invalid', 'Clearview', 'Window Care', 'Demo business provider for home maintenance services.', 'Door and Cabinet Adjustments', 'Adjust hinges, handles, and sticking cabinet doors.', 'Home Maintenance', 65.00, 'HOURLY'),
    ('demo_provider_011', 'demo_provider_011@marketplace.invalid', 'Liam', 'Fraser', 'Demo individual provider for cleaning services.', 'Standard House Cleaning', 'Routine cleaning of kitchens, bathrooms, and living areas.', 'Cleaning', 38.00, 'HOURLY'),
    ('demo_provider_012', 'demo_provider_012@marketplace.invalid', 'Hearthstone', 'Repairs', 'Demo business provider for cleaning services.', 'Deep Kitchen Cleaning', 'Detailed cleaning of counters, appliances, cabinets, and floors.', 'Cleaning', 135.00, 'FIXED'),
    ('demo_provider_013', 'demo_provider_013@marketplace.invalid', 'Ava', 'Morgan', 'Demo individual provider for cleaning services.', 'Move-Out Cleaning', 'Thorough cleaning to prepare a home for new occupants.', 'Cleaning', 220.00, 'FIXED'),
    ('demo_provider_014', 'demo_provider_014@marketplace.invalid', 'Cedar & Oak', 'Landscaping', 'Demo business provider for cleaning services.', 'Office Cleaning', 'After-hours cleaning of small offices and workspaces.', 'Cleaning', 45.00, 'HOURLY'),
    ('demo_provider_015', 'demo_provider_015@marketplace.invalid', 'Lucas', 'Singh', 'Demo individual provider for cleaning services.', 'Window Washing', 'Interior and exterior cleaning of accessible windows.', 'Cleaning', 95.00, 'FIXED'),
    ('demo_provider_016', 'demo_provider_016@marketplace.invalid', 'Bluebird', 'Pet Care', 'Demo business provider for tutoring services.', 'Math Tutoring Grades 7-12', 'One-on-one tutoring for algebra, geometry, and functions.', 'Tutoring', 40.00, 'HOURLY'),
    ('demo_provider_017', 'demo_provider_017@marketplace.invalid', 'Isabella', 'Reed', 'Demo individual provider for tutoring services.', 'Introductory Physics Tutoring', 'Help with high school physics concepts and problem solving.', 'Tutoring', 45.00, 'HOURLY'),
    ('demo_provider_018', 'demo_provider_018@marketplace.invalid', 'Cornerstone', 'Tech Support', 'Demo business provider for tutoring services.', 'English Essay Coaching', 'Support with essay outlines, structure, and editing skills.', 'Tutoring', 35.00, 'HOURLY'),
    ('demo_provider_019', 'demo_provider_019@marketplace.invalid', 'Daniel', 'Kim', 'Demo individual provider for tutoring services.', 'Beginner French Lessons', 'Conversational French and grammar lessons for beginners.', 'Tutoring', 35.00, 'HOURLY'),
    ('demo_provider_020', 'demo_provider_020@marketplace.invalid', 'Urban Nest', 'Organizing', 'Demo business provider for tutoring services.', 'Programming Tutoring', 'Introductory Java, Python, and coding fundamentals.', 'Tutoring', 50.00, 'HOURLY'),
    ('demo_provider_021', 'demo_provider_021@marketplace.invalid', 'Emma', 'Clarke', 'Demo individual provider for lessons services.', 'Guitar Lessons', 'Beginner acoustic and electric guitar instruction.', 'Lessons', 42.00, 'HOURLY'),
    ('demo_provider_022', 'demo_provider_022@marketplace.invalid', 'Silver Birch', 'Photography', 'Demo business provider for lessons services.', 'Piano Lessons', 'Personalized piano lessons for children and adults.', 'Lessons', 48.00, 'HOURLY'),
    ('demo_provider_023', 'demo_provider_023@marketplace.invalid', 'Benjamin', 'Ortiz', 'Demo individual provider for photography services.', 'Portrait Photography', 'Outdoor and studio-style personal portrait sessions.', 'Photography', 150.00, 'FIXED'),
    ('demo_provider_024', 'demo_provider_024@marketplace.invalid', 'Golden Hour', 'Photo Studio', 'Demo business provider for photography services.', 'Event Photography', 'Photography for birthdays, celebrations, and local events.', 'Photography', 100.00, 'HOURLY'),
    ('demo_provider_025', 'demo_provider_025@marketplace.invalid', 'Chloe', 'Nguyen', 'Demo individual provider for photography services.', 'Product Photography', 'Clear product photos for small businesses and online shops.', 'Photography', 120.00, 'FIXED'),
    ('demo_provider_026', 'demo_provider_026@marketplace.invalid', 'Summit', 'Fitness Studio', 'Demo business provider for pet care services.', 'Dog Walking', 'Neighborhood walks tailored to your dog and schedule.', 'Pet Care', 25.00, 'FIXED'),
    ('demo_provider_027', 'demo_provider_027@marketplace.invalid', 'Alexander', 'Wright', 'Demo individual provider for pet care services.', 'Cat Sitting Visits', 'Feeding, litter care, and check-ins while you are away.', 'Pet Care', 28.00, 'FIXED'),
    ('demo_provider_028', 'demo_provider_028@marketplace.invalid', 'Little Sprout', 'Garden Co.', 'Demo business provider for pet care services.', 'Pet Grooming', 'Bathing, brushing, and basic coat care for dogs.', 'Pet Care', 65.00, 'FIXED'),
    ('demo_provider_029', 'demo_provider_029@marketplace.invalid', 'Grace', 'Campbell', 'Demo individual provider for pet care services.', 'Aquarium Maintenance', 'Tank cleaning and basic freshwater aquarium upkeep.', 'Pet Care', 70.00, 'FIXED'),
    ('demo_provider_030', 'demo_provider_030@marketplace.invalid', 'Coastal', 'Moving Services', 'Demo business provider for pet care services.', 'Pet Photography', 'Relaxed photo session featuring your pet.', 'Pet Care', 95.00, 'FIXED'),
    ('demo_provider_031', 'demo_provider_031@marketplace.invalid', 'Samuel', 'Rivera', 'Demo individual provider for technology services.', 'Computer Setup and Updates', 'Set up computers, install updates, and configure user accounts.', 'Technology', 60.00, 'HOURLY'),
    ('demo_provider_032', 'demo_provider_032@marketplace.invalid', 'Fresh Start', 'Cleaning Co.', 'Demo business provider for technology services.', 'Wi-Fi Troubleshooting', 'Diagnose home wireless coverage and connectivity problems.', 'Technology', 70.00, 'HOURLY'),
    ('demo_provider_033', 'demo_provider_033@marketplace.invalid', 'Nora', 'Hughes', 'Demo individual provider for technology services.', 'Printer Setup', 'Connect printers to home computers and wireless networks.', 'Technology', 55.00, 'FIXED'),
    ('demo_provider_034', 'demo_provider_034@marketplace.invalid', 'Red Maple', 'Design Studio', 'Demo business provider for technology services.', 'Phone and Tablet Lessons', 'Learn device settings, apps, and everyday functions.', 'Technology', 35.00, 'HOURLY'),
    ('demo_provider_035', 'demo_provider_035@marketplace.invalid', 'Henry', 'Park', 'Demo individual provider for technology services.', 'Data Backup Setup', 'Configure local or cloud backups for personal files.', 'Technology', 85.00, 'FIXED'),
    ('demo_provider_036', 'demo_provider_036@marketplace.invalid', 'Open Book', 'Learning Centre', 'Demo business provider for professional services services.', 'Resume Formatting', 'Improve resume formatting, readability, and organization.', 'Professional Services', 60.00, 'FIXED'),
    ('demo_provider_037', 'demo_provider_037@marketplace.invalid', 'Zoe', 'Foster', 'Demo individual provider for professional services services.', 'Basic Bookkeeping', 'Organize invoices, receipts, and routine records.', 'Professional Services', 45.00, 'HOURLY'),
    ('demo_provider_038', 'demo_provider_038@marketplace.invalid', 'Pinecrest', 'Handyman Services', 'Demo business provider for professional services services.', 'Social Media Graphics', 'Create simple branded graphics for online posts.', 'Professional Services', 75.00, 'FIXED'),
    ('demo_provider_039', 'demo_provider_039@marketplace.invalid', 'James', 'Ahmed', 'Demo individual provider for professional services services.', 'Website Content Editing', 'Revise headings, descriptions, and page text for clarity.', 'Professional Services', 50.00, 'HOURLY'),
    ('demo_provider_040', 'demo_provider_040@marketplace.invalid', 'Paws & Paths', 'Pet Services', 'Demo business provider for professional services services.', 'Small Business Logo Design', 'Create an initial logo concept and basic revisions.', 'Professional Services', 180.00, 'FIXED'),
    ('demo_provider_041', 'demo_provider_041@marketplace.invalid', 'Amelia', 'Scott', 'Demo individual provider for moving & delivery services.', 'Moving Help', 'Help load, unload, and carry furniture and boxes.', 'Moving & Delivery', 45.00, 'HOURLY'),
    ('demo_provider_042', 'demo_provider_042@marketplace.invalid', 'Cityline', 'Courier Co.', 'Demo business provider for moving & delivery services.', 'Furniture Delivery', 'Local pickup and delivery of small furniture items.', 'Moving & Delivery', 80.00, 'FIXED'),
    ('demo_provider_043', 'demo_provider_043@marketplace.invalid', 'Leo', 'Thompson', 'Demo individual provider for moving & delivery services.', 'Packing Assistance', 'Pack household items and label boxes for a move.', 'Moving & Delivery', 35.00, 'HOURLY'),
    ('demo_provider_044', 'demo_provider_044@marketplace.invalid', 'Willow Creek', 'Maintenance', 'Demo business provider for organization services.', 'Garage Organization', 'Sort and organize tools, storage bins, and garage items.', 'Organization', 50.00, 'HOURLY'),
    ('demo_provider_045', 'demo_provider_045@marketplace.invalid', 'Hannah', 'Price', 'Demo individual provider for organization services.', 'Closet Organization', 'Declutter and reorganize clothing and storage areas.', 'Organization', 45.00, 'HOURLY'),
    ('demo_provider_046', 'demo_provider_046@marketplace.invalid', 'True North', 'Bookkeeping', 'Demo business provider for food services services.', 'Meal Prep Assistance', 'Help prepare ingredients and meals for the upcoming week.', 'Food Services', 40.00, 'HOURLY'),
    ('demo_provider_047', 'demo_provider_047@marketplace.invalid', 'Owen', 'Murphy', 'Demo individual provider for food services services.', 'Birthday Cake Decorating', 'Custom cake decorating for small celebrations.', 'Food Services', 90.00, 'FIXED'),
    ('demo_provider_048', 'demo_provider_048@marketplace.invalid', 'White Pine', 'Home Care', 'Demo business provider for fitness services.', 'Personal Fitness Coaching', 'Individual sessions focused on basic fitness technique.', 'Fitness', 55.00, 'HOURLY'),
    ('demo_provider_049', 'demo_provider_049@marketplace.invalid', 'Layla', 'Desai', 'Demo individual provider for fitness services.', 'Beginner Yoga Instruction', 'Private yoga session tailored to beginner ability.', 'Fitness', 50.00, 'HOURLY'),
    ('demo_provider_050', 'demo_provider_050@marketplace.invalid', 'Clear Signal', 'Tech Services', 'Demo business provider for repairs services.', 'Bicycle Tune-Up', 'Basic adjustments, chain lubrication, and safety checks.', 'Repairs', 75.00, 'FIXED'),
    ('demo_provider_051', 'demo_provider_051@marketplace.invalid', 'Jack', 'Collins', 'Demo individual provider for home & garden services.', 'Seasonal Leaf Removal', 'Rake and bag leaves from lawns, paths, and garden beds.', 'Home & Garden', 95.00, 'FIXED'),
    ('demo_provider_052', 'demo_provider_052@marketplace.invalid', 'Morning Light', 'Photography', 'Demo business provider for home & garden services.', 'Patio Pressure Washing', 'Pressure wash patios and walkways to remove surface grime.', 'Home & Garden', 145.00, 'FIXED'),
    ('demo_provider_053', 'demo_provider_053@marketplace.invalid', 'Aria', 'Lopez', 'Demo individual provider for home & garden services.', 'Vegetable Garden Setup', 'Prepare garden beds and plant seasonal vegetable seedlings.', 'Home & Garden', 120.00, 'FIXED'),
    ('demo_provider_054', 'demo_provider_054@marketplace.invalid', 'Neighbourhood', 'Tutors', 'Demo business provider for home & garden services.', 'Tree Pruning Consultation', 'Inspect small ornamental trees and recommend routine pruning.', 'Home & Garden', 85.00, 'FIXED'),
    ('demo_provider_055', 'demo_provider_055@marketplace.invalid', 'William', 'Turner', 'Demo individual provider for home & garden services.', 'Snow Shoveling', 'Clear snow from residential walkways and driveways.', 'Home & Garden', 45.00, 'FIXED'),
    ('demo_provider_056', 'demo_provider_056@marketplace.invalid', 'Tidy Spaces', 'Organizing Co.', 'Demo business provider for home maintenance services.', 'Deck Staining', 'Prepare and stain outdoor decks with customer-approved products.', 'Home Maintenance', 350.00, 'NEGOTIABLE'),
    ('demo_provider_057', 'demo_provider_057@marketplace.invalid', 'Ella', 'Shah', 'Demo individual provider for home maintenance services.', 'Weatherstripping Installation', 'Replace worn weatherstripping around accessible doors.', 'Home Maintenance', 95.00, 'FIXED'),
    ('demo_provider_058', 'demo_provider_058@marketplace.invalid', 'Trailhead', 'Bicycle Repairs', 'Demo business provider for home maintenance services.', 'Curtain Rod Installation', 'Measure and install curtain rods and brackets.', 'Home Maintenance', 75.00, 'FIXED'),
    ('demo_provider_059', 'demo_provider_059@marketplace.invalid', 'Logan', 'Bailey', 'Demo individual provider for home maintenance services.', 'Shelf Mounting', 'Mount household shelving in suitable wall surfaces.', 'Home Maintenance', 80.00, 'FIXED'),
    ('demo_provider_060', 'demo_provider_060@marketplace.invalid', 'Oak & Ash', 'Painting Co.', 'Demo business provider for home maintenance services.', 'Caulking Touch-Ups', 'Replace deteriorated caulking around tubs and sinks.', 'Home Maintenance', 110.00, 'FIXED'),
    ('demo_provider_061', 'demo_provider_061@marketplace.invalid', 'Mila', 'Ross', 'Demo individual provider for cleaning services.', 'Carpet Spot Cleaning', 'Treat common spots and light stains on area carpets.', 'Cleaning', 75.00, 'FIXED'),
    ('demo_provider_062', 'demo_provider_062@marketplace.invalid', 'Greenway', 'Yard Services', 'Demo business provider for cleaning services.', 'Post-Renovation Cleanup', 'Remove dust and tidy accessible surfaces after renovation work.', 'Cleaning', 210.00, 'NEGOTIABLE'),
    ('demo_provider_063', 'demo_provider_063@marketplace.invalid', 'Caleb', 'Watson', 'Demo individual provider for cleaning services.', 'Upholstery Cleaning', 'Clean fabric sofas and chairs using appropriate methods.', 'Cleaning', 125.00, 'FIXED'),
    ('demo_provider_064', 'demo_provider_064@marketplace.invalid', 'Sparkle & Shine', 'Cleaning', 'Demo business provider for cleaning services.', 'Balcony Cleaning', 'Sweep and wash accessible condo balconies.', 'Cleaning', 80.00, 'FIXED'),
    ('demo_provider_065', 'demo_provider_065@marketplace.invalid', 'Sarah', 'Hassan', 'Demo individual provider for cleaning services.', 'Weekly Apartment Cleaning', 'Recurring cleaning for kitchens, bathrooms, and floors.', 'Cleaning', 90.00, 'FIXED'),
    ('demo_provider_066', 'demo_provider_066@marketplace.invalid', 'Blue Sky', 'Lessons', 'Demo business provider for tutoring services.', 'Chemistry Tutoring', 'High school chemistry lessons and practice problem support.', 'Tutoring', 45.00, 'HOURLY'),
    ('demo_provider_067', 'demo_provider_067@marketplace.invalid', 'Mason', 'Bell', 'Demo individual provider for tutoring services.', 'Elementary Reading Support', 'Build reading comprehension and confidence for young learners.', 'Tutoring', 35.00, 'HOURLY'),
    ('demo_provider_068', 'demo_provider_068@marketplace.invalid', 'Friendly Paws', 'Pet Sitting', 'Demo business provider for tutoring services.', 'Adult ESL Conversation', 'Practice everyday spoken English in one-on-one lessons.', 'Tutoring', 38.00, 'HOURLY'),
    ('demo_provider_069', 'demo_provider_069@marketplace.invalid', 'Lily', 'Carter', 'Demo individual provider for tutoring services.', 'Statistics Tutoring', 'Introductory statistics support for college students.', 'Tutoring', 50.00, 'HOURLY'),
    ('demo_provider_070', 'demo_provider_070@marketplace.invalid', 'Simple Steps', 'Fitness', 'Demo business provider for tutoring services.', 'Study Skills Coaching', 'Develop note-taking, planning, and exam preparation habits.', 'Tutoring', 40.00, 'HOURLY'),
    ('demo_provider_071', 'demo_provider_071@marketplace.invalid', 'Adrian', 'Young', 'Demo individual provider for lessons services.', 'Violin Lessons', 'Private beginner and intermediate violin instruction.', 'Lessons', 55.00, 'HOURLY'),
    ('demo_provider_072', 'demo_provider_072@marketplace.invalid', 'Bright Desk', 'Business Support', 'Demo business provider for lessons services.', 'Drum Lessons', 'Learn rhythm, technique, and basic drum-kit coordination.', 'Lessons', 50.00, 'HOURLY'),
    ('demo_provider_073', 'demo_provider_073@marketplace.invalid', 'Ruby', 'Edwards', 'Demo individual provider for photography services.', 'Family Photo Session', 'Outdoor family photography with edited digital portraits.', 'Photography', 190.00, 'FIXED'),
    ('demo_provider_074', 'demo_provider_074@marketplace.invalid', 'Little Lantern', 'Web Studio', 'Demo business provider for photography services.', 'Real Estate Photography', 'Photograph residential interiors for property listings.', 'Photography', 220.00, 'FIXED'),
    ('demo_provider_075', 'demo_provider_075@marketplace.invalid', 'Isaac', 'Ali', 'Demo individual provider for photography services.', 'Graduation Portraits', 'Graduation photo session with selected edited images.', 'Photography', 140.00, 'FIXED'),
    ('demo_provider_076', 'demo_provider_076@marketplace.invalid', 'Home Harmony', 'Organization', 'Demo business provider for pet care services.', 'Puppy Training Basics', 'Reward-based lessons covering everyday puppy manners.', 'Pet Care', 65.00, 'HOURLY'),
    ('demo_provider_077', 'demo_provider_077@marketplace.invalid', 'Stella', 'Green', 'Demo individual provider for pet care services.', 'Overnight Pet Sitting', 'In-home overnight care for pets while owners travel.', 'Pet Care', 95.00, 'FIXED'),
    ('demo_provider_078', 'demo_provider_078@marketplace.invalid', 'Fieldstone', 'Delivery Co.', 'Demo business provider for pet care services.', 'Dog Waste Yard Cleanup', 'Remove and dispose of pet waste from residential yards.', 'Pet Care', 55.00, 'FIXED'),
    ('demo_provider_079', 'demo_provider_079@marketplace.invalid', 'Finn', 'Cooper', 'Demo individual provider for pet care services.', 'Small Animal Care Visits', 'Feed and check on rabbits, guinea pigs, and other small pets.', 'Pet Care', 30.00, 'FIXED'),
    ('demo_provider_080', 'demo_provider_080@marketplace.invalid', 'Moss & Fern', 'Garden Design', 'Demo business provider for pet care services.', 'Pet Taxi Service', 'Local transportation to scheduled veterinary or grooming visits.', 'Pet Care', 45.00, 'FIXED'),
    ('demo_provider_081', 'demo_provider_081@marketplace.invalid', 'Alice', 'Robinson', 'Demo individual provider for technology services.', 'Laptop Performance Check', 'Review startup programs, storage use, and common slowdowns.', 'Technology', 70.00, 'FIXED'),
    ('demo_provider_082', 'demo_provider_082@marketplace.invalid', 'Fresh Frame', 'Photography', 'Demo business provider for technology services.', 'Smart TV Setup', 'Connect and configure smart TVs and streaming applications.', 'Technology', 65.00, 'FIXED'),
    ('demo_provider_083', 'demo_provider_083@marketplace.invalid', 'Theo', 'Walker', 'Demo individual provider for technology services.', 'Home Network Assessment', 'Review router placement and home network performance.', 'Technology', 90.00, 'FIXED'),
    ('demo_provider_084', 'demo_provider_084@marketplace.invalid', 'North End', 'Maintenance', 'Demo business provider for technology services.', 'Password Manager Setup', 'Help configure a password manager and import accounts safely.', 'Technology', 75.00, 'FIXED'),
    ('demo_provider_085', 'demo_provider_085@marketplace.invalid', 'Eva', 'Kaur', 'Demo individual provider for technology services.', 'Spreadsheet Help', 'Build and format simple spreadsheets for everyday tasks.', 'Technology', 45.00, 'HOURLY'),
    ('demo_provider_086', 'demo_provider_086@marketplace.invalid', 'Paper & Pixel', 'Creative Studio', 'Demo business provider for professional services services.', 'LinkedIn Profile Review', 'Improve profile wording and professional presentation.', 'Professional Services', 75.00, 'FIXED'),
    ('demo_provider_087', 'demo_provider_087@marketplace.invalid', 'Nathan', 'Morris', 'Demo individual provider for professional services services.', 'Presentation Slide Design', 'Create clear slides for small business presentations.', 'Professional Services', 110.00, 'FIXED'),
    ('demo_provider_088', 'demo_provider_088@marketplace.invalid', 'Maple Leaf', 'Moving Co.', 'Demo business provider for professional services services.', 'Invoice Template Setup', 'Prepare reusable invoice templates for independent workers.', 'Professional Services', 65.00, 'FIXED'),
    ('demo_provider_089', 'demo_provider_089@marketplace.invalid', 'Jasmine', 'Lee', 'Demo individual provider for professional services services.', 'Proofreading Service', 'Proofread short documents for grammar and consistency.', 'Professional Services', 35.00, 'HOURLY'),
    ('demo_provider_090', 'demo_provider_090@marketplace.invalid', 'Sunny Side', 'Meal Services', 'Demo business provider for professional services services.', 'Business Card Design', 'Design print-ready business card layouts and revisions.', 'Professional Services', 80.00, 'FIXED'),
    ('demo_provider_091', 'demo_provider_091@marketplace.invalid', 'Eli', 'Parker', 'Demo individual provider for moving & delivery services.', 'Small Item Courier', 'Local pickup and drop-off for parcels and documents.', 'Moving & Delivery', 40.00, 'FIXED'),
    ('demo_provider_092', 'demo_provider_092@marketplace.invalid', 'Horizon', 'Computer Help', 'Demo business provider for moving & delivery services.', 'Student Move Assistance', 'Help students pack, carry, and load smaller moves.', 'Moving & Delivery', 40.00, 'HOURLY'),
    ('demo_provider_093', 'demo_provider_093@marketplace.invalid', 'Clara', 'Hill', 'Demo individual provider for organization services.', 'Storage Unit Organization', 'Sort and label storage unit contents for easy retrieval.', 'Organization', 55.00, 'HOURLY'),
    ('demo_provider_094', 'demo_provider_094@marketplace.invalid', 'Harmony Keys', 'Music Studio', 'Demo business provider for organization services.', 'Pantry Organization', 'Declutter and arrange kitchen pantry shelves and containers.', 'Organization', 85.00, 'FIXED'),
    ('demo_provider_095', 'demo_provider_095@marketplace.invalid', 'David', 'Santos', 'Demo individual provider for organization services.', 'Home Office Organization', 'Organize desks, paperwork, and office storage.', 'Organization', 50.00, 'HOURLY'),
    ('demo_provider_096', 'demo_provider_096@marketplace.invalid', 'Velvet Leaf', 'Cleaning', 'Demo business provider for food services services.', 'Private Baking Lessons', 'Learn basic baking techniques in a guided lesson.', 'Food Services', 65.00, 'HOURLY'),
    ('demo_provider_097', 'demo_provider_097@marketplace.invalid', 'Ivy', 'Mitchell', 'Demo individual provider for food services services.', 'Meal Planning Consultation', 'Plan practical weekly meals and grocery lists.', 'Food Services', 60.00, 'FIXED'),
    ('demo_provider_098', 'demo_provider_098@marketplace.invalid', 'Parkside', 'Pet Services', 'Demo business provider for fitness services.', 'Outdoor Group Fitness', 'Lead small outdoor group fitness sessions for beginners.', 'Fitness', 25.00, 'FIXED'),
    ('demo_provider_099', 'demo_provider_099@marketplace.invalid', 'Ryan', 'Stewart', 'Demo individual provider for fitness services.', 'Mobility and Stretching Session', 'Guided beginner mobility and stretching exercises.', 'Fitness', 45.00, 'HOURLY'),
    ('demo_provider_100', 'demo_provider_100@marketplace.invalid', 'Riverbend', 'Home Repairs', 'Demo business provider for repairs services.', 'Bicycle Flat Tire Repair', 'Repair or replace standard bicycle inner tubes.', 'Repairs', 35.00, 'FIXED');

-- Create one account per listing only when its username is absent.
-- The hash matches the application's existing PBKDF2WithHmacSHA256 format.
INSERT INTO users (username, email, password_hash, first_name, last_name, bio)
SELECT s.username, s.email,
       'PBKDF2WithHmacSHA256$600000$6YiGoRxz4ss5xCWeUPYRJg==$zaFj+jcf6FrjPkl3HGFqdTQt3XS6VhmsiMSzwLpZtDY=',
       s.first_name, s.last_name, s.bio
FROM temp_seed_marketplace_100 s
WHERE NOT EXISTS (
    SELECT 1 FROM users u WHERE u.username = s.username
);

-- Migrate the original 50 entries that were assigned to the old shared demo provider.
-- Preserve their listing_id, related requests/reviews, and other fields.
UPDATE listings l
JOIN users old_provider ON l.provider_id = old_provider.user_id
JOIN temp_seed_marketplace_100 s ON s.title = l.title
JOIN users new_provider ON new_provider.username = s.username
SET l.provider_id = new_provider.user_id
WHERE old_provider.username = 'marketplace_demo_provider';

-- Insert new entries. A listing already owned by its intended demo provider is not duplicated.
INSERT INTO listings (provider_id, title, description, category, price, price_type, status)
SELECT u.user_id, s.title, s.description, s.category, s.price, s.price_type, 'ACTIVE'
FROM temp_seed_marketplace_100 s
JOIN users u ON u.username = s.username
WHERE NOT EXISTS (
    SELECT 1 FROM listings l
    WHERE l.provider_id = u.user_id AND l.title = s.title
);

DROP TEMPORARY TABLE temp_seed_marketplace_100;

-- Optional verification queries:
SELECT COUNT(*) AS demo_listings
FROM listings l JOIN users u ON u.user_id = l.provider_id
WHERE u.username LIKE 'demo_provider_%';
SELECT l.listing_id, l.title, CONCAT(u.first_name, ' ', u.last_name) AS provider_name
FROM listings l JOIN users u ON u.user_id = l.provider_id
WHERE u.username LIKE 'demo_provider_%'
ORDER BY l.listing_id LIMIT 10;

# Local Services Marketplace — Setup

## Requirements

- JDK 21
- Apache Maven
- Apache Tomcat 9
- MySQL 8

## Database setup

1. Start your local MySQL server.
2. Open database_setup.sql in MySQL Workbench.
3. Execute the entire script.

The script creates local_services_marketplace and these tables:

- users
- listings
- service_requests
- reviews
- reports

It does not delete existing tables or data. Existing tables are
not automatically migrated if their structure differs.

## Database connection

DatabaseConnection.java currently connects to:

jdbc:mysql://localhost:3306/local_services_marketplace

The configured username is root.

Set the MARKETPLACE_DB_PASSWORD environment variable to your
local MySQL password. Do not put your password in source code.

If your database host, port, or username differs, adjust the URL
or USER setting in DatabaseConnection.java for your environment.

## Running through Eclipse

1. Import the repository using:
   File > Import > Maven > Existing Maven Projects.
2. Use JDK 21.
3. Under Project Properties > Project Facets, enable:
   - Java 21
   - Dynamic Web Module 4.0
4. Use src/main/webapp as the web content directory.
5. Set the context root to local-services-marketplace.
6. Under Deployment Assembly, ensure Maven Dependencies maps
   to /WEB-INF/lib.
7. In Servers, double-click the Tomcat 9 server.
8. Open its launch configuration and select Environment.
9. Add MARKETPLACE_DB_PASSWORD with your local MySQL password.
10. Add the project to Tomcat using Add and Remove.
11. Restart Tomcat.

## Running a Maven-built WAR

From the repository directory, run:

```shell
mvn clean package
```

Copy target/local-services-marketplace.war into Tomcat's webapps
directory.

Set MARKETPLACE_DB_PASSWORD in the environment of the process
that starts Tomcat, then restart Tomcat. Eclipse launch variables
apply only when Tomcat is started through that Eclipse configuration.

## Pages

With Tomcat running on port 8080:

- Registration:
  http://localhost:8080/local-services-marketplace/register
- Login:
  http://localhost:8080/local-services-marketplace/login
- Account:
  http://localhost:8080/local-services-marketplace/account
- Local database diagnostic:
  http://localhost:8080/local-services-marketplace/db-test

Create a new account through the registration page.
No shared test account is included in the setup script.

## Authentication checks

The following were manually verified locally:

- Account registration succeeds.
- Valid credentials open the account page.
- Incorrect passwords are rejected.
- After logout, opening /account redirects to /login.

PasswordUtilCheck also passed its six standalone password checks.

## Current scope

This contribution adds database-backed registration, login,
an account details page, and logout.

Listings, requests, reviews, profile editing, email verification,
and password recovery still need implementation or integration.
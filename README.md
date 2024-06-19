
# Toyota Spring Boot Backend Project
  This repository developed for the backend project of Toyota's kickoff recruitment process.

# Description
  This project aims to establish a shopping system like supermarkets.It includes many operations such as user login, logout, product addition, deletion, sales and reporting.Each operation is specific to the user in different roles.While some users are interested in user operations such as adding and deleting users, others are interested in product sales.Some users are responsible for reviewing the reports that appear after the sales transaction.This usage aims to improve teamwork.

# Used Technologies
 * JDK17
 * Spring Boot = > Spring Web & Spring Data
 * Maven
 * Git
 * PostgreSQL
 * Log4j2
 * jUnit5

# Things To Pay Attention To
 * Using Spring Boot Framework
 * The use of layered architecture
 * Object Oriented Programming
 * Accordance with SOLID Principles
 * Token based authentication and authorization
 * Logging
 * Unit tests
 * Java doc
 * Use of git
 * Soft delete
 * Microservice Architecture and docker environments

# Application Architecture


![image](https://github.com/cebo54/kasiyerApp/assets/93757760/91499233-abce-47b2-ae96-177bf345acf9)


**Resource(Web Service)** 

It is the layer that exposes the code we write to the outside.API is created through web services

**DTO(Data Transfer Object)**

It is a copy of domain classes.They are used to exchange data with the frontend

**Service**

Layers that contain business logic.The algorithm of the work to be done is carried out in this layer

**Domain** 

Are java classes that correspond to tables in the database.Used to write and read data into the database

**Dao(Data Access Object)**

Used to access the database and perform operations


# Database Design

![image](https://github.com/cebo54/kasiyerApp/assets/93757760/0b0af483-c6ef-4b01-9f6b-0add87fa131f)

There are 4 relationships in the database design;

**User Role Relation**

* In the User Role relation user must have at least 1 role.Also, a user can have more than one role and a role can have more than one user.For this reason the relationship between them is set up as many-to-many.

**Product Category Relation**

* In this relation while a product may have one category, a category may have more than one product, so this relationship is set up as many-to-one.

**Category Campaign**

* In my application, campaigns are applied by category.There can be a maximum of one campaign in a category, while a campaign can be applied to more than one category,so this relationship is set up as one-to-many.

**SoldProduct , Sale , Product and Campaign Relation**

* In my sold products table, I keep the id of the sale, the id of the products sold, and the ID of the applied campaign.in this table, there may be more than one sales, more than one product, more than one applied campaign, so all relationships are set up as one-to-many.

# Security

A role-based authentication and authorization structure has been created using the jwt token to provide access to the relevant endpoints. This structure is provided using the  JJWT library. There are 3 different roles in accessing endpoints:
   * Admin
   * Storemanager
   * Cashier

# Login

When the user wants to log in, he enters the necessary information, the accuracy of this information is checked, and if it is correct, the unique token is generated, the user assigns a request to the endpoints with this token.

**Login Architecture

![image](https://github.com/cebo54/ToyotaBackendProject/assets/93757760/37a76028-510b-46d6-8603-a9b21570d5e2)

# The Process of Accessing Endpoints

The user sends a request via the API Gateway.Gateway receives the incoming request and extracts the JWT (JSON Web Token) information in it.The authentication service checks the validity of the incoming JWT with the auth/validate/{token} method. To do this, it verifies the token's signature and looks at the validity period.If the token is valid, the Authentication service returns this "Token is valid." message to the Gateway and the request is allowed to continue.If the token is not valid, the Authentication service sends a 401 Unauthorized response to the Gateway.


![WhatsApp Image 2024-06-18 at 00 48 11](https://github.com/cebo54/ToyotaBackendProject/assets/93757760/829f6da3-9f03-4abc-b788-652293e9831f)

# Microservice Architecture

![WhatsApp Image 2024-06-19 at 20 50 03](https://github.com/cebo54/ToyotaBackendProject/assets/93757760/66431f11-e3e9-487a-a025-9f0a48a1dc25)

**Api Gateway

The API Gateway is a central point that meets all requests from the outside world. Gateway redirects incoming requests to the appropriate microservices, performs security verification and provides load balancing.
When the user sends a request, the API Gateway outputs the JWT (JSON Web Token) content. Gateway contacts the Authentication Service and checks the validity of the JWT. If the token is valid, the request is redirected to the target service, otherwise a 401 Unauthorized response returns.

**Discovery Server









  

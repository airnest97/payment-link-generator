# oxygen-task
A demo task for Oxygen

# Invoice and Payment Link using Spring Boot

The Invoice and Payment Link is a comprehensive web application developed using Spring Boot, aimed at providing users with a feature-rich platform to efficiently manage and organize their invoice and collect payment. This README provides an overview of the system, installation instructions, usage guidelines, API documentation, and details for contributing to the project.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
    - [Prerequisites Requirement](#prerequisites-requirement)
    - [Optional Requirement](#optional-requirement)
    - [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Postman Documentation](#postman-documentation)


## Features

This Invoice and Payment Link System comes equipped with a range of powerful features designed to make invoice and payment collection seamless and efficient:

- **Invoice Creation**: Users can easily create an invoice to send to their customers seamlessly.
- **Unique Payment Link Generator**: Users can easily create a unique payment link to send to their customers to make payment easy and seamlessly.
- **Integration Support**: The system offers well-defined API endpoints for seamless integration with external applications.

## Technologies Used

The Invoice and Payment Link System leverages modern technologies to deliver a robust and efficient experience:

- **Java**: The core programming language for developing the application logic.
- **Spring Boot**: A powerful framework for building robust and scalable applications.
- **Spring Data JPA**: Provides data access and manipulation capabilities using the Java Persistence API.
- **Spring Web**: Facilitates the creation of web APIs and interfaces.
- **H2**: A widely-used in-memory database management system.
- **Maven**: Manages project dependencies and provides a structured build process.
- **Git**: Version control for collaborative development.
- **Docker**: Containerization lets you build, test, and deploy applications quickly.

## Getting Started

### Prerequisites Requirement

Before getting started, ensure you have the following components installed:

1. **A docker file is on the project root directory, you would need docker installed on your local machine to run the docker file.**
2. **This project was built using JDK 17, you would need JDK 17 installed on you local machine.**

- [Java Development Kit (JDK 17)](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/products/docker-desktop/)


### Optional Requirement

1. **Docker.**
- **The first command builds the docker image.**
- **The second command runs the docker build.**

    ```bash
   docker build -t task:latest . 
   
   docker run -d -p 6040:6040 task:latest
    ```

### Installation

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/airnest97/payment-link-generator.git
   cd oxygen-task
   ```

2. **Configure the Database:**

   Modify the `src/main/resources/application.properties` file to include your database connection details:

   ```properties
   spring.datasource.url=jdbc:h2:mem:your_database_name
   spring.datasource.username=your_database_username
   spring.datasource.password=your_database_password
   ```

## Build and Run the Application:

Execute the following command to build and run the application:

````bash
mvn spring-boot:run
````

## Access the Application

Open your browser and navigate to `http://localhost:6040` to access the Invoice and Payment Link System.

## Usage

### Invoice and Payment Link

- **Create Invoice:** User passes their information and product details to create an invoice .
- **Link to Invoice:** A link to view the generated invoice is returned to the user.
- **Invoice:** User is routed to this endpoint to view generated invoice. The invoice generated can be viewed on this endpoint.
- **Payment Link:** A unique payment link is generated and returned to the user to complete the transaction using the invoice Id.

### API Integration

If you need to integrate the Invoice and Payment Link System with other applications, you can use the provided API endpoints for seamless data exchange.

## API Endpoints

The Invoice and Payment Link System offers the following API endpoints:

### Book Endpoints

- Create an invoice: `POST /api/v1/invoice/create`
- Generate link to invoice: `POST /api/v1/invoice/generate-invoice-link`
- Route to generated invoice: `GET /api/v1/invoice/view-invoice/{merchantId}/{invoiceId}`
- Generate Payment Link: `POST /api/v1/invoice//payment-link?invoiceId=invoiceId`


### Future Improvements

Given the short time frame for the task, I could not implement security on the application endpoints. If given more time, I would add security to the application, thereby making the application more secured for usage in production.



## postman documentation

For more detailed information about these API endpoints, refer to the API documentation.
- [Postman Documentation Collection](https://documenter.getpostman.com/view/21596187/2sA35Bbj9C)



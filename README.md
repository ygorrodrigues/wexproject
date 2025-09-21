# WEX Interview Project

A Spring Boot REST API application for managing purchase transactions with currency exchange rate calculations. This project was developed as part of the WEX interview process.

## Features

- **Purchase Management**: Create and retrieve purchase transactions
- **Currency Exchange**: Calculate exchange rates for purchases using external API
- **H2 Database**: In-memory database for development with web console
- **RESTful API**: Clean REST endpoints for all operations
- **Input Validation**: Comprehensive validation for all API inputs
- **Unit Testing**: Extensive test coverage for services and controllers

## Prerequisites

### Java Requirements
- **Java 21** or higher
- **Gradle 8.0+** (included via wrapper)

### Verify Java Installation
```bash
java -version
# Should show Java 21.x.x

gradle -v
# Should show Gradle version 8.0 or higher
```

## 🛠️ Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd wexproject
```

### 2. Build the Project
```bash
# Using Gradle wrapper (recommended)
./gradlew build

# On Windows
gradlew.bat build
```

### 3. Run the Application
```bash
# Using Gradle wrapper
./gradlew bootRun

# On Windows
gradlew.bat bootRun
```

### 4. Verify Installation
The application will start on `http://localhost:8080`

Test the health endpoint:
```bash
curl http://localhost:8080/ping
# Expected response: pong
```

## Database Configuration

### H2 In-Memory Database
The application uses H2 in-memory database for development:

- **Database URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (empty)
- **Console URL**: `http://localhost:8080/h2-console`

### Accessing H2 Console
1. Start the application
2. Navigate to `http://localhost:8080/h2-console`
3. Use the following connection details:
   - **JDBC URL**: `jdbc:h2:mem:testdb`
   - **User Name**: `sa`
   - **Password**: (leave empty)
4. Click "Connect"

### Database Schema
The application automatically creates the following tables:
- `purchases`: Stores purchase transaction data

## API Documentation

### Base URL
```
http://localhost:8080
```

### Available Endpoints

#### 1. Health Check
```http
GET /ping
```
**Response**: `pong`

#### 2. Create Purchase
```http
POST /purchase
Content-Type: application/json

{
  "description": "Sample Purchase",
  "amount": 100.50,
  "transactionDate": "2024-01-15"
}
```

**Response**:
```json
{
  "id": 1,
  "description": "Sample Purchase",
  "amount": 100.50,
  "transactionDate": "2024-01-15"
}
```

#### 3. Get Exchange Rate for Purchase
```http
GET /purchase/{id}/exchange?countryCurrency=Mexico-Peso
```

**Parameters**:
- `id`: Purchase ID (integer)
- `countryCurrency`: Country and currency in format "Country-Currency"

**Response**:
```json
{
  "originalAmount": 100.50,
  "originalCurrency": "USD",
  "convertedAmount": 1700.85,
  "convertedCurrency": "MXN",
  "exchangeRate": 16.93,
  "transactionDate": "2024-01-15",
  "conversionDate": "2024-01-15"
}
```

## Testing

### Run All Tests
```bash
./gradlew test
```

### Run Tests with Coverage
```bash
./gradlew test jacocoTestReport
```

### Test Reports
Test reports are generated in `build/reports/tests/test/index.html`

## Development

### Key Dependencies
- **Spring Boot 3.5.5**: Main framework
- **Spring Data JPA**: Database operations
- **H2 Database**: In-memory database
- **Lombok**: Reduces boilerplate code
- **Spring Boot Validation**: Input validation
- **JUnit 5**: Testing framework

### Configuration Files
- `application.properties`: Main configuration
- `build.gradle`: Project dependencies and build configuration

## Deployment

### Build JAR
```bash
./gradlew build
```

### Run JAR
```bash
java -jar build/libs/wexproject-0.0.1-SNAPSHOT.jar
```

### Production Considerations
- Replace H2 with PostgreSQL/MySQL for production
- Configure proper logging
- Set up monitoring and health checks
- Use environment-specific configuration files

## API Usage Examples

### Creating a Purchase
```bash
curl -X POST http://localhost:8080/purchase \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Office Supplies",
    "amount": 250.75,
    "transactionDate": "2024-01-15"
  }'
```

### Getting Exchange Rate
```bash
curl "http://localhost:8080/purchase/1/exchange?countryCurrency=Canada-Dollar"
```

## Project Decisions

- Integer IDs: Used simple auto-incrementing integers instead of UUIDs for simplicity
- Country-Currency Format: Used "Country-Currency" format to handle countries with same currency names
- H2 Database: In-memory database for easy setup
- REST API: Clean RESTful endpoints following Spring Boot conventions

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [H2 Database Documentation](https://www.h2database.com/)
- [Gradle Documentation](https://gradle.org/docs/)
- [REST API Best Practices](https://restfulapi.net/)

## TODO
- Review error response for exchange rate not found in 6 months
- Review unit tests
- Revalidated purchase description to a max of 50 chars and add unit test
- Revalidated amount: must be a valid positive amount rounded to the nearest cent and add unit test
- Handle future dates from transaction date (should handle past also? i think not)
- Add controller unit tests
- Move call to api on exchange rate service to a new exchange rate repository
- Add repository unit tests
- Add and/or verify test fixtures for currencies, with some that has within 6 months and others dont
- Integration tests using the api
- Review how to be a production ready

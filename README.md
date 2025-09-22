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
  "amount": 100,
  "transactionDate": "2025-09-15"
}
```

**Response**:
```json
{
  "id": 1,
  "description": "Sample Purchase",
  "amount": 100,
  "transactionDate": "2025-09-15"
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
  "transactionDate": "2025-09-15",
  "conversionDate": "2025-09-15"
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
    "transactionDate": "2025-09-15"
  }'
```

### Getting Exchange Rate
```bash
curl "http://localhost:8080/purchase/1/exchange?countryCurrency=Canada-Dollar"
```

## Project Decisions

- Integer IDs: Used simple auto-incrementing integers instead of UUIDs for simplicity
- H2 Database: In-memory database for easy setup
- REST API: Clean RESTful endpoints following Spring Boot conventions
- Country-Currency Format: Used "Country-Currency" format to handle countries with same currency names
- The round to 2 decimal places of the converted purchase occurs after the conversion
- Always sort the newest currency exchange rate so we can get the first result, if none is shown using the last 6 months we return an empty data
- The currency conversion rate should be equal to or 6 months before the purchase date, cannot use future currencies

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [H2 Database Documentation](https://www.h2database.com/)
- [Gradle Documentation](https://gradle.org/docs/)
- [REST API Best Practices](https://restfulapi.net/)

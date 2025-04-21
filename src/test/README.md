# Testing Guide for Marketplace Sales API

This guide describes the testing strategy and how to execute tests for the Marketplace Sales API project.

## Test Types

### 1. Unit Tests
- **Focus**: Service layer and business logic validation
- **Tools**: JUnit 5 + Mockito
- **Example**: `OrderServiceTest` validates order cancellation logic

### 2. Integration Tests
- **Focus**: Repository queries and JPA functionality
- **Tools**: `@DataJpaTest` with H2 in-memory database
- **Example**: `ProductRepositoryTest` verifies product search functionality

### 3. REST API Tests
- **Focus**: HTTP endpoints and contracts
- **Tools**: MockMvc for controller tests
- **Example**: `OrderControllerTest` verifies request/response formats and status codes

### 4. Security Tests
- **Focus**: Role-based access control
- **Tools**: Spring Security Test annotations
- **Example**: `SecurityTest` verifies authorization rules

### 5. Migration Tests
- **Focus**: Database schema validation
- **Tools**: Flyway + TestContainers (PostgreSQL)
- **Example**: `MigrationTest` verifies migration scripts apply correctly

## Running Tests

### Running All Tests
```bash
mvn clean test
```

### Running a Specific Test Category
```bash
# Run only unit tests
mvn test -Dtest=*ServiceTest

# Run only integration tests
mvn test -Dtest=*RepositoryTest

# Run only controller tests
mvn test -Dtest=*ControllerTest
```

### Running a Specific Test
```bash
mvn test -Dtest=OrderServiceTest
```

## Test Environment Configuration

### H2 In-Memory Database (Default for tests)
- Configured in `application-test.properties`
- Used for fast repository and service tests

### TestContainers (For PostgreSQL tests)
- Used for migration tests
- Requires Docker running locally

## Key Test Scenarios

### Critical Flow Tests
1. **Complete Order Process**: Create → Payment → Stock Update → Notification
2. **Concurrent Product Purchase**: Multiple users buying last item
3. **Security Validation**: Authorized access to resources

## Writing New Tests

1. **Unit Tests**: Place in corresponding service package
2. **Integration Tests**: Place in repository package or integration package
3. **Controller Tests**: Place in controller package
4. **All test classes**: Should end with `Test` suffix

## Test Data

- Use test utility classes in `util` package
- Follow existing patterns for creating test entities 
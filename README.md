# Statistics Project

This project provides two thread-safe implementations of the `Statistics` interface for collecting and calculating statistical data such as minimum, maximum, mean, and variance. The project also demonstrates SPI (Service Provider Interface) to allow switching between different implementations dynamically.

---

## Features

- **Thread-Safe Implementations**:
    - `AtomicThreadSafeStatistics`: Uses `Atomic` variables for thread safety.
    - `LockBasedThreadSafeStatistics`: Uses `ReentrantLock` for thread safety.
- **SPI Integration**: Allows dynamic switching between implementations.

---

## Project Structure

```
statistics
├── statistics-spi        # SPI interface module
├── statistics-atomic     # Atomic implementation
├── statistics-lock       # Lock-based implementation
├── statistics-test-base  # Base test cases
├── statistics-client     # SPI client to use implementations
└── dist                           # Packaged distribution
```

---

## Getting Started

### Prerequisites

- **Java**: Ensure `JAVA_HOME` is set to a valid JDK installation.
- **Maven**: Verify Maven is installed and available in `PATH`.

### Building the Project

1. Build all modules:
   ```bash
   ./build-and-package.sh
   ```

This script builds the project, and required artifacts are placed in dist.

---

## Running the SPI Client

1. Execute the client:
   ```bash
   cd ./dist/bin/
   source run-spi-client.sh
   ```

---

## Development

### Adding a New Implementation

1. Create a new module (e.g., `statistics-newimpl`).
2. Implement the `Statistics` interface.
3. Register the implementation in `META-INF/services/in.shashwattiwari.statistics.Statistics`.

### Running Tests

To run all tests:

```bash
mvn test
```

---

## Project Dependencies

- **JUnit 5**: Used for testing.
- **Maven**: For build and dependency management.

---

## Future Enhancements

- Add more statistical methods.
- Improve SPI client logging.
- Support for distributed statistics computation.
- Supports performance benchmarking using JMH.
- Can be extended to handle backpressure scenarios.
- Utilise Actor Model based Frameworks like Akka, which inherently handle concurrency.
  - One message at a time get processed by respective actors.
  - Would be easier to scale horizontally. 
---

## Contact

For questions or feedback, please contact:
- **Shashwat Tiwari**: [shashwat.tiwari@myyahoo.com]


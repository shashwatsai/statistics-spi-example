#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Define directories and configurations
MODULES=("statistics-spi" "statistics-test-base" "statistics-atomic" "statistics-lock")
CLIENT_MODULE="statistics-client"
MAIN_CLASS="in.shashwattiwari.statistics.StatisticsClient"
OUTPUT_DIR="dist"
ARCHIVE_NAME="spi-client.tar.gz"
BIN_SCRIPT="run-spi-client.sh"

# Functions for colored output
info() {
    echo -e "\033[1;34m[INFO]\033[0m $1"
}

error() {
    echo -e "\033[1;31m[ERROR]\033[0m $1"
}

# Function to find and set JAVA_HOME
if [ -z "$JAVA_HOME" ]; then
    error "JAVA_HOME is not set."
    exit 1
fi

# Ensure Maven is available
if ! command -v mvn &> /dev/null; then
    error "Maven is not available in PATH. Please install Maven or add it to PATH."
    exit 1
fi

info "Using Maven: $(mvn -version | head -n 1)"

# Build all modules
info "Building modules: ${MODULES[*]} and $CLIENT_MODULE"
for module in "${MODULES[@]}" "$CLIENT_MODULE"; do
    if [ -d "$module" ]; then
        info "Building module: $module"
        (cd "$module" && mvn clean install -DskipTests)
    else
        error "Module directory '$module' does not exist."
        exit 1
    fi
done

# Locate the client JAR
CLIENT_JAR_PATH=$(find "$CLIENT_MODULE/target" -name "*.jar" ! -name "*sources.jar" ! -name "*tests.jar" | head -n 1)

if [ -z "$CLIENT_JAR_PATH" ]; then
    error "Failed to find the client JAR in $CLIENT_MODULE/target."
    exit 1
fi

info "Found client JAR: $CLIENT_JAR_PATH"

# Create distribution directory
info "Creating distribution directory: $OUTPUT_DIR"
rm -rf "$OUTPUT_DIR"
mkdir -p "$OUTPUT_DIR/bin" "$OUTPUT_DIR/lib"

# Copy client JAR to lib folder
info "Copying client JAR to lib folder..."
cp "$CLIENT_JAR_PATH" "$OUTPUT_DIR/lib/"

# Copy other module JARs to lib folder
info "Copying other module JARs to lib folder..."
for module in "${MODULES[@]}"; do
    MODULE_JAR_PATH=$(find "$module/target" -name "*.jar" ! -name "*sources.jar" ! -name "*tests.jar" | head -n 1)
    if [ -z "$MODULE_JAR_PATH" ]; then
        error "Failed to find JAR for module '$module' in $module/target."
        exit 1
    fi
    cp "$MODULE_JAR_PATH" "$OUTPUT_DIR/lib/"
    info "Copied $MODULE_JAR_PATH to lib"
done

# Create run script
info "Creating run script..."
cat > "$OUTPUT_DIR/bin/$BIN_SCRIPT" <<EOF
#!/bin/bash
if [ -z "\$JAVA_HOME" ]; then
    echo "JAVA_HOME is not set. Attempting to locate JDK..."
    JAVA_BIN=\$(command -v java)
    if [ -z "\$JAVA_BIN" ]; then
        echo "Java is not installed or not available in PATH. Please install Java or set JAVA_HOME manually."
        exit 1
    fi
    export JAVA_HOME=\$(dirname "\$(dirname "\$JAVA_BIN")")
    echo "JAVA_HOME set to: \$JAVA_HOME"
fi

java -cp "../lib/*" $MAIN_CLASS
EOF
chmod +x "$OUTPUT_DIR/bin/$BIN_SCRIPT"

# Create dist directory if not exists
mkdir -p "$OUTPUT_DIR"

# Package into tar.gz
info "Packaging distribution into $OUTPUT_DIR/$ARCHIVE_NAME"
tar -czf "$ARCHIVE_NAME" -C "$OUTPUT_DIR" .

# Clean up
info "Cleaning up temporary files..."
# rm -rf "$OUTPUT_DIR"


info "Build and packaging complete. Archive: $OUTPUT_DIR/$ARCHIVE_NAME"

# Step 1: Lightweight, secure Java 17 Alpine Runtime
#FROM eclipse-temurin:17-jre-alpine

# Step 2: Set internal container working directory
#WORKDIR /app

# Step 3: Copy the compiled Spring Boot executable JAR file
#COPY target/eComm-0.0.1-SNAPSHOT.jar app.jar

# Step 4: your JVM memory tuning options (-Xms200m -Xmx250m)
#ENV JAVA_TOOL_OPTIONS="-Xms200m -Xmx250m -XX:+UseSerialGC"

# Step 5: Expose default Spring Boot application port
#EXPOSE 8080

# Step 6: Launch Spring Boot application
#ENTRYPOINT ["java", "-jar", "app.jar"]

# FOR RENDER DEPLOYMENT
# ─── Stage 1: Build JAR with Maven ────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first (Docker cache - won't re-download if pom unchanged)
COPY pom.xml .

# Download all dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build JAR - skip tests for faster build
RUN mvn clean package -DskipTests -B

# ─── Stage 2: Run JAR ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy JAR from build stage
# Your artifactId=eComm, version=0.0.1-SNAPSHOT
COPY --from=build /app/target/eComm-0.0.1-SNAPSHOT.jar app.jar

# JVM Memory tuning (good for Render free plan)
ENV JAVA_TOOL_OPTIONS="-Xms200m -Xmx250m -XX:+UseSerialGC"

# Spring Boot default port
EXPOSE 8080

# Run
ENTRYPOINT ["java", "-jar", "app.jar"]

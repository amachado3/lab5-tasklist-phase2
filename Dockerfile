# ==========================================
# Stage 1: Build
# ==========================================
FROM maven:3.9-eclipse-temurin-21 AS build

#ARG MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
#WORKDIR .

COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Runtime
# ==========================================
FROM eclipse-temurin:21-jre-alpine

LABEL description="TodoList REST API"
LABEL java.version="21"

WORKDIR /todo-service-2

# Security: non-root user
RUN addgroup -S javalin && adduser -S javalin -G javalin

# Copiar apenas o JAR compilado
COPY --from=build target/taskslist-phase2-1.0-SNAPSHOT-jar-with-dependencies.jar todo-service-2.jar

# Ownership
RUN chown -R javalin:javalin /todo-service-2
USER javalin

# Executar
ENTRYPOINT ["sh", "-c", "java -jar todo-service-2.jar"]
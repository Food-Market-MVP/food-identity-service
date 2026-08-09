# syntax=docker/dockerfile:1.7

FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

# Copy build metadata first so dependency downloads are cached independently of source changes.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw --batch-mode --no-transfer-progress dependency:go-offline

COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw --batch-mode --no-transfer-progress package -DskipTests

# Distroless provides only a Java 21 runtime and an unprivileged nonroot user.
FROM gcr.io/distroless/java21-debian12:nonroot AS runtime

WORKDIR /app
COPY --from=build --chown=nonroot:nonroot /workspace/target/food-identity-*.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

EXPOSE 8081
USER nonroot:nonroot

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

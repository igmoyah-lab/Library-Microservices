FROM eclipse-temurin:25 AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew \
    && ./gradlew clean bootJar --no-daemon -x test

FROM eclipse-temurin:25

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
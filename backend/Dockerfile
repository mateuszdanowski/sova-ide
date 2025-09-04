FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle ./

COPY app-core/build.gradle app-core/
COPY plugin-api/build.gradle plugin-api/

RUN ./gradlew build -x test || return 0

COPY . .

RUN ./gradlew :app-core:bootJar --no-daemon

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/app-core/build/libs/*.jar app.jar

COPY plugins ./plugins

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

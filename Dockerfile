# --- Build stage ---
FROM docker.io/library/maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# --- Runtime stage ---
FROM docker.io/library/eclipse-temurin:21.0.12_8-jre-alpine-3.24
RUN apk update && apk upgrade --no-cache && \
    apk del --no-cache gnupg gnupg-dirmngr gnupg-gpgconf gnupg-keyboxd gnupg-utils gnupg-wks-client gpg gpg-agent gpg-wks-server gpgsm gpgv 2>/dev/null || true
WORKDIR /app
RUN addgroup -S -g 10001 app && adduser -S -u 10001 -G app app
COPY --from=build /app/target/*.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
# --- ETAPA 1: Builder (Compilación con Gradle) ---
FROM amazoncorretto:21-alpine-jdk AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

COPY src src

RUN ./gradlew clean build --refresh-dependencies -x test --no-daemon

RUN java -Djarmode=layertools -jar build/libs/*.jar extract --destination /extracted

FROM amazoncorretto:21-alpine-jdk
WORKDIR /application

COPY --from=builder /extracted/dependencies/ ./
COPY --from=builder /extracted/spring-boot-loader/ ./
COPY --from=builder /extracted/snapshot-dependencies/ ./
COPY --from=builder /extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

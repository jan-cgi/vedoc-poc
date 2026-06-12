FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /workspace

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

RUN java -Djarmode=tools -jar build/libs/*.jar extract --layers --launcher --destination extracted

FROM eclipse-temurin:25-jre-noble
WORKDIR /app

COPY --from=build --chown=1000:1000 /workspace/extracted/dependencies/ ./
COPY --from=build --chown=1000:1000 /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=1000:1000 /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=1000:1000 /workspace/extracted/application/ ./

USER 1000:1000

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
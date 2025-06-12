# OpenJDK 21 기반 이미지 사용
FROM eclipse-temurin:21-jre

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY build/libs/*.jar app.jar

# 환경변수 파일 복사
COPY src/main/resources/application-dev.properties /app/config/application-dev.properties
COPY src/main/resources/application-prod.properties /app/config/application-prod.properties

# 로그 디렉토리 생성 및 권한 설정
RUN mkdir -p /var/log/app && \
    chmod 777 /var/log/app

# 8080 포트 오픈
EXPOSE 8080

# Spring Boot 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
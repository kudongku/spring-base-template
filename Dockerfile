# OpenJDK 21 기반 이미지 사용
FROM eclipse-temurin:21-jre

# JAR 파일 복사 (빌드 후 실제 파일명에 맞게 수정 필요)
COPY build/libs/*.jar app.jar

# 8080 포트 오픈
EXPOSE 8080

# Spring Boot 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]


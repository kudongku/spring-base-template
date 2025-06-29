# Teen MyData 백엔드

## 프로젝트 개요

- **목적**: 마이데이터 조성사업의 기술스택으로 Spring Boot를 채택하였으며, 스프링부트의 로깅 시스템과 모니터링 시스템 등 비즈니스 로직이 아닌 서비스 로직에 집중하여 프로젝트를 설계·구현하고 있습니다.
- **구현 기능**:
  - 구글 등 OAuth2 로그인 → JWT 발급 → 권한별 API 접근 제어
  - 일관된 API 응답 DTO (`ApiResponse`)
  - AOP 사용한 API 호출/응답/에러 로깅 (DB+메일)
  - P6Spy 사용한 쿼리 파라미터 로깅
  - Swagger(OpenAPI) 문서 자동화 + 인증기능 구현
  - **Spring Boot Actuator, Prometheus, Grafana**를 활용한 리소스/헬스 모니터링
  - **Loki + Grafana + Promtail** 기반의 로그 수집 및 대시보드 구축

---

## 🚀 기술 스택

| 구분        | 사용 기술 및 도구                                                                            | 비고/설명                                 |
| ----------- | -------------------------------------------------------------------------------------------- | ----------------------------------------- |
| Language    | ![Java](https://img.shields.io/badge/Java-21-blue?logo=java)                                 | 가장 최신 LTS 버전 유지                   |
| Framework   | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen?logo=springboot) | java version과 호환되는 LTS               |
| ORM         | Spring Data JPA                                                                              | DB 연동, 트랜잭션 관리, QueryDsl 도입예정 |
| Database    | ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)                             | 관계형 데이터베이스                       |
| Build Tool  | ![Gradle](https://img.shields.io/badge/Gradle-7.x-02303A?logo=gradle)                        | 빌드/의존성 관리                          |
| Security    | Spring Security (OAuth2, JWT)                                                                | 소셜 로그인, JWT 인증/인가                |
| Docs        | SpringDoc OpenAPI (Swagger)                                                                  | API 문서 자동화                           |
| Mail        | Spring Mail                                                                                  | 에러/알림 메일 발송                       |
| Logging/AOP | AOP, SLF4J, P6Spy                                                                            | API 호출/응답/에러 로깅                   |
| Monitoring  | Spring Boot Actuator, Prometheus, Grafana                                                    | 리소스/헬스 모니터링, 대시보드            |
| Log Collect | Loki, Promtail, Grafana                                                                      | 로그 수집, 중앙집중 대시보드              |
| Container   | ![Docker](https://img.shields.io/badge/Docker-%230db7ed.svg?logo=docker&logoColor=white)     | 개발/운영 환경 컨테이너 지원              |
| Test        | JUnit                                                                                        | 단위/통합 테스트 작성예정                 |
| CI/CD       | ![Jenkins](https://img.shields.io/badge/Jenkins-%232C3A42.svg?logo=jenkins&logoColor=white)  | 테스트, 빌드, 배포 자동화                 |
| 기타        | Lombok                                                                                       | 코드 간결화(어노테이션 기반)              |

---

## 📝 TODO

- [ ] Jenkins 기반 CI/CD 파이프라인 구현 (테스트, 빌드, 배포 자동화)
- [ ] Log 수집/저장 모델 개선 (API 로그 구조 및 저장 방식 고도화)
- [ ] Querydsl 도입 (복잡한 동적 쿼리 및 타입 안전성 확보)
- [ ] 데이터베이스를 MySQL → PostgreSQL로 이전 (이식성 및 확장성 강화)
- [ ] 브랜치 전략 (squash and merge?)
- [ ] 서비스 로직과 비즈니스 로직 분리
---

## 폴더/아키텍처 구조

```
src/main/java/kr/soulware/teen_mydata/
├── aop/           # AOP(로깅 등)
├── config/        # 보안, Swagger 등 설정
├── controller/    # API 엔드포인트
├── dto/           # 요청/응답 DTO
├── entity/        # JPA 엔티티
├── enums/         # Enum(권한 등)
├── exception/     # 커스텀 예외
├── filter/        # JWT 인증 필터
├── handler/       # 예외/인증 핸들러
├── repository/    # JPA Repository
├── service/       
└── TeenMydataApplication.java
```

---

## 설치 및 실행

### 1) 환경 변수

- `src/main/resources/application-local.properties` 생성  
  (예시는 `application.properties.example` 참고)
- 필수 환경 변수 예시:
  - DB 접속 정보
  - OAuth2 Client ID/Secret
  - JWT Secret Key
  - 메일 서버 정보 등

### 2) 의존성 설치 및 빌드

```bash
./gradlew build
```

### 3) 서버 실행

```bash
./gradlew bootRun
```

### 4) Swagger API 문서

- http://localhost:8080/swagger-ui.html

### 5) oauth 로그인

- http://localhost:8080/oauth2/authorization/google

---

## git workflow

### 1) github 이슈 생성

- 이슈에 맞는 브랜치 생성
- 브랜치 이름, ex) [issue number]-[issue title]
- 커밋메세지, ex) [type]/[branch_name]:[issue title]

### 2) feature → develop

- Rebase and Merge 방식

### 3) develop → main

- Squash and Merge 방식 
- 커밋메세지, ex) v[version]: [release summary]

---

## 컨벤션

### 공통 응답 포맷

```json
{
  "success": true,
  "status": 200,
  "data": {},
  "message": null,
  "errorCode": null
}
```

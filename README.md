# 마이데이터 조성사업

---

## 기술 스택

- Java 21
- Spring Boot 3.5.0
- Spring Data JPA
- MySQL
- Lombok
- SpringDoc OpenAPI (Swagger)
- Spring Mail

## 프로젝트 실행 방법

1. 프로젝트 클론

```bash
git clone [repository-url]
```

2. 의존성 설치

```bash
./gradlew build
```

3. VM 옵션 설정

```
-Dspring.profiles.active={profile}
```

4. application-local.properties 생성

```application.properties.example```을 참고해서 생성

5. 애플리케이션 실행

```bash
./gradlew bootRun
```

## API 문서

Swagger UI를 통해 API 문서를 확인할 수 있습니다.

- URL: ```http://[your_ip or localhost]:8080/swagger-ui.html```

## 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── kr/soulware/teen_mydata/
│   │       ├── controller/    # API 엔드포인트 정의
│   │       ├── service/       # 비즈니스 로직
│   │       ├── repository/    # 데이터 접근 계층
│   │       ├── entity/        # JPA 엔티티
│   │       ├── dto/           # 데이터 전송 객체
│   │       │   ├── request/   # 요청 DTO
│   │       │   └── response/  # 응답 DTO
│   │       ├── filter/        # 서블릿 필터
│   │       └── handler/       # 예외 처리
│   └── resources/
│       ├── application-local.properties    # 로컬 환경변수 설정
│       ├── application-dev.properties      # 개발 환경변수 설정
│       ├── application-prod.properties     # 운영 환경변수 설정
│       └── application.properties.example  # 환경변수 가이드
└── test/
```

## 컨벤션

### 1. 네이밍 컨벤션

- 클래스명: PascalCase
- 메서드명: camelCase
- 변수명: camelCase
- 상수: UPPER_SNAKE_CASE
- 패키지명: 소문자

### 2. 어노테이션 컨벤션

1. Spring stereotype
   (@RestController, @Service, @Repository, @Component 등)
2. Request/Mapping, hidden
   (@RequestMapping, @GetMapping, @Hidden)
3. Swagger/OpenAPI
   (@Tag, @Operation 등)
4. Lombok
   (@Getter, @Setter → @Builder → @NoArgsConstructor, @RequiredArgsConstructor, @AllArgsConstructor → @ToString,
   @EqualsAndHashCode -> @Slf4j)
5. Spring AOP/트랜잭션, table
   (@Transactional, @table)
6. 비동기/스케줄링
   (@Async, @Scheduled 등)
   클래스/메서드 선언

### 3. API 문서화, 응답형식

- 모든 API 응답은 `ApiResponse` 클래스를 사용하여 일관된 형식으로 반환합니다:
- controller 상단에 tag, method 상단에 operation을 작성한다.

```java

@Tag(name = "API 그룹명", description = "API 그룹 설명")
// ...
public class ControllerName {

    @Operation(summary = "API 요약", description = "API 상세 설명")
    // ...
    public ResponseEntity<ApiResponse<T>> methodName() {
        // ...
    }

}
```

#### case of success

```json
{
  "success": true,
  "status": 200,
  "data": {},
  "message": null,
  "errorCode": null
}
```

#### case of fail

```json
{
  "success": false,
  "status": 400,
  "data": {},
  "message": "fail",
  "errorCode": "FAIL"
}
```

### 4. 로깅 규칙

- 로그 레벨 사용 기준:
    - ERROR: 예외 발생 시
    - WARN: 잠재적 문제 발생 시
    - INFO: 중요 비즈니스 로직 실행 시
    - DEBUG: 상세 디버깅 정보
- 로그 포맷: `[로그 주제] 메시지 | 파라미터1: 값1 | 파라미터2: 값2`

### 5. 비동기 처리

- `@Async` 어노테이션을 사용한 비동기 메서드는 명확한 네이밍 규칙을 따릅니다:
    - 메서드명 끝에 'Async' 접미사 사용
    - 예: `saveLogAsync()`, `sendErrorMailAsync()`

### 6. 예외 처리

- 모든 예외는 `GlobalExceptionHandler`에서 처리
- 커스텀 예외는 적절한 HTTP 상태 코드와 함께 처리
- 예외 발생 시 로그는 반드시 남기도록 구현

## 기여 방법

1. 이슈 생성
2. 브랜치 생성
3. 변경사항 커밋
4. Pull Request 생성

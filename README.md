# My PT AI

[![CI](https://github.com/geunnseung/myPtAi/actions/workflows/ci.yml/badge.svg)](https://github.com/geunnseung/myPtAi/actions/workflows/ci.yml)

식단과 운동 기록을 바탕으로 개인화된 AI 코칭을 제공하는 Spring Boot 웹앱입니다.

## 기술 스택

- Java 21
- Spring Boot 3.5.11
- Spring Web
- Spring Data JPA
- Bean Validation
- Thymeleaf
- Gradle
- H2 Database: 로컬 개발용
- MySQL: 운영용
- Flyway: 데이터베이스 마이그레이션 관리
- OpenAI API: 백엔드에서만 연동

## 실행

```bash
./gradlew bootRun
```

## Docker 실행

이미지 빌드:

```bash
docker build -t my-pt-ai .
```

운영 프로필 실행:

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e MYSQL_URL=jdbc:mysql://host.docker.internal:3306/myptai \
  -e MYSQL_USERNAME=myptai \
  -e MYSQL_PASSWORD=change-me \
  -e OPENAI_API_KEY=your-openai-api-key \
  my-pt-ai
```

헬스체크:

```bash
curl http://localhost:8080/actuator/health
```

현재 프로젝트는 실무 커밋 단위로 작게 쌓아갑니다.

## CI

GitHub Actions에서 `main`, `codex/**` 브랜치 푸쉬와 `main` 대상 PR마다 테스트를 실행합니다.

## 문서

- [MVP 요구사항](docs/mvp-requirements.md)
- [도메인 모델](docs/domain-model.md)

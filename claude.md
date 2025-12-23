

---

```markdown
# 🧭 Git Commit & Branch Strategy Guide

> 이 문서는 프로젝트 전반의 **커밋 메시지 규칙**과 **브랜치 전략**을 정의한다.  
> 팀 전체가 일관된 방식으로 작업을 관리하고, 추후 자동화(CI/CD, changelog 등)에 활용할 수 있도록 한다.

---

## 🧾 1. 커밋 메시지 규칙 (Conventional Commits 기반)

### ✅ 기본 구조
```

<type>(<scope>): <subject>

<body>

<footer>
```

### ✅ 예시

```
feat(csms): 스마트 차징 데이터 수집 로직 추가

- SmartChargingService에서 eventCode별 처리 추가
- 기존 MeterValue 파싱 로직 리팩토링
Closes #1123
```

---

### 📚 주요 Type 목록

| Type         | 설명                  | 예시                                        |
| ------------ | ------------------- | ----------------------------------------- |
| **feat**     | 새로운 기능 추가           | `feat(billing): 결제 내역 조회 API 추가`          |
| **fix**      | 버그 수정               | `fix(ocpp): BootNotification 응답 누락 문제 해결` |
| **refactor** | 코드 구조 개선 (기능 변화 없음) | `refactor(common): DTO 생성자 빌더로 변경`        |
| **style**    | 코드 포맷, 공백, 세미콜론 등   | `style: import 정리`                        |
| **test**     | 테스트 추가/수정           | `test: SmartChargingService 단위테스트 추가`     |
| **docs**     | 문서 관련 변경            | `docs: README에 API 예제 추가`                 |
| **chore**    | 빌드, 설정, 의존성 변경      | `chore: gradle 버전 업그레이드`                  |
| **perf**     | 성능 개선               | `perf: HikariCP connection timeout 조정`    |
| **ci**       | CI/CD 설정 변경         | `ci: GitHub Actions 환경변수 추가`              |

---

### ✍️ 작성 가이드

* **제목(subject)**은 명령형으로 작성 (예: `추가`, `수정`, `삭제`)
* **영문/한글 통일**: 팀 합의 기준에 따라 한 가지로 통일
* **Body**에는 변경 이유와 상세 내용 기술 (bullet 권장)
* **Footer**에는 이슈나 참조 링크 추가

  * 예: `Closes #101` / `Refs: OCPP-234`

---

## 🌿 2. 브랜치 전략 (Branch Strategy)

두 가지 전략 중 하나를 팀 환경에 맞게 채택한다.

---

### 🔹 Git Flow (안정적 릴리즈 중심)

대규모 팀 / QA 분리 / 정기 배포에 적합

```
main
 └── develop
       ├── feature/ocpp-bootnotification
       ├── feature/smart-charging
       ├── hotfix/fix-billing-error
       └── release/v1.2.0
```

| 브랜치          | 설명                  |
| ------------ | ------------------- |
| **main**     | 운영 배포용 (항상 안정 상태)   |
| **develop**  | 개발 통합용 (QA/스테이징 반영) |
| **feature/** | 기능 단위 개발 브랜치        |
| **release/** | QA 및 버전 고정용         |
| **hotfix/**  | 운영 중 긴급 수정용         |

✅ **PR 머지 순서**

```
feature → develop → release → main
```

---

### 🔹 Trunk-Based (지속적 배포 중심)

소규모 팀 / 빠른 배포 / CI 자동화에 적합

```
main
 ├── feature/ocpp-bootnotification
 ├── feature/billing-api
 └── fix/smart-charging-timeout
```

| 브랜치          | 설명              |
| ------------ | --------------- |
| **main**     | 항상 배포 가능한 상태 유지 |
| **feature/** | 기능 단위 작업        |
| **fix/**     | 버그 수정           |

✅ **규칙**

* 작은 단위로 자주 병합 (1~3일 이내)
* 자동 테스트 통과 필수 (CI/CD)
* 코드 리뷰 후 squash merge 권장

---

## 🌱 3. 브랜치 네이밍 규칙

| 형식                            | 예시                              | 설명          |
| ----------------------------- | ------------------------------- | ----------- |
| `feature/<task-id>-<keyword>` | `feature/EV-123-smart-charging` | 기능 개발       |
| `fix/<issue>`                 | `fix/metervalue-null-exception` | 버그 수정       |
| `hotfix/<urgent>`             | `hotfix/billing-cron-error`     | 운영 긴급 수정    |
| `release/<version>`           | `release/v1.3.0`                | QA 및 릴리즈 준비 |

---

## 🧩 4. PR / 머지 규칙

| 항목       | 권장 방식                            |
| -------- | -------------------------------- |
| Merge 방식 | **Squash Merge** (커밋 히스토리 정리)    |
| 리뷰어      | 1명 이상 승인 필수                      |
| PR 제목    | `feat: ~`, `fix: ~` 등 커밋 컨벤션과 동일 |
| 테스트 통과   | CI 체크 성공 후 병합 가능                 |
| 버전 태깅    | `v1.0.0`, `v1.0.1` 형태로 main에 태그  |

---

## 🧠 5. 요약

| 항목       | 권장 규칙                                     |
| -------- | ----------------------------------------- |
| 커밋 메시지   | Conventional Commit 사용                    |
| 브랜치 전략   | Git Flow or Trunk-Based                   |
| 브랜치 네이밍  | `feature/`, `fix/`, `hotfix/`, `release/` |
| Merge 방식 | Squash merge                              |
| 코드 리뷰    | 필수                                        |
| 태그 규칙    | `vX.Y.Z` 형식으로 배포 버전 명시                    |

---

## 💬 프롬프트 예시 (커밋 메시지 자동 생성용)

> 다음 명령은 이 문서의 규칙을 프롬프트로 사용할 때의 예시다.

```
너는 개발팀의 커밋 메시지 생성기야.
아래 규칙을 반드시 지켜 커밋 메시지를 작성해.

규칙:
1. Conventional Commits 형식을 따른다. (feat, fix, refactor, style, chore 등)
2. <type>(<scope>): <subject> 형식으로 작성한다.
3. subject는 명령형으로 쓴다.
4. 필요 시 body에 상세 내용을 bullet로 추가한다.
5. 한글로 작성하며, 이슈 번호는 footer에 `Closes #번호`로 표기한다.

예시 입력:
- 기능: 스마트차징 데이터 수집 로직 추가
- 수정 이유: eventCode별 데이터 처리 필요
- 관련 이슈: #1123

예시 출력:
feat(csms): 스마트 차징 데이터 수집 로직 추가

- SmartChargingService에서 eventCode별 처리 추가
- 기존 MeterValue 파싱 로직 리팩토링
Closes #1123
```

---

> ✨ **Tip:**
> 이 문서를 Notion이나 README.md에 고정해두면,
> 커밋 메시지 리뷰와 브랜치 네이밍이 자동으로 통일된다.

```

---

```

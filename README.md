# 비욘드 메디슨 최종 과제 (작성자: 김동균) 

## 1. 프로젝트 소개

### 1.1. 프로젝트 구조

~~~
├── BeyondMedicineTestApplication.kt
├── [ common ] // 공통기능 패키지
│   ├── dto
│   │   └── ApiResponse.kt
│   ├── exception
│   │   ├── AccessCodeRetryFailException.kt
│   │   └── GlobalExceptionHandler.kt
│   └── util
├── [ config ] // Bean 설정 , 초기 데이터 와 같은 설정 패키지
│   ├── DataLoader.kt
│   ├── SecureConfig.kt
│   └── swagger
│       └── SwaggerConfig.kt
├── [ prescription ] // 처방코드 패키지
│   ├── controller
│   │   └── AccessCodeController.kt
│   ├── domain
│   │   ├── AccessCodeHistory.kt
│   │   ├── UserAccessCode.kt
│   │   └── constant
│   │       └── AccessCodeStatus.kt
│   ├── dto
│   │   ├── AccessCodeHistoryDto.kt
│   │   ├── ActivateAccessCodeRequestDto.kt
│   │   ├── CreateAccessCodeRequestDto.kt
│   │   ├── CreateAccessCodeResponseDto.kt
│   │   └── UserAccessCodeDto.kt
│   ├── repository
│   │   ├── AccessCodeHistoryRepository.kt
│   │   ├── AccessCodeRepository.kt
│   │   ├── AccessCodeRepositoryImpl.kt
│   │   └── UserAccessCodeRepository.kt
│   └── service
│       ├── AccessCodeService.kt
│       └── AccessCodeServiceImpl.kt
└── [ user ] //사용자 검증 패키지
    ├── constants
    │   └── UpdateStatus.kt
    ├── controller
    │   └── UserVerificationController.kt
    ├── domain
    │   ├── AppVersion.kt
    │   └── UserVerificationLog.kt
    ├── dto
    │   ├── AppVersionDto.kt
    │   ├── UserVerificationLogDto.kt
    │   └── UserVerificationRequestDto.kt
    ├── repository
    │   ├── AppVersionRepository.kt
    │   ├── UserVerificationLogRepository.kt
    │   ├── UserVerificationRepository.kt
    │   └── UserVerificationRepositoryImpl.kt
    └── service
        ├── UserVerificationService.kt
        └── UserVerificationServiceImpl.kt
~~~

- 프로젝트는 공통, 설정, 처방코드, 사용자 검증 별로 패키지를 나누어 구성하였습니다.
- 각 패키지는 Layered Architecture를 따르며, Controller, Service, Repository로 구성되어 있습니다.

### 1.2. 프로젝트 기능 소개

- 처방코드 관련 기능
  
  - 처방코드 생성 : 
    - 병원 id 를 통해 처방코드를 생성하고, 생성 이력을 DB에 저장 합니다.
    - 처방코드는 4자리의 숫자와 4자리의 대문자 구성으로 난수를 통해 생성됩니다.
    - 난수로 생성된 처방코드가 DB 에 존재하는 경우, 재시도를 하며 총 10번의 재시도를 진행합니다.
    - 10번의 재시도를 모두 실패할 경우, AccessCodeRetryFailException을 발생시킵니다.
  
  - 처방코드 활성화 :
    - userId 와 처방코드를 통해 처방코드를 활성화 합니다. 
    - userId가 UUID가 아닌경우, 처방코드가 8자리가 아닌경우 에 BindException을 발생시킵니다.
    - 처방코드가 생성이력에 존재하지 않으면 NoSuchElementException, 이미 활성화된 경우, AccessCodeAlreadyActivatedException 이 발생합니다.
    - 처방코드가 생성이력에 존재하고, 활성화 되지 않은 경우나 기존 처방코드가 이미 만료된 경우, 활성화 처리를 합니다.
    - 처방코드의 만료가 되는 기준은 6주후 자정, 즉 43일 후 00시 00분 00초 로 설정되어 있습니다.

- 사용자 검증 관련 기능
   - 사용자 검증 및 로그 저장:
     - 사용자의 userId, version, os, mode, hash 를 통해 사용자 검증을 진행합니다.
     - 검증에 앞서 사용자 로그를 저장합니다. (검증 성공 / 실패 여부와 상관 없이)
     - 요청 파라미터가 유효하지 않은 경우 IllegalArgumentException을 발생시킵니다. 
     - 이 모든조건이 만족된 상태에서 version 검증을 진행합니다. version 은 os, mode 에 따라 DB에 저장 됩니다.
       - version < minimum version 이면 UpdateStatus.FORCE_UPDATE_REQUIRED
       - minimum version <= version < latest version 이면 UpdateStatus.UPDATE_REQUIRED
       - latest version <= version 이면 UpdateStatus.NO_UPDATE_REQUIRED

### 1.3. 프로젝트 구현중 아쉬웠던 점

  1. access code 생성시 동시성 이슈에 대해 재시도 로직을 도입하였으나 단순히 10회 반복하는 방식으로 구현하였습니다
      - 재시도 로직이 많아지게 될 경우 DB 부하가 증가하는 방식이라 생각됩니다.
      - 이에 대해 재시도 로직의 지수적 증가 를 고려하였으나, 시간관계상 구현하지 못하였습니다.
  
  2. hash 함수 생성에서 단순히 SHA-256 알고리즘으로 해싱을 구현했습니다.
      - 두가지 이상의 알고리즘을 섞어서 사용하고, Bcypt 와 같은 알고리즘을 사용하여 보안성을 높일 수 있었습니다.
  
  3. version 비교시 비교 함수가 복잡하게 되었습니다.
      - version 비교를 위한 Utility Class 도입으로 가독성을 높일 수 있었습니다.
  
  
  
### 1.4. 프로젝트 환경

- Language : Kotlin 1.9.25 (JDK 17)
- Framework : Spring Boot 3.4.4
- Database : MySQL 9.2.0
- Build Tool : Gradle 8.13
- Test : kotest 5.9.1


## 2. 프로젝트 실행 방법 [프로젝트의 루트에서 실행하는것을 권장합니다.]  

빌드 방법은 다음과 같습니다.
~~~
./gradlew clean build
~~~

실행 방법은 다음과 같습니다. 
~~~
# 먼저 start_db.sh 를 실행하여 DB를 실행합니다. [해당 스크립트는 docker 를 실행합니다. 따라서 docker 가 설치중이고 실행중이어야 합니다.]

sh start_db.sh

# 그리고 다음과 같이 실행합니다.

java -jar build/libs/beyond-medicine-test-1.0.0.jar

# 실행 후에는 sample data 가 insert 되어 바로 테스트 가능합니다.
~~~

## 3. API 문서
- 본 프로젝트에는 swagger 를 통해 API 문서를 제공합니다.
- 프로젝트 실행 후 http://localhost:8080/swagger-ui.html 에서 확인 가능합니다.




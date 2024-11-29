# LMS demo
Spring boot와 Kotlin을 활용한 Learning Management System (LMS) 데모입니다.

## Features
### 회원 가입
모든 유저는 수강생, 강사의 역할로 회원가입이 가능합니다.

### 로그인
회원 가입한 유저는 각각 가입한 역할에 따라 수강생, 강사로 로그인이 가능합니다.

### 강의 개설
강사는 본인의 강의를 개설할 수 있습니다.

### 강의 조회
수강생, 강사는 모두 등록된 강의를 조회할 수 있습니다.

강의 리스트는 등록순(createdAt), 신청자수(applicationCount), 신청률(applicationRate)를 기준으로 오름차순, 내림차순으로 정렬이 가능합니다.

### 강의 신청
수강생, 강의를 개설하지 않은 강사는 강의를 신청할 수 있습니다.

한번에 여러개의 강의를 신청할 수 있으며, 신청 결과에서 신청에 성공한 강의와 실패한 강의를 확인할 수 있습니다.

인기 강의에 신청이 폭주하는 것을 대비하여 락을 통해 동시성 문제를 해결하였습니다.

기본적으로 `Redis`을 이용한 분산락과 `ReentrantLock`을 이용한 단일 프로세스에서의 락이 구현되어 있습니다.

추가적인 락을 사용하고 싶다면 `com.example.lms.lock.LockService`를 구현하여 사용할 수 있습니다.

## How to run

### Prerequisites
1. Install [JDK 21](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
2. Clone this repository:
    ```bash
    git clone https://github.com/mkroo/lms-demo.git
    cd repository
    ```

### Running Locally
1. Install dependencies:
    ```bash
    ./gradlew build 
    ```
2. Start the application:
    ```bash
    ./gradlew bootRun
    ```
3. Open your browser and navigate to `http://localhost:8080`

### Running Tests
1. Run tests:
    ```bash
    ./gradlew test
    ```

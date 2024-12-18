# 동시성 제어 방식 분석 보고서

## 개요

`LockManager` 클래스와 `PointService` 클래스를 통해 동시성 제어를 수행합니다.  
`LockManager`는 특정 `id`에 기반한 락을 관리하며, `PointService`는 이를 활용해 포인트 정보를 안전하게 읽거나 수정합니다.  
이 문서에서는 코드의 동시성 제어 방식을 분석하고, 장점과 적절성을 보고합니다.

---

## 동작 방식

### `LockManager`

- `LockManager`는 `ConcurrentHashMap`을 사용해 `id`별로 고유한 `ReentrantLock` 객체를 관리합니다.
    - `ConcurrentHashMap`은 스레드-세이프한 해시 맵 구현체로, 여러 스레드가 동시에 접근해도 데이터 정합성을 보장합니다.
- `computeIfAbsent` 메서드를 통해 `id`에 해당하는 `ReentrantLock`을 생성하거나 반환합니다. 이는 락 생성 로직을 단순화하고, 중복된 락 생성 시도를 방지합니다.
- `withLock` 메서드를 제공하며, 주어진 `id`에 대해 락을 획득한 후 실행할 코드를 블록으로 받아 실행합니다.
- `ReentrantLock`의 공정 모드를 활성화하여 락 대기 순서를 보장합니다. 이를 통해 동일한 `id`에 대해 다수의 요청이 발생할 경우, 요청이 도착한 순서대로 처리됩니다.

### `PointService`

- `PointService`는 `LockManager`를 활용하여 포인트 관련 데이터의 동시성을 제어합니다.
    - `getPoint`: 특정 `id`의 포인트 정보를 읽습니다.
    - `charge` 및 `use`: 특정 `id`의 포인트 정보를 수정하며, 각각 충전과 사용에 대한 히스토리를 기록합니다.
- 포인트 업데이트 작업(`charge`, `use`)은 `LockManager`의 `withLock`을 사용해 락을 획득한 상태에서 수행되므로, 동일 `id`에 대한 경쟁 조건을 방지합니다.
- 반면, 히스토리 저장 작업은 동시성 제어의 필요성이 없으므로, 락 없이 수행됩니다. 이를 통해 동작의 병렬성을 유지하며 성능을 최적화합니다.

---

## 동시성 테스트

`PointServiceConcurrencyIT` 클래스에서 동시성 테스트를 수행했습니다.  
각 테스트는 `@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)`를 활용하여 매 테스트 실행 시 컨텍스트를 초기화함으로써
테스트 간의 상호작용을 방지하고, 독립적이고 신뢰할 수 있는 테스트 환경을 보장했습니다.

다음 세 가지 테스트로 동작 정합성을 검증하였습니다.

### 1. 동일한 사용자의 포인트 충전 요청에 대한 동시성 보장

- **설명**: 다수의 스레드가 동일한 사용자의 포인트를 동시에 충전하는 상황을 가정하여 테스트를 수행했습니다.
- **결과**: 모든 충전 요청이 정확히 처리되었으며, 데이터 정합성이 유지됨을 확인했습니다.

### 2. 동일한 사용자의 포인트 사용 요청에 대한 동시성 보장

- **설명**: 다수의 스레드가 동일한 사용자의 포인트를 동시에 사용하는 상황을 가정하여 테스트를 수행했습니다.
- **결과**: 모든 사용 요청이 충돌 없이 정확히 처리되었으며, 데이터 정합성이 유지됨을 확인했습니다.

### 3. 포인트 충전 및 사용 요청 혼합 상황에 대한 동시성 보장

- **설명**: 포인트 충전 및 사용 요청이 동시에 발생하는 복합 상황을 가정하여 테스트를 수행했습니다.
- **결과**: 모든 요청이 충돌 없이 정확히 처리되었으며, 데이터 정합성이 유지됨을 확인했습니다.

### 참고) 테스트 도우미 메서드 `concurrencyTestHelper` 동작 방식

- **설명**: `concurrencyTestHelper`는 다수의 태스크를 병렬로 실행하는 도우미 메서드로, 다음과 같은 동작 과정을 따릅니다.
    - `Executors.newFixedThreadPool`: 스레드 풀을 생성하여 고정된 수의 스레드로 작업을 처리합니다.
    - `repeat(times)`: 주어진 횟수만큼 반복하여 테스트 작업을 제출합니다.
    - `executorService.submit(task)`: 각 작업을 스레드 풀에 제출하여 실행합니다.
    - `futures.forEach { it.get() }`: 모든 태스크가 완료될 때까지 기다립니다.
    - `executorService.shutdown()`: 모든 작업 완료 후 스레드 풀을 정리합니다.
- **결과**: 동시 요청에 따른 데이터 정합성을 확인할 수 있도록 모든 태스크가 병렬로 실행된 후 완료 상태를 검증했습니다.

---

## 장점

### 1. 간단하고 명확한 구현

- `LockManager`를 통해 ID 기반 락 관리가 캡슐화되어 있으며, 동시성 제어가 필요한 코드가 명확히 분리되어 있습니다.
- `withLock` 메서드는 락 획득 및 해제를 추상화하여 코드 중복을 방지합니다.

### 2. ID별 독립된 락 관리

- `ConcurrentHashMap`을 활용해 ID별로 락을 독립적으로 관리하므로, 서로 다른 ID에 대한 작업은 동시 실행이 가능합니다.

### 3. 요청 순서 보장

- `ReentrantLock`의 공정 모드를 활성화하여 락 대기 순서를 보장합니다. 이를 통해 동일한 ID에 대해 다수의 요청이 발생할 경우 처리 순서를 예측할 수 있습니다.

### 4. 내역 저장의 최적화

- 히스토리 저장 작업은 동시성 제어의 필요성이 없으므로, 락 없이 수행되어 성능을 최적화합니다.

---

## 적절성 평가

해당 과제의 필수 사항 내에 "분산 환경은 고려하지 않습니다."라는 제약이 명시되어 있습니다.  
따라서 위의 동시성 처리는 단일 JVM 환경에서 ID별로 독립적인 동시성 제어를 수행하는 데 적절하다고 볼 수 있습니다.  
또한 테스트를 통해 데이터 정합성을 보장함을 확인할 수 있습니다.

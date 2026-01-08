# kafka 실습 & 스터디

---

1. Postman/requet : POST /kafka/sending 호출
2. Controller : KafkaProducerService 호출
3. Produces : KafkaTemplate을 통해 topic 이라는 저장소로 "hello kafka" 전송
4. Kafka Broker : 메시지를 보관하고 컨슈머에게 "새 데이터 왔다"고 알림
5. Consumer : @KafkaListener가 이를 낚아채서 listen() 메서드 실행 콘솔 출력

---

## 참조

- Producer N : 1 : Consumer N 구조

### 나눌 떄 주의할 점

> 1. 공통 DTO 관리 : Producer가 UserDto 객체를 JSON으로 보내면 Consumer도 똑같은 구조의 UserDto 클래스를 가지고 있어야 깨지지 않고 읽을 수 있다.
> 2. Topic 이름 동일 : Producer가 topic_A에 쐈는 데 Consumer가 topic_B를 보고 있으면 데이터가 전달되지 않는다.
> 3. Group ID 관리 : Consumer 프로젝트가 여러 개일 때, 각각의 프로젝트가 데이터를 모두 다 받아야 한다면 group_id를 서로 다르게 설정 (같은 그룹 ID를 쓰면 메시지를 나눠 갖게 된다.)

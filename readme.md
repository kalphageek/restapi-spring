## Test를 위한 Profile 적용방법
1. test/resources/application-test.yml 생성한다.
   * 이름을 다르게 하면 override 한다.
2. Project Structure > Modules : test/resources를 Test Resources Folders 적용한다.
3. Test Java 소스에 @ActiveProfiles("test") 선언한다.

## application.yml
* SQL은 DEBUG모드로 logging. SQL Bind Variable을 찍는다.
```yaml
logging:
  level:
    org:
      hibernate:
        SQL: DEBUG
        type.descriptor.sql.BasicBinder: TRACE
```
* Test에서 <br>
  andExpect(status().isBadRequest()) --> "Controller에서 Dto를 받을때, Dto가 받을 수 없는 속성이 있으면 에러를 발생시켜라
```yaml
spring:
  jackson:
    deserialization:
      fail-on-unknown-properties: true
```
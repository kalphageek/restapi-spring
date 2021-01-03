## Test에서만 H2를 사용하려고 할 때
1. test/resources/application-test.yml 생성한다.
   * 이름을 다르게 하면 override 한다.
2. Project Structure > Modules : test/resources를 Test Resources Folders 적용한다.
3. Test Java 소스에 @ActiveProfiles("test") 선언한다.

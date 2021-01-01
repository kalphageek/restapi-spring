## 한글 깨짐 해결
```yaml
$vi application.yml
spring:
  http:
    encoding:
      charset: UTF-8
      enabled: true
      force: true
```

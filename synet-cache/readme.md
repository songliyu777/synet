###bean 定义配合mongo
```
@Document(value = "user")
@Data
public class User implements Serializable {
    @Id
    private String account;
    private Long user_id;
    private String password;
}
```
###dao 定义

```
@Repository
public class UserDao {

    @Autowired
    private UserRepository userRepository;

    @ReactiveCachePut(value = "user", key = "#result.account")
    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    @ReactiveCacheable(value = "user", key = "#account")
    public Mono<User> findOne(String account) {
        return userRepository.findById(account);
    }

    @ReactiveCacheEvict(value = "user", key = "#account")
    public Mono<User> delete(String account) {
        return userRepository.findById(account).flatMap(t -> userRepository.deleteById(t.getAccount()).then(Mono.just(t)));
    }
}
```

### repository 定义
```
@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
```

### 启动定义
```
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, ReactiveLoadBalancerAutoConfiguration.class})
@EnableReactiveCaching
public class LoginApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class, args);
    }
}
```

### 配置定义
```
## cache 配置
synet:
  cache:
    type: redis
    redis:
      time-to-live: 3h
      cache-null-values: false
```
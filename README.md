이 프로젝트는 Spring Boot, MyBatis, 그리고 PostgreSQL을 사용하여 Read-Write Lock 패턴을 구현한 것입니다.

1. **UserMapper**: MyBatis를 사용하여 데이터베이스와의 상호작용을 담당합니다. `getUserById` 메소드를 통해 특정 사용자의 정보를 데이터베이스에서 조회합니다.

```java
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM public.user WHERE id = #{id}")
    User getUserById(int id);
}
```

2. **UserService**: `UserMapper`를 사용하여 데이터베이스와의 상호작용을 수행하며, Read-Write Lock 패턴을 구현합니다. `getUserById` 메소드에서는 읽기 잠금을 사용하고, `updateUser` 메소드에서는 쓰기 잠금을 사용합니다.

```java
@Service
public class UserService {
    private final UserMapper userMapper;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User getUserById(int id) {
        readWriteLock.readLock().lock();
        try {
            return userMapper.getUserById(id);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    // Assume there's a method to update user
    public void updateUser(User user) {
        readWriteLock.writeLock().lock();
        try {
            // Call the method to update user in the database
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
```

3. **UserController**: `UserService`를 사용하여 HTTP 요청을 처리합니다. `getUserById` 메소드는 `/user/{id}` 경로로 들어오는 GET 요청을 처리하며, 이때 요청 경로의 `{id}` 부분은 메소드의 매개변수로 바인딩됩니다.

```java
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable("id") int id) {
        return userService.getUserById(id);
    }
}
```

이 프로젝트의 핵심 로직은 `UserService`에서 구현된 Read-Write Lock 패턴입니다. 이 패턴은 여러 스레드가 동시에 데이터를 읽을 수 있게 해주지만, 쓰기 작업을 수행하는 동안에는 다른 스레드가 읽거나 쓰는 것을 방지하여 데이터의 일관성을 유지합니다.

이 프로젝트에서는 `java.util.concurrent.locks.ReadWriteLock`을 사용하여 쓰기 작업을 수행하는 동안 다른 스레드가 읽거나 쓰는 것을 방지하고 있습니다. `ReadWriteLock`은 읽기 잠금과 쓰기 잠금을 모두 제공합니다. 쓰기 잠금이 활성화되면 다른 스레드가 읽거나 쓰는 것을 방지합니다.

다음은 `UserService`에서 `updateUser` 메소드를 사용하여 쓰기 작업을 수행하는 예입니다:

```java
@Service
public class UserService {
    private final UserMapper userMapper;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // Assume there's a method to update user
    public void updateUser(User user) {
        readWriteLock.writeLock().lock();
        try {
            // Call the method to update user in the database
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
```

읽고 쓰는 메커니즘은 아래와 같다.
위의 코드에서 `readWriteLock.writeLock().lock();`는 쓰기 잠금을 활성화하고, `readWriteLock.writeLock().unlock();`는 쓰기 잠금을 비활성화합니다. 이 두 줄의 코드 사이에서 수행되는 모든 작업은 쓰기 잠금이 활성화된 상태에서 수행되므로, 이 시간 동안 다른 스레드가 읽거나 쓰는 것을 방지합니다.

`readWriteLock.readLock().lock();` 구문은 읽기 잠금을 획득하는 데 사용됩니다. Java의 `ReadWriteLock` 인터페이스는 읽기와 쓰기 작업을 위한 잠금 메소드 쌍을 제공합니다. 읽기 잠금은 작성자가 없는 한 여러 읽기 스레드에 의해 동시에 보유될 수 있습니다. 쓰기 잠금은 독점적입니다.

`getUserById` 메소드에서 스레드가 `readWriteLock.readLock().lock();`를 호출하면, 읽기 잠금을 요청합니다. 다른 스레드가 쓰기를 하지 않거나 쓰기 잠금을 요청하지 않았다면, 읽기 잠금이 부여되고 스레드는 데이터를 읽을 수 있습니다. 스레드가 현재 쓰기 중이거나 쓰기 잠금을 요청했다면, 읽기 잠금 요청은 쓰기 작업이 완료될 때까지 대기합니다.

`getUserById`를 동시에 호출하는 5개의 스레드가 있을 때, 각 스레드는 읽기 잠금을 요청합니다. 읽기 잠금은 동시에 여러 스레드에 의해 보유될 수 있기 때문에(쓰기 잠금이 보유되지 않은 한), 모든 5개의 스레드는 읽기 잠금을 획득하고 동시에 데이터를 읽을 수 있습니다. 이것이 관찰하는 readLock 현상입니다.

다음은 일어나는 일의 간단한 단계별 분해입니다:
1. 스레드 1이 `getUserById`를 호출하고, 읽기 잠금을 요청하고, 다른 스레드가 쓰지 않기 때문에 잠금을 얻습니다.
2. 스레드 2가 `getUserById`를 호출하고, 읽기 잠금을 요청하고, 다른 스레드가 쓰지 않기 때문에 잠금을 얻습니다.
3. 스레드 3이 `getUserById`를 호출하고, 읽기 잠금을 요청하고, 다른 스레드가 쓰지 않기 때문에 잠금을 얻습니다.
4. 스레드 4가 `getUserById`를 호출하고, 읽기 잠금을 요청하고, 다른 스레드가 쓰지 않기 때문에 잠금을 얻습니다.
5. 스레드 5가 `getUserById`를 호출하고, 읽기 잠금을 요청하고, 다른 스레드가 쓰지 않기 때문에 잠금을 얻습니다.

모든 5개의 스레드는 모두 읽기 잠금을 보유하고 있기 때문에 동시에 데이터를 읽을 수 있습니다. 이 시간 동안 스레드가 쓰기 잠금을 요청했다면, 모든 읽기 잠금이 해제될 때까지 기다려야 합니다.
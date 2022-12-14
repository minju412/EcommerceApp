package com.example.userservice.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;

import io.micrometer.core.annotation.Timed;

@RestController
@RequestMapping("/") // gateway 에서 RewritePath 를 지정했기 때문에 prefix 를 제거한다.
public class UserController {

    @Autowired // 이처럼 직접 주입 받는 것은 권장하지 않는다.
    private Greeting greeting;

    private Environment env;
    private UserService userService;

    @Autowired
    public UserController(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/health-check")
    @Timed(value = "users.status", longTask = true)
    public String status() {
        return String.format("It's Working in User Service"
            + ", port(local.server.port) = " + env.getProperty("local.server.port") // 랜덤으로 할당받은 포트 번호
            + ", port(server.port) = " + env.getProperty("server.port") // 0 (랜덤포트 사용)
            + ", token secret = " + env.getProperty("token.secret") // Configuration Server 에서 가져온 값
            + ", token expiration time = " + env.getProperty("token.expiration_time")  // Configuration Server 에서 가져온 값
        );
    }

    @GetMapping("/welcome")
    @Timed(value = "users.welcome", longTask = true)
    public String welcome() {
        // return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser requestUser) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> userList = userService.getUserByAll();
        List<ResponseUser> result = new ArrayList<>();

        ModelMapper mapper = new ModelMapper();
        userList.forEach(x -> {
            result.add(mapper.map(x, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser responseUser = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }
}

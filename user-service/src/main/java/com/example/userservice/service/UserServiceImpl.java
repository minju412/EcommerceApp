package com.example.userservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    Environment env;
    // RestTemplate restTemplate;

    OrderServiceClient orderServiceClient;

    CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,  // BCryptPasswordEncoder -> UserServiceApplication 에 빈 등록
                           Environment env,
                           OrderServiceClient orderServiceClient,
                           CircuitBreakerFactory circuitBreakerFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.orderServiceClient = orderServiceClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(
            userEntity.getEmail(),
            userEntity.getEncryptedPwd(),
            true,
            true,
            true,
            true,
            new ArrayList<>()
        );
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);

        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        // 주문 생성
        /* Using RestTemplate */
        // String orderUrl = String.format(env.getProperty("order-service.url"), userId);
        // ResponseEntity<List<ResponseOrder>> orderListResponse =
        //     restTemplate.exchange(orderUrl, HttpMethod.GET, null,
        //                           new ParameterizedTypeReference<List<ResponseOrder>>() {});
        // List<ResponseOrder> orderList = orderListResponse.getBody();

        /* Using FeignClient */
        /* Feign exception handling */
        // List<ResponseOrder> orderList = null;
        // try{
        //     orderList = orderServiceClient.getOrders(userId);
        // } catch (FeignException ex) {
        //     log.error(ex.getMessage());
        // }

        /* ErrorDecoder */
        // List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);

        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orderList = circuitbreaker.run(() -> orderServiceClient.getOrders(userId),
            throwable -> new ArrayList<>());

        userDto.setOrders(orderList);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException("email 정보가 없습니다.");
        }

        return new ModelMapper().map(userEntity, UserDto.class); // userEntity 를 UserDto 형태로 변환하여 반환한다.
    }
}

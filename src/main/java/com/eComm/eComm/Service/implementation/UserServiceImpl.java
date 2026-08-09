package com.eComm.eComm.Service.Implementation;

import com.eComm.eComm.Repository.UserRepository;
import com.eComm.eComm.Service.UserService;
import com.eComm.eComm.Entity.UserEntity;
import com.eComm.eComm.io.UserRequest;
import com.eComm.eComm.io.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{

    //Dependency Injection
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request)
    {
        UserEntity newUser = convertToEntity(request);
        newUser = userRepository.save(newUser);
        return covertToResponse(newUser);
    }

    private UserEntity convertToEntity(UserRequest request) {

       return  UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase())
                .name(request.getName())
                .build();
    }


    private UserResponse covertToResponse(UserEntity newUser) {

       return  UserResponse.builder()

               //.name(newUser.getUserId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .userId(newUser.getUserId())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .role(newUser.getRole())
                .build();
    }

    @Override
    public String getUserRole(String email) {
       UserEntity existingUser =  userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("user not found for the email "+email));
       return existingUser.getRole();
    }

    @Override
    public List<UserResponse> readUsers() {

      return   userRepository.findAll()
                .stream()
                .map(user-> covertToResponse(user))
                .collect(Collectors.toList());

    }

    @Override
    public void deleteUser(String id) {

       UserEntity existingUser = userRepository.findByUserId(id)
                .orElseThrow(()-> new UsernameNotFoundException("User not found "));
        userRepository.delete(existingUser);

    }
}

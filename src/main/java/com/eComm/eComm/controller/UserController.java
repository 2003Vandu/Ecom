package com.eComm.eComm.Controller;

import com.eComm.eComm.Service.UserService;
import com.eComm.eComm.io.UserRequest;
import com.eComm.eComm.io.UserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name ="User API")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registeruser(@RequestBody UserRequest request)
    {
        try{
            return userService.createUser(request);
        }catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unable to creat a user");
        }

    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.FOUND)
    public List<UserResponse> readUser()
    {
        return userService.readUsers();
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id )
    {
            try{
                userService.deleteUser(id);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
            }
    }
}

package com.eComm.eComm.Controller;

import com.eComm.eComm.Service.UserService;
import com.eComm.eComm.Service.Implementation.AppUserDetailsService;
import com.eComm.eComm.Utils.JwtUtil;
import com.eComm.eComm.io.AuthRequest;
import com.eComm.eComm.io.AuthResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth API")
public class AuthController
{
    // dependency injection
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;



    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) throws  Exception
    {
        authenticate(request.getEmail(), request.getPassword());
        final UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());
        final String jwtToken = jwtUtil.generateToken(userDetails);
        String role =  userService.getUserRole(request.getEmail());
        return new AuthResponse(request.getEmail(),jwtToken,role);

    }

    private void authenticate(String email, String password) throws Exception
    {
        try {

          authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));

        }catch (DisabledException e)
        {
               throw  new Exception("user Disable ");
        }catch (BadCredentialsException e)
        {
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email or password is incorrect");
        }
    }


    @PostMapping("/encode")
    public String encodePassword(@RequestBody Map<String,String> request)
    {
        return  passwordEncoder.encode(request.get("password"));

    }

}

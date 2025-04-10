package com.sarthakpawar.SERVICES.AUTH;

import com.sarthakpawar.DTO.SignUpRequest;
import com.sarthakpawar.DTO.UserDto;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.ENUMS.UserRole;
import com.sarthakpawar.REPOSITORY.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    private UserRepository userRepository;

   @PostConstruct
    public void createAnAdminAccount(){
        Optional<User> adminAccount=userRepository.findByUserRole(UserRole.ADMIN);
        if(adminAccount.isEmpty()){
            User user=new User();
            user.setEmail("admin@test.com");
            user.setName("Admin");
            user.setUserRole(UserRole.ADMIN);
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            userRepository.save(user);
            System.out.println("Admin Account created successfully");
        }else{
            System.out.println("Admin account already exist");
        }
    }

    public UserDto createUser(SignUpRequest signUpRequest){
        if (userRepository.findFirstByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new EntityExistsException("User already present with Email: " + signUpRequest.getEmail());
        }
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setName(signUpRequest.getName());
        user.setUserRole(UserRole.CUSTOMER);
        user.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        User createdUser = userRepository.save(user);
        return new UserDto(
                createdUser.getId(),
                createdUser.getEmail(),
                createdUser.getName(),
                createdUser.getUserRole()
        );
    }

}

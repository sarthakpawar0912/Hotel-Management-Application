package com.sarthakpawar.SERVICES.JWT;

import com.sarthakpawar.REPOSITORY.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDetailsService userDetailsService(){
        return new  UserDetailsService(){
            @Override
            public UserDetails loadUserByUsername(String username)  throws UsernameNotFoundException {
                return userRepository.findFirstByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
            }
        };
    }

}

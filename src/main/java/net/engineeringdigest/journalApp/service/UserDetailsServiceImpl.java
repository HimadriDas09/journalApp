package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username); // db data
        if (user != null) {

            // bcz Spring Security automatically adds the prefix: "ROLE_" to the role, so removing it and setting it in the userDetail object.

//            String[] rolesArray = user.getRoles().stream().map(role -> role.replace("ROLE_", "")).toArray(String[]::new);

            // below User belongs to userdetails
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRoles().toArray(new String[0])) // roles(String... roles)
                    .build();

            System.out.println("=====> loaded user : " +  user.getUsername() + " roles : " + user.getRoles() + " password : " + user.getPassword());
            System.out.println("Testing match : " + passwordEncoder.matches("sup", user.getPassword()));

            return userDetails;
        }
        throw new UsernameNotFoundException("Username not found with username : " + username);
    }
}

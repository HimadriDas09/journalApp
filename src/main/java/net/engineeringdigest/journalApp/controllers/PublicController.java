package net.engineeringdigest.journalApp.controllers;

import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {
    // public controller will be unauthenticated.
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> test(){
        return new ResponseEntity<>("test working...", HttpStatus.OK);
    }

    @PostMapping("/create-user")
    public void createUser(@RequestBody User user){
        userService.saveNewUser(user);
    }
}

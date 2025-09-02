package net.engineeringdigest.journalApp.controllers;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    JournalEntryService journalEntryService;

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUser(){

        // if user details(sent via header) gets authenticated, then they are saved in the SecurityContextHolder.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("authorities : " + authentication.getAuthorities());
        String username = authentication.getName();
        System.out.println("authentication done in getAllJournalEntriesOfUser()");

        User user = userService.findByUsername(username);

        if(user != null){
            List<JournalEntry> journalEntriesOfUser = user.getJournalEntries();

            if(!journalEntriesOfUser.isEmpty()){
                return new ResponseEntity<>(journalEntriesOfUser, HttpStatus.FOUND);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myEntry){
        // authenticated user is saved in SecurityContextHolder.

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("user authenticated && username : " + username);

        try{
            journalEntryService.saveEntry(myEntry, username);
            return new ResponseEntity<>(HttpStatus.CREATED);

        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId){
        // NOTE: JournalEntry to be returned must belong to user.

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);

        List<JournalEntry> list = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).toList(); // user specific search && if present, it should be 1 entry bcz id is unique.

        if(!list.isEmpty()) {
            // authenticated user has a JournalEntry with id equals myId
            Optional<JournalEntry> result = journalEntryService.findById(myId);

            if(result.isPresent())
                return new ResponseEntity<>(result.get(), HttpStatus.FOUND);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // authenticated user -> must have a journal with id: myId. If found, then only delete from journal_entries && users. If user doesn't have that journal, then we cannot do anything.
    // So either both happens or nothing should happen => Transactional.
    @DeleteMapping("/id/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId){

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            boolean removed = journalEntryService.deleteById(myId, username);
            if(removed) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<?> updateJournalEntryById(
            @PathVariable ObjectId myId,
            @RequestBody JournalEntry newEntry){

        // myId should belong to a Journal + Journal should belong to authenticated user.
        // to update only in journal_entries bcz 'users' contain only DBRef.

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        List<JournalEntry> journalEntries = user.getJournalEntries().stream().filter(journal -> journal.getId().equals(myId)).toList();

        if(!journalEntries.isEmpty()) {
            // user contain the Journal with myId
            Optional<JournalEntry> journalEntryOptional = journalEntryService.findById(myId);

            if(journalEntryOptional.isPresent()) {
                JournalEntry oldEntry = journalEntryOptional.get();

                oldEntry.setTitle(newEntry.getTitle() != null && !(newEntry.getTitle().isEmpty()) ? newEntry.getTitle() : oldEntry.getTitle());

                oldEntry.setContent(newEntry.getContent() != null && !(newEntry.getContent().isEmpty()) ? newEntry.getContent() : oldEntry.getContent());

                journalEntryService.saveEntry(oldEntry);
                return new ResponseEntity<>(oldEntry, HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

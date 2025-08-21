package net.engineeringdigest.journalApp.controllers;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    JournalEntryService journalEntryService;

    @Autowired
    UserService userService;

    @GetMapping("{username}")
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUser(@PathVariable String username){
        User user = userService.findByUsername(username);

        if(user != null){
            List<JournalEntry> journalEntriesOfUser = user.getJournalEntries();

            if(!journalEntriesOfUser.isEmpty()){
                return new ResponseEntity<>(journalEntriesOfUser, HttpStatus.FOUND);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("{username}")
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myEntry, @PathVariable String username){
        // save in journal_entries && save id of entry in journalEntries[] of user
        try{
            boolean res = journalEntryService.saveEntry(myEntry, username);

            if(res) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            }

        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // TODO : changes to be made when adding authentication of user.
    @GetMapping("id/{myId}") // {myId} represent path variable
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId){
        Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);
        if(journalEntry.isPresent()){
            return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // delete journalEntry from journal_entries and also from users
    @DeleteMapping("/id/{username}/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId, @PathVariable String username){
        journalEntryService.deleteById(myId, username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // TODO : PathVariable username will be used for updating JournalEntry in case of authentication.
    @PutMapping("/id/{username}/{myId}")
    public ResponseEntity<?> updateJournalEntryById(
            @PathVariable ObjectId myId,
            @RequestBody JournalEntry newEntry,
            @PathVariable String username){

        // flow : find journal by id: update title, content (but id remains same). So no changes to be made in "users" collection.

        JournalEntry oldEntry = journalEntryService.findById(myId).orElse(null);

        if(oldEntry != null){
            oldEntry.setTitle(newEntry.getTitle() != null && !(newEntry.getTitle().isEmpty()) ? newEntry.getTitle() : oldEntry.getTitle());

            oldEntry.setContent(newEntry.getContent() != null && !(newEntry.getContent().isEmpty()) ? newEntry.getContent() : oldEntry.getContent());

            journalEntryService.saveEntry(oldEntry);
            return new ResponseEntity<>(oldEntry, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

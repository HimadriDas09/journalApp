package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {

    @Autowired
    JournalEntryRepository journalEntryRepository;

    @Autowired
    UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username){
        journalEntry.setDate(LocalDateTime.now());

        User user = userService.findByUsername(username); // authenticated user + present in db (password hashed)

        JournalEntry saved = journalEntryRepository.save(journalEntry); // IMP to first save journalEntry bcz then only we can save its id in journalEntries[] of user.

        user.getJournalEntries().add(saved);

        userService.saveEntry(user);

    }

    public void saveEntry(JournalEntry journalEntry){
        journalEntry.setDate(LocalDateTime.now());

        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll(){
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id){
        return journalEntryRepository.findById(id);
    }

    // Transactional: means all the operations will be treated as a single operation, if any error occurs in between, then we roll back to the prev state (before any operation was performed)
    @Transactional
    public boolean deleteById(ObjectId id, String username){
        boolean removed = false;
        try {
            User user = userService.findByUsername(username);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));

            if(removed){
                userService.saveEntry(user);
                journalEntryRepository.deleteById(id);
            }
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("An error occured while deleting the entry : " + e);
        }
        return removed;
    }
}

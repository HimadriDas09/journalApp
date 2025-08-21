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
    public boolean saveEntry(JournalEntry journalEntry, String username){
        journalEntry.setDate(LocalDateTime.now());

        User user = userService.findByUsername(username);

        if(user != null) {
            JournalEntry saved = journalEntryRepository.save(journalEntry); // IMP to first save journalEntry bcz then only we can save it's id in journalEntries of user.

            user.getJournalEntries().add(saved);
            user.setUsername(null); // explicitly throwing exception.
            userService.saveEntry(user);

            return true;
        }
        return false;
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

    public void deleteById(ObjectId id, String username){
        User user = userService.findByUsername(username);

        if(user != null){
            user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            userService.saveEntry(user);
        }

        journalEntryRepository.deleteById(id);
    }
}

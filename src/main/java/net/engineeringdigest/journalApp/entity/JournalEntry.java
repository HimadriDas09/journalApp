package net.engineeringdigest.journalApp.entity;


import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data // equivalent to Getters, Setters, RequiredArgsConstructor, ToString, EqualsAndHashCode
@NoArgsConstructor
@Document(collection = "journal_entries") // if collection is not created, it gets created.
public class JournalEntry {

    @Id
    private ObjectId id; // ObjectId is a type we can use in mongodb application.
    @NonNull
    private String title;
    private String content;

    private LocalDateTime date;

}

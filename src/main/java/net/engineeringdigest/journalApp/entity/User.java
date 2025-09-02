package net.engineeringdigest.journalApp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private ObjectId id;
    
    @Indexed(unique = true) // field is indexed for faster lookup + field will have unique value.
    @NonNull // lombok: won't let us set null value -> else throws NullPointerException
    private String username;


    @NonNull
    private String password;


    @DBRef // reference of another mongodb document is stored instead of storing the actual document.
    private List<JournalEntry> journalEntries = new ArrayList<>();

    // journalEntries: [DBRef("journal_entries", ObjectId("...........")), DBRef("journal_entries", ObjectId("......")), ]

    // DBRef(lazy = "true") -> to lazily

    // NOTE: we will maintain List<String> to store roles of a User : to know what is he authorized to do
    private List<String> roles;
}

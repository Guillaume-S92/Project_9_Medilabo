package com.medilabo.auth.repository;

import java.util.Optional;

import com.medilabo.auth.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByUsername(String username);
}

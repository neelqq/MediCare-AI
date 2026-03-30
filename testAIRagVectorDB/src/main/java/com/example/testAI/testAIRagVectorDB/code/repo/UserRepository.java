package com.example.testAI.testAIRagVectorDB.code.repo;

import com.example.testAI.testAIRagVectorDB.code.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
package vn.iostar.emailservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.emailservice.entity.Email;

import java.util.Optional;

@Repository
public interface EmailRepository extends MongoRepository<Email, String> {

    // Find by email
    Optional<Email> findByEmail(String email);
}

package vn.iotstart.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.security.Provider;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {
    Optional<Provider> findByCode(String code);
    Optional<Provider> findByName(String name);
}

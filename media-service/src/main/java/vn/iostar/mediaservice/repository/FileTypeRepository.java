package vn.iostar.mediaservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.mediaservice.entity.FileType;

import java.util.Optional;

@Repository
public interface FileTypeRepository extends MongoRepository<FileType, String> {
    Optional<FileType> findByName(String fileTypeName);

    Optional<FileType> findByExtensionContaining(String extension);
}

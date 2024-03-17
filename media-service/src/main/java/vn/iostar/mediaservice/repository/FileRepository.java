package vn.iostar.mediaservice.repository;

import io.micrometer.core.instrument.Tags;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.mediaservice.entity.File;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<File, String> {
    Optional<File> findByRefUrl(String refUrl);
    List<File> findAllByAuthorIdAndTypeId(String authorId, String typeId);
    Page<File> findAllByAuthorIdAndTypeId(String authorId, String typeId, Pageable pageable);

    Page<File> findAllByGroupIdAndTypeId(String userId, String id, Pageable pageable);

    Page<File> findAllByGroupIdAndTypeIdIn(String userId, List<String> ids, Pageable pageable);
}

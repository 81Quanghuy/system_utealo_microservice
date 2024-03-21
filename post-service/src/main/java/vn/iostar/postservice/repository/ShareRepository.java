package vn.iostar.postservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.entity.Share;

import java.util.Collection;
import java.util.List;

@Repository
public interface ShareRepository extends MongoRepository<Share, String> {
    List<Share> findByUserIdAndPrivacyLevelInOrderByCreateAtDesc(String userId,
                                                                 Collection<PrivacyLevel> privacyLevel, Pageable pageable);
}

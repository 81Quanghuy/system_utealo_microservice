package vn.iostar.userservice.config.entityListener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.mapper.UserMapper;
import vn.iostar.userservice.model.UserDocument;
import vn.iostar.userservice.repository.elasticsearch.UsersElasticSearchRepository;

@Component
public class UserEntityListener {
    @Autowired
    private UsersElasticSearchRepository usersElasticSearchRepository;

    @PostPersist
    @PostUpdate
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterSaveOrUpdate(User user) {
        UserDocument userDocument = UserMapper.toUserDocument(user);
        usersElasticSearchRepository.save(userDocument);
    }

    @PostRemove
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterDelete(User user) {
        UserDocument userDocument = UserMapper.toUserDocument(user);
        usersElasticSearchRepository.delete(userDocument);
    }
}

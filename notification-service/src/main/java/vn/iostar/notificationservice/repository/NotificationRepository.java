package vn.iostar.notificationservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.notificationservice.entity.Notification;


import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

}

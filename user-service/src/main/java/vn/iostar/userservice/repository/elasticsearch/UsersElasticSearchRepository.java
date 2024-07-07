package vn.iostar.userservice.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.userservice.model.UserDocument;

@Repository
public interface UsersElasticSearchRepository extends ElasticsearchRepository<UserDocument, String> {
    //tìm kiếm user theo nhiều 3 trường dữ liệu cùng 1 lúc: username, email, phone
    UserDocument findByUserNameOrEmailOrPhone(String userName, String email, String phone);

}

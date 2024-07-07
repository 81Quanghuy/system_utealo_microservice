package vn.iostar.userservice.service.synchronization;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import vn.iostar.model.UserElastic;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.mapper.UserMapper;
import vn.iostar.userservice.model.UserDocument;
import vn.iostar.userservice.repository.elasticsearch.UsersElasticSearchRepository;
import vn.iostar.userservice.repository.jpa.UserRepository;
import vn.iostar.userservice.util.ESUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class UserSynchronizationService {

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private UsersElasticSearchRepository usersElasticSearchRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;


    @PostConstruct
    public void synchronizeData() {
        List<User> users = usersRepository.findAll();
        List<UserDocument> userDocuments = new ArrayList<>();
        for (User user : users) {
            userDocuments.add(UserMapper.toUserDocument(user));
        }
        usersElasticSearchRepository.saveAll(userDocuments);
    }

    private SearchResponse<UserDocument> autoSuggestProduct(String partialProductName) throws IOException {

        Supplier<Query> supplier = ESUtil.createSupplierAutoSuggest(partialProductName);
        SearchResponse<UserDocument> searchResponse  = elasticsearchClient
                .search(s->s.index("users").query(supplier.get()), UserDocument.class);
        System.out.println(" elasticsearch auto suggestion query"+supplier.get().toString());
        return searchResponse;
    }
    public List<UserElastic> autoSuggestUserSearch (String partialProductName) throws IOException {
        if (partialProductName == null || partialProductName.isEmpty()) {
            return new ArrayList<>();
        }
        SearchResponse<UserDocument> searchResponse = autoSuggestProduct(partialProductName);
        List<Hit<UserDocument>> hitList = searchResponse.hits().hits();
        List<UserElastic> userList = new ArrayList<>();
        for (Hit<UserDocument> hit : hitList) {
            userList.add(UserMapper.toUserElastic(hit.source()));
        }
        return userList;
    }
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;


}

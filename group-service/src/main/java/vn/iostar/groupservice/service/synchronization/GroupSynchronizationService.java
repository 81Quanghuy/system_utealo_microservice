package vn.iostar.groupservice.service.synchronization;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.mapper.GroupMapper;
import vn.iostar.groupservice.model.GroupDocument;
import vn.iostar.groupservice.repository.elasticsearch.GroupElasticSearchRepository;
import vn.iostar.groupservice.repository.jpa.GroupRepository;
import vn.iostar.groupservice.util.ESUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class GroupSynchronizationService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupElasticSearchRepository groupElasticSearchRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;


    @PostConstruct
    public void synchronizeData() {
        List<Group> groups = groupRepository.findAll();
        List<GroupDocument> groupDocuments = new ArrayList<>();
        for (Group group : groups) {
            groupDocuments.add(GroupMapper.toGroupDocument(group));
        }
        groupElasticSearchRepository.saveAll(groupDocuments);
    }

    private SearchResponse<GroupDocument> autoSuggestProduct(String partialProductName) throws IOException {

        Supplier<Query> supplier = ESUtil.createSupplierAutoSuggest(partialProductName);
        SearchResponse<GroupDocument> searchResponse  = elasticsearchClient
                .search(s->s.index("groups").query(supplier.get()), GroupDocument.class);
        System.out.println(" elasticsearch auto suggestion query"+supplier.get().toString());
        return searchResponse;
    }
    public List<GroupDocument> autoSuggestUserSearch (String partialProductName) throws IOException {
        if (partialProductName == null || partialProductName.isEmpty()) {
            return new ArrayList<>();
        }
        SearchResponse<GroupDocument> searchResponse = autoSuggestProduct(partialProductName);
        List<Hit<GroupDocument>> hitList = searchResponse.hits().hits();
        List<GroupDocument> userList = new ArrayList<>();
        for (Hit<GroupDocument> hit : hitList) {
            userList.add(hit.source());
        }
        return userList;
    }
}

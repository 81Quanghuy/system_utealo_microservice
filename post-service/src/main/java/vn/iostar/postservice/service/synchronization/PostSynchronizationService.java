package vn.iostar.postservice.service.synchronization;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iostar.model.PostElastic;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.mapper.PostMapper;
import vn.iostar.postservice.model.PostDocument;
import vn.iostar.postservice.repository.elasticsearch.PostElasticSearchRepository;
import vn.iostar.postservice.repository.jpa.PostRepository;
import vn.iostar.postservice.util.ESUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class PostSynchronizationService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostElasticSearchRepository postElasticSearchRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;


    @PostConstruct
    public void synchronizeData() {
        List<Post> posts = postRepository.findAll();
        List<PostDocument> postDocumentList = new ArrayList<>();
        for (Post post : posts) {
            postDocumentList.add(PostMapper.toPostDocument(post));
        }
        postElasticSearchRepository.saveAll(postDocumentList);
    }

    private SearchResponse<PostDocument> autoSuggestProduct(String partialProductName) throws IOException {

        Supplier<Query> supplier = ESUtil.createSupplierAutoSuggest(partialProductName);
        SearchResponse<PostDocument> searchResponse  = elasticsearchClient
                .search(s->s.index("posts").query(supplier.get()), PostDocument.class);
        System.out.println(" elasticsearch auto suggestion query"+supplier.get().toString());
        return searchResponse;
    }
    public List<PostElastic> autoSuggestUserSearch (String partialProductName) throws IOException {
        if (partialProductName == null || partialProductName.isEmpty()) {
            return new ArrayList<>();
        }
        SearchResponse<PostDocument> searchResponse = autoSuggestProduct(partialProductName);
        List<Hit<PostDocument>> hitList = searchResponse.hits().hits();
        List<PostElastic> userList = new ArrayList<>();
        for (Hit<PostDocument> hit : hitList) {
            assert hit.source() != null;
            userList.add(PostMapper.toPostElastic(hit.source()));
        }
        return userList;
    }


}

package vn.iostar.postservice.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.model.PostDocument;

@Repository
public interface PostElasticSearchRepository extends ElasticsearchRepository<PostDocument, String> {


}

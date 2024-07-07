package vn.iostar.groupservice.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.groupservice.model.GroupDocument;

@Repository
public interface GroupElasticSearchRepository extends ElasticsearchRepository<GroupDocument, String> {

}

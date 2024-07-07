package vn.iostar.groupservice.util;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ESUtil {
    public static Supplier<Query> createSupplierAutoSuggest(String partialProductName){
        return () -> {
            BoolQuery.Builder boolQuery = QueryBuilders.bool();
                boolQuery.should(QueryBuilders.wildcard(w -> w
                        .field("postGroupName")
                        .value("*" + partialProductName + "*")
                        .caseInsensitive(true)
                ));
            return boolQuery.build()._toQuery();
        };
    }
}

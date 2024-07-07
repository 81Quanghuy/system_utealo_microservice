package vn.iostar.postservice.util;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ESUtil {
    public static final String NAME_PATTERN = "^[\\p{L}\\s]+";
    public static Matcher matcher = null;
    public static Pattern pattern = null;
    public static boolean isName(String name) {
        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(name);
        return matcher.matches();
    }
    public static Supplier<Query> createSupplierAutoSuggest(String partialProductName){
        return () -> {
            BoolQuery.Builder boolQuery = QueryBuilders.bool();
                boolQuery.should(QueryBuilders.wildcard(w -> w
                        .field("content")
                        .value("*" + partialProductName + "*")
                        .caseInsensitive(true)
                ));
                if( isName(partialProductName)){
                    boolQuery.should(QueryBuilders.matchPhrase(m -> m
                            .field("location")
                            .query(partialProductName)
                    ));
                }
            return boolQuery.build()._toQuery();
        };
    }
}

package vn.iostar.userservice.util;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.val;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ESUtil {
    public static final String VALID_EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";
    public static final String NAME_PATTERN = "^[\\p{L}\\s]+";
    public static Matcher matcher = null;
    public static Pattern pattern = null;
    public static boolean isEmail(String email) {
        pattern = Pattern.compile(VALID_EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean isName(String name) {
        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(name);
        return matcher.matches();
    }
    public static Supplier<Query> createSupplierAutoSuggest(String partialProductName){

        return () -> {
            BoolQuery.Builder boolQuery = QueryBuilders.bool();
            if (isName(partialProductName)) {
                boolQuery.should(QueryBuilders.wildcard(w -> w
                        .field("userName")
                        .value("*" + partialProductName + "*")
                        .caseInsensitive(true)
                ));
            }
            boolQuery.should(QueryBuilders.matchPhrase(m -> m
                    .field("phone")
                    .query(partialProductName)
            ));
            if(isEmail(partialProductName)){
                boolQuery.should(QueryBuilders.matchPhrase(m -> m
                        .field("email")
                        .query(partialProductName)
                ));
            }
            return boolQuery.build()._toQuery();
        };
    }
}

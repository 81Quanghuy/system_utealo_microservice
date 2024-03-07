package vn.iostar.userservice.util;

import vn.iostar.userservice.entity.Token;

public class TokenUtil {

    public static boolean tokenIsExpiredOrRevoked(Token token) {
        if (token == null)
            return false;
        return token.getIsRevoked() || token.getIsExpired();
    }
}

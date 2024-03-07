package vn.iostar.groupservice.dto.request;

import lombok.Data;

@Data
public class LockMemberRequest {

    private String groupMemberId;
    private String lockedReason;

}

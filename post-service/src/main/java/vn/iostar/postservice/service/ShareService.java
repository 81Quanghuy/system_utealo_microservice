package vn.iostar.postservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.GenericResponseAdmin;
import vn.iostar.postservice.dto.SharePostRequestDTO;
import vn.iostar.postservice.dto.response.SharesResponse;
import vn.iostar.postservice.entity.Share;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ShareService {
    <S extends Share> S save(S entity);
    Optional<Share> findById(String id);
    ResponseEntity<Object> sharePost(String token, SharePostRequestDTO requestDTO);
    ResponseEntity<Object> updateSharePost(SharePostRequestDTO requestDTO, String currentUserId);
    ResponseEntity<GenericResponse> deleteSharePost(String shareId, String token, String userId);
    List<SharesResponse> findUserSharePosts(String currentUserId, String userId, Pageable pageable);
    SharesResponse getSharePost(Share share, String currentUserId);
    // Lấy tất cả bài Shares trong hệ thống
    ResponseEntity<GenericResponseAdmin> getAllShares(String authorizationHeader, int page, int itemsPerPage);
    // Tìm tất cả bài Shares trong hệ thống
    Page<SharesResponse> findAllShares(int page, int itemsPerPage);
    // Admin xóa bài Shares trong hệ thống
    ResponseEntity<GenericResponse> deleteShareByAdmin(String shareId, String authorizationHeader);
    // Thống kê bài Shares trong ngày hôm nay
    List<SharesResponse> getSharesToday();
    // Thống kê bài Shares trong 1 ngày
    List<SharesResponse> getSharesInDay(Date day);
    // Thống kê bài Shares trong 7 ngày
    List<SharesResponse> getSharesIn7Days();
    // Thống kê bài Shares trong 1 tháng
    List<SharesResponse> getSharesIn1Month();
    // Đếm số lượng bài Shares trong ngày hôm nay
    long countSharesToday();
    // Đếm số lượng bài Shares trong 7 ngày
    public long countSharesInWeek();
    // Đếm số lượng bài Shares trong 1 tháng
    long countSharesInMonthFromNow();
    // Đếm số lượng bài Shares trong 1 năm
    long countSharesInOneYearFromNow();
    // Đếm số lượng bài Shares trong 9 tháng
    long countSharesInNineMonthsFromNow();
    // Đếm số lượng bài Shares trong 6 tháng
    long countSharesInSixMonthsFromNow();
    // Đếm số lượng bài Shares trong 3 tháng
    long countSharesInThreeMonthsFromNow();
    // Đếm số lượng bài Shares từng tháng trong năm
    Map<String, Long> countSharesByMonthInYear();
    // Đếm số lượng bài Shares của user từng tháng trong năm
    Map<String, Long> countSharesByUserMonthInYear(String userId);
    // Lấy tất cả bài share của 1 user có phân trang
    Page<SharesResponse> findAllSharesByUserId(int page, int itemsPerPage, String userId);
    // Lấy tất cả bài share của 1 user trong 1 tháng có phân trang
    Page<SharesResponse> findAllSharesInMonthByUserId(int page, int itemsPerPage, String userId);
    // Xem chi tiết bài share
    ResponseEntity<GenericResponse> getShare(String currentUserId, String shareId);
    // Lấy tất cả bài share của 1 user
    List<SharesResponse> findMySharePosts(String currentUserId, Pageable pageable);
    // Lấy những bài share liên quan đến mình như: nhóm, bạn bè, cá nhân
    ResponseEntity<GenericResponse> getTimeLineSharePosts(String currentUserId, Integer page, Integer size);
    // Lấy tất cả các bài share của những nhóm mình tham gia
    ResponseEntity<GenericResponse> getShareOfPostGroup(String currentUserId, Pageable pageable);
    // Lấy những bài share của group theo id
    ResponseEntity<GenericResponse> getGroupSharePosts(String currentUserId, String postGroupId, Integer page,
                                                       Integer size);

}

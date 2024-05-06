package vn.iostar.postservice.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.postservice.dto.CountDTO;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.GenericResponseAdmin;
import vn.iostar.postservice.dto.PaginationInfo;
import vn.iostar.postservice.dto.response.SharesResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.repository.ShareRepository;
import vn.iostar.postservice.service.ShareService;
import vn.iostar.postservice.service.client.UserClientService;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/share/admin")
public class ShareManagerController {

    private final ShareService shareService;
    private final UserClientService userClientService;
    private final ShareRepository shareRepository;

    // Lấy tất cả bài share post trong hệ thống
    @GetMapping("/list")
    public ResponseEntity<GenericResponseAdmin> getAllShares(@RequestHeader("Authorization") String authorizationHeader,
                                                             @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int items) {
        return shareService.getAllShares(authorizationHeader, page, items);
    }

    // Xóa bài share post trong hệ thống
    @DeleteMapping("/delete/{shareId}")
    public ResponseEntity<GenericResponse> deleteShare(@RequestHeader("Authorization") String authorizationHeader,
                                                      @PathVariable("shareId") String shareId) {
        return shareService.deleteShareByAdmin(shareId, authorizationHeader);
    }

    // Thống kê bài share post trong ngày hôm nay
    // Thống kê bài share post trong 1 ngày
    // Thống kê bài share post trong 7 ngày
    // Thống kê bài share post trong 1 tháng
    @GetMapping("/filterByDate")
    public List<SharesResponse> getShares(@RequestParam(required = false) String action,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        switch (action != null ? action.toLowerCase() : "") {
            case "today":
                return shareService.getSharesToday();
            case "day":
                if (date != null) {
                    return shareService.getSharesInDay(date);
                }
                break;
            case "7days":
                return shareService.getSharesIn7Days();
            case "month":
                return shareService.getSharesIn1Month();
            default:
                // Nếu không có action hoặc action không hợp lệ, có thể trả về thông báo lỗi
                // hoặc một giá trị mặc định
                break;
        }
        // Trả về null hoặc danh sách rỗng tùy theo logic của bạn
        return null;
    }

    // Đếm số lượng bài share post
    @GetMapping("/countShare")
    public ResponseEntity<CountDTO> countPostsToday() {
        try {
            long shareCountToDay = shareService.countSharesToday();
            long shareCountInWeek = shareService.countSharesInWeek();
            long shareCountIn1Month = shareService.countSharesInMonthFromNow();
            long shareCountIn3Month = shareService.countSharesInThreeMonthsFromNow();
            long shareCountIn6Month = shareService.countSharesInSixMonthsFromNow();
            long shareCountIn9Month = shareService.countSharesInNineMonthsFromNow();
            long shareCountIn1Year = shareService.countSharesInOneYearFromNow();

            CountDTO shareCountDTO = new CountDTO(shareCountToDay, shareCountInWeek, shareCountIn1Month,
                    shareCountIn3Month, shareCountIn6Month, shareCountIn9Month, shareCountIn1Year);
            return ResponseEntity.ok(shareCountDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Đếm số lượng bài share post từng tháng trong năm
    @GetMapping("/countSharesByMonthInYear")
    public ResponseEntity<Map<String, Long>> countSharesByMonthInYear() {
        try {
            Map<String, Long> shareCountsByMonth = shareService.countSharesByMonthInYear();
            return ResponseEntity.ok(shareCountsByMonth);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Đếm số lượng bài share post của user từng tháng trong năm
    @GetMapping("/countSharesByMonthInYear/{userId}")
    public ResponseEntity<Map<String, Long>> countSharesByUserMonthInYear(@PathVariable("userId") String userId) {
        try {
            Map<String, Long> shareCountsByMonth = shareService.countSharesByUserMonthInYear(userId);
            return ResponseEntity.ok(shareCountsByMonth);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Cập nhật controller để lấy danh sách tất cả bài share của một userId cụ thể
    @GetMapping("/listShare/{userId}")
    public ResponseEntity<GenericResponseAdmin> getAllSharesByUserId(@RequestParam(defaultValue = "1") int page,
                                                                     @RequestParam(defaultValue = "10") int items, @PathVariable("userId") String userId) {

        // Sử dụng shareService để lấy danh sách tất cả bài share của một userId
        Page<SharesResponse> userSharesPage = shareService.findAllSharesByUserId(page, items, userId);

        UserProfileResponse user = userClientService.getUser(userId);
        if (user != null) {
            long totalShares = shareRepository.countSharesByUserId(user.getUserId());

            PaginationInfo pagination = new PaginationInfo();
            pagination.setPage(page);
            pagination.setItemsPerPage(items);
            pagination.setCount(totalShares);
            pagination.setPages((int) Math.ceil((double) totalShares / items));

            if (userSharesPage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                        .message("No Shares Found for User").statusCode(HttpStatus.NOT_FOUND.value()).build());
            } else {
                return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
                        .message("Retrieved List Shares Successfully").result(userSharesPage).pagination(pagination)
                        .statusCode(HttpStatus.OK.value()).build());
            }
        } else {
            // Xử lý trường hợp không tìm thấy User
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                    .message("User Not Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
    }

    // Cập nhật controller để lấy danh sách tất cả bài share của một userId trong 1
    // tháng
    @GetMapping("/listShareInMonthPage/{userId}")
    public ResponseEntity<GenericResponseAdmin> getAllSharesInMonthByUserId(@RequestParam(defaultValue = "1") int page,
                                                                            @RequestParam(defaultValue = "5") int items, @PathVariable("userId") String userId) {
        // Sử dụng shareService để lấy danh sách tất cả bài share của một userId
        Page<SharesResponse> userSharesPage = shareService.findAllSharesInMonthByUserId(page, items, userId);
        UserProfileResponse user = userClientService.getUser(userId);
        if (user != null) {

            long totalShares = shareRepository.countSharesByUserId(user.getUserId());

            PaginationInfo pagination = new PaginationInfo();
            pagination.setPage(page);
            pagination.setItemsPerPage(items);
            pagination.setCount(totalShares);
            pagination.setPages((int) Math.ceil((double) totalShares / items));

            if (userSharesPage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                        .message("No Shares Found for User").statusCode(HttpStatus.NOT_FOUND.value()).build());
            } else {
                return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
                        .message("Retrieved List Shares Successfully").result(userSharesPage).pagination(pagination)
                        .statusCode(HttpStatus.OK.value()).build());
            }
        } else {
            // Xử lý trường hợp không tìm thấy User
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                    .message("User Not Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
    }

    // Cập nhật controller để lấy danh sách tất cả bài share của một userId trong 1
    // tháng không có phân trang
    @GetMapping("/listShareInMonth/{userId}")
    public ResponseEntity<GenericResponseAdmin> getAllSharesInMonthByUserId(@RequestParam(defaultValue = "1") int page,
                                                                            @PathVariable("userId") String userId) {
        UserProfileResponse user = userClientService.getUser(userId);
        if (user != null) {
            // Tính tổng số lượng bài share của người dùng trong tháng
            long totalShares = shareRepository.countSharesByUserId(user.getUserId());

            // Kiểm tra nếu totalShares là 0, trả về result rỗng
            if (totalShares == 0) {
                PaginationInfo pagination = new PaginationInfo();
                pagination.setPage(page);
                pagination.setItemsPerPage(0); // Số lượng mục trên trang là 0 khi không có bài share
                pagination.setCount(0);
                pagination.setPages(0);

                return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
                        .message("No Shares Found for User").result("")
                        .pagination(pagination).statusCode(HttpStatus.OK.value()).build());
            }

            // Sử dụng totalShares cho items để lấy tất cả bài share trong một trang
            Page<SharesResponse> userSharesPage = shareService.findAllSharesInMonthByUserId(page, (int) totalShares,
                    userId);

            PaginationInfo pagination = new PaginationInfo();
            pagination.setPage(page);
            pagination.setItemsPerPage((int) totalShares);
            pagination.setCount(totalShares);
            pagination.setPages(1); // Chỉ có 1 trang

            if (userSharesPage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                        .message("No Shares Found for User").statusCode(HttpStatus.NOT_FOUND.value()).build());
            } else {
                return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
                        .message("Retrieved List Shares Successfully").result(userSharesPage).pagination(pagination)
                        .statusCode(HttpStatus.OK.value()).build());
            }
        } else {
            // Xử lý trường hợp không tìm thấy User
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                    .message("User Not Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
    }

}

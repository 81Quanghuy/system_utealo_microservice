package vn.iostar.postservice.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.postservice.dto.CountDTO;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.GenericResponseAdmin;
import vn.iostar.postservice.dto.PaginationInfo;
import vn.iostar.postservice.dto.request.CreateCommentPostRequestDTO;
import vn.iostar.postservice.dto.response.CommentsResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.repository.CommentRepository;
import vn.iostar.postservice.service.CommentService;
import vn.iostar.postservice.service.client.UserClientService;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/commentManager")
public class CommentManagerController {

    private final CommentService commentService;
    private final UserClientService userClientService;
    private final CommentRepository commentRepository;

    // Lấy tất cả comment trong hệ thống
    @GetMapping("/list")
    public ResponseEntity<GenericResponseAdmin> getAllComments(
            @RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int items) {
        return commentService.getAllComments(authorizationHeader, page, items);
    }

    // Admin xóa comment trong hệ thống
    @PutMapping("/delete/{commentId}")
    public ResponseEntity<GenericResponse> deleteCommentOfPost(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable("commentId") String commentId) {
        return commentService.deleteCommentByAdmin(commentId, authorizationHeader);
    }

    // Thêm comment
    @PostMapping("/create")
    public ResponseEntity<Object> createCommentPost(@ModelAttribute CreateCommentPostRequestDTO requestDTO,
                                                    @RequestHeader("Authorization") String token) {
        return commentService.createCommentPost(token, requestDTO);
    }

    // Đếm số lượng comment từng tháng trong năm
    @GetMapping("/countCommentsByMonthInYear")
    public ResponseEntity<Map<String, Long>> countComemntsByMonthInYear() {
        try {
            Map<String, Long> commentCountsByMonth = commentService.countCommentsByMonthInYear();
            return ResponseEntity.ok(commentCountsByMonth);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Đếm số lượng comment
    @GetMapping("/countComment")
    public ResponseEntity<CountDTO> countCommentsToday() {
        try {
            long commentCountIn1Year = commentService.countCommentsInOneYearFromNow();

            CountDTO groupCountDTO = new CountDTO(0, 0, 0, 0, 0, 0, commentCountIn1Year);
            return ResponseEntity.ok(groupCountDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Thống kê bài post trong ngày hôm nay
    // Thống kê bài post trong 1 ngày
    // Thống kê bài post trong 7 ngày
    // Thống kê bài post trong 1 tháng
    @GetMapping("/filterByDate")
    public List<CommentsResponse> getPosts(@RequestParam(required = false) String action,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        switch (action != null ? action.toLowerCase() : "") {
            case "today":
                return commentService.getCommentsToday();
            case "7days":
                return commentService.getCommentsIn7Days();
            case "month":
                return commentService.getCommentsIn1Month();
            default:
                // Nếu không có action hoặc action không hợp lệ, có thể trả về thông báo lỗi
                // hoặc một giá trị mặc định
                break;
        }
        // Trả về null hoặc danh sách rỗng tùy theo logic của bạn
        return null;
    }

    // Cập nhật controller để lấy danh sách tất cả bình luận của một userId cụ thể
    @GetMapping("/listComment/{userId}")
    public ResponseEntity<GenericResponseAdmin> getAllCommentsByUserId(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "10") int items, @PathVariable("userId") String userId) {
        // Sử dụng postService để lấy danh sách tất cả bài post của một userId
        Streamable<Object> userCommentsPage = commentService.findAllCommentsByUserId(page, items, userId);

        UserProfileResponse userProfileResponse = userClientService.getUser(userId);
        if (userProfileResponse != null) {
            long totalComments = commentRepository.countCommentsByUserId(userProfileResponse.getUserId());

            PaginationInfo pagination = new PaginationInfo();
            pagination.setPage(page);
            pagination.setItemsPerPage(items);
            pagination.setCount(totalComments);
            pagination.setPages((int) Math.ceil((double) totalComments / items));

            if (userCommentsPage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                        .message("No Comments Found for User").statusCode(HttpStatus.NOT_FOUND.value()).build());
            } else {
                return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
                        .message("Retrieved List Comments Successfully").result(userCommentsPage).pagination(pagination)
                        .statusCode(HttpStatus.OK.value()).build());
            }
        } else {
            // Xử lý trường hợp không tìm thấy User
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                    .message("User Not Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
    }

    // Cập nhật controller để lấy danh sách tất cả bình luận của một userId cụ thể
    @GetMapping("/listCommentInMonth/{userId}")
    public ResponseEntity<GenericResponseAdmin> getAllCommentsInMonthByUserId(
            @RequestParam(defaultValue = "1") int page, @PathVariable("userId") String userId) {
        UserProfileResponse userProfileResponse = userClientService.getUser(userId);
        if (userProfileResponse != null) {
            // Tính tổng số lượng bình luận của người dùng trong tháng
            long totalComments = commentRepository.countCommentsByUserId(userProfileResponse.getUserId());

            // Kiểm tra nếu totalShares là 0, trả về result rỗng
            if (totalComments == 0) {
                PaginationInfo pagination = new PaginationInfo();
                pagination.setPage(page);
                pagination.setItemsPerPage(0); // Số lượng mục trên trang là 0 khi không có bài share
                pagination.setCount(0);
                pagination.setPages(0);

                return ResponseEntity.ok(GenericResponseAdmin.builder().success(true).message("No Comments Found for User")
                        .result("").pagination(pagination).statusCode(HttpStatus.OK.value()).build());
            }

            // Sử dụng totalComments cho items để lấy tất cả bình luận trong một trang
            Streamable<Object> userCommentsPage = commentService.findAllCommentsByUserId(page, (int) totalComments,
                    userId);

            PaginationInfo pagination = new PaginationInfo();
            pagination.setPage(page);
            pagination.setItemsPerPage((int) totalComments);
            pagination.setCount(totalComments);
            pagination.setPages(1); // Chỉ có 1 trang

            if (userCommentsPage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                        .message("No Comments Found for User").statusCode(HttpStatus.NOT_FOUND.value()).build());
            } else {
                return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
                        .message("Retrieved List Comments Successfully").result(userCommentsPage).pagination(pagination)
                        .statusCode(HttpStatus.OK.value()).build());
            }
        } else {
            // Xử lý trường hợp không tìm thấy User
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                    .message("User Not Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
    }

    // Đếm số lượng comment từng tháng trong năm
    @GetMapping("/countCommentsByMonthInYear/{userId}")
    public ResponseEntity<Map<String, Long>> countCommentsByUserMonthInYear(@PathVariable("userId") String userId) {
        try {
            Map<String, Long> commentCountsByMonth = commentService.countCommentsByUserMonthInYear(userId);
            return ResponseEntity.ok(commentCountsByMonth);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

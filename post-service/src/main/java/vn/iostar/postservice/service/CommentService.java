package vn.iostar.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.util.Streamable;
import org.springframework.http.ResponseEntity;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.GenericResponseAdmin;
import vn.iostar.postservice.dto.request.*;
import vn.iostar.postservice.dto.response.CommentsResponse;
import vn.iostar.postservice.entity.Comment;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentService {
    <S extends Comment> S save(S entity);
    Optional<Comment> findById(String id);
    ResponseEntity<GenericResponse> getCommentOfPost(String postId) throws JsonProcessingException;
    ResponseEntity<Object> createCommentPost(String token, CreateCommentPostRequestDTO requestDTO);
    ResponseEntity<Object> replyCommentPost(String token, ReplyCommentPostRequestDTO requestDTO);
    ResponseEntity<Object> updateComment(String commentId, CommentUpdateRequest request, String currentUserId)
            throws Exception;
    ResponseEntity<GenericResponse> deleteCommentOfPost(String commentId);
    ResponseEntity<GenericResponse> getCommentReplyOfComment(String commentId) throws JsonProcessingException;
    ResponseEntity<GenericResponse> getCountCommentOfPost(String postId);
    ResponseEntity<GenericResponse> getCommentOfShare(String shareId);
    ResponseEntity<GenericResponse> getCommentReplyOfCommentShare(String commentId);
    ResponseEntity<Object> createCommentShare(String token, CreateCommentShareRequestDTO requestDTO);
    ResponseEntity<Object> replyCommentShare(String token, ReplyCommentShareRequestDTO requestDTO);
    ResponseEntity<GenericResponseAdmin> getAllComments(String authorizationHeader, int page, int itemsPerPage);
    // Lấy tất cả bài post trong hệ thống
    Streamable<Object> findAllComments(int page, int itemsPerPage);
    // Admin xóa comment trong hệ thống
    ResponseEntity<GenericResponse> deleteCommentByAdmin(String commentId, String authorizationHeader);
    // Đếm số lượng comment từng tháng trong năm
    Map<String, Long> countCommentsByMonthInYear();
    // Đếm số lượng comment trong 1 năm
    long countCommentsInOneYearFromNow();
    // Thống kê bình luận trong ngày hôm nay
    List<CommentsResponse> getCommentsToday();
    // Thống kê bình luận trong 7 ngày
    List<CommentsResponse> getCommentsIn7Days();
    // Thống kê bình luận trong 1 tháng
    List<CommentsResponse> getCommentsIn1Month();
    // Lấy tất cả bình luận của 1 user có phân trang
    Streamable<Object> findAllCommentsByUserId(int page, int itemsPerPage, String userId);
    // Đếm số lượng comment của 1 user từng tháng trong năm
    Map<String, Long> countCommentsByUserMonthInYear(String userId);
    // Đếm số lượng comment của user
    Long countCommentsByUserId(String userId, Date start, Date end);
}

package vn.iostar.postservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.GenericResponseAdmin;
import vn.iostar.postservice.dto.request.CreatePostRequestDTO;
import vn.iostar.postservice.dto.request.PostUpdateRequest;
import vn.iostar.postservice.dto.response.PostsResponse;
import vn.iostar.postservice.entity.Post;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostService {

    <S extends Post> S save(S entity);
    Optional<Post> findById(String id);
    // Tạo bài post
    ResponseEntity<Object> createUserPost(String token, CreatePostRequestDTO requestDTO);
    // Xóa bài post của mình
    ResponseEntity<GenericResponse> deletePost(String postId, String token, String userId);
    // Sửa bài post của mình
    ResponseEntity<Object> updatePost(String postId, PostUpdateRequest request, String currentUserId) throws Exception;
    // Xem chi tiết bài post
    ResponseEntity<GenericResponse> getPost(String userIdToken, String postId);
    // Lấy những bài post của mình
    List<PostsResponse> findUserPosts(String currentUserId, String userId, Pageable pageable);
    // Lấy tất cả hình của user đó
    List<String> findAllPhotosByUserIdOrderByPostTimeDesc(String userId);
    // Lấy 9 hình đầu tiên của user
    ResponseEntity<Object> findLatestPhotosByUserId(String currentUserId, String userId, Pageable pageable);
    // Lấy tất cả bài post trong hệ thống
    ResponseEntity<GenericResponseAdmin> getAllPosts(String authorizationHeader, int page, int itemsPerPage);
    // Tìm tất cả bài post trong hệ thống
    Page<PostsResponse> findAllPosts(int page, int itemsPerPage);
    // Admin xóa bài post trong hệ thống
    ResponseEntity<GenericResponse> deletePostByAdmin(String postId, String authorizationHeader);
    // Thống kê bài post trong ngày hôm nay
    List<PostsResponse> getPostsToday();
    // Thống kê bài post trong 1 ngày
    List<PostsResponse> getPostsInDay(Date day);
    // Thống kê bài post trong 7 ngày
    List<PostsResponse> getPostsIn7Days();
    // Thống kê bài post trong 1 tháng
    List<PostsResponse> getPostsIn1Month();
    // Đếm số lượng bài post trong ngày hôm nay
    long countPostsToday();
    // Đếm số lượng bài post trong 7 ngày
    public long countPostsInWeek();
    // Đếm số lượng bài post trong 1 tháng
    long countPostsInMonthFromNow();
    // Đếm số lượng bài post trong 1 năm
    long countPostsInOneYearFromNow();
    // Đếm số lượng bài post trong 9 tháng
    long countPostsInNineMonthsFromNow();
    // Đếm số lượng bài post trong 6 tháng
    long countPostsInSixMonthsFromNow();
    // Đếm số lượng bài post trong 3 tháng
    long countPostsInThreeMonthsFromNow();
    // Đếm số lượng bài post của user từng tháng trong năm
    Map<String, Long> countPostsByUserMonthInYear(String userId);
    // Đếm số lượng bài post từng tháng trong năm
    Map<String, Long> countPostsByMonthInYear();
    // Lấy tất cả bài post của 1 user có phân trang
    Page<PostsResponse> findAllPostsByUserId(int page, int itemsPerPage, String userId);
    // Lấy tất cả bài post của 1 user trong 1 tháng có phân trang
    Page<PostsResponse> findAllPostsInMonthByUserId(int page, int itemsPerPage, String userId);


}

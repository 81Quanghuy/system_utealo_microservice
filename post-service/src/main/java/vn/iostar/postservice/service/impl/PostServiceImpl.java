package vn.iostar.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.constant.RoleName;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.GenericResponseAdmin;
import vn.iostar.postservice.dto.PaginationInfo;
import vn.iostar.postservice.dto.request.CreatePostRequestDTO;
import vn.iostar.postservice.dto.request.PostUpdateRequest;
import vn.iostar.postservice.dto.response.*;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.entity.Share;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.repository.CommentRepository;
import vn.iostar.postservice.repository.LikeRepository;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.repository.ShareRepository;
import vn.iostar.postservice.service.CloudinaryService;
import vn.iostar.postservice.service.PostService;
import vn.iostar.postservice.service.client.GroupClientService;
import vn.iostar.postservice.service.client.UserClientService;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final JwtService jwtService;
    private final UserClientService userClientService;
    private final GroupClientService groupClientService;
    private final CloudinaryService cloudinaryService;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final ShareRepository shareRepository;

    @Override
    public <S extends Post> S save(S entity) {
        return postRepository.save(entity);
    }

    @Override
    public Optional<Post> findById(String id) {
        return postRepository.findById(id);
    }

    @Override
    public ResponseEntity<Object> createUserPost(String token, CreatePostRequestDTO requestDTO) {

        List<String> allowedFileExtensions = Arrays.asList("docx", "txt", "pdf");

        if (requestDTO.getLocation() == null && requestDTO.getContent() == null) {
            return ResponseEntity.badRequest().body("Please provide all required fields.");
        }

        String accessToken = token.substring(7);
        String userId = jwtService.extractUserId(accessToken);

        // Tạo một đối tượng Post từ dữ liệu trong DTO
        String postId = UUID.randomUUID().toString();
        Post post = new Post();
        post.setId(postId);
        post.setLocation(requestDTO.getLocation());
        post.setContent(requestDTO.getContent());
        post.setPrivacyLevel(requestDTO.getPrivacyLevel());

        try {
            if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
                post.setPhotos("");
            } else {
                post.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
            }
            if (requestDTO.getFiles() == null || requestDTO.getFiles().getContentType() == null) {
                post.setFiles("");
            } else {
                String fileExtension = StringUtils.getFilenameExtension(requestDTO.getFiles().getOriginalFilename());
                if (fileExtension != null && allowedFileExtensions.contains(fileExtension.toLowerCase())) {
                    post.setFiles(cloudinaryService.uploadFile(requestDTO.getFiles()));
                } else {
                    throw new IllegalArgumentException("Not support for this file.");
                }
            }
        } catch (IOException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }

        if (userId == null) {
            return ResponseEntity.badRequest().body("User not found");
        } else {
            post.setUserId(userId);
        }

        GroupProfileResponse groupProfileResponse = null;
        if (requestDTO.getPostGroupId() == null || Integer.valueOf(requestDTO.getPostGroupId()) == 0) {
            post.setGroupId(null);
        } else {
            // Kiểm tra xem người dùng có quyền đăng bài trong nhóm không
            post.setGroupId(requestDTO.getPostGroupId());
            groupProfileResponse = groupClientService.getGroup(requestDTO.getPostGroupId());
        }

        // Thiết lập các giá trị cố định
        post.setPostTime(new Date());
        post.setUpdatedAt(new Date());
        // Tiếp tục xử lý tạo bài đăng
        save(post);

        // Lấy thông tin người dùng từ user-service
        UserProfileResponse userOfPostResponse = userClientService.getUser(userId);
        PostsResponse postsResponse = new PostsResponse(post, userOfPostResponse, groupProfileResponse);
        List<String> count = new ArrayList<>();
        postsResponse.setComments(count);
        postsResponse.setLikes(count);

        GenericResponse response = GenericResponse.builder().success(true).message("Post Created Successfully")
                .result(postsResponse).statusCode(200).build();

        return ResponseEntity.ok(response);
    }

    // Xóa bài post của mình
    @Override
    @Transactional
    public ResponseEntity<GenericResponse> deletePost(String postId, String token, String userId) {

        String accessToken = token.substring(7);
        String currentUserId = jwtService.extractUserId(accessToken);
        String a = userId.replace("\"", "").replace("\r\n\r\n", "");
        ;
        if (!currentUserId.equals(userId.replace("\"", "").replace("\r\n\r\n", ""))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Delete denied!", null, HttpStatus.NOT_FOUND.value()));
        }
        Optional<Post> optionalPost = findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            List<String> comments = post.getComments();
            if (comments != null) {
                for (String commentId : comments) {
                    commentRepository.deleteById(commentId);
                }
            }
            List<String> likes = post.getLikes();
            if (likes != null) {
                for (String likeId : likes) {
                    likeRepository.deleteById(likeId);
                }
            }
            post.setComments(new ArrayList<>());
            post.setLikes(new ArrayList<>());
            postRepository.delete(post);

            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
        }
        // Khi không tìm thấy bài post với id
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Cannot found post!", null, HttpStatus.NOT_FOUND.value()));
        }
    }

    @Override
    public ResponseEntity<Object> updatePost(String postId, PostUpdateRequest request, String currentUserId)
            throws Exception {

        List<String> allowedFileExtensions = Arrays.asList("docx", "txt", "pdf");
        UserProfileResponse userOfPostResponse = userClientService.getUser(currentUserId);
        GroupProfileResponse groupProfileResponse = null;


        Optional<Post> postOp = findById(postId);
        if (postOp.isEmpty()) {
            throw new Exception("Post doesn't exist");
        }
        Post post = postOp.get();
        if (!currentUserId.equals(postOp.get().getUserId())) {
            throw new Exception("Update denied");
        }
        post.setContent(request.getContent());
        post.setLocation(request.getLocation());
        post.setPrivacyLevel(request.getPrivacyLevel());
        post.setUpdatedAt(new Date());
        if (post.getGroupId() != null) {
            groupProfileResponse = groupClientService.getGroup(post.getGroupId());
        }
        try {
            if (request.getPhotos() == null || request.getPhotos().getContentType() == null) {
                post.setPhotos(request.getPhotoUrl());
            } else {
                post.setPhotos(cloudinaryService.uploadImage(request.getPhotos()));
            }

            if (request.getFiles() == null || request.getFiles().getContentType() == null) {
                post.setFiles(request.getFileUrl());
            } else {
                String fileExtension = StringUtils.getFilenameExtension(request.getFiles().getOriginalFilename());
                if (fileExtension != null && allowedFileExtensions.contains(fileExtension.toLowerCase())) {
                    post.setFiles(cloudinaryService.uploadFile(request.getFiles()));
                } else {
                    throw new IllegalArgumentException("Not support for this file.");
                }
            }
        } catch (IOException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }
        save(post);
        PostsResponse postResponse = new PostsResponse(post, userOfPostResponse, groupProfileResponse);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful")
                .result(postResponse).statusCode(200).build());
    }

    @Override
    public ResponseEntity<GenericResponse> getPost(String currentId, String postId) {
        Optional<Post> post = postRepository.findById(postId);
        UserProfileResponse userOfPostResponse = userClientService.getUser(currentId);
        GroupProfileResponse groupProfileResponse = null;
        if (post.get().getGroupId() != null) {
            groupProfileResponse = groupClientService.getGroup(post.get().getGroupId());
        }
        if (post.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(null).message("not found post").result(null)
                    .statusCode(HttpStatus.NOT_FOUND.value()).build());

        PostsResponse postsResponse = new PostsResponse(post.get(), userOfPostResponse, groupProfileResponse);


        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
                .result(postsResponse).statusCode(HttpStatus.OK.value()).build());
    }


    @Override
    public List<PostsResponse> findUserPosts(String currentUserId, String userId, Pageable pageable) {
        List<PrivacyLevel> privacyLevels = Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS);
        if (currentUserId.equals(userId))
            privacyLevels = Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS, PrivacyLevel.PRIVATE);

        List<Post> userPosts = postRepository.findByUserIdAndPrivacyLevelInOrderByPostTimeDesc(userId,
                privacyLevels, pageable);

        UserProfileResponse userOfPostResponse = userClientService.getUser(userId);
        GroupProfileResponse groupProfileResponse = null;

        List<PostsResponse> simplifiedUserPosts = new ArrayList<>();
        for (Post post : userPosts) {
            if (post.getGroupId() != null) {
                groupProfileResponse = groupClientService.getGroup(post.getGroupId());
            }
            PostsResponse postsResponse = new PostsResponse(post, userOfPostResponse, groupProfileResponse);
            simplifiedUserPosts.add(postsResponse);
        }
        return simplifiedUserPosts;
    }

    @Override
    public List<String> findAllPhotosByUserIdOrderByPostTimeDesc(String userId) {
        List<String> jsonStrings = postRepository.findAllPhotosByUserIdOrderByPostTimeDesc(userId);
        List<String> photos = new ArrayList<>();

        for (String jsonString : jsonStrings) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject.has("photos")) {
                    photos.add(jsonObject.getString("photos"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return photos;
    }


    @Override
    public ResponseEntity<Object> findLatestPhotosByUserId(String currentUserId, String userId, Pageable pageable) {
        UserProfileResponse userOfPostResponse = userClientService.getUser(userId);
        if (userOfPostResponse == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
                    .message("User not found.").statusCode(HttpStatus.NOT_FOUND.value()).build());
        List<PrivacyLevel> privacyLevels = Arrays.asList(PrivacyLevel.GROUP_MEMBERS);
        if (!currentUserId.equals(userId)) {
            privacyLevels = Arrays.asList(PrivacyLevel.GROUP_MEMBERS, PrivacyLevel.PRIVATE);
        }
        List<PhoToResponse> list = postRepository.findLatestPhotosByUserIdAndNotNull(privacyLevels, userId, pageable);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved user posts successfully")
                .result(list).statusCode(HttpStatus.OK.value()).build());

    }

    @Override
    public ResponseEntity<GenericResponseAdmin> getAllPosts(String authorizationHeader, int page, int itemsPerPage) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        UserProfileResponse user = userClientService.getUser(currentUserId);
        RoleName roleName = user.getRoleName();
        if (!roleName.name().equals("Admin")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                    .message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }

        Page<PostsResponse> userPostsPage = findAllPosts(page, itemsPerPage);
        long totalPosts = postRepository.count();

        PaginationInfo pagination = new PaginationInfo();
        pagination.setPage(page);
        pagination.setItemsPerPage(itemsPerPage);
        pagination.setCount(totalPosts);
        pagination.setPages((int) Math.ceil((double) totalPosts / itemsPerPage));

        if (userPostsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                    .message("No Posts Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
        } else {
            return ResponseEntity
                    .ok(GenericResponseAdmin.builder().success(true).message("Retrieved List Posts Successfully")
                            .result(userPostsPage).pagination(pagination).statusCode(HttpStatus.OK.value()).build());
        }
    }

    // Lấy tất cả bài post trong hệ thống
    @Override
    public Page<PostsResponse> findAllPosts(int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Page<Post> userPostsPage = postRepository.findAllByOrderByPostTimeDesc(pageable);

        return userPostsPage.map(post -> {
            UserProfileResponse userOfPostResponse = userClientService.getUser(post.getUserId());
            GroupProfileResponse groupProfileResponse = null;
            if (post.getGroupId() != null) {
                groupProfileResponse = groupClientService.getGroup(post.getGroupId());
            }
            PostsResponse postsResponse = new PostsResponse(post, userOfPostResponse, groupProfileResponse);
            return postsResponse;
        });
    }

    // Admin xóa bài post trong hệ thống
    @Override
    @Transactional
    public ResponseEntity<GenericResponse> deletePostByAdmin(String postId, String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        UserProfileResponse user = userClientService.getUser(currentUserId);
        RoleName roleName = user.getRoleName();
        if (!roleName.name().equals("Admin")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
                    .message("Delete denied!").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
        Optional<Post> optionalPost = findById(postId);
        Page<PostsResponse> userPostsPage = findAllPosts(1, 10);
        // tìm thấy bài post với postId
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            List<String> comments = post.getComments();
            if (comments != null) {
                for (String commentId : comments) {
                    commentRepository.deleteById(commentId);
                }
            }
            List<String> likes = post.getLikes();
            if (likes != null) {
                for (String likeId : likes) {
                    likeRepository.deleteById(likeId);
                }
            }
            post.setComments(new ArrayList<>());
            post.setLikes(new ArrayList<>());
            postRepository.delete(post);
            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Delete Successful!", userPostsPage, HttpStatus.OK.value()));
        }
        // Khi không tìm thấy bài post với id
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Cannot found post!", null, HttpStatus.NOT_FOUND.value()));
        }
    }

    // Thống kê bài post trong ngày hôm nay
    @Override
    public List<PostsResponse> getPostsToday() {
        Date startDate = getStartOfDay(new Date());
        Date endDate = getEndOfDay(new Date());
        List<Post> posts = postRepository.findByPostTimeBetween(startDate, endDate);
        return mapToPostsResponseList(posts);
    }

    // Thống kê bài post trong 1 ngày
    @Override
    public List<PostsResponse> getPostsInDay(Date day) {
        Date startDate = getStartOfDay(day);
        Date endDate = getEndOfDay(day);
        List<Post> posts = postRepository.findByPostTimeBetween(startDate, endDate);
        return mapToPostsResponseList(posts);
    }

    // Thống kê bài post trong 7 ngày
    @Override
    public List<PostsResponse> getPostsIn7Days() {
        Date startDate = getStartOfDay(getNDaysAgo(6));
        Date endDate = getEndOfDay(new Date());
        List<Post> posts = postRepository.findByPostTimeBetween(startDate, endDate);
        return mapToPostsResponseList(posts);
    }

    @Override
    public List<PostsResponse> getPostsIn1Month() {
        Date startDate = getStartOfDay(getNDaysAgo(30));
        Date endDate = getEndOfDay(new Date());
        List<Post> posts = postRepository.findByPostTimeBetween(startDate, endDate);
        return mapToPostsResponseList(posts);
    }

    // Chuyển từ kiểu Post sang PostsResponse
    private List<PostsResponse> mapToPostsResponseList(List<Post> posts) {
        List<PostsResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            UserProfileResponse userOfPostResponse = userClientService.getUser(post.getUserId());
            GroupProfileResponse groupProfileResponse = null;
            if (post.getGroupId() != null) {
                groupProfileResponse = groupClientService.getGroup(post.getGroupId());
            }
            PostsResponse postsResponse = new PostsResponse(post, userOfPostResponse, groupProfileResponse);
            responses.add(postsResponse);
        }
        return responses;
    }

    // Chuyển sang giờ bắt đầu của 1 ngày là 00:00:00
    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // Chuyển sang giờ kết thức của 1 ngày là 23:59:59
    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    // Lấy thời gian của ngày cách đây n ngày
    private Date getNDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }

    // Đếm số lượng bài post trong ngày hôm nay
    @Override
    public long countPostsToday() {
        Date startDate = getStartOfDay(new Date());
        Date endDate = getEndOfDay(new Date());
        return postRepository.countByPostTimeBetween(startDate, endDate);
    }

    // Đếm số lượng bài post trong 7 ngày
    @Override
    public long countPostsInWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minus(1, ChronoUnit.WEEKS);
        Date startDate = Date.from(weekAgo.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        return postRepository.countByPostTimeBetween(startDate, endDate);
    }

    // Đếm số lượng bài post trong 1 tháng
    @Override
    public long countPostsInMonthFromNow() {
        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Thời gian bắt đầu là thời điểm hiện tại trừ 1 tháng
        LocalDateTime startDate = now.minusMonths(1);

        // Thời gian kết thúc là thời điểm hiện tại
        LocalDateTime endDate = now;

        // Chuyển LocalDateTime sang Date (với ZoneId cụ thể, ở đây là
        // ZoneId.systemDefault())
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

        // Truy vấn số lượng bài post trong khoảng thời gian này
        return postRepository.countByPostTimeBetween(startDateAsDate, endDateAsDate);
    }

    // Đếm số lượng bài post trong 3 tháng
    @Override
    public long countPostsInThreeMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(3);
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        return postRepository.countByPostTimeBetween(startDateAsDate, endDateAsDate);
    }

    // Đếm số lượng bài post trong 6 tháng
    @Override
    public long countPostsInSixMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(6);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return postRepository.countByPostTimeBetween(startDateAsDate, endDateAsDate);
    }

    // Đếm số lượng bài post trong 9 tháng
    @Override
    public long countPostsInNineMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(9);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return postRepository.countByPostTimeBetween(startDateAsDate, endDateAsDate);
    }

    // Đếm số lượng bài post trong 1 năm
    @Override
    public long countPostsInOneYearFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusYears(1);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return postRepository.countByPostTimeBetween(startDateAsDate, endDateAsDate);
    }

    @Override
    public Map<String, Long> countPostsByUserMonthInYear(String userId) {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        UserProfileResponse user = userClientService.getUser(userId);

        // Tạo một danh sách các tháng
        List<Month> months = Arrays.asList(Month.values());
        Map<String, Long> postCountsByMonth = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

        for (Month month : months) {
            LocalDateTime startDate = LocalDateTime.of(currentYear, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

            Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

            long postCount = postRepository.countByUserIdAndPostTimeBetween(user.getUserId(), startDateAsDate, endDateAsDate);
            postCountsByMonth.put(month.toString(), postCount);
        }

        return postCountsByMonth;
    }

    // Đếm số lượng bài post từng tháng trong năm
    @Override
    public Map<String, Long> countPostsByMonthInYear() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        // Tạo một danh sách các tháng
        List<Month> months = Arrays.asList(Month.values());
        Map<String, Long> postCountsByMonth = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

        for (Month month : months) {
            LocalDateTime startDate = LocalDateTime.of(currentYear, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

            Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

            long postCount = postRepository.countByPostTimeBetween(startDateAsDate, endDateAsDate);
            postCountsByMonth.put(month.toString(), postCount);
        }

        return postCountsByMonth;
    }
    // Thay đổi phương thức findAllPosts để lấy tất cả bài post của một userId
    @Override
    public Page<PostsResponse> findAllPostsByUserId(int page, int itemsPerPage, String userId) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        // Sử dụng postRepository để tìm tất cả bài post của một userId cụ thể
        Page<Post> userPostsPage = postRepository.findAllByUserIdOrderByPostTimeDesc(userId, pageable);

        return userPostsPage.map(post -> {
            UserProfileResponse userOfPostResponse = userClientService.getUser(userId);
            GroupProfileResponse groupProfileResponse = null;
            if (post.getGroupId() != null) {
                groupProfileResponse = groupClientService.getGroup(post.getGroupId());
            }
            PostsResponse postsResponse = new PostsResponse(post, userOfPostResponse, groupProfileResponse);
            return postsResponse;
        });
    }

    @Override
    public Page<PostsResponse> findAllPostsInMonthByUserId(int page, int itemsPerPage, String userId) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        UserProfileResponse user = userClientService.getUser(userId);
        Date startDate = getStartOfDay(getNDaysAgo(30));
        Date endDate = getEndOfDay(new Date());
        // Sử dụng postRepository để tìm tất cả bài post của một userId cụ thể
        Page<Post> userPostsPage = postRepository.findByUserIdAndPostTimeBetween(user.getUserId(), startDate, endDate,
                pageable);

        return userPostsPage.map(post -> {
            UserProfileResponse userOfPostResponse = userClientService.getUser(userId);
            GroupProfileResponse groupProfileResponse = null;
            if (post.getGroupId() != null) {
                groupProfileResponse = groupClientService.getGroup(post.getGroupId());
            }
            PostsResponse postsResponse = new PostsResponse(post, userOfPostResponse, groupProfileResponse);
            return postsResponse;
        });
    }





}

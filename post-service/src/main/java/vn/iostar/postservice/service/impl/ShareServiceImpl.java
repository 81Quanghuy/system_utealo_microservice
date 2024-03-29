package vn.iostar.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.constant.RoleName;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.GenericResponseAdmin;
import vn.iostar.postservice.dto.PaginationInfo;
import vn.iostar.postservice.dto.SharePostRequestDTO;
import vn.iostar.postservice.dto.response.GroupProfileResponse;
import vn.iostar.postservice.dto.response.SharesResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.entity.Like;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.entity.Share;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.repository.CommentRepository;
import vn.iostar.postservice.repository.LikeRepository;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.repository.ShareRepository;
import vn.iostar.postservice.service.PostService;
import vn.iostar.postservice.service.ShareService;
import vn.iostar.postservice.service.client.GroupClientService;
import vn.iostar.postservice.service.client.UserClientService;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final ShareRepository shareRepository;
    private final PostService postService;
    private final JwtService jwtService;
    private final UserClientService userClientService;
    private final GroupClientService groupClientService;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    @Override
    public <S extends Share> S save(S entity) {
        return shareRepository.save(entity);
    }

    @Override
    public Optional<Share> findById(String id) {
        return shareRepository.findById(id);
    }

    @Override
    public ResponseEntity<Object> sharePost(String token, SharePostRequestDTO requestDTO) {
        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse user = userClientService.getUser(userId);
        if (user == null)
            return ResponseEntity.badRequest().body("User not found");
        Optional<Post> post = postService.findById(requestDTO.getPostId());
        if (post.isEmpty())
            return ResponseEntity.badRequest().body("Post not found");

        GroupProfileResponse postGroup = null;
        UserProfileResponse userProfileResponse = userClientService.getUser(userId);

        String shareId = UUID.randomUUID().toString();
        Share share = new Share();
        share.setId(shareId);
        share.setContent(requestDTO.getContent());
        share.setCreateAt(new Date());
        share.setUpdateAt(new Date());
        share.setPost(post.get());
        share.setUserId(user.getUserId());
        share.setPrivacyLevel(requestDTO.getPrivacyLevel());
        if (requestDTO.getPostGroupId() != null)
            if (requestDTO.getPostGroupId() != "" || requestDTO.getPostGroupId() != null) {
                postGroup = groupClientService.getGroup(requestDTO.getPostGroupId());
            if (postGroup != null) {
                share.setPostGroupId(postGroup.getId());
            }

        }
        save(share);
        SharesResponse sharesResponse = new SharesResponse(share, userProfileResponse, postGroup);
        List<Integer> count = new ArrayList<>();
        sharesResponse.setComments(count);
        sharesResponse.setLikes(count);

        GenericResponse response = GenericResponse.builder().success(true).message("Share Post Successfully")
                .result(sharesResponse).statusCode(200).build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> updateSharePost(SharePostRequestDTO requestDTO, String currentUserId) {
        Optional<Share> shareOp = findById(requestDTO.getShareId());
        if (shareOp.isEmpty())
            return ResponseEntity.badRequest().body("Share post doesn't exist");
        if (!currentUserId.equals(shareOp.get().getUserId()))
            return ResponseEntity.badRequest().body("Update denied");
        Share share = shareOp.get();
        share.setContent(requestDTO.getContent());
        share.setPrivacyLevel(requestDTO.getPrivacyLevel());
        if (requestDTO.getPostGroupId() != null) {
            if (requestDTO.getPostGroupId() != "") {
                GroupProfileResponse groupProfileResponse = groupClientService.getGroup(requestDTO.getPostGroupId());
                if (groupProfileResponse != null)
                    share.setPostGroupId(groupProfileResponse.getId());
            }
        }

        share.setUpdateAt(requestDTO.getUpdateAt());
        save(share);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful").result(null)
                .statusCode(200).build());
    }

    @Override
    @Transactional
    public ResponseEntity<GenericResponse> deleteSharePost(String shareId, String token, String userId) {
        String jwt = token.substring(7);
        String currentUserId = jwtService.extractUserId(jwt);
        if (!currentUserId.equals(userId.replaceAll("^\"|\"$", ""))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Delete denied!", null, HttpStatus.NOT_FOUND.value()));
        }

        Optional<Share> optionalShare = findById(shareId);

        if (optionalShare.isPresent()) {
            Share share = optionalShare.get();
            List<String> comments = share.getComments();
            if (comments != null) {
                for (String commentId : comments) {
                    commentRepository.deleteById(commentId);
                }
            }
            List<String> likes = share.getLikes();
            if (likes != null) {
                for (String likeId : likes) {
                    likeRepository.deleteById(likeId);
                }
            }
            share.setComments(new ArrayList<>());
            share.setLikes(new ArrayList<>());
            shareRepository.delete(share);
            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
        }
        // Khi không tìm thấy bài post với id
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Cannot found share post!", null, HttpStatus.NOT_FOUND.value()));
        }
    }

    @Override
    public List<SharesResponse> findUserSharePosts(String currentUserId, String userId, Pageable pageable) {
        List<PrivacyLevel> privacyLevels = Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS);
        List<Share> userSharePosts = shareRepository.findByUserIdAndPrivacyLevelInOrderByCreateAtDesc(userId,
                privacyLevels, pageable);
        List<SharesResponse> sharesResponses = new ArrayList<>();
        UserProfileResponse userProfileResponse = userClientService.getUser(userId);
        GroupProfileResponse groupProfileResponse = null;
        for (Share share : userSharePosts) {
            if (share.getPostGroupId() != null) {
                groupProfileResponse = groupClientService.getGroup(share.getPostGroupId());
            }
            SharesResponse sharesResponse = new SharesResponse(share, userProfileResponse, groupProfileResponse);
            sharesResponses.add(sharesResponse);
        }
        return sharesResponses;
    }

    @Override
    public SharesResponse getSharePost(Share share, String currentUserId) {
        UserProfileResponse userProfileResponse = userClientService.getUser(share.getUserId());
        GroupProfileResponse groupProfileResponse = null;
        if (share.getPostGroupId() != null) {
            groupProfileResponse = groupClientService.getGroup(share.getPostGroupId());
        }
        SharesResponse sharesResponse = new SharesResponse(share, userProfileResponse, groupProfileResponse);
        return sharesResponse;
    }

    @Override
    public Page<SharesResponse> findAllShares(int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Page<Share> userSharesPage = shareRepository.findAllByOrderByCreateAtDesc(pageable);

        return userSharesPage.map(share -> {
            UserProfileResponse userProfileResponse = userClientService.getUser(share.getUserId());
            GroupProfileResponse groupProfileResponse = null;
            if (share.getPostGroupId() != null) {
                groupProfileResponse = groupClientService.getGroup(share.getPostGroupId());
            }
            SharesResponse sharesResponse = new SharesResponse(share, userProfileResponse, groupProfileResponse);
            return sharesResponse;
        });
    }

    @Override
    public ResponseEntity<GenericResponseAdmin> getAllShares(String authorizationHeader, int page, int itemsPerPage) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        UserProfileResponse user = userClientService.getUser(currentUserId);
        RoleName roleName = user.getRoleName();
        if (!roleName.name().equals("Admin")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                    .message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }

        Page<SharesResponse> userSharesPage = findAllShares(page, itemsPerPage);
        long totalShares = shareRepository.count();

        PaginationInfo pagination = new PaginationInfo();
        pagination.setPage(page);
        pagination.setItemsPerPage(itemsPerPage);
        pagination.setCount(totalShares);
        pagination.setPages((int) Math.ceil((double) totalShares / itemsPerPage));

        if (userSharesPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                    .message("No Shares Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
        } else {
            return ResponseEntity
                    .ok(GenericResponseAdmin.builder().success(true).message("Retrieved List Shares Successfully")
                            .result(userSharesPage).pagination(pagination).statusCode(HttpStatus.OK.value()).build());
        }
    }

    @Override
    public ResponseEntity<GenericResponse> deleteShareByAdmin(String shareId, String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        UserProfileResponse user = userClientService.getUser(currentUserId);
        RoleName roleName = user.getRoleName();
        if (!roleName.name().equals("Admin")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
                    .message("Delete denied!").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
        Optional<Share> optionalShare = findById(shareId);
        Page<SharesResponse> userSharesPage = findAllShares(1, 10);
        // tìm thấy bài share post với shareId
        if (optionalShare.isPresent()) {
            Share share = optionalShare.get();
            List<String> comments = share.getComments();
            if (comments != null) {
                for (String commentId : comments) {
                    commentRepository.deleteById(commentId);
                }
            }
            List<String> likes = share.getLikes();
            if (likes != null) {
                for (String likeId : likes) {
                    likeRepository.deleteById(likeId);
                }
            }
            share.setComments(new ArrayList<>());
            share.setLikes(new ArrayList<>());
            shareRepository.delete(share);
            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Delete Successful!", userSharesPage, HttpStatus.OK.value()));
        }
        // Khi không tìm thấy bài share với id
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Cannot found share post!", null, HttpStatus.NOT_FOUND.value()));
        }
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

    // Chuyển từ kiểu Share sang SharesResponse
    private List<SharesResponse> mapToSharesResponseList(List<Share> shares) {
        List<SharesResponse> responses = new ArrayList<>();
        for (Share share : shares) {
            UserProfileResponse userProfileResponse = userClientService.getUser(share.getUserId());
            GroupProfileResponse groupProfileResponse = null;
            if (share.getPostGroupId() != null) {
                groupProfileResponse = groupClientService.getGroup(share.getPostGroupId());
            }
            SharesResponse sharesResponse = new SharesResponse(share, userProfileResponse, groupProfileResponse);
            responses.add(sharesResponse);
        }
        return responses;
    }

    private Date getNDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }

    @Override
    public List<SharesResponse> getSharesToday() {
        Date startDate = getStartOfDay(new Date());
        Date endDate = getEndOfDay(new Date());
        List<Share> shares = shareRepository.findByCreateAtBetween(startDate, endDate);
        return mapToSharesResponseList(shares);
    }
    @Override
    public List<SharesResponse> getSharesInDay(Date day) {
        Date startDate = getStartOfDay(day);
        Date endDate = getEndOfDay(day);
        List<Share> shares = shareRepository.findByCreateAtBetween(startDate, endDate);
        return mapToSharesResponseList(shares);
    }

    @Override
    public List<SharesResponse> getSharesIn7Days() {
        Date startDate = getStartOfDay(getNDaysAgo(6));
        Date endDate = getEndOfDay(new Date());
        List<Share> shares = shareRepository.findByCreateAtBetween(startDate, endDate);
        return mapToSharesResponseList(shares);
    }

    @Override
    public List<SharesResponse> getSharesIn1Month() {
        Date startDate = getStartOfDay(getNDaysAgo(30));
        Date endDate = getEndOfDay(new Date());
        List<Share> shares = shareRepository.findByCreateAtBetween(startDate, endDate);
        return mapToSharesResponseList(shares);
    }

    @Override
    public long countSharesToday() {
        Date startDate = getStartOfDay(new Date());
        Date endDate = getEndOfDay(new Date());
        return shareRepository.countByCreateAtBetween(startDate, endDate);
    }

    @Override
    public long countSharesInWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minus(1, ChronoUnit.WEEKS);
        Date startDate = Date.from(weekAgo.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        return shareRepository.countByCreateAtBetween(startDate, endDate);
    }

    @Override
    public long countSharesInMonthFromNow() {
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

        // Truy vấn số lượng bài share post trong khoảng thời gian này
        return shareRepository.countByCreateAtBetween(startDateAsDate, endDateAsDate);
    }

    @Override
    public long countSharesInOneYearFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusYears(1);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return shareRepository.countByCreateAtBetween(startDateAsDate, endDateAsDate);
    }

    @Override
    public long countSharesInNineMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(9);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return shareRepository.countByCreateAtBetween(startDateAsDate, endDateAsDate);
    }

    @Override
    public long countSharesInSixMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(6);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return shareRepository.countByCreateAtBetween(startDateAsDate, endDateAsDate);
    }

    @Override
    public long countSharesInThreeMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(3);
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        return shareRepository.countByCreateAtBetween(startDateAsDate, endDateAsDate);
    }

    @Override
    public Map<String, Long> countSharesByMonthInYear() {
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

            long postCount = shareRepository.countByCreateAtBetween(startDateAsDate, endDateAsDate);
            postCountsByMonth.put(month.toString(), postCount);
        }

        return postCountsByMonth;
    }

    @Override
    public Map<String, Long> countSharesByUserMonthInYear(String userId) {
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

            long postCount = shareRepository.countByUserIdAndCreateAtBetween(user.getUserId(), startDateAsDate, endDateAsDate);
            postCountsByMonth.put(month.toString(), postCount);
        }

        return postCountsByMonth;
    }

    @Override
    public Page<SharesResponse> findAllSharesByUserId(int page, int itemsPerPage, String userId) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Page<Share> userSharesPage = shareRepository.findAllByUserIdOrderByCreateAtDesc(userId, pageable);
        return userSharesPage.map(share -> {
            UserProfileResponse userProfileResponse = userClientService.getUser(share.getUserId());
            GroupProfileResponse groupProfileResponse = null;
            if (share.getPostGroupId() != null) {
                groupProfileResponse = groupClientService.getGroup(share.getPostGroupId());
            }
            SharesResponse sharesResponse = new SharesResponse(share, userProfileResponse, groupProfileResponse);
            return sharesResponse;
        });
    }

    @Override
    public Page<SharesResponse> findAllSharesInMonthByUserId(int page, int itemsPerPage, String userId) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Date startDate = getStartOfDay(getNDaysAgo(30));
        Date endDate = getEndOfDay(new Date());
        UserProfileResponse user = userClientService.getUser(userId);
        Page<Share> userSharesPage = shareRepository.findByUserIdAndCreateAtBetween(user.getUserId(), startDate, endDate,
                pageable);
        return userSharesPage.map(share -> {
            UserProfileResponse userProfileResponse = userClientService.getUser(share.getUserId());
            GroupProfileResponse groupProfileResponse = null;
            if (share.getPostGroupId() != null) {
                groupProfileResponse = groupClientService.getGroup(share.getPostGroupId());
            }
            SharesResponse sharesResponse = new SharesResponse(share, userProfileResponse, groupProfileResponse);
            return sharesResponse;
        });
    }


    public ResponseEntity<GenericResponse> getShare(String currentUserId, String shareId) {
        Optional<Share> share = shareRepository.findById(shareId);
        if (share.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(null).message("not found share").result(null)
                    .statusCode(HttpStatus.NOT_FOUND.value()).build());

        SharesResponse sharePost = getSharePost(share.get(), currentUserId);

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
                .result(sharePost).statusCode(HttpStatus.OK.value()).build());

    }

    @Override
    public List<SharesResponse> findMySharePosts(String currentUserId, Pageable pageable) {
        List<Share> userSharePosts = shareRepository.findByUserUserId(currentUserId, pageable);
        List<SharesResponse> sharesResponses = new ArrayList<>();
        for (Share share : userSharePosts) {
            UserProfileResponse userProfileResponse = userClientService.getUser(share.getUserId());
            GroupProfileResponse groupProfileResponse = null;
            if (share.getPostGroupId() != null) {
                groupProfileResponse = groupClientService.getGroup(share.getPostGroupId());
            }
            SharesResponse sharesResponse = new SharesResponse(share, userProfileResponse, groupProfileResponse);
            sharesResponses.add(sharesResponse);
        }
        return sharesResponses;
    }
}

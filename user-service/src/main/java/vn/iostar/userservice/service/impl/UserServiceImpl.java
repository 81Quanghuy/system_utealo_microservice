package vn.iostar.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.constant.*;
import vn.iostar.model.*;
import vn.iostar.userservice.constant.RoleUserGroup;
import vn.iostar.userservice.dto.*;
import vn.iostar.userservice.dto.request.AccountManager;
import vn.iostar.userservice.dto.request.ChangePasswordRequest;
import vn.iostar.userservice.dto.request.UserManagerRequest;
import vn.iostar.userservice.dto.request.UserUpdateRequest;
import vn.iostar.userservice.dto.response.FriendResponse;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.dto.response.UserProfileResponse;
import vn.iostar.userservice.dto.response.UserResponse;
import vn.iostar.userservice.entity.*;
import vn.iostar.userservice.exception.wrapper.BadRequestException;
import vn.iostar.userservice.exception.wrapper.NotFoundException;
import vn.iostar.userservice.jwt.service.JwtService;
import vn.iostar.userservice.mapper.RelationShipMapper;
import vn.iostar.userservice.mapper.UserMapper;
import vn.iostar.userservice.repository.elasticsearch.UsersElasticSearchRepository;
import vn.iostar.userservice.repository.jpa.*;
import vn.iostar.userservice.service.AccountService;
import vn.iostar.userservice.service.RoleService;
import vn.iostar.userservice.service.TokenService;
import vn.iostar.userservice.service.UserService;
import vn.iostar.userservice.service.client.GroupClient;
import vn.iostar.userservice.service.client.PostClient;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ProfileRepository profileRepository;

    private final AccountService accountService;

    private final PasswordEncoder passwordEncoder;

    private final PasswordResetOtpRepository passwordResetOtpRepository;

    private final TokenRepository tokenRepository;

    private final JwtService jwtService;

    private final RoleService roleService;

    private final GroupClient groupClient;
    private final PostClient  postClient;
    private final RelationshipRepository relationshipRepository;

    private final KafkaTemplate<String, PasswordReset> kafkaTemplate;
    private final KafkaTemplate<String, EmailVerify> kafkaTemplateVerify;
    private final KafkaTemplate<String, VerifyParent> kafkaTemplateParent;
    private final UsersElasticSearchRepository usersElasticSearchRepository;
    private final TokenService tokenService;
    final String indexCell = "Tại dòng ";

    @Override
    public <S extends User> S save(S entity) {
        S user = userRepository.save(entity);
        usersElasticSearchRepository.save(UserMapper.toUserDocument(user));
        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return userRepository.count(example);
    }

    @Override
    public long count() {
        return userRepository.count();
    }


    public <S extends User> void saveAll(Iterable<S> entities) {
        userRepository.saveAll(entities);
        for (User user : entities) {
            usersElasticSearchRepository.save(UserMapper.toUserDocument(user));
        }
    }

    @Override
    public void deleteById(String id) {
        usersElasticSearchRepository.deleteById(id);
        userRepository.deleteById(id);
    }

    @Override
    public void delete(User entity) {
        usersElasticSearchRepository.delete(UserMapper.toUserDocument(entity));
        userRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        usersElasticSearchRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Override
    public ResponseEntity<GenericResponse> getProfile(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) throw new RuntimeException("Người dùng không tồn tại");


        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully").result(new UserProfileResponse(user.get())).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> changePassword(String userId, ChangePasswordRequest request) throws Exception {

        if (request.getNewPassword().length() < 8 || request.getNewPassword().length() > 32)
            throw new RuntimeException("Mật khẩu phải dài từ 8 đến 32 ký tự");

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new RuntimeException("Mật khẩu và mật khẩu xác nhận không khớp");

        Optional<User> userOptional = findById(userId);

        if (userOptional.isEmpty()) throw new RuntimeException("Người dùng không tồn tại");

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getAccount().getPassword()))
            throw new BadRequestException("Mật khẩu không chính xác");

        if (passwordEncoder.matches(request.getNewPassword(), user.getAccount().getPassword()))
            throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu cũ");

        user.getAccount().setPassword(passwordEncoder.encode(request.getNewPassword()));
        save(user);

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Đổi mật khẩu thành công").result(null).statusCode(200).build());

    }

    @Override
    public Optional<User> findByAccountEmail(String email) {
        return userRepository.findByAccountEmail(email);
    }

    @Override
    public void createPasswordResetOtpForUser(User user, String otp) {
        PasswordResetOtp myOtp = null;
        if (passwordResetOtpRepository.findByUser(user).isPresent()) {
            myOtp = passwordResetOtpRepository.findByUser(user).get();
            myOtp.updateOtp(otp);
        } else {

            myOtp = new PasswordResetOtp(otp, user);
        }
        passwordResetOtpRepository.save(myOtp);
    }

    @Override
    public String validatePasswordResetOtp(String otp) {
        Optional<PasswordResetOtp> passOtp = passwordResetOtpRepository.findByOtp(otp);
        Calendar cal = Calendar.getInstance();

        if (passOtp.isEmpty()) {
            return "Invalid token/link";
        }
        if (passOtp.get().getExpiryDate().before(cal.getTime())) {
            return "Token/link expired";
        }
        return null;
    }

    @Override
    public String validateVerificationAccount(String token) {
        Optional<Token> verificationToken = tokenRepository.findByToken(token);
        if (verificationToken.isEmpty()) {
            return "Token không hợp lệ, vui lòng kiểm tra token lần nữa!";
        }
        User user = verificationToken.get().getUser();
        user.setIsVerified(true);
        save(user);
        return "Xác minh tài khoản thành công, vui lòng đăng nhập!";
    }

    @Override
    public Optional<PasswordResetOtp> getUserByPasswordResetOtp(String otp) {
        return passwordResetOtpRepository.findByOtp(otp);
    }

    @Override
    public void changeUserPassword(User user, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword))
            throw new RuntimeException("Mật khẩu và mật khẩu xác nhận không khớp");
        user.setIsActive(true);
        user.setIsVerified(true);
        user.getAccount().setIsVerified(true);
        user.getAccount().setPassword(passwordEncoder.encode(newPassword));
        save(user);
    }

    // Cập nhật thông tin người dùng
    @Override
    public ResponseEntity<Object> updateProfile(String userId, UserUpdateRequest request) throws Exception {
        Optional<User> userOptional = findById(userId);

        User user = userOptional.orElseThrow(() -> new Exception("Người dùng không tồn tại"));

        // Kiểm tra DateOfBirth trước
        if (request.getDateOfBirth() != null && request.getDateOfBirth().after(new Date())) {
            throw new Exception("Ngày sinh không hợp lệ");
        }

        // Sử dụng ifPresent để kiểm tra và thực hiện hành động
        userOptional.ifPresent(u -> {
            if (request.getFullName() != null) u.setUserName(request.getFullName());
            if (request.getPhone() != null) u.setPhone(request.getPhone());
            if (request.getGender() != null) u.setGender(request.getGender());
            if (request.getDateOfBirth() != null) u.setDayOfBirth(request.getDateOfBirth());
            if (request.getAddress() != null) u.setAddress(request.getAddress());
            u.getProfile().setBio(request.getAbout());
        });

        // Sử dụng orElse để tránh gọi save(user) nếu user không tồn tại
        save(user);

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("ập nhật thông tin người dùng thành công").result(new UserProfileResponse(user)).statusCode(200).build());
    }

    // Quản lý tài khoản người dùng
    @Override
    public ResponseEntity<Object> accountManager(String authorizationHeader, UserManagerRequest request) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Optional<User> userManager = findById(currentUserId);
        RoleName roleName = userManager.get().getRole().getRoleName();
        if (!roleName.name().equals("Admin")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false).message("Không có quyền truy cập").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
        Optional<User> user = findById(request.getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false).message("Người dùng không tồn tại").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
        user.get().getRole().setRoleName(request.getRoleName());
        user.get().getAccount().setIsActive(request.getIsActive());
        save(user.get());
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Cập nhật tài khoản thành công").result(new UserProfileResponse(user.get())).statusCode(200).build());
    }

    @Override
    public ResponseEntity<GenericResponse> getAvatarAndName(String userId) {
        Optional<User> user = findById(userId);
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false).message("Người dùng không tồn tại").statusCode(HttpStatus.NOT_FOUND.value()).build());
        UserMessage userMessage = new UserMessage();
        userMessage.setAvatar(user.get().getProfile().getAvatar());
        userMessage.setUserName(user.get().getUserName());
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Lấy thông tin người dùng thành công").result(userMessage).statusCode(200).build());
    }

    // Xóa user
    @Override
    public ResponseEntity<GenericResponse> deleteUser(String idFromToken) {
        try {
            Optional<User> optionalUser = findById(idFromToken);
            /// tìm thấy user với id
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                // không xóa user , chỉ cập nhật active về flase
                user.getAccount().setIsActive(false);

                User updatedUser = save(user);
                /// nếu cập nhật active về false
                if (updatedUser != null) {
                    return ResponseEntity.ok().body(new GenericResponse(true, "Xóa thành công!", updatedUser, HttpStatus.OK.value()));
                }
                /// cập nhật không thành công
                else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse(false, "Cập nhật thất bại!", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
                }
            }
            /// khi không tìm thấy user với id
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponse(false, "Không tìm thấy người dùng", null, HttpStatus.NOT_FOUND.value()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new GenericResponse(false, "Invalid arguments!", null, HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse(false, "An internal server error occurred!", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Lấy toàn bộ thông tin người dùng
//	@Override
//	public UserProfileResponse getFullProfile(Optional<User> user, Pageable pageable) {
//		UserProfileResponse profileResponse = new UserProfileResponse(user.get());
//		List<FriendResponse> fResponse = friendRepository.findFriendUserIdsByUserId(user.get().getUserId());
//		profileResponse.setFriends(fResponse);
//
//		List<GroupPostResponse> groupPostResponses = postGroupRepository
//				.findPostGroupInfoByUserId(user.get().getUserId(), pageable);
//		profileResponse.setPostGroup(groupPostResponses);
//		return profileResponse;
//	}

    // Tìm tất cả người dùng trong hệ thống
    @Override
    public Page<UserResponse> findAllUsers(int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Page<UserResponse> usersPage = userRepository.findAllUsers(pageable);

        return usersPage.map(userItem -> {
            Optional<User> userOptional = findById(userItem.getUserId());
            if (userOptional.isPresent()) {

                boolean isActive = userOptional.get().getAccount().getIsActive();
                // Kiểm tra isActive và thiết lập trạng thái tương ứng
                userItem.setIsActive(isActive);
                userItem.setRoleName(userOptional.get().getRole().getRoleName());
                userItem.setEmail(userOptional.get().getAccount().getEmail());

                // Đếm số lượng bài post của người dùng
//				Long countPosts = postRepository.countPostsByUser(userOptional.get());
//				userItem.setCountPost(countPosts);

                // Đếm số lượng shares của người dùng
//				Long countShares = shareRepository.countSharesByUser(userOptional.get());
//				userItem.setCountShare(countShares);

                // Đếm số lượng comments của người dùng
//				Long countComments = commentRepository.countCommentsByUser(userOptional.get());
//				userItem.setCountComment(countComments);
            }
            return userItem;
        });
    }

    // Lấy tất cả người dùng trong hệ thống
    @Override
    public ResponseEntity<GenericResponseAdmin> getAllUsers(String authorizationHeader, int page, int itemsPerPage) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Optional<User> user = findById(currentUserId);
        RoleName roleName = user.get().getRole().getRoleName();
        if (!roleName.name().equals("Admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(GenericResponseAdmin.builder().success(false).message("Không có quyền truy cập").statusCode(HttpStatus.FORBIDDEN.value()).build());
        }

        Page<UserResponse> users = findAllUsers(page, itemsPerPage);
        long totalUsers = userRepository.count();

        PaginationInfo pagination = new PaginationInfo();
        pagination.setPage(page);
        pagination.setItemsPerPage(itemsPerPage);
        pagination.setCount(totalUsers);
        pagination.setPages((int) Math.ceil((double) totalUsers / itemsPerPage));

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(true).message("Empty").result(null).statusCode(HttpStatus.NOT_FOUND.value()).build());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(GenericResponseAdmin.builder().success(true).message("Lấy danh sách người dùng thành công").result(users).pagination(pagination).statusCode(HttpStatus.OK.value()).build());
        }
    }

    // Lấy tất cả người dùng không phân trang
    @Override
    public List<ListUsers> getAllUsersIdAndName() {
        return userRepository.findAllUsersIdAndName();
    }

    // Thống kê user trong ngày hôm nay
    @Override
    public List<UserResponse> getUsersToday() {
        Date startDate = getStartOfDay(new Date());
        Date endDate = getEndOfDay(new Date());
        List<UserResponse> users = userRepository.findUsersByAccountCreatedAtBetween(startDate, endDate);
        return users;
    }

    // Thống kê user trong 1 ngày
    @Override
    public List<UserResponse> getUsersInDay(Date day) {
        Date startDate = getStartOfDay(day);
        Date endDate = getEndOfDay(day);
        List<UserResponse> users = userRepository.findUsersByAccountCreatedAtBetween(startDate, endDate);
        return users;
    }

    // Thống kê user trong 7 ngày
    @Override
    public List<UserResponse> getUsersIn7Days() {
        Date startDate = getStartOfDay(getNDaysAgo(6));
        Date endDate = getEndOfDay(new Date());
        List<UserResponse> users = userRepository.findUsersByAccountCreatedAtBetween(startDate, endDate);
        return users;
    }

    // Thống kê user trong 1 tháng
    @Override
    public List<UserResponse> getUsersInMonth(Date month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = getStartOfDay(calendar.getTime());

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        Date endDate = getEndOfDay(calendar.getTime());

        List<UserResponse> users = userRepository.findUsersByAccountCreatedAtBetween(startDate, endDate);
        return users;
    }

    // Chuyển sang giờ bắt đầu của 1 ngày là 00:00:00
    public Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // Chuyển sang giờ kết thức của 1 ngày là 23:59:59
    public Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public Date getNDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }

    // Đếm số lượng user từng tháng trong năm
    @Override
    public Map<String, Long> countUsersByMonthInYear() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        // Tạo một danh sách các tháng
        Month[] months = Month.values();
        Map<String, Long> userCountsByMonth = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

        for (Month month : months) {
            LocalDateTime startDate = LocalDateTime.of(currentYear, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

            Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

            long userCount = userRepository.countUsersByAccountCreatedAtBetween(startDateAsDate, endDateAsDate);
            userCountsByMonth.put(month.toString(), userCount);
        }

        return userCountsByMonth;
    }

    // Đếm số lượng user trong ngày hôm nay
    @Override
    public long countUsersToday() {
        Date startDate = getStartOfDay(new Date());
        Date endDate = getEndOfDay(new Date());
        return userRepository.countUsersByAccountCreatedAtBetween(startDate, endDate);
    }

    // Đếm số lượng user trong 7 ngày
    @Override
    public long countUsersInWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusWeeks(1);
        Date startDate = Date.from(weekAgo.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        return userRepository.countUsersByAccountCreatedAtBetween(startDate, endDate);
    }

    // Đếm số lượng user trong 1 tháng
    @Override
    public long countUsersInMonthFromNow() {
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

        // Truy vấn số lượng user trong khoảng thời gian này
        return userRepository.countUsersByAccountCreatedAtBetween(startDateAsDate, endDateAsDate);
    }

    // Đếm số lượng user trong 3 tháng
    @Override
    public long countUsersInThreeMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(3);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return userRepository.countUsersByAccountCreatedAtBetween(startDateAsDate, endDateAsDate);
    }

    @Override
    public void changeOnlineStatus(UserDTO user) {
        Optional<User> userOptional = findById(user.getUserId());
        if (userOptional.isPresent()) {
            User userEntity = userOptional.get();
            userEntity.setIsOnline(user.getIsOnline());
            save(userEntity);
        }
    }

    // Đếm số lượng user trong 6 tháng
    @Override
    public long countUsersInSixMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(6);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return userRepository.countUsersByAccountCreatedAtBetween(startDateAsDate, endDateAsDate);
    }

    // Đếm số lượng user trong 9 tháng
    @Override
    public long countUsersInNineMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(9);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return userRepository.countUsersByAccountCreatedAtBetween(startDateAsDate, endDateAsDate);
    }

    // Đếm số lượng user trong 1 năm
    @Override
    public long countUsersInOneYearFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusYears(1);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return userRepository.countUsersByAccountCreatedAtBetween(startDateAsDate, endDateAsDate);
    }

    // Thống kê user trong 1 tháng
    @Override
    public List<UserResponse> getUsersIn1Month() {
        Date startDate = getStartOfDay(getNDaysAgo(30)); // Sử dụng 30 ngày thay vì 7 ngày
        Date endDate = getEndOfDay(new Date());
        List<UserResponse> users = userRepository.findUsersByAccountCreatedAtBetween(startDate, endDate);
        return users;
    }

    // Tính % người dùng mới trong tháng
    @Override
    public double calculatePercentageNewUsersThisMonth() {
        // Lấy thời điểm bắt đầu của tháng hiện tại
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Đặt ngày là 1 để bắt đầu từ đầu tháng
        Date startOfMonth = calendar.getTime();

        // Lấy ngày hiện tại
        Date currentDate = new Date();

        // Lấy danh sách người dùng được tạo trong tháng này
        List<UserResponse> usersThisMonth = getUsersInMonth(currentDate);

        // Lấy tổng số người dùng trong tháng này
        long totalUsersThisMonth = usersThisMonth.size();

        // Tính số lượng người dùng mới (tạo từ đầu tháng đến hiện tại)
        List<UserResponse> newUsersThisMonth = getUsersInMonth(currentDate);

        // Lấy tổng số người dùng mới
        long newUsersCount = newUsersThisMonth.size();

        // Tính phần trăm người dùng mới
        double percentageNewUsers = 0.0;
        if (totalUsersThisMonth > 0) {
            percentageNewUsers = (double) (countUsersToday()) / totalUsersThisMonth * 100.0;
        }

        return percentageNewUsers;
    }

    @Override
    public List<Top3UserOfMonth> getTop3UsersWithMostActivityInMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(1);

        Date start = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        DateResponse dateResponse = new DateResponse();
        dateResponse.setStartDate(start);
        dateResponse.setEndDate(end);

        List<User> allUsers = userRepository.findAll(); // Lấy tất cả người dùng

        // Tạo một Map để lưu userId và tổng số hoạt động của từng người dùng
        Map<String, Long> userActivityMap = new HashMap<>();

        for (User user : allUsers) {
			long postCount = postClient.countPostsByUserId(user.getUserId(), dateResponse);
			long shareCount = postClient.countSharesByUserId(user.getUserId(), dateResponse);
			long commentCount = postClient.countCommentsByUserId(user.getUserId(), dateResponse);

            // Tổng số hoạt động của người dùng = bài post + bài share + bình luận
			long totalActivity = postCount + shareCount + commentCount;

            userActivityMap.put(user.getUserId(), totalActivity);
        }

        // Sắp xếp Map theo giá trị (tổng số hoạt động) giảm dần
        List<Map.Entry<String, Long>> sortedList = new ArrayList<>(userActivityMap.entrySet());
        sortedList.sort(Map.Entry.<String, Long>comparingByValue().reversed());

        // Lấy ra 3 người dùng có tổng số hoạt động lớn nhất
        List<Top3UserOfMonth> top3UsersWithProfile = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, Long> entry : sortedList) {
            if (count >= 3) {
                break;
            }
            User user = userRepository.findById(entry.getKey()).orElse(null);
            if (user != null) {
                Profile profile = user.getProfile(); // Lấy profile của user

                long postCount = postClient.countPostsByUserId(user.getUserId(), dateResponse);
                long shareCount = postClient.countSharesByUserId(user.getUserId(), dateResponse);
                long commentCount = postClient.countCommentsByUserId(user.getUserId(), dateResponse);
				long total = postCount + shareCount + commentCount;

                Top3UserOfMonth top3UserOfMonth = new Top3UserOfMonth();
                top3UserOfMonth.setUserName(user.getUserName());
                top3UserOfMonth.setUserId(user.getUserId());
				top3UserOfMonth.setCountPostOfMonth(postCount);
				top3UserOfMonth.setCountCommentOfMonth(commentCount);
				top3UserOfMonth.setCountShareOfMonth(shareCount);
                top3UserOfMonth.setTotal(total);
                top3UserOfMonth.setAvatar(profile != null ? profile.getAvatar() : null); // Lấy avatar từ profile

                top3UsersWithProfile.add(top3UserOfMonth);
                count++;
            }
        }

        return top3UsersWithProfile;
    }

    @Override
    public ResponseEntity<Object> createAccount(String authorizationHeader, AccountManager request) throws IOException {
        // Tạo đối tượng MultipartFile
        MultipartFile multipartFile = request.getFile();
        // Lấy input stream của file
        InputStream inputStream = multipartFile.getInputStream();
        int countMember = 0;
        try (// Tạo đối tượng XSSFWorkbook để đọc file Excel
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            List<User> listUsers = new ArrayList<>();
            List<Account> listAccounts = new ArrayList<>();
            List<GroupResponse> listGroups = new ArrayList<>();
            List<Profile> listProfiles = new ArrayList<>();
            // vòng lặp qua từng sheet
            for (int j = 0; j < workbook.getNumberOfSheets(); j++) {
                // Lấy sheet đầu tiên
                XSSFSheet sheet = workbook.getSheetAt(j);
                // Duyệt qua các hàng trong sheet
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    User user = new User();

                    UserFileDTO userDTO = mapRowToUserDTO(sheet, i, inputStream,j);
                    System.out.println(userDTO);
                    // Nếu người dùng chưa tồn tại trong hệ thống
                    if (userDTO.getAccount() == null) {
                        countMember += 1;
                        user.setIsActive(true);
                        if (userDTO.getAddress() != null) {
                            user.setAddress(userDTO.getAddress());
                        }
                        if (userDTO.getDateOfBirth() != null) {
                            user.setDayOfBirth(userDTO.getDateOfBirth());
                        }

                        if (userDTO.getPhone() != null) {
                            user.setPhone(String.format("%011.0f", userDTO.getPhone()));
                        }

                        user.setIsVerified(false);
                        user.setUserName(userDTO.getUserName());
                        Optional<Role> role = roleService.findByRoleName(userDTO.getRoleGroup());
                        role.ifPresent(user::setRole);

                        if (userDTO.getGender() != null) {
                            if (userDTO.getGender().equals("Nam")) {
                                user.setGender(Gender.MALE);
                            } else if (userDTO.getGender().equals("Nữ")) {
                                user.setGender(Gender.FEMALE);
                            } else {
                                user.setGender(Gender.OTHER);
                            }
                        }

                        // Hàm thêm tài khoản
                        Account account = new Account();
                        account.setIsVerified(false);
                        if (userDTO.getPhone() != null) {
                            account.setPhone(String.format("%01.0f", userDTO.getPhone()));
                        }
                        account.setEmail(userDTO.getEmail());
                        account.setIsActive(true);
                        account.setUser(user);
                        account.setCreatedAt(new Date());

                        // Hàm thêm profile
                        Profile profile = new Profile();
                        profile.setUser(user);
                        if (userDTO.getGender() != null) {
                            if (userDTO.getGender().equals("Nữ")) {
                                profile.setAvatar("https://i.pinimg.com/736x/01/48/0f/01480f29ce376005edcbec0b30cf367d.jpg");

                            } else {
                                profile.setAvatar("https://www.prettywoman.vn/wp-content/uploads/2023/06/hinh-anh-avatar-nam-1-600x600.jpg");
                            }
                        }
                        listProfiles.add(profile);
                        listAccounts.add(account);
                        listUsers.add(user);

                        if (userDTO.getClassUser() != null) {
                            AdminInGroup check = groupClient.checkAdminInGroup(userDTO.getClassUser());
                            if (check.equals(AdminInGroup.ADMIN) && userDTO.getRoleUserGroup().equals(RoleUserGroup.Admin)) {
                                throw new BadRequestException("Nhóm của người dùng tại dòng " + i + " đã có nhóm trưởng");
                            } else {
                                GroupResponse groupResponse = new GroupResponse();
                                groupResponse.setName(userDTO.getClassUser());
                                groupResponse.setEmailUser(userDTO.getEmail());
                                groupResponse.setUsername(userDTO.getUserName());
                                groupResponse.setRoleUser(userDTO.getRoleGroup());
                                groupResponse.setRole(GroupMemberRoleType.valueOf(String.valueOf(userDTO.getRoleUserGroup())));
                                listGroups.add(groupResponse);
                            }
                        }
                    }
                }
            }
            saveAll(listUsers);
            accountService.saveAll(listAccounts);
            profileRepository.saveAll(listProfiles);
            groupClient.updateGroupFromExcel(listGroups);
        }
        // Đóng file Excel
        inputStream.close();
        return ResponseEntity.status(HttpStatus.OK).body(GenericResponseAdmin.builder().success(true).message("Có " + countMember + " dòng đã được thêm vào hệ thống!!!").statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<Object> searchUser(String fields, String query) {

        List<UserResponse> users = userRepository.searchUser(query);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false).message("Không tìm thấy thông tin người dùng").statusCode(HttpStatus.NOT_FOUND.value()).build());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(GenericResponse.builder().success(true).message("Tìm thấy thông tin người dùng!!!").result(users).statusCode(HttpStatus.OK.value()).build());
        }
    }

    private String notFormat(int j , int i) {
        return " sheet "+ j +1+" cột thứ " + i +1 + " không đúng định dạng !!!";
    }

    public static boolean isCellEmpty(final Cell cell) {
        if (cell == null) { // use row.getCell(x, Row.CREATE_NULL_AS_BLANK) to avoid null cells
            return true;
        } else if (cell.getCellType() == CellType.BLANK) {
            return true;
        } else return cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty();
    }

    private UserFileDTO mapRowToUserDTO(XSSFSheet sheet, int i, InputStream inputStream, int j) throws IOException {
        UserFileDTO userFileDTO = new UserFileDTO();
        Cell cell = sheet.getRow(i).getCell(0);

        if (cell != null && cell.getCellType() == CellType.STRING) {
            if (cell.getStringCellValue().trim().contains("@student.hcmute.edu.vn")) {
                if ( sheet.getRow(i).getCell(8) != null) {
                    if (!sheet.getRow(i).getCell(8).getStringCellValue().trim().contains("Sinh viên")) {
                        inputStream.close();
                        throw new BadRequestException(indexCell + i + notFormat(j,8));
                    }
                } else {
                    inputStream.close();
                    throw new BadRequestException(indexCell + i + notFormat(j,8));
                }
            } else {
                if (cell.getStringCellValue().trim().contains("@hcmute.edu.vn")) {
                    if (!sheet.getRow(i).getCell(8).getStringCellValue().trim().contains("Giảng viên")
                            && !sheet.getRow(i).getCell(8).getStringCellValue().trim().contains("Nhân viên")) {
                        inputStream.close();
                        throw new BadRequestException(indexCell + i + notFormat(j,8));
                    }

                } else {
                    inputStream.close();
                    throw new BadRequestException(indexCell + i + notFormat(j,8));
                }
            }
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(j,0));
        }
        userFileDTO.setEmail(cell.getStringCellValue());
        Optional<Account> acOptional = accountService.findByEmail(userFileDTO.getEmail());
        if (acOptional.isPresent()) {
            userFileDTO.setAccount(acOptional.get());
            return userFileDTO;
        } else {
            cell = sheet.getRow(i).getCell(1);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                userFileDTO.setUserName(cell.getStringCellValue());
            } else {
                inputStream.close();
                throw new BadRequestException(indexCell + i + notFormat(j,1));
            }

            cell = sheet.getRow(i).getCell(2);
            if (isCellEmpty(cell)) {
                userFileDTO.setAddress(null);
            } else if (cell.getCellType() == CellType.STRING) {
                userFileDTO.setAddress(cell.getStringCellValue());
            } else {
                inputStream.close();
                throw new BadRequestException(indexCell + i + notFormat(j,2));
            }

            cell = sheet.getRow(i).getCell(3);
            if (isCellEmpty(cell)) {
                userFileDTO.setDateOfBirth(null);
            } else if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                userFileDTO.setDateOfBirth(cell.getDateCellValue());
            } else {
                inputStream.close();
                throw new BadRequestException(indexCell + i + notFormat(j,3));
            }

            cell = sheet.getRow(i).getCell(4);
            if (isCellEmpty(cell)) {
                userFileDTO.setGender(null);
            } else if (cell.getCellType() == CellType.STRING) {
                userFileDTO.setGender(cell.getStringCellValue());
            } else {
                inputStream.close();
                throw new BadRequestException(indexCell + i + notFormat(j,4));
            }

            cell = sheet.getRow(i).getCell(5);
            if (isCellEmpty(cell)) {
                userFileDTO.setPhone(null);
            } else if (cell.getCellType() == CellType.NUMERIC) {
                userFileDTO.setPhone(cell.getNumericCellValue());
            } else {
                inputStream.close();
                throw new BadRequestException(indexCell + i + notFormat(j,5));
            }

            cell = sheet.getRow(i).getCell(6);

            if (isCellEmpty(cell)) {
                userFileDTO.setClassUser(null);
            } else if (cell.getCellType() == CellType.STRING) {
                userFileDTO.setClassUser(cell.getStringCellValue());
            } else {
                inputStream.close();
                throw new BadRequestException(indexCell + i + notFormat(j,6));
            }

            cell = sheet.getRow(i).getCell(7);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                if (cell.getStringCellValue().trim().equals("Thành viên")) {
                    userFileDTO.setRoleUserGroup(RoleUserGroup.Member);
                } else if (cell.getStringCellValue().trim().equals("Quản trị viên")) {
                    userFileDTO.setRoleUserGroup(RoleUserGroup.Admin);
                } else if (cell.getStringCellValue().trim().equals("Phó quản trị viên")) {
                    userFileDTO.setRoleUserGroup(RoleUserGroup.Deputy);
                } else {
                    inputStream.close();
                    throw new BadRequestException(indexCell + i + notFormat(j,7));
                }
            }

                cell = sheet.getRow(i).getCell(8);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    if (cell.getStringCellValue().trim().equals("Sinh viên")) {
                        userFileDTO.setRoleGroup(RoleName.SinhVien);
                    } else if (cell.getStringCellValue().trim().equals("Giảng viên")) {
                        userFileDTO.setRoleGroup(RoleName.GiangVien);
                    } else if (cell.getStringCellValue().trim().equals("Nhân viên")) {
                        userFileDTO.setRoleGroup(RoleName.NhanVien);
                    } else {
                        inputStream.close();
                        throw new BadRequestException(indexCell + i + notFormat(j,8));
                    }
                } else {
                    inputStream.close();
                    throw new BadRequestException(indexCell + i + notFormat(j,8));
                }
            }
            return userFileDTO;
    }

    public UserStatisticsDTO getUserStatistics(String userId) {
        Optional<User> userOp = findById(userId);
        User user = userOp.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(1);

        Date start = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateResponse dateResponse = new DateResponse(start,end);

        long postCount = postClient.countPostsByUserId(user.getUserId(), dateResponse);
        long shareCount = postClient.countSharesByUserId(user.getUserId(), dateResponse);
        long commentCount = postClient.countCommentsByUserId(user.getUserId(), dateResponse);

        UserStatisticsDTO userStatisticsDTO = new UserStatisticsDTO();
		userStatisticsDTO.setPostCount(postCount);
		userStatisticsDTO.setShareCount(shareCount);
		userStatisticsDTO.setCommentCount(commentCount);

        return userStatisticsDTO;

    }

    @Override
    public List<User> findByRoleRoleName(RoleName roleName) {
        return userRepository.findByRoleRoleName(roleName);
    }

    @Override
    public List<User> findAllByUserId(String userId) {
        return userRepository.findAllByUserId(userId);
    }


    // Lấy toàn bộ thông tin người dùng
    @Override
    public UserProfileResponse getFullProfile(User user) {
        return new UserProfileResponse(user);
    }
    @Override
    public List<FriendResponse> getFriendByListUserId(UserIds listUserId) {

        List<FriendResponse> listFriend = new ArrayList<>();
        for (String userId : listUserId.getUserId()) {
            Optional<User> user = findById(userId);
            if (user.isPresent()) {
                FriendResponse friend = new FriendResponse();
                friend.setUserId(user.get().getUserId());
                friend.setBackground(user.get().getProfile().getBackground());
                friend.setAvatar(user.get().getProfile().getAvatar());
                friend.setUsername(user.get().getUserName());
                friend.setIsOnline(user.get().getIsOnline());
                listFriend.add(friend);
            }
        }
        return listFriend;
    }

    @Override
    public ResponseEntity<GenericResponse> forgotPassword(User user) {

        String otp = UUID.randomUUID().toString();
        createPasswordResetOtpForUser(user, otp);
        PasswordReset passwordRequest = new PasswordReset(user.getAccount().getEmail(), otp);
        kafkaTemplate.send(KafkaTopicName.EMAIL_FORGOT_PASSWORD_TOPIC, passwordRequest);
        return ResponseEntity.ok().body(new GenericResponse(true, "Đã gửi email xác " +
                "nhận đổi mật khẩu!", null, HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<GenericResponse> updateOnline(String userId, Boolean isOnline) {
        Optional<User> user = findById(userId);
        if (user.isEmpty()) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                   new GenericResponse(false,
                           "Người dùng không tồn tại",
                           null,
                           HttpStatus.NOT_FOUND.value()));
        }
        user.get().setIsOnline(isOnline);
        save(user.get());
        return ResponseEntity.ok().body(new GenericResponse(true,
                "Cập nhật trạng thái online thành công!",
                new UserResponse(user.get()),
                HttpStatus.OK.value()));
    }

    @Override
    public List<String> getAllUserId() {
        return userRepository.findAllUserId();
    }

    @Override
    public ResponseEntity<GenericResponse> verifyUser(String email) {
        Optional<Account> account =accountService.findByEmail(email);
        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new GenericResponse(false,
                            "Người dùng không tồn tại",
                            null,
                            HttpStatus.NOT_FOUND.value()));
        }
        account.get().setIsVerified(true);
        accountService.save(account.get());
        return ResponseEntity.ok().body(new GenericResponse(true,
                "Xác thực người dùng thành công!",
                new UserResponse(account.get().getUser()),
                HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<GenericResponse> getChildren(String userId) {
        Optional<User> user = findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Người dùng không tồn tại");
        }
        List<Relationship> children = relationshipRepository.findByParentAndIsAcceptedTrue(user.get());
        List<UserResponse> childrenResponse = new ArrayList<>();
        for (Relationship child : children) {
            childrenResponse.add(new UserResponse(child.getChild()));
        }
        return ResponseEntity.ok().body(new GenericResponse(true,
                "Lấy danh sách con thành công!",
                childrenResponse,
                HttpStatus.OK.value()));
    }

    @Override
    public RelationshipResponse getRelationship(String currentId, String userId) {
        Optional<User> currentUser = findById(currentId);
        Optional<User> user = findById(userId);
        if (currentUser.isEmpty() || user.isEmpty()) {
            throw new NotFoundException("Người dùng không tồn tại");
        }
        Optional<Relationship> relationship = relationshipRepository.findByParentUserIdAndChildUserId(currentUser.get().getUserId(), user.get().getUserId());
        if (relationship.isEmpty()) {
            return new RelationshipResponse();
        }
        RelationshipResponse relationshipResponse = new RelationshipResponse();
        relationshipResponse.setId(relationship.get().getId());
        relationshipResponse.setChildUserId(userId);
        relationshipResponse.setParentUserId(currentId);
        relationshipResponse.setIsAccepted(relationship.get().getIsAccepted());
        return relationshipResponse;
    }

    @Override
    @Transactional
    public ResponseEntity<GenericResponse> addRelationShip(String currentId, String email) {
        Optional<User> currentUser = findById(currentId);
        Optional<User> user = findByAccountEmail(email);
        if (currentUser.isEmpty() || user.isEmpty()) {
            throw new NotFoundException("Sinh vien co email " + email + " khong ton tai");
        }
        if (currentUser.get().getUserId().equals(user.get().getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new GenericResponse(false,
                            "Không thể thêm mối quan hệ với chính mình",
                            null,
                            HttpStatus.BAD_REQUEST.value()));
        }
        Optional<Relationship> relationship = relationshipRepository.findByParentUserIdAndChildUserId(currentUser.get().getUserId(), user.get().getUserId());
        if (relationship.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new GenericResponse(false,
                            "Mối quan hệ đã tồn tại",
                            null,
                            HttpStatus.BAD_REQUEST.value()));
        }
        Relationship newRelationship = new Relationship();
        newRelationship.setParent(currentUser.get());
        newRelationship.setChild(user.get());
        newRelationship.setIsAccepted(false);
        relationshipRepository.save(newRelationship);
        Token token = tokenService.createTokenVerifyParent(currentUser.get());
        VerifyParent verifyParent = new VerifyParent(email,currentUser.get().getAccount().getEmail(),currentUser.get().getUserName(), token.getToken());
        kafkaTemplateParent.send(KafkaTopicName.EMAIL_VERIFY_TOPIC, verifyParent);
        return ResponseEntity.ok().body(new GenericResponse(true,
                "Thêm mối quan hệ thành công!",
                RelationShipMapper.toRelationshipResponse(newRelationship),
                HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<GenericResponse> getParent(String userId) {
        Optional<User> user = findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Người dùng không tồn tại");
        }
        List<Relationship> parents = relationshipRepository.findByChildUserIdAndIsAcceptedTrue(userId);
        List<UserResponse> parentResponse = new ArrayList<>();
        for (Relationship parent : parents) {
            parentResponse.add(new UserResponse(parent.getParent()));
        }
        return ResponseEntity.ok().body(new GenericResponse(true,
                "Lấy danh sách phụ huynh thành công!",
                parentResponse,
                HttpStatus.OK.value()));

    }

    @Override
    public ResponseEntity<GenericResponse> getParentNotVerify(String userId) {
        Optional<User> user = findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Người dùng không tồn tại");
        }
        List<Relationship> parents = relationshipRepository.findByChildUserIdAndIsAcceptedFalse(userId);
        List<UserResponse> parentResponse = new ArrayList<>();
        for (Relationship parent : parents) {
            parentResponse.add(new UserResponse(parent.getParent()));
        }
        return ResponseEntity.ok().body(new GenericResponse(true,
                "Lấy danh sách phụ huynh chưa xác nhận thành công!",
                parentResponse,
                HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<GenericResponse> acceptParent(String currentUserId, String userId) {
        Optional<User> currentUser = findById(currentUserId);
        Optional<User> user = findById(userId);
        if (currentUser.isEmpty() || user.isEmpty()) {
            throw new NotFoundException("Người dùng không tồn tại");
        }
        Optional<Relationship> relationship = relationshipRepository.findByParentUserIdAndChildUserId(user.get().getUserId(), currentUser.get().getUserId());
        if (relationship.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new GenericResponse(false,
                            "Mối quan hệ không tồn tại",
                            null,
                            HttpStatus.BAD_REQUEST.value()));
        }
        relationship.get().setIsAccepted(true);
        relationshipRepository.save(relationship.get());
        return ResponseEntity.ok().body(new GenericResponse(true,
                "Xác nhận phụ huynh thành công!",
                RelationShipMapper.toRelationshipResponse(relationship.get()),
                HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<GenericResponse> declineParent(String currentUserId, String userId) {
        Optional<User> currentUser = findById(currentUserId);
        Optional<User> user = findById(userId);
        if (currentUser.isEmpty() || user.isEmpty()) {
            throw new NotFoundException("Người dùng không tồn tại");
        }
        Optional<Relationship> relationship = relationshipRepository.findByParentUserIdAndChildUserId(user.get().getUserId(), currentUser.get().getUserId());
        if (relationship.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new GenericResponse(false,
                            "Mối quan hệ không tồn tại",
                            null,
                            HttpStatus.BAD_REQUEST.value()));
        }
        relationshipRepository.delete(relationship.get());
        return ResponseEntity.ok().body(new GenericResponse(true,
                "Từ chối phụ huynh thành công!",
                null,
                HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<GenericResponse> getAllParentNotVerify(String userId, int page, int size) {
        Optional<User> user = findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Người dùng không tồn tại");
        }
        Pageable pageable = PageRequest.of(page, size);
        if(!user.get().getRole().getRoleName().equals(RoleName.Admin)){
           throw new BadRequestException("Không có quyền truy cập");
        }
        Page<Relationship> parents = relationshipRepository.findAllByIsAcceptedFalse(pageable);
        List<RelationShipResponseAll> parentResponse = new ArrayList<>();
        for (Relationship parent : parents) {
            parentResponse.add(RelationShipMapper.toRelationShipResponseAll(parent));
        }
        return ResponseEntity.ok().body(new GenericResponse(true,
                "Lấy danh sách phụ huynh chưa xác nhận thành công!",
                parentResponse,
                HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<GenericResponse> adminAcceptParent(String currentUserId, String relationId) {
        Optional<User> currentUser = findById(currentUserId);
        Optional<Relationship> relationship = relationshipRepository.findById(relationId);
        if (currentUser.isEmpty() || relationship.isEmpty()) {
            throw new NotFoundException("Người dùng không tồn tại");
        }
        if(!currentUser.get().getRole().getRoleName().equals(RoleName.Admin)){
            throw new BadRequestException("Không có quyền truy cập");
        }
        relationship.get().setIsAccepted(true);
        relationshipRepository.save(relationship.get());
        EmailVerify  emailVerify = new EmailVerify(relationship.get().getParent().getAccount().getEmail(),
                "Thông tin của bạn đã được xác nhận bởi admin");
        kafkaTemplateVerify.send(KafkaTopicName.EMAIL_VERIFY_PARENT_TOPIC,emailVerify);
        return ResponseEntity.ok().body(new GenericResponse(true,
                "Xác nhận phụ huynh thành công!",
                RelationShipMapper.toRelationShipResponseAll(relationship.get()),
                HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<GenericResponse> adminDeclineParent(String currentUserId, String relationId) {
        Optional<User> currentUser = findById(currentUserId);
        Optional<Relationship> relationship = relationshipRepository.findById(relationId);
        if (currentUser.isEmpty() || relationship.isEmpty()) {
            throw new NotFoundException("Người dùng không tồn tại");
        }
        if(!currentUser.get().getRole().getRoleName().equals(RoleName.Admin)){
            throw new BadRequestException("Không có quyền truy cập");
        }
        relationshipRepository.delete(relationship.get());
        EmailVerify  emailVerify = new EmailVerify(relationship.get().getParent().getAccount().getEmail(),
                "Thông tin của bạn đã bị từ chối bởi admin");
        kafkaTemplateVerify.send(KafkaTopicName.EMAIL_VERIFY_PARENT_TOPIC,emailVerify);
        return ResponseEntity.ok().body(new GenericResponse(true,
                "Từ chối phụ huynh thành công!",
                null,
                HttpStatus.OK.value()));

    }
}
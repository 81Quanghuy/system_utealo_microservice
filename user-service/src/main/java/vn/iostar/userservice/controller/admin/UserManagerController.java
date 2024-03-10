package vn.iostar.userservice.controller.admin;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import vn.iostar.userservice.dto.*;
import vn.iostar.userservice.dto.request.AccountManager;
import vn.iostar.userservice.dto.request.UserManagerRequest;
import vn.iostar.userservice.dto.response.UserResponse;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.repository.UserRepository;
import vn.iostar.userservice.security.JwtTokenProvider;
import vn.iostar.userservice.service.UserService;

@RestController
@RequestMapping("/api/v1/admin/userManager")
public class UserManagerController {

	@Autowired
	UserService userService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	UserRepository userRepository;

	@PostMapping("/uploadExcel")
	public ResponseEntity<Object> updateAccount(@ModelAttribute AccountManager request,
			@RequestHeader("Authorization") String authorizationHeader) throws IOException, ParseException {
		return userService.createAccount(authorizationHeader, request);
	}

	// Lấy danh sách tất cả user trong hệ thống
	@GetMapping("/list")
	public ResponseEntity<GenericResponseAdmin> getAllUsers(@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int items) {
		return userService.getAllUsers(authorizationHeader, page, items);
	}

	// Lấy tất cả người dùng không phân trang
	@GetMapping("/listUsers")
	public List<ListUsers> getAllUsersByNameAndId() {
		return userService.getAllUsersIdAndName();

	}

	// Cập nhật trạng thái tài khoản và cập nhật quyền admin của user
	@PutMapping("/update")
	public ResponseEntity<Object> updateUser(@ModelAttribute UserManagerRequest request,
			@RequestHeader("Authorization") String authorizationHeader) {
		return userService.accountManager(authorizationHeader, request);
	}

	// Thống kê user trong ngày hôm nay
	// Thống kê user trong 1 ngày
	// Thống kê user trong 7 ngày
	// Thống kê user trong 1 tháng
	@GetMapping("/filterByDate")
	public List<UserResponse> getUsers(@RequestParam(required = false) String action,
									   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		switch (action != null ? action.toLowerCase() : "") {
		case "today":
			return userService.getUsersToday();
		case "day":
			if (date != null) {
				return userService.getUsersInDay(date);
			}
			break;
		case "7days":
			return userService.getUsersIn7Days();
		case "month":
			return userService.getUsersIn1Month();
		default:
			// Nếu không có action hoặc action không hợp lệ, có thể trả về thông báo lỗi
			// hoặc một giá trị mặc định
			break;
		}
		// Trả về null hoặc danh sách rỗng tùy theo logic của bạn
		return null;
	}

	// Đếm số lượng user từng tháng trong năm
	@GetMapping("/countUsersByMonthInYear")
	public ResponseEntity<Map<String, Long>> countUsersByMonthInYear() {
		try {
			Map<String, Long> userCountsByMonth = userService.countUsersByMonthInYear();
			return ResponseEntity.ok(userCountsByMonth);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Đếm số lượng user
	@GetMapping("/countUser")
	public ResponseEntity<CountDTO> countUsersToday() {
		try {
			long userCountToDay = userService.countUsersToday();
			long userCountInWeek = userService.countUsersInWeek();
			long userCountIn1Month = userService.countUsersInMonthFromNow();
			long userCountIn3Month = userService.countUsersInThreeMonthsFromNow();
			long userCountIn6Month = userService.countUsersInSixMonthsFromNow();
			long userCountIn9Month = userService.countUsersInNineMonthsFromNow();
			long userCountIn1Year = userService.countUsersInOneYearFromNow();
			double percentNewUser = userService.calculatePercentageNewUsersThisMonth();
			double percentUserOnline = 0.0;
			percentUserOnline = (double) (userRepository.countByIsOnlineTrue()) / userService.count() * 100.0;

			CountDTO userCountDTO = new CountDTO(userCountToDay, userCountInWeek, userCountIn1Month, userCountIn3Month,
					userCountIn6Month, userCountIn9Month, userCountIn1Year, percentNewUser, percentUserOnline);
			return ResponseEntity.ok(userCountDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/top3UserAwards")
	public ResponseEntity<List<Top3UserOfMonth>> getTop3MostActiveUsers() {
		List<Top3UserOfMonth> top3Users = userService.getTop3UsersWithMostActivityInMonth();
		return ResponseEntity.ok(top3Users);
	}

	@GetMapping("/getCountPostShareComment/{userId}")
	public ResponseEntity<UserStatisticsDTO> getCountPostShareComment(@PathVariable("userId") String userId) {
		UserStatisticsDTO userStatisticsDTO = userService.getUserStatistics(userId);
		return ResponseEntity.ok(userStatisticsDTO);
	}

	@GetMapping("/getIsOnline/{userId}")
	public Boolean getIsOnlineOfUser(@PathVariable("userId") String userId) {
		Optional<User> user = userService.findById(userId);
		return user.get().getIsOnline();
	}

	// Tìm kiếm người dùng theo userName,address,email
	@GetMapping("/search")
	public ResponseEntity<Object> searchUsers(@RequestParam(name = "fields") String fields,
			@RequestParam(name = "q") String query) {
		return userService.searchUser(fields, query);
	}
}

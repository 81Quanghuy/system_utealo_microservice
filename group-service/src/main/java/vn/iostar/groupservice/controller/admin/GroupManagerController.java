package vn.iostar.groupservice.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.groupservice.dto.CountDTO;
import vn.iostar.groupservice.dto.PostGroupDTO;
import vn.iostar.groupservice.dto.SearchPostGroup;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.dto.response.GenericResponseAdmin;
import vn.iostar.groupservice.service.GroupService;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/groupManager")
public class GroupManagerController {

    private final GroupService postGroupService;


    // Lấy danh sách tất cả các nhóm trong hệ thống
    @GetMapping("/list")
    public ResponseEntity<GenericResponseAdmin> getAllGroups(@RequestHeader("Authorization") String authorizationHeader,
                                                             @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int items) {
        return postGroupService.getAllGroups(authorizationHeader, page, items);
    }

    // Admin xóa nhóm trong hệ thống
    @PutMapping("/delete/{postGroupId}")
    public ResponseEntity<GenericResponse> deleteCommentOfPost(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("postGroupId") String postGroupId) {
        return postGroupService.deletePostGroupByAdmin(postGroupId, authorizationHeader);
    }


    // Đếm số lượng nhóm từng tháng trong năm
    @GetMapping("/countGroupsByMonthInYear")
    public ResponseEntity<Map<String, Long>> countGroupsByMonthInYear() {
        try {
            Map<String, Long> groupCountsByMonth = postGroupService.countGroupsByMonthInYear();
            return ResponseEntity.ok(groupCountsByMonth);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Đếm số lượng group
    @GetMapping("/countGroup")
    public ResponseEntity<CountDTO> countGroupsToday() {
        try {
            long groupCountToDay = postGroupService.countGroupsToday();
            long groupCountInWeek = postGroupService.countGroupsInWeek();
            long groupCountIn1Month = postGroupService.countGroupsInMonthFromNow();
            long groupCountIn3Month = postGroupService.countGroupsInThreeMonthsFromNow();
            long groupCountIn6Month = postGroupService.countGroupsInSixMonthsFromNow();
            long groupCountIn9Month = postGroupService.countGroupsInNineMonthsFromNow();
            long groupCountIn1Year = postGroupService.countGroupsInOneYearFromNow();

            CountDTO groupCountDTO = new CountDTO(groupCountToDay, groupCountInWeek, groupCountIn1Month,
                    groupCountIn3Month, groupCountIn6Month, groupCountIn9Month, groupCountIn1Year);
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
    public List<SearchPostGroup> getGroups(@RequestParam(required = false) String action,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        switch (action != null ? action.toLowerCase() : "") {
            case "today":
                return postGroupService.getGroupsToday();
            case "7days":
                return postGroupService.getGroupsIn7Days();
            case "month":
                return postGroupService.getGroupsInMonth();
            default:
                // Nếu không có action hoặc action không hợp lệ, có thể trả về thông báo lỗi
                // hoặc một giá trị mặc định
                break;
        }
        // Trả về null hoặc danh sách rỗng tùy theo logic của bạn
        return null;
    }

}

package vn.iostar.groupservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import vn.iostar.groupservice.constant.AppConstant;
import vn.iostar.groupservice.repository.jpa.GroupRepository;

import java.util.Date;
import java.util.UUID;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(info = @Info(title = "Group API", version = "1.0", description = "Documentation Friend API v1.0"))
@EnableScheduling
@EnableAsync
public class GroupServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroupServiceApplication.class, args);
    }

    @Autowired
    private GroupRepository groupRepository;

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            Date now = new Date();
            if (groupRepository.findByPostGroupName(AppConstant.GROUP_NAME_PARENT).isEmpty()) {
                groupRepository.save(
                        vn.iostar.groupservice.entity.Group.builder()
                                .id(UUID.randomUUID().toString())
                                .postGroupName(AppConstant.GROUP_NAME_PARENT)
                                .bio("Cộng đồng phụ huynh trường Đại học Sư phạm Kỹ thuật")
                                .authorId("1")
                                .isSystem(true)
                                .isActive(true)
                                .avatarGroup(AppConstant.AVATAR_GROUP_PARENT_LINK)
                                .backgroundGroup(AppConstant.BACKGROUND_GROUP_LINK)
                                .isPublic(false)
                                .isApprovalRequired(true)
                                .createdAt(now)
                                .build()
                );
            }
            if (groupRepository.findByPostGroupName(AppConstant.GROUP_NAME_STUDENT).isEmpty()) {
                groupRepository.save(
                        vn.iostar.groupservice.entity.Group.builder()
                                .id(UUID.randomUUID().toString())
                                .postGroupName(AppConstant.GROUP_NAME_STUDENT)
                                .bio("Cộng đồng sinh viên trường Đại học Sư phạm Kỹ thuật")
                                .authorId("1")
                                .isSystem(true)
                                .avatarGroup(AppConstant.AVATAR_GROUP_STUDENT_LINK)
                                .isActive(true)
                                .isPublic(false)
                                .backgroundGroup(AppConstant.BACKGROUND_GROUP_LINK)
                                .isApprovalRequired(true)
                                .createdAt(now)
                                .build()
                );
            }
            if (groupRepository.findByPostGroupName(AppConstant.GROUP_NAME_TEACHER).isEmpty()) {
                groupRepository.save(
                        vn.iostar.groupservice.entity.Group.builder()
                                .id(UUID.randomUUID().toString())
                                .postGroupName(AppConstant.GROUP_NAME_TEACHER)
                                .bio("Cộng đồng giáo viên trường Đại học Sư phạm Kỹ thuật")
                                .authorId("1")
                                .isSystem(true)
                                .avatarGroup(AppConstant.AVATAR_GROUP_TEACHER_LINK)
                                .isActive(true)
                                .isPublic(false)
                                .backgroundGroup(AppConstant.BACKGROUND_GROUP_LINK)
                                .isApprovalRequired(true)
                                .createdAt(now)
                                .build()
                );
            }
            if (groupRepository.findByPostGroupName(AppConstant.GROUP_NAME_STAFF).isEmpty()) {
                groupRepository.save(
                        vn.iostar.groupservice.entity.Group.builder()
                                .id(UUID.randomUUID().toString())
                                .postGroupName(AppConstant.GROUP_NAME_STAFF)
                                .bio("Cộng đồng nhân viên trường Đại học Sư phạm Kỹ thuật")
                                .authorId("1")
                                .isSystem(true)
                                .avatarGroup(AppConstant.AVATAR_GROUP_STAFF_LINK)
                                .isActive(true)
                                .backgroundGroup(AppConstant.BACKGROUND_GROUP_LINK)
                                .isPublic(false)
                                .isApprovalRequired(true)
                                .createdAt(now)
                                .build()
                );
            }
        };
    }
}

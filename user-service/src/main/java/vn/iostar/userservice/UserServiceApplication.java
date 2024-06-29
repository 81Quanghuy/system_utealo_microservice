package vn.iostar.userservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iostar.constant.Gender;
import vn.iostar.userservice.constant.RoleName;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.repository.AccountRepository;
import vn.iostar.userservice.repository.ProfileRepository;
import vn.iostar.userservice.repository.RoleRepository;
import vn.iostar.userservice.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@EnableAsync
@EnableFeignClients
@OpenAPIDefinition(info =
@Info(title = "User API", version = "1.0", description = "Documentation User API v1.0")
)
@EnableScheduling
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-service");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
	@Bean
	InitializingBean sendDatabase() {
		return () -> {
			if(roleRepository.findByRoleName(RoleName.Admin).isEmpty()) {
				roleRepository.save(vn.iostar.userservice.entity.Role.builder()
						.id(UUID.randomUUID().toString())
						.roleName(RoleName.Admin)
						.code("ADMIN")
						.description("Admin role full permission")
						.build());
			}
			if(roleRepository.findByRoleName(RoleName.SinhVien).isEmpty()) {
				roleRepository.save(vn.iostar.userservice.entity.Role.builder()
						.id(UUID.randomUUID().toString())
						.roleName(RoleName.SinhVien)
						.code("USER")
						.description("User role")
						.build());
			}
			if(roleRepository.findByRoleName(RoleName.GiangVien).isEmpty()) {
				roleRepository.save(vn.iostar.userservice.entity.Role.builder()
						.id(UUID.randomUUID().toString())
						.roleName(RoleName.GiangVien)
						.code("TEACHER")
						.description("Teacher role")
						.build());
			}
			if(roleRepository.findByRoleName(RoleName.NhanVien).isEmpty()) {
				roleRepository.save(vn.iostar.userservice.entity.Role.builder()
						.id(UUID.randomUUID().toString())
						.roleName(RoleName.NhanVien)
						.code("STAFF")
						.description("Staff role")
						.build());
			}
			if(roleRepository.findByRoleName(RoleName.PhuHuynh).isEmpty()) {
				roleRepository.save(vn.iostar.userservice.entity.Role.builder()
						.id(UUID.randomUUID().toString())
						.roleName(RoleName.PhuHuynh)
						.code("PARENT")
						.description("Parent role")
						.build());
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			if (userRepository.findByRoleRoleName(RoleName.Admin).isEmpty()) {
				User user = userRepository.save(
						vn.iostar.userservice.entity.User.builder()
								.userId(UUID.randomUUID().toString())
								.userName("admin@gmail.com")
								.address("Gia Lai")
								.dayOfBirth(sdf.parse("05-10-2002"))
								.gender(Gender.MALE)
								.isActive(true)
								.isVerified(true)
								.role(roleRepository.findByRoleName(RoleName.Admin).get())
								.phone("0123456789")
								.isOnline(false)
								.build());
				accountRepository.save(
						vn.iostar.userservice.entity.Account.builder()
								.id(UUID.randomUUID().toString())
								.email("admin@gmail.com")
								.user(user)
								.password(passwordEncoder.encode("admin@123"))
								.phone("0123456789")
								.isActive(true)
								.isVerified(true)
								.build());
				profileRepository.save(
						vn.iostar.userservice.entity.Profile.builder()
								.profileId(UUID.randomUUID().toString())
								.user(user)
								.avatar("resources/images/avatarAdmin.png")
								.background("resources/images/avatarAdmin.png")
								.bio("Admin")
								.build());
			}
		};
	}
}
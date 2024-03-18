package com.maurice.DocumentManagement;

import com.maurice.DocumentManagement.dto.CategoryDto;
import com.maurice.DocumentManagement.dto.PlanDto;
import com.maurice.DocumentManagement.dto.UserRequest;
import com.maurice.DocumentManagement.entities.Document;
import com.maurice.DocumentManagement.entities.Plan;
import com.maurice.DocumentManagement.entities.UserEntity;
import com.maurice.DocumentManagement.repository.CategoryRepository;
import com.maurice.DocumentManagement.services.*;
import com.maurice.DocumentManagement.utils.StorageProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DocumentManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentManagementApplication.class, args);

	}

	@Bean
	public CommandLineRunner commandLineRunner(
			PlanService planService,
			AuthenticationService authenticationService,
			UserService userService,
			CategoryService categoryService,
			DocumentService documentService,
			StorageService storageService
	){
		return args -> {
			// Refresh the folder local storage
			storageService.deleteAll();
			storageService.init();

			PlanDto planDto = new PlanDto(1l, "Basic", "Nothing else", 0.0f);
			Plan plan = planService.registerPlan(planDto);

			PlanDto plan2Dto = new PlanDto(2l, "Developer", "Featured functionalities", 5.5f);
			Plan plan2 = planService.registerPlan(plan2Dto);

			UserRequest user = new UserRequest("maurice", "Solis", "mau.solis5t@gmail.com", "1a2b3c4d5t", plan2.getId());

			authenticationService.registerUser(user);

			UserRequest user2 = new UserRequest("bastian", "Prandelli", "bastian@gmail.com", "1q2w3e4r5t", plan2.getId());
			authenticationService.registerUser(user2);

			userService.getUsersByPlanName("Developer", 0, 1).content().forEach(System.out::println);

//			PlanDto plan3Dto = new PlanDto(3l, "Developer", "New brand Featured functionalities", 12.5f);
//			Plan plan3 = planService.registerPlan(plan3Dto);

		};
	}


}

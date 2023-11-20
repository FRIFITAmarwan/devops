package com.devops.devops;

import com.devops.devops.repository.TutorialRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DevOpsApplicationTests {

	@Autowired
	private TutorialRepository tutorialRepository; // Assurez-vous d'ajuster le type selon votre configuration

	@Test
	void contextLoads() {
		// Assert that the TutorialRepository bean is not null
		Assert.assertNotNull(tutorialRepository);
	}

}

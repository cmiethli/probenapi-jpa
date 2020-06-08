package de.proben.probenapijpa.apps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.proben.probenapijpa.persistence.ProbeRepository;

@SpringBootApplication(scanBasePackages = "de.proben.probenapijpa.config")
@EnableJpaRepositories(basePackageClasses = ProbeRepository.class)
@EntityScan("de.proben.probenapijpa.persistence")
public class ForTests {

	public static void main(String[] args) {
		SpringApplication.run(ForTests.class);
	}
}

package de.proben.probenapijpa.client;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.proben.probenapijpa.persistence.Probe;
import de.proben.probenapijpa.persistence.ProbeRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = ProbeRepository.class)
@EntityScan("de.proben.probenapijpa.persistence")
public class ProbenapiJpaApplication {

	private static final Logger log = LoggerFactory
			.getLogger(ProbenapiJpaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ProbenapiJpaApplication.class);
	}

	@Bean
	public CommandLineRunner demo(ProbeRepository repository) {
		return (args) -> {
			// save a few customers
			repository.save(new Probe(LocalDateTime.now(), 0));
			repository.save(new Probe(LocalDateTime.now(), 10000));
			repository.save(new Probe(LocalDateTime.now(), 5000));
			repository.save(new Probe(LocalDateTime.now(), 0));
			repository.save(new Probe(LocalDateTime.now(), 0));

			// fetch all Proben
			log.info("Probes found with findAll():");
			log.info("-------------------------------");
			for (Probe customer : repository.findAll()) {
				log.info(customer.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			long id = 1;
			Optional<Probe> customer = repository.findById(id);
			customer.ifPresentOrElse(p -> {
				log.info("Probe found with findById(1L):");
				log.info("--------------------------------");
				log.info(p.toString());
				log.info("");
			}, () -> log.info("no Probe with id: " + id + " found\n"));

//			// fetch customers by last name
//			log.info("Probe found with findByLastName('Bauer'):");
//			log.info("--------------------------------------------");
//			repository.findByLastName("Bauer")
//					.forEach(bauer -> {
//						log.info(bauer.toString());
//					});
//			// for (Probe bauer : repository.findByLastName("Bauer")) {
//			// log.info(bauer.toString());
//			// }
//			log.info("");
		};
	}

}

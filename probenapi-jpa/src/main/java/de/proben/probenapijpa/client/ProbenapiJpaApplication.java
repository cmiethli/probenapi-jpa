package de.proben.probenapijpa.client;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.proben.probenapijpa.api.ProbenVerwalten;
import de.proben.probenapijpa.persistence.Probe;
import de.proben.probenapijpa.persistence.Probe.Ergebnis;
import de.proben.probenapijpa.persistence.ProbeRepository;

@SpringBootApplication(scanBasePackages = "de.proben.probenapijpa.config")
@EnableJpaRepositories(basePackageClasses = ProbeRepository.class)
@EntityScan("de.proben.probenapijpa.persistence")
public class ProbenapiJpaApplication {

	private static final Logger log = LoggerFactory
			.getLogger(ProbenapiJpaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ProbenapiJpaApplication.class);
	}

	@Autowired
	@Qualifier("db")
	private ProbenVerwalten pv;

	@Bean
	public CommandLineRunner demo(ProbeRepository repository) {
		return (args) -> {
			pv.addProbe(new Probe(LocalDateTime.now(), 0));
			pv.addProbe(new Probe(LocalDateTime.now(), 10000));
			pv.addProbe(new Probe(LocalDateTime.now(), 5000));
			pv.addProbe(new Probe(LocalDateTime.now()));
			pv.addProbe(new Probe());

			// fetch all Proben
			log.info("Probes found with findAll():");
			log.info("-------------------------------");
			pv.findAll()
					.forEach(p -> log.info(p.toString()));

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

			repository.findByErgebnis(Ergebnis.NEGATIV)
					.forEach(p -> log.info(p.toString()));

			log.info("");
//			log.info(repository.xxx()
//					.toString());
			repository.updateMesswert(557, 3L);
			log.info(repository.findById(3L)
					.toString());
			log.info("" + pv.addMesswert(5, 7777));
			log.info(repository.findById(5L)
					.toString());
		};

	}

}

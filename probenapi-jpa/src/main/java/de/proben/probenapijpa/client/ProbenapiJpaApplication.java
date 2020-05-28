package de.proben.probenapijpa.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

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
import de.proben.probenapijpa.persistence.ProbeRepository;
import de.proben.probenapijpa.util.Constants;

@SpringBootApplication(scanBasePackages = "de.proben.probenapijpa.config")
@EnableJpaRepositories(basePackageClasses = ProbeRepository.class)
@EntityScan("de.proben.probenapijpa.persistence")
public class ProbenapiJpaApplication {

	private static final Logger log = LoggerFactory
		.getLogger(ProbenapiJpaApplication.class);
	private static Probe probeOhneMw;

	public static void main(String[] args) {
		SpringApplication.run(ProbenapiJpaApplication.class);
	}

	@Autowired
	@Qualifier("inMem")
	private ProbenVerwalten pvInMemList;

	@Autowired
	@Qualifier("db")
	private ProbenVerwalten pvDb;

	@Bean
	public CommandLineRunner demo(ProbeRepository repository) {
		return (args) -> {

			testProbenVerwalten(pvInMemList);
			alleProbenAusDbLoeschenUndIdGleich1(pvInMemList);
			log.info("");
			log.info("############  InMem Liste geloescht: " + pvInMemList.findAll());

			System.out.println();
			System.out.println("######################################"
				+ "###############################################");

//fuer h2 eigentlich nicht noetig, weil DB sowieso immer neu erstellt wird 
//(und am Ende geloescht wird)
			alleProbenAusDbLoeschenUndIdGleich1(pvDb);
			testProbenVerwalten(pvDb);

		};

	}

	private static void testProbenVerwalten(ProbenVerwalten proVerwInstance) {
		generateProben(proVerwInstance);

		String name = proVerwInstance.getClass()
			.getSimpleName();
		log.info("##### " + name + ": findAll() ##########");
		proVerwInstance.findAll()
			.forEach(p -> log.info(p.toString()));

		log.info("");
		log.info("##### " + name + ": timeSorted(AeltesteZuerst) #############");
		boolean isAeltesteZuerst = true;
		proVerwInstance.timeSorted(isAeltesteZuerst)
			.forEach(p -> log.info(p.toString()));

		log.info("");
		log.info("#### " + name + ": filtered(Ergebnis.xxx) #############");
		proVerwInstance.filtered(Probe.Ergebnis.FRAGLICH)
			.forEach(p -> log.info(p.toString()));
		proVerwInstance.filtered(Probe.Ergebnis.POSITIV)
			.forEach(p -> log.info(p.toString()));
		proVerwInstance.filtered(Probe.Ergebnis.NEGATIV)
			.forEach(p -> log.info(p.toString()));

		log.info("");
		log.info("##### " + name + ": removeProbe(id) #############");
		log.info("remove id=0: " + proVerwInstance.removeProbe(0));
		proVerwInstance.findAll()
			.stream()
			.findAny()
			.ifPresentOrElse(probe -> {
				long id = probe.getProbeId();
				log.info("remove id={}: {}", id, proVerwInstance.removeProbe(id));
			}, () -> log.info("nothing to remove"));
		proVerwInstance.findAll()
			.forEach(p -> log.info(p.toString()));

		log.info("");
		int mw = 88;
//		int mw = -88; // ConstraintViolationException
		log.info("##### " + name + ": addMesswert(" + mw + ") #############");
		log.info("ProbeId=" + probeOhneMw.getProbeId() + ": "
			+ proVerwInstance.addMesswert(probeOhneMw.getProbeId(), mw));

		Probe keineMwAenderung = proVerwInstance.findAll()
			.get(0);
		log.info("ProbeId=" + keineMwAenderung.getProbeId() + ": "
			+ proVerwInstance.addMesswert(keineMwAenderung.getProbeId(), mw));

		int mw2 = mw / 2;
		log.info("##### " + name + ": updateMesswert(" + mw2 + ") #############");
		Probe mwUpdate = proVerwInstance.findAll()
			.get(1);
		log.info("ProbeId=" + mwUpdate.getProbeId() + ": "
			+ proVerwInstance.updateMesswert(mwUpdate.getProbeId(), mw2));
		proVerwInstance.findAll()
			.forEach(p -> log.info(p.toString()));
	}

//	###################### Helper Meths #################################
	private static void generateProben(ProbenVerwalten proVerwInstance) {
		for (int i = 0; i < 10; i++) {
			proVerwInstance.addProbe(generateRandomProbe());
		}
		probeOhneMw = new Probe(LocalDateTime.now());
		proVerwInstance.addProbe(probeOhneMw);
	}

	private static Probe generateRandomProbe() {
		LocalTime t = LocalTime.MIN;
		int thisYear = LocalDate.now()
			.getYear();
		LocalDate d = LocalDate.ofEpochDay(ThreadLocalRandom.current()
			.nextInt(365))
			.withYear(thisYear);
		return new Probe(LocalDateTime.of(d, t), ThreadLocalRandom.current()
			.nextInt(Constants.MW_UPPER_BOUND + 1));
	}

	private static void alleProbenAusDbLoeschenUndIdGleich1(ProbenVerwalten db) {
		db.truncateTableProbe();
	}

}

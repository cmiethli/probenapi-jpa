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
	@Qualifier("db")
	private ProbenVerwalten pv;

	@Bean
	public CommandLineRunner demo(ProbeRepository repository) {
		return (args) -> {
			alleProbenAusDbLoeschenUndIdGleich1(pv);
			testProbenVerwalten(pv);
		};

	}

	private static void testProbenVerwalten(ProbenVerwalten proVerwInstance) {
		generateProben(proVerwInstance);

		String name = proVerwInstance.getClass()
				.getSimpleName();
		System.out.println("##### " + name + ": findAll() ##########");
		proVerwInstance.findAll()
				.forEach(System.out::println);

		System.out.println();
		System.out.println(
				"##### " + name + ": timeSorted(AeltesteZuerst) #############");
		boolean isAeltesteZuerst = true;
		proVerwInstance.timeSorted(isAeltesteZuerst)
				.forEach(System.out::println);

		System.out.println();
		System.out
				.println("#### " + name + ": filtered(Ergebnis.xxx) #############");
		proVerwInstance.filtered(Probe.Ergebnis.FRAGLICH)
				.forEach(System.out::println);
		proVerwInstance.filtered(Probe.Ergebnis.POSITIV)
				.forEach(System.out::println);
		proVerwInstance.filtered(Probe.Ergebnis.NEGATIV)
				.forEach(System.out::println);

		System.out.println();
		System.out.println("##### " + name + ": removeProbe(id) #############");
		System.out.println("remove id=0: " + proVerwInstance.removeProbe(0));
		proVerwInstance.findAll()
				.stream()
				.findAny()
				.ifPresentOrElse(p -> {
					long id = p.getProbeId();
					System.out.printf("remove id=%d: %s%n", id,
							proVerwInstance.removeProbe(id));
				}, () -> System.out.println("nothing to remove"));
		proVerwInstance.findAll()
				.forEach(System.out::println);

		System.out.println();
		int mw = 88;
//		int mw = -88; // IllegalArgExc
		System.out
				.println("##### " + name + ": addMesswert(" + mw + ") #############");
		System.out.println("ProbeId=" + probeOhneMw.getProbeId() + ": "
				+ proVerwInstance.addMesswert(probeOhneMw.getProbeId(), mw));

		Probe keineMwAenderung = proVerwInstance.findAll()
				.get(0);
		System.out.println("ProbeId=" + keineMwAenderung.getProbeId() + ": "
				+ proVerwInstance.addMesswert(keineMwAenderung.getProbeId(), mw));

		proVerwInstance.findAll()
				.forEach(System.out::println);

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

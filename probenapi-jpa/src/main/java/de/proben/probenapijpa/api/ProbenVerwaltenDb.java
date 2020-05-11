package de.proben.probenapijpa.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.proben.probenapijpa.persistence.Probe;
import de.proben.probenapijpa.persistence.Probe.Ergebnis;
import de.proben.probenapijpa.persistence.ProbeRepository;

/**
 * Database Implementierung von {@linkplain ProbenVerwalten}. Die Speicherung
 * der Proben laeuft ueber eine Datenbank in MySQL.
 * 
 * @author cmiethli
 *
 */

@Component("db")
public class ProbenVerwaltenDb implements ProbenVerwalten {

	@Autowired
	private ProbeRepository repository;

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Probe> findAll() {

		TypedQuery<Probe> query = em.createQuery("SELECT k FROM Probe k",
				Probe.class);
		return query.getResultList();
//		return repository.findAll();
	}

	@Override
	public List<Probe> timeSorted(boolean isAeltesteZuerst) {
		List<Probe> proben = repository.findAll();
		return timeSortedPackageScope(isAeltesteZuerst, proben);
	}

	@Override
	public List<Probe> filtered(Ergebnis ergebnis) {
		return repository.findByErgebnis(ergebnis);
	}

	@Override
	public void addProbe(Probe probe) {
		repository.save(probe);
	}

	@Override
	public void addProbe(LocalDateTime zeitpunkt, int messwert) {
		addProbe(new Probe(zeitpunkt, messwert));
	}

	@Override
	public void addProbe(LocalDateTime zeitpunkt) {
		addProbe(new Probe(zeitpunkt));
	}

	@Override
	public boolean removeProbe(long probeId) {
		repository.deleteById(probeId);
		return repository.existsById(probeId);
	}

	@Override
	public boolean addMesswert(long probeId, Integer messwert) {
		int i = 1;// repository.addMesswert(messwert, probeId);

//	TypedQuery<Probe> query = em
//	.createQuery("Probe.findByMesswert", Probe.class)
//	.setParameter("messwert", 0);
		return i == 1 ? true : false;
	}

//	######### Helper Meths ###########################
	static List<Probe> timeSortedPackageScope(boolean isAeltesteZuerst,
			List<Probe> proben) {
		Stream<Probe> probenSorted;
		if (isAeltesteZuerst) {
			probenSorted = proben.stream()
					.sorted((p1, p2) -> p1.getZeitpunkt()
							.compareTo(p2.getZeitpunkt()));
		} else {
			probenSorted = proben.stream()
					.sorted((p1, p2) -> p2.getZeitpunkt()
							.compareTo(p1.getZeitpunkt()));
		}
		return probenSorted.collect(Collectors.toList());
	}
}

package de.proben.probenapijpa.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.proben.probenapijpa.persistence.Probe;
import de.proben.probenapijpa.persistence.Probe.Ergebnis;
import de.proben.probenapijpa.persistence.ProbeRepository;
import de.proben.probenapijpa.util.Konstanten;

/**
 * Database Implementierung von {@linkplain ProbenVerwalten}. Die Speicherung
 * der Proben laeuft ueber eine Datenbank in MySQL oder H2.
 * 
 * @author cmiethli
 *
 */

@Component(Konstanten.DB_QUALIFIER)
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
		if (repository.existsById(probeId)) {
			repository.deleteById(probeId);
			return true;
		} else {
			return false;
		}
	}

	@Autowired
	DataSource ds;

	@Override
	@Transactional
	public void clearProben() { // TABLE fuer H2, in MariaDb optional
		Query q = em
			.createNativeQuery("TRUNCATE TABLE " + Konstanten.DB_NAME + ".probe");
		q.executeUpdate();

//		XXX gibt bestimmt bessere Variante als ds.toString()
		Query q2;
		String dsName = ds.toString()
			.toLowerCase();
		if (dsName.contains("mysql")) {
			q2 = em.createNativeQuery("UPDATE " + Konstanten.DB_NAME + "."
				+ Konstanten.SEQ_GEN + " SET next_val = 1");
		} else if (dsName.contains("h2")) {
			q2 = em.createNativeQuery("ALTER SEQUENCE " + Konstanten.DB_NAME + "."
				+ Konstanten.SEQ_GEN + " RESTART WITH 1");
		} else {
			throw new AssertionError("invalid database...");
		}
		q2.executeUpdate();
	}

	@Override
	@Transactional
	public boolean addMesswert(long probeId, Integer messwert) {
		Optional<Probe> probeOpt = repository.findById(probeId);
		boolean isAdded = false;
		if (probeOpt.isPresent()) {
			Probe p = probeOpt.get();
			if (p.getMesswert() != null) {
// messwert schon vorhanden >> keine Aktion
			} else {
				p.setMesswert(messwert);
				isAdded = updateProbe(messwert, probeId, p.getErgebnis());
			}
		}
		return isAdded;
	}

	@Override
	@Transactional
	public boolean updateMesswert(long probeId, Integer messwert) {
		Optional<Probe> probeOpt = repository.findById(probeId);
		boolean isAdded = false;
		if (probeOpt.isPresent()) {
			Probe p = probeOpt.get();
			p.setMesswert(messwert);
			isAdded = updateProbe(messwert, probeId, p.getErgebnis());
		}
		return isAdded;
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

	private boolean updateProbe(Integer messwert, long probeId,
		Ergebnis ergebnis) {
		Query probe = em
			.createQuery("UPDATE Probe p " + "SET p.messwert = :mw, "
				+ " p.ergebnis = :erg WHERE p.id = :id")
			.setParameter("mw", messwert)
			.setParameter("erg", ergebnis)
			.setParameter("id", probeId);
		return probe.executeUpdate() == 1 ? true : false;
	}

}

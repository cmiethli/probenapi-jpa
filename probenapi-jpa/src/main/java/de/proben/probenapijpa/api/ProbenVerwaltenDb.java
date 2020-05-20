package de.proben.probenapijpa.api;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
		if (repository.existsById(probeId)) {
			repository.deleteById(probeId);
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public void truncateTableProbe() { // TABLE fuer SQL >> H2, in MySQL optional
		Query q = em
//			.createNativeQuery("TRUNCATE TABLE " + Constants.dbName + ".probe");
			.createNativeQuery("TRUNCATE TABLE probe"); // TODO
																									// H2

		try {
			DatabaseMetaData meta = ((org.hibernate.engine.spi.SessionImplementor) em
				.getDelegate()).connection()
					.getMetaData();

			ResultSet resultSet = meta.getCatalogs(); // alle DBs von phpMyAdmin!

			while (resultSet.next()) {
				for (int i = 1; i <= resultSet.getMetaData()
					.getColumnCount(); i++) {
					System.out.println(resultSet.getMetaData()
						.getColumnName(i) // TABLE_CAT
						+ " -> " + resultSet.getObject(i));// DB Name
				}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		q.executeUpdate();
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

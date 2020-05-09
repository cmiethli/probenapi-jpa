package de.proben.probenapijpa.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ProbeRepository extends CrudRepository<Probe, Long> {
	@Override
	List<Probe> findAll();

	List<Probe> findByErgebnis(Probe.Ergebnis ergebnis);

//	@Query(value = "SELECT p FROM Probe p WHERE p.ergebnis= 'NEGATIV' ")
//	List<Probe> findByErgebnisNegativ();

	@Modifying
	@Transactional
	@Query("UPDATE Probe p SET p.messwert = :mw  WHERE p.id = :id")
	int addMesswert(@Param("mw") Integer mw, @Param("id") Long id);
}

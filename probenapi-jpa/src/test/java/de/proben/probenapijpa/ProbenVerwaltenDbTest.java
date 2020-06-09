package de.proben.probenapijpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import de.proben.probenapijpa.api.ProbenVerwalten;
import de.proben.probenapijpa.apps.ForTests;
import de.proben.probenapijpa.persistence.Probe;
import de.proben.probenapijpa.persistence.Probe.Ergebnis;
import de.proben.probenapijpa.util.Konstanten;

@SpringBootTest(classes = ForTests.class)
class ProbenVerwaltenDbTest {

	@Autowired
	@Qualifier(Konstanten.DB_QUALIFIER)
	private ProbenVerwalten db;

	private static LocalDateTime ldt = LocalDateTime
		.of(LocalDate.of(2001, 01, 01), LocalTime.of(0, 0));
	private static int mwNeg = Konstanten.MW_LOWER_BOUND_NEGATIV;
	private static int mwFrag = Konstanten.MW_LOWER_BOUND_FRAGLICH + 1;
	private static int mwPos = Konstanten.MW_UPPER_BOUND_POSITIV;
	private static int mwExc1 = Konstanten.MW_LOWER_BOUND - 1;
	private static int mwExc2 = Konstanten.MW_UPPER_BOUND + 1;

	private static Probe p1 = new Probe(ldt.plusDays(1), mwNeg);
	private static Probe p2 = new Probe(ldt, mwFrag);
	private static Probe p3 = new Probe(ldt.plusDays(2), mwPos);

	private static Probe p4 = new Probe(ldt.plusDays(7)); // ohne Messwert

	@BeforeEach
	void setUp() throws Exception {
		removeAllProben();
		db.addProbe(p1);
		db.addProbe(p2);
		db.addProbe(p3);
		db.addProbe(p4);
		System.out.println(db.findAll());
	}

	@Test
	void getAllRichtig() {
		List<Probe> proben = db.findAll();
		assertTrue(proben.contains(p1));
		assertTrue(proben.contains(p2));
		assertTrue(proben.contains(p3));
		assertTrue(proben.contains(p4));
		assertEquals(4, proben.size());
	}

	@Test
	void emptyProben() {
		removeAllProben();
		List<Probe> proben = db.findAll();
		assertTrue(proben.isEmpty());

		proben = db.filtered(Ergebnis.POSITIV);
		assertTrue(proben.isEmpty());

		proben = db.timeSorted(true);
		assertTrue(proben.isEmpty());
	}

	@Test
	void timeSortedRichtig() {
		boolean isAeltesteZuerst = true;
		List<Probe> proben = db.timeSorted(isAeltesteZuerst);
//	p1=ldt.plusDays(1), p2=ldt, p3=ldt.plusDays(2), p4=ldt.plusDays(7)
		assertEquals(p2, proben.get(0));
		assertEquals(p1, proben.get(1));
		assertEquals(p3, proben.get(2));
		assertEquals(p4, proben.get(3));

		isAeltesteZuerst = false;
		proben = db.timeSorted(isAeltesteZuerst);
		assertEquals(p4, proben.get(0));
		assertEquals(p3, proben.get(1));
		assertEquals(p1, proben.get(2));
		assertEquals(p2, proben.get(3));
	}

	@Test
	void filteredRichtig() {
//	p1=mwNeg, p2=mwFraglich, p3=mwPos
//		System.out.println(db.findAll()); // bei H2 fehlt p1, nicht nur hier... sporadisch
		List<Probe> proben = db.filtered(Ergebnis.NEGATIV);
		assertEquals(p1, proben.get(0));

		proben = db.filtered(Ergebnis.FRAGLICH);
		assertEquals(p2, proben.get(0));

		proben = db.filtered(Ergebnis.POSITIV);
		assertEquals(p3, proben.get(0));
	}

	@Test
	void removeProbeRichtig() {
		Probe p = db.findAll()
			.parallelStream()
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
		long id = p.getProbeId();
		assertTrue(db.removeProbe(id));
		assertFalse(db.removeProbe(id)); // p schon entfernt
		assertFalse(db.findAll()
			.contains(p));
	}

	@Test
	void addProbeRichtig() {
//	aus setUp()
		assertTrue(db.findAll()
			.contains(p1));

//	Probe p4 ohne Messwert
		assertTrue(db.findAll()
			.contains(p4));
	}

	@Test
	void addProbe_LocalDateTimeIntRichtig() {
		removeAllProben();
		db.addProbe(ldt, mwPos);
		assertTrue(ldt.equals(db.findAll()
			.get(0)
			.getZeitpunkt()));
		assertTrue(mwPos == db.findAll()
			.get(0)
			.getMesswert());
	}

	@Test
	void addProbe_NurLocalDateTimeRichtig() {
		removeAllProben();
		db.addProbe(ldt);
		assertTrue(ldt.equals(db.findAll()
			.get(0)
			.getZeitpunkt()));
		assertTrue(null == db.findAll()
			.get(0)
			.getMesswert());
		assertTrue(null == db.findAll()
			.get(0)
			.getErgebnis());
	}

	@Test
	void addMesswertRichtig() {
		Integer newMesswert = mwFrag;
		Ergebnis newErgebnis = Ergebnis.FRAGLICH;

//	Messwert noch nicht vorhanden
		assertTrue(db.addMesswert(4, newMesswert));
		assertEquals(newMesswert, db.findAll()
			.get(3)
			.getMesswert());
		assertEquals(newErgebnis, db.findAll()
			.get(3)
			.getErgebnis());

//	Probe nicht vorhanden
		assertFalse(db.addMesswert(0, newMesswert));
//	Messwert schon vorhanden
		assertFalse(db.addMesswert(1, newMesswert));
//p1 hat mwNeg
		assertFalse(newMesswert.equals(db.findAll()
			.get(0)
			.getMesswert()));
	}

	@Test
	void addProbeExc() {
		assertThrows(ConstraintViolationException.class,
			() -> db.addProbe(new Probe(ldt, mwExc1)));
		assertThrows(ConstraintViolationException.class,
			() -> db.addProbe(ldt, mwExc2));
	}

//	#######################################
//	######### Helper Meths #################
	private void removeAllProben() {
//	id beginnt wieder bei 1
		db.truncateTableProbe();
		p1.setProbeId(null);
		p2.setProbeId(null);
		p3.setProbeId(null);
		p4.setProbeId(null);
	}
}

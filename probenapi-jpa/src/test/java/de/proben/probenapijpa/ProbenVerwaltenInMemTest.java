package de.proben.probenapijpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
class ProbenVerwaltenInMemTest {

	@Autowired
	@Qualifier(Konstanten.IN_MEM_LIST_QUALIFIER)
	private ProbenVerwalten inMem;

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
		inMem.addProbe(p1);
		inMem.addProbe(p2);
		inMem.addProbe(p3);
		inMem.addProbe(p4);
	}

	@Test
	void getAllRichtig() {
		List<Probe> proben = inMem.findAll();
		assertTrue(proben.contains(p1));
		assertTrue(proben.contains(p2));
		assertTrue(proben.contains(p3));
		assertTrue(proben.contains(p4));
		assertEquals(4, proben.size());
	}

	@Test
	void emptyProben() {
		removeAllProben();
		List<Probe> proben = inMem.findAll();
		assertTrue(proben.isEmpty());

		proben = inMem.filtered(Ergebnis.POSITIV);
		assertTrue(proben.isEmpty());

		proben = inMem.timeSorted(true);
		assertTrue(proben.isEmpty());
	}

	@Test
	void timeSortedRichtig() {
		boolean isAeltesteZuerst = true;
		List<Probe> proben = inMem.timeSorted(isAeltesteZuerst);
//	p1=ldt.plusDays(1), p2=ldt, p3=ldt.plusDays(2), p4=ldt.plusDays(7)
		assertEquals(p2, proben.get(0));
		assertEquals(p1, proben.get(1));
		assertEquals(p3, proben.get(2));
		assertEquals(p4, proben.get(3));

		isAeltesteZuerst = false;
		proben = inMem.timeSorted(isAeltesteZuerst);
		assertEquals(p4, proben.get(0));
		assertEquals(p3, proben.get(1));
		assertEquals(p1, proben.get(2));
		assertEquals(p2, proben.get(3));
	}

	@Test
	void filteredRichtig() {
//	p1=mwNeg, p2=mwFraglich, p3=mwPos
		List<Probe> proben = inMem.filtered(Ergebnis.NEGATIV);
		assertEquals(p1, proben.get(0));

		proben = inMem.filtered(Ergebnis.FRAGLICH);
		assertEquals(p2, proben.get(0));

		proben = inMem.filtered(Ergebnis.POSITIV);
		assertEquals(p3, proben.get(0));
	}

	@Test
	void removeProbeRichtig() {
		assertTrue(inMem.removeProbe(1)); // p1
		assertFalse(inMem.removeProbe(1)); // p1 schon entfernt
		assertFalse(inMem.findAll()
			.contains(p1));
	}

	@Test
	void addProbeRichtig() {
//	aus setUp()
		assertTrue(inMem.findAll()
			.contains(p1));

//	Probe p4 ohne Messwert
		assertTrue(inMem.findAll()
			.contains(p4));
	}

	@Test
	void addProbe_LocalDateTimeIntRichtig() {
		removeAllProben();
		inMem.addProbe(ldt, mwPos);
		assertTrue(ldt.equals(inMem.findAll()
			.get(0)
			.getZeitpunkt()));
		assertTrue(mwPos == inMem.findAll()
			.get(0)
			.getMesswert());
	}

	@Test
	void addProbe_NurLocalDateTimeRichtig() {
		removeAllProben();
		inMem.addProbe(ldt);
		assertTrue(ldt.equals(inMem.findAll()
			.get(0)
			.getZeitpunkt()));
		assertTrue(null == inMem.findAll()
			.get(0)
			.getMesswert());
		assertTrue(null == inMem.findAll()
			.get(0)
			.getErgebnis());
	}

	@Test
	void addMesswertRichtig() {
		Integer newMesswert = mwFrag;
		Ergebnis newErgebnis = Ergebnis.FRAGLICH;

//	Messwert noch nicht vorhanden
		assertTrue(inMem.addMesswert(4, newMesswert));
		assertEquals(newMesswert, inMem.findAll()
			.get(3)
			.getMesswert());
		assertEquals(newErgebnis, inMem.findAll()
			.get(3)
			.getErgebnis());

//	Probe nicht vorhanden
		assertFalse(inMem.addMesswert(0, newMesswert));
//	Messwert schon vorhanden
		assertFalse(inMem.addMesswert(1, newMesswert));
//p1 hat mwNeg
		assertFalse(newMesswert.equals(inMem.findAll()
			.get(0)
			.getMesswert()));
	}

	@Test
	void addProbeExc() {
		assertThrows(IllegalArgumentException.class,
			() -> inMem.addProbe(new Probe(ldt, mwExc1)));
		assertThrows(IllegalArgumentException.class,
			() -> inMem.addProbe(ldt, mwExc2));
	}

//	#######################################
//	######### Helper Meths #################
	private void removeAllProben() {
//		List<Probe> list = new ArrayList<Probe>(inMem.findAll());
// List<Probe> proben in ProVerwInMem MUSS concurrent sein!
		List<Probe> list = inMem.findAll();
		list.stream()
			.mapToLong(p -> p.getProbeId())
			.forEach(inMem::removeProbe);
	}
}

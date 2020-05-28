package de.proben.probenapijpa.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import de.proben.probenapijpa.persistence.Probe;
import de.proben.probenapijpa.util.Konstanten;

/**
 * InMemory Implementierung von {@linkplain ProbenVerwalten}. Die Speicherung
 * der Proben
 * laeuft InMemory ueber eine java.util.list
 * 
 * @author cmiethli
 *
 */
@Component(Konstanten.IN_MEM_LIST_QUALIFIER)
public class ProbenVerwaltenInMemList implements ProbenVerwalten {
	private List<Probe> proben;

	ProbenVerwaltenInMemList() {
//Concurrent weil: siehe ProVerwInMemTEST.java >> removeAllProben()
		this.proben = new CopyOnWriteArrayList<Probe>();
	}

	@Override
	public List<Probe> findAll() {
		return proben;
	}

	@Override
	public List<Probe> timeSorted(boolean isAeltesteZuerst) {
		return timeSortedPackageScope(isAeltesteZuerst, proben);
	}

	@Override
	public List<Probe> filtered(Probe.Ergebnis erg) {
		return proben.stream()
			.filter(p -> p.getErgebnis() == erg)
			.collect(Collectors.toList());
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
	public void addProbe(Probe probe) {
		setProbeId(probe);
		proben.add(probe);
	}

	@Override
	public boolean removeProbe(long id) {
		Optional<Probe> probeToRemove = getProbe(id);

		return probeToRemove.isPresent() ? proben.remove(probeToRemove.get())
			: false;
	}

	@Override
	public boolean addMesswert(long probeId, Integer messwert) {
		Optional<Probe> pr = getProbe(probeId);
		boolean isMesswertSet = false;
		if (pr.isPresent()) {
			if (pr.get()
				.getMesswert() == null) {
// Messwert noch nicht vorhanden
				pr.get()
					.setMesswert(messwert);
				isMesswertSet = true;
			} else {
// Messwert schon vorhanden
				isMesswertSet = false;
			}
		}
		return isMesswertSet;
	}

	@Override
	public boolean updateMesswert(long probeId, Integer messwert) {
		Optional<Probe> pr = getProbe(probeId);
		if (pr.isPresent()) {
			pr.get()
				.setMesswert(messwert);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void truncateTableProbe() {
		proben.clear();
	}

// ############# Helper Meths ####################
	private void setProbeId(Probe pr) {
		if (pr.getProbeId() == null) {
			OptionalLong maxId = proben.parallelStream()
				.mapToLong(p -> p.getProbeId())
				.max();
			if (maxId.isEmpty()) { // first Probe
				pr.setProbeId(1L);
			} else {
				pr.setProbeId(maxId.getAsLong() + 1L);
			}
		}
	}

	private Optional<Probe> getProbe(long id) {
		return proben.parallelStream()
			.filter(p -> p.getProbeId() == id)
			.findAny();
	}

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

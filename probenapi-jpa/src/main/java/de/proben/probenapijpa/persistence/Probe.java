package de.proben.probenapijpa.persistence;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.Id;

import de.proben.probenapijpa.util.Constants;

@Entity
public class Probe {

	private static long idCounter = 1;
	@Id
	private Long probeId;
	private LocalDateTime zeitpunkt;
	private Integer messwert;
	private Ergebnis ergebnis;

	public Probe() {

	}

	public Probe(LocalDateTime time) {
		probeId = idCounter++;
		this.zeitpunkt = time;
	}

	public Probe(LocalDateTime time, Integer messwert) {
		testMesswert(messwert);

		probeId = idCounter++;
		this.zeitpunkt = time;
		this.messwert = messwert;
		berechneErgebnis();
	}

	public Probe(Long id, LocalDateTime time) {
		this.probeId = id;
		idCounter = id;
		this.zeitpunkt = time;
	}

	public Probe(Long id, LocalDateTime time, Integer mw, Ergebnis erg) {
		this.probeId = id;
		idCounter = id;
		this.zeitpunkt = time;
		this.messwert = mw;
		this.ergebnis = erg;
	}

	@Override
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
//		Java12 Feature
		String formatKilosStr;
		if (messwert == null) {
			formatKilosStr = null;
		} else {
			NumberFormat formatKilos = NumberFormat.getCompactNumberInstance(
					new Locale("en", "US"), NumberFormat.Style.SHORT);
			formatKilos.setMaximumFractionDigits(1);
			formatKilosStr = formatKilos.format(messwert);
		}

		return String.format("[id=%3d, zeit=%8s, messwert=%5s, ergebnis=%s",
				probeId, zeitpunkt.format(formatter), formatKilosStr, ergebnis + "]");
//		return "[id=" + probeId + ", zeit="
//				+ zeitpunkt.truncatedTo(ChronoUnit.MINUTES)
//						.toLocalDate()
//				+ ", mw=" + messwert + ", erg=" + ergebnis + "]";
	}

	@Override
	public int hashCode() {
		return Long.valueOf(this.getProbeId())
				.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Probe)) {
			return false;
		}

		Probe other = (Probe) object;
		if (this.getProbeId() == other.getProbeId()) {
			return true;
		} else {
			return false;
		}

	}

	public Long getProbeId() {
		return probeId;
	}

	public void setProbeId(Long probeId) {
		this.probeId = probeId;
	}

	public LocalDateTime getZeitpunkt() {
		return zeitpunkt;
	}

	public void setZeitpunkt(LocalDateTime zeitpunkt) {
		this.zeitpunkt = zeitpunkt;
	}

	public Integer getMesswert() {
		return messwert;
	}

	public void setMesswert(Integer messwert) {
		this.messwert = messwert;
		berechneErgebnis();
	}

	public Ergebnis getErgebnis() {
		return ergebnis;
	}

	public void setErgebnis(Ergebnis ergebnis) {
		this.ergebnis = ergebnis;
	}

	// Enum
	public static enum Ergebnis {
		POSITIV, NEGATIV, FRAGLICH
	}

//	##################### Helper Meths ##################
	private void berechneErgebnis() {
		if (messwert > Constants.MW_UPPER_BOUND_FRAGLICH) {
			ergebnis = Ergebnis.POSITIV;
		} else if (messwert >= Constants.MW_LOWER_BOUND_FRAGLICH
				&& messwert <= Constants.MW_UPPER_BOUND_FRAGLICH) {
			ergebnis = Ergebnis.FRAGLICH;
		} else {
			ergebnis = Ergebnis.NEGATIV;
		}
	}

	private void testMesswert(Integer messwert) {
		if (messwert < Constants.MW_LOWER_BOUND
				|| messwert > Constants.MW_UPPER_BOUND) {
			throw new IllegalArgumentException("invalid messwert:" + messwert);
		}
	}

}

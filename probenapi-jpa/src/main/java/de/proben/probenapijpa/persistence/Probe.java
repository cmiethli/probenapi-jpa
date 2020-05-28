package de.proben.probenapijpa.persistence;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.validator.constraints.Range;

import de.proben.probenapijpa.util.Konstanten;

@Entity
@SequenceGenerator(name = "seq", initialValue = 1, allocationSize = 10)
public class Probe {

	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
	private Long probeId;

	@Column(columnDefinition = "TIMESTAMP", nullable = false)
	private LocalDateTime zeitpunkt;
//	Validation API benoetigt 
	@Range(min = Konstanten.MW_LOWER_BOUND, max = Konstanten.MW_UPPER_BOUND)
	private Integer messwert;
	@Enumerated(EnumType.STRING)
	@Column(length = 8) // FRAGLICH hat 8 Buchstaben!
	private Ergebnis ergebnis;

	public Probe() {
		this.zeitpunkt = LocalDateTime.now();
	}

	public Probe(LocalDateTime time) {
		this.zeitpunkt = time;
	}

	public Probe(LocalDateTime time, Integer messwert) {
		this.zeitpunkt = time;
		this.messwert = messwert;
		berechneErgebnis();
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

//	Getter+Setter	
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
		if (messwert > Konstanten.MW_UPPER_BOUND_FRAGLICH) {
			ergebnis = Ergebnis.POSITIV;
		} else if (messwert >= Konstanten.MW_LOWER_BOUND_FRAGLICH
			&& messwert <= Konstanten.MW_UPPER_BOUND_FRAGLICH) {
			ergebnis = Ergebnis.FRAGLICH;
		} else {
			ergebnis = Ergebnis.NEGATIV;
		}
	}
}

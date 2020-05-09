package de.proben.probenapijpa.api;

/**
 * Diese Klasse ist eine Factory fuer Instanzen von
 * {@linkplain ProbenVerwalten}.
 * 
 * @author cmiethli
 *
 */
public class ProbenVerwaltenFactory {

	/**
	 * private Constructor
	 */
	private ProbenVerwaltenFactory() {
	}

	/**
	 * Gibt eine Instanz vom Typ ProbenVerwalten zurueck.
	 * 
	 * @param instance enum {@link ProbenVerwaltenFactory.Instance}: IN_MEM, DB
	 * @return IN_MEM gibt eine Instanz von ProbenVerwaltenInMem zurueck,<br>
	 *         DB gibt eine Instanz von ProbenVerwaltenDb zurueck
	 */
	public static ProbenVerwalten getInstance(Instance instance) {
		ProbenVerwalten inst;
		switch (instance) {
		case IN_MEM:
			inst = new ProbenVerwaltenInMem();
			break;
		case DB:
			inst = new ProbenVerwaltenDb();
			break;
		default:
			throw new AssertionError("invalid instance...");
////		fuer Prerelease ist Java 13 notwendig...
//		case IN_MEM -> instance = new ProbenVerwaltenInMem();
//		case DB -> instance = null;
//		default -> throw new AssertionError();
		}
		return inst;
	}

	/**
	 * Hilfsenum fuer {@link ProbenVerwaltenFactory#getInstance(Instance)
	 * getInstance(Instance)}. Sie bestimmt die zurueck gegebene Instanz.
	 * 
	 * @author cmiethli
	 *
	 */
	public static enum Instance {
		/**
		 * Gibt bei {@link #getInstance(Instance)} Instanz
		 * vom Typ ProbenVerwaltenInMem zurueck
		 */
		IN_MEM,
		/**
		 * Gibt bei {@link #getInstance(Instance)} Instanz vom Typ ProbenVerwaltenDb
		 * zurueck
		 */
		DB
	}
}

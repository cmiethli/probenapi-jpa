package de.proben.probenapijpa.util;

public final class Constants {
	private Constants() {
	}

	public static final int MW_LOWER_BOUND = 0;
	public static final int MW_UPPER_BOUND = 10_000;

	public static final int MW_LOWER_BOUND_FRAGLICH = 4000;
	public static final int MW_UPPER_BOUND_FRAGLICH = 6000;

	public static final int MW_LOWER_BOUND_POSITIV = MW_UPPER_BOUND_FRAGLICH + 1;
	public static final int MW_UPPER_BOUND_POSITIV = MW_UPPER_BOUND;

	public static final int MW_LOWER_BOUND_NEGATIV = MW_LOWER_BOUND;
	public static final int MW_UPPER_BOUND_NEGATIV = MW_LOWER_BOUND_FRAGLICH - 1;
}
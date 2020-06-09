package de.proben.probenapijpa.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public final class Konstanten {
	private Konstanten() {
	}

	public static final int MW_LOWER_BOUND = 0;
	public static final int MW_UPPER_BOUND = 10_000;

	public static final int MW_LOWER_BOUND_FRAGLICH = 4000;
	public static final int MW_UPPER_BOUND_FRAGLICH = 6000;

	public static final int MW_LOWER_BOUND_POSITIV = MW_UPPER_BOUND_FRAGLICH + 1;
	public static final int MW_UPPER_BOUND_POSITIV = MW_UPPER_BOUND;

	public static final int MW_LOWER_BOUND_NEGATIV = MW_LOWER_BOUND;
	public static final int MW_UPPER_BOUND_NEGATIV = MW_LOWER_BOUND_FRAGLICH - 1;

	public static final String DB_QUALIFIER = "db";
	public static final String IN_MEM_LIST_QUALIFIER = "inMemList";

	public static final String SEQ_GEN = "seq_gen";

//	Maven Properties 
	public static final String DB_NAME;
	static {
		Resource resource = new ClassPathResource("application.properties");
		Properties props = new Properties();
		try (InputStream inputStream = resource.getInputStream();) {
			props.load(inputStream);
			DB_NAME = props.getProperty("dbName");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
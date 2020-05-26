package de.proben.probenapijpa.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
// alle Componenten finden
@ComponentScan(basePackages = { "de.proben.probenapijpa.api" })
//@PropertySource({
//	"classpath:persistence-${persistenceTarget:mysql}.properties" })
@PropertySource({ "classpath:persistence-${persistenceTarget:h2}.properties" })
public class Config {

//// hier waere Config Factory von ProbenVerwalten
//// nicht mehr notwendig da Klassen als Component annotiert
//	@Bean("db")
//	public ProbenVerwalten a() {
//		return ProbenVerwaltenFactory.getInstance(Instance.DB);
//	}
//
//	@Bean("inMem")
//	public ProbenVerwalten b() {
//		return ProbenVerwaltenFactory.getInstance(Instance.IN_MEM);
//	}
}

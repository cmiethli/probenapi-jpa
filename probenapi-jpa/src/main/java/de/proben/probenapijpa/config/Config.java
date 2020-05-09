package de.proben.probenapijpa.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// alle Componenten finden
@ComponentScan(basePackages = { "de.proben.probenapijpa.api" })
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

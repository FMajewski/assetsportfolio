package portfolio.porfolio;

import org.springframework.boot.SpringApplication;

public class TestPorfolioApplication {

	public static void main(String[] args) {
		SpringApplication.from(PorfolioApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

package portfolio.porfolio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PorfolioApplicationTests {

	@Test
	void contextLoads() {
	}

}

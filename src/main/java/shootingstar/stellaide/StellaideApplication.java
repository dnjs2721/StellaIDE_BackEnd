package shootingstar.stellaide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class StellaideApplication {

	public static void main(String[] args) {
		SpringApplication.run(StellaideApplication.class, args);
	}

}

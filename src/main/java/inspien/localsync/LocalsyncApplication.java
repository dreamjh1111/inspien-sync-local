package inspien.localsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LocalsyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalsyncApplication.class, args);
	}

}

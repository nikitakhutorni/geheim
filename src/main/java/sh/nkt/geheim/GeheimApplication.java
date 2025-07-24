package sh.nkt.geheim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// required for @Scheduled tasks, such as the purge job
@EnableScheduling
public class GeheimApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeheimApplication.class, args);
	}

}

package ifrn.pi.comercio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ComercioApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComercioApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("senha123"));
	}

}

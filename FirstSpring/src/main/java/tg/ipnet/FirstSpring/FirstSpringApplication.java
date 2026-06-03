package tg.ipnet.FirstSpring;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class FirstSpringApplication{
	
	public static void main(String[] args) {
		SpringApplication.run(FirstSpringApplication.class, args);
	}
	
}

package hello;



import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class Application {
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        // simple DS for test (not for production!)
        
        // now lets create some tables
        DatabaseTest x = new DatabaseTest();
        x.createTables();
    }
}

package hello;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class DatabaseTest {
	
	public DatabaseTest() {
	}
	
	public void createTables() {
		
	    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
	    dataSource.setDriverClass(org.h2.Driver.class);
	    dataSource.setUsername("sa");
	    dataSource.setUrl("jdbc:h2:mem");
	    dataSource.setPassword("");

	    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

	    System.out.println("Creating tables");
	    jdbcTemplate.execute("drop table customers if exists");
	    jdbcTemplate.execute("create table customers(" +
	            "id serial, first_name varchar(255), last_name varchar(255))");

	    String[] names = "John Woo;Jeff Dean;Josh Bloch;Josh Long".split(";");
	    for (String fullname : names) {
	        String[] name = fullname.split(" ");
	        System.out.printf("Inserting customer record for %s %s\n", name[0], name[1]);
	        jdbcTemplate.update(
	                "INSERT INTO customers(first_name,last_name) values(?,?)",
	                name[0], name[1]);
	    }

	    System.out.println("Querying for customer records where first_name = 'Josh':");
	}
}

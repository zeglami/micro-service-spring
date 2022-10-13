package me.zegit.customerservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/*
https://fr.wikipedia.org/wiki/HATEOAS
By default we get the HATEOAS fomat

To use the APIs:
http://localhost:8081/customers?page=0&size=2
http://localhost:8081/customers
http://localhost:8081/customers/1

to access to the database:
http://localhost:8081/h2-console/(see application.proprities)
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
}

@RepositoryRestResource
interface CustomerRepository extends JpaRepository<Customer, Long> {

}

//http://localhost:8081/customers/2?projection=p1
@Projection(name = "p1", types=Customer.class)
interface CustomerProjection{
    public Long getId();
    public String getName();
}

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }


    @Bean
    CommandLineRunner start(CustomerRepository customerRepository, RepositoryRestConfiguration repositoryRestConfiguration) {
        return args -> {
            repositoryRestConfiguration.exposeIdsFor(Customer.class);
            customerRepository.save(new Customer(null, "Abdel", "Abdel@contact.ma"));
            customerRepository.save(new Customer(null, "Yim", "Yim@contact.ma"));
            customerRepository.save(new Customer(null, "Antho", "Antho@contact.ma"));
            customerRepository.findAll().forEach(System.out::println);
        };
    }
}

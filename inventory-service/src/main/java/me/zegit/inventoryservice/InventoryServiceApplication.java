package me.zegit.inventoryservice;

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
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/*
To test the APIs
http://localhost:8082/products
http://localhost:8082/products/1

to access to the database:
http://localhost:8082/h2-console/(see application.proprities)
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
}

@RepositoryRestResource
interface ProductRepository extends JpaRepository<Product, Long> {
}

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(ProductRepository productRepository, RepositoryRestConfiguration repositoryRestConfiguration) {
        return args -> {
            repositoryRestConfiguration.exposeIdsFor(Product.class);
            productRepository.save(new Product(null, "Computer Desk Top HP", 900));
            productRepository.save(new Product(null, "Printer Epson", 80));
            productRepository.save(new Product(null, "MacBook Pro Lap Top", 1800));
            productRepository.findAll().forEach(System.out::println);
        };
    }

}

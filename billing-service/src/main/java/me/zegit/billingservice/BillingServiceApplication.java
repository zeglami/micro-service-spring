package me.zegit.billingservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
class Bill {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Date billingDate;
	private long customerID;
	@OneToMany(mappedBy = "bill")
	private Collection<ProductItem> productItems;

}
@RepositoryRestResource
interface BillRepository extends JpaRepository<Bill, Long> {
}
@Projection(name="full",types=Bill.class)
interface BillProjection{
	public Long getId();
	public Date getBillingDate();
	public Long getCustomerID();
	public Collection<ProductItem> getProductItems();

}



@RepositoryRestResource
interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
	List<ProductItem> findByBillId(Long billID);
}

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
class ProductItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private long productID;
	private double price;
	private double quantity;
	@ManyToOne
	private Bill bill;
}

@Data
class Customer {
	private Long id;
	private String name;
	private String email;
}


@FeignClient(name = "CUSTOMER-SERVICE")
interface  CustomerService{
@GetMapping("/customers/{id}")
public Customer findCustomerById(@PathVariable(name="id") Long id);
}

@Data
class Product {
	private Long id;
	private String name;
	private double price;
}


@FeignClient(name = "INVENTORY-SERVICE")
interface  InventoryService{
	@GetMapping("/products/{id}")
	public Product findProductById(@PathVariable(name="id") Long id);
}

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
	}



	@Bean
	CommandLineRunner start(BillRepository billRepository,ProductItemRepository productItemRepository,CustomerService customerService,InventoryService inventoryService){

		return args -> {
			Customer c1=customerService.findCustomerById(1L);
			System.out.println("------------------------------------------------");
			System.out.println(c1.toString());
			System.out.println(c1.getEmail());
			System.out.println("------------------------------------------------");

			Product p1=inventoryService.findProductById(1L);
			System.out.println("------------------------------------------------");
			System.out.println(p1.getId());
			System.out.println(p1.toString());
			System.out.println(p1.getName());
			System.out.println("------------------------------------------------");
		Bill bill1=	billRepository.save(new Bill(null,new Date(),1L,null));
			productItemRepository.save(new ProductItem(null, p1.getId(), p1.getPrice(), 30,bill1));
			Product p2=inventoryService.findProductById(2L);
			productItemRepository.save(new ProductItem(null, p2.getId(), p2.getPrice(), 30,bill1));
			Product p3=inventoryService.findProductById(2L);
			productItemRepository.save(new ProductItem(null, p3.getId(), p3.getPrice(), 30,bill1));

		};
	}

}

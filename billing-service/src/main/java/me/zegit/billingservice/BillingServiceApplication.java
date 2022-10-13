package me.zegit.billingservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


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
	@Transient
	private Customer customer;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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
	@Transient// dont serialize to save in the data base
	private Product product;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private long productID;
	private double price;
	private double quantity;
	@ManyToOne
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//JSON IGNORE
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

	@GetMapping("/products")
	public PagedModel<Product> findAllProducts();
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

			Bill bill1=	billRepository.save(new Bill(null,new Date(),null,c1.getId(),null));
			PagedModel<Product> products=inventoryService.findAllProducts();

			products.getContent().forEach(

					p->{
						productItemRepository.save(new ProductItem(null, null,p.getId(), p.getPrice(), 30,bill1));
					}

			);



		};
	}

}


@RestController
class BillRestControler{
	@Autowired
	private BillRepository billRepository;
	@Autowired
	private ProductItemRepository productItemRepository;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private InventoryService inventoryService;

	@GetMapping("fullBill/{id}")
	public Bill getBill(@PathVariable(name="id") Long id){
		Bill bill=billRepository.findById(id).get();
		bill.setCustomer(customerService.findCustomerById(bill.getCustomerID()));
		bill.getProductItems().forEach(pi->{
			pi.setProduct(inventoryService.findProductById(pi.getProductID()));
		});
		return bill;
	}
}
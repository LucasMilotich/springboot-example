package rakuten.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import rakuten.domain.Product;

@Component
public interface ProductRepository extends CrudRepository<Product, Long> {

}

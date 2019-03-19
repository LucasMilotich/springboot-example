package rakuten.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rakuten.clients.fixer.FixerClient;
import rakuten.domain.Product;
import rakuten.exceptions.ConvertCurrencyException;
import rakuten.exceptions.ProductNotFoundException;
import rakuten.repository.ProductRepository;

import java.util.Optional;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(FixerClient.class);

    @Autowired
    public ProductService(ProductRepository productRepository, FixerClient fixerClient) {
        this.productRepository = productRepository;
        this.fixerClient = fixerClient;
    }

    private final ProductRepository productRepository;
    private final FixerClient fixerClient;

    @Value("${currency}")
    private String currency;

    @CachePut("products")
    public Product create(Product product) {

        convertCurrencyAndPrice(product);

        return productRepository.save(product);
    }

    @Cacheable("products")
    public Product show(Long id) throws Exception {

        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            return product.get();
        } else {
            throw new ProductNotFoundException(id.toString() + " not found");
        }
    }

    @CacheEvict("products")
    public void delete(long id) throws Exception {

        Product product = show(id);
        productRepository.delete(product);
    }

    @CachePut("products")
    public Product update(long id, String categoryId) throws Exception {

        Product product = show(id);
        product.setProductCategory(categoryId);
        return productRepository.save(product);

    }

    private void convertCurrencyAndPrice(Product product) {

        if (!currency.equals(product.getCurrency())) {
            try {
                product.setPrice(fixerClient.convertCurrency(product.getPrice(), product.getCurrency(), currency));
                product.setCurrency(currency);

            } catch (ConvertCurrencyException e){
                logger.error(e.getMessage());
            }

        }
    }

    public void updateCategory(long productId, long categoryId) throws Exception {
        Product product = show(productId);
        product.setProductCategory(Long.toString(categoryId));
        productRepository.save(product);
    }
}

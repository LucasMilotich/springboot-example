package rakuten.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.cache.annotation.Cacheable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Cacheable
public class Product {

    public Product(){

    }

    public Product(String productName, String productCategory, String currency, BigDecimal price) {
        this.productName = productName;
        this.productCategory = productCategory;
        this.currency = currency;
        this.price = price;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @JsonProperty("id")
    long id;

    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("product_category")
    private String productCategory;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("price")
    private BigDecimal price;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


}

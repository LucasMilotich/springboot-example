package rakuten.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rakuten.domain.Product;
import rakuten.services.ProductService;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @RequestMapping(path = "/product" , method = RequestMethod.POST, produces={"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) {

        product.setCurrency(product.getCurrency().toUpperCase());

        return productService.create(product);


    }

    @RequestMapping(path = "/product/{id}" , method = RequestMethod.GET, produces={"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public Product show(@PathVariable( "id") Long id) throws Exception {

        return productService.show(id);

    }

    @RequestMapping(path = "/product/{id}" , method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable( "id") Long id) throws Exception {

         productService.delete(id);

    }

    @RequestMapping(path = "/product/{id}/category/{category_id}" , method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public Product updateCategory(@PathVariable( "id") Long id, @PathVariable( "category_id") String categoryId) throws Exception {


        return productService.update(id, categoryId);

    }

}

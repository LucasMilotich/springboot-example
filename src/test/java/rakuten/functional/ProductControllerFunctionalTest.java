package rakuten.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import rakuten.ProductAPIApplication;
import rakuten.clients.fixer.FixerClient;
import rakuten.clients.fixer.FixerConvertResponse;
import rakuten.domain.Product;
import rakuten.exceptions.ConvertCurrencyException;
import rakuten.exceptions.ProductNotFoundException;
import rakuten.services.ProductService;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;




@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@TestPropertySource(locations="classpath:test.properties")
@EnableWebMvc
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(classes=ProductAPIApplication.class)

public class ProductControllerFunctionalTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @MockBean
    private FixerClient fixerClient;

    @Value("${currency}")
    private String currency;



    @Test
    public void createProductTest() throws Exception {


        Product product =new Product("test", "category", "COL", new BigDecimal(123));

        Mockito.when(fixerClient.convertCurrency(product.getPrice(), product.getCurrency(),currency )).thenReturn( new BigDecimal(10));


        MvcResult resultPost = mockMvc.perform(MockMvcRequestBuilders
                .post("/product")
                .content(asJsonString(product))
            .contentType(MediaType.APPLICATION_JSON)

                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_name", is("test")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_category", is("category")))
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();

        Product actual = mapper.readValue(resultPost.getResponse().getContentAsString(), Product.class);

        Assert.assertEquals(actual.getPrice(), new BigDecimal(10));
        Assert.assertEquals(actual.getCurrency(), currency);

    }


    @Test
    public void createProductTestWithErrorInFixerClient() throws Exception {


        Product product =new Product("test", "category", "COL", new BigDecimal(123));

        Mockito.when(fixerClient.convertCurrency(product.getPrice(), product.getCurrency(),currency )).thenThrow(ConvertCurrencyException.class);


        MvcResult resultPost = mockMvc.perform(MockMvcRequestBuilders
                .post("/product")
                .content(asJsonString(product))
                .contentType(MediaType.APPLICATION_JSON)

                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_name", is("test")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_category", is("category")))
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();

        Product actual = mapper.readValue(resultPost.getResponse().getContentAsString(), Product.class);

        Assert.assertEquals(actual.getPrice(), product.getPrice());
        Assert.assertEquals(actual.getCurrency(), product.getCurrency());

    }

    @Test
    public void showProductTest() throws Exception {

        Product product = new Product("productName","category1","COL",new BigDecimal(12));
        product = productService.create(product);


        mockMvc.perform(MockMvcRequestBuilders
                .get("/product/" + Long.toString(product.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_name", is("productName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_category", is("category1")));


    }

    @Test(expected = ProductNotFoundException.class)
    public void deleteProductTest() throws  Exception {

        Product product = new Product("productName","category1","COL",new BigDecimal(12));
        product = productService.create(product);


        mockMvc.perform(MockMvcRequestBuilders
                .delete("/product/" + Long.toString(product.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        productService.show(product.getId());


    }

    @Test()
    public void changeCategory() throws  Exception {

        Product product = new Product("productName","category1","COL",new BigDecimal(12));
        product = productService.create(product);

        String category = "1";

        mockMvc.perform(MockMvcRequestBuilders
                .put("/product/" + Long.toString(product.getId()) + "/category/" + category )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Assert.assertEquals(productService.show(product.getId()).getProductCategory(), category);


    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

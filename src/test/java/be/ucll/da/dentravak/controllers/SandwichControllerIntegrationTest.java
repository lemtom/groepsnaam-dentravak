package be.ucll.da.dentravak.controllers;

import be.ucll.da.dentravak.Application;
import be.ucll.da.dentravak.model.Sandwich;
import be.ucll.da.dentravak.repositories.SandwichRepository;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static be.ucll.da.dentravak.model.SandwichTestBuilder.aSandwich;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SandwichControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private SandwichRepository sandwichRepository;

    @Before
    public void setUpASavedSandwich() {
        sandwichRepository.deleteAll();
    }

    @Test
    public void testGetSandwiches_NoSavedSandwiches_EmptyList() throws JSONException {
        String actualSandwiches = httpGet("/sandwiches");
        String expectedSandwiches = "[]";

        assertThatJson(actualSandwiches).isEqualTo(expectedSandwiches);
    }

    @Test
    public void testPostSandwich() throws JSONException {
        Sandwich sandwich = aSandwich().withName("Americain").withIngredients("Vlees").withPrice(4.0).build();

        String actualSandwichAsJson = httpPost("/sandwiches", sandwich);
        String expectedSandwichAsJson = "{\"id\":\"${json-unit.ignore}\",\"name\":\"Americain\",\"ingredients\":\"Vlees\",\"price\":4}";

        assertThatJson(actualSandwichAsJson).isEqualTo(expectedSandwichAsJson);
    }

    @Test
    public void testPutSandwich() throws JSONException {
       // throw new RuntimeException("Implement this test and then the production code");
        Sandwich sandwich = aSandwich().withName("Testkaas").withIngredients("Vlees").withPrice(4.0).build();
        String expectedSandwichAsJson = "{\"id\":\"${json-unit.ignore}\",\"name\":\"Testkaas\",\"ingredients\":\"Vlees\",\"price\":4}";
        String actualSandwichAsJson = httpPost("/sandwiches", sandwich);


        assertThatJson(actualSandwichAsJson).isEqualTo(expectedSandwichAsJson);
//now updates
        sandwich = aSandwich().withName("Testkaas").withIngredients("Vlees").withPrice(5.0).build();

        actualSandwichAsJson = httpPut("/sandwiches/" + sandwichRepository.findByName(sandwich.getName()).get(0).getId(), sandwich);

        expectedSandwichAsJson = "{\"id\":\"${json-unit.ignore}\",\"name\":\"Testkaas\",\"ingredients\":\"Vlees\",\"price\":5}";

        assertThatJson(actualSandwichAsJson).isEqualTo(expectedSandwichAsJson);

    }

    @Test
    public void testGetSandwiches_WithSavedSandwiches_ListWithSavedSandwich() throws JSONException {
        Sandwich sandwich1 = aSandwich().withName("Testkaas").withIngredients("Kaas").withPrice(4.44).build();
        Sandwich sandwich2 = aSandwich().withName("Tesp").withIngredients("Tesp").withPrice(5.44).build();
        String actualSandwich1AsJson = httpPost("/sandwiches", sandwich1);
        String actualSandwich2AsJson = httpPost("/sandwiches", sandwich2);
        String expectedSandwiches = "[" + actualSandwich1AsJson + ", " + actualSandwich2AsJson + "]";
        String actualSandwiches = httpGet("/sandwiches");
        assertThatJson(actualSandwiches).isEqualTo(expectedSandwiches);
        }
}

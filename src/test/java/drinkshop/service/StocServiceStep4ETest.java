package drinkshop.service;

import drinkshop.domain.Stoc;
import drinkshop.repository.file.FileStocRepository;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class StocServiceStep4ETest {

    private FileStocRepository stocRepo;
    private StocValidator stocValidator;
    private StocService stocService;
    private final String testFileName = "test_stoc_integration_final.csv";

    @BeforeEach
    void setUp() {
        try {
            new File(testFileName).createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Step 4: Integrare totală. S, V, R și E sunt clase reale (nicio clasă mock-uită).
        stocRepo = new FileStocRepository(testFileName);
        stocValidator = new StocValidator();
        stocService = new StocService(stocRepo, stocValidator);
    }

    @AfterEach
    void tearDown() {
        new File(testFileName).delete();
    }

    @Test
    void testAddStoc_IntegrationE_Success() {
        // Arrange - Entitate reală E
        Stoc stoc = new Stoc(1, "Apa", 10, 2);

        // Act
        assertDoesNotThrow(() -> stocService.add(stoc));

        // Assert - testăm cu componente 100% reale
        assertEquals(1, stocRepo.findAll().size());
        assertEquals("Apa", stocRepo.findOne(1).getIngredient());
    }

    @Test
    void testAddStoc_IntegrationE_Fail() {
        // Arrange - Entitate reală E cu date nevalide (ID invalid)
        Stoc stoc = new Stoc(-1, "Apa", 10, 2);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> stocService.add(stoc));
        assertTrue(exception.getMessage().contains("ID invalid"));

        // Assert - ne asigurăm că nu s-a scris nimic în fișier (R a fost protejat de V)
        assertEquals(0, stocRepo.findAll().size());
    }
}

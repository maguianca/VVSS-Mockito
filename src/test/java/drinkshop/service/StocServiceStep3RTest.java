package drinkshop.service;

import drinkshop.domain.Stoc;
import drinkshop.repository.file.FileStocRepository;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StocServiceStep3RTest {

    private FileStocRepository stocRepo;
    private StocValidator stocValidator;
    private StocService stocService;
    private final String testFileName = "test_stoc_integration.csv";

    @Mock
    private Stoc mockStoc;

    @BeforeEach
    void setUp() {
        // Creăm un fișier gol pentru Repository-ul real
        try {
            new File(testFileName).createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Integrare cu R real. S și V sunt reale. E este mock.
        stocRepo = new FileStocRepository(testFileName); 
        stocValidator = new StocValidator(); 
        stocService = new StocService(stocRepo, stocValidator); 
    }

    @AfterEach
    void tearDown() {
        // Curățăm fișierul după teste
        new File(testFileName).delete();
    }

    @Test
    void testAddStoc_IntegrationR_Success() {
        // Arrange - Setează mock-ul să returneze valori valide
        // Aceste valori sunt apelate și de V, dar și de R când face salvarea în fișier (extractEntity)
        when(mockStoc.getId()).thenReturn(1);
        when(mockStoc.getIngredient()).thenReturn("Apa");
        when(mockStoc.getCantitate()).thenReturn((double)10);
        when(mockStoc.getStocMinim()).thenReturn((double)2);

        // Act
        assertDoesNotThrow(() -> stocService.add(mockStoc));

        // Assert - Verificăm că Repository-ul real chiar a salvat elementul
        assertEquals(1, stocRepo.findAll().size());
        assertEquals("Apa", stocRepo.findOne(1).getIngredient());
    }

    @Test
    void testAddStoc_IntegrationR_Fail() {
        // Arrange
        when(mockStoc.getId()).thenReturn(-1);

        // Act & Assert
        assertThrows(ValidationException.class, () -> stocService.add(mockStoc));

        // Assert - Verificăm că Repository-ul real NU a salvat elementul din cauza excepției
        assertEquals(0, stocRepo.findAll().size());
    }
}

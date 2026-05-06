package drinkshop.service;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StocServiceStep2VTest {

    @Mock
    private Repository<Integer, Stoc> stocRepo;

    @Mock
    private Stoc mockStoc;

    private StocValidator stocValidator;
    private StocService stocService;

    @BeforeEach
    void setUp() {
        stocValidator = new StocValidator(); 
        stocService = new StocService(stocRepo, stocValidator); 
    }

    @Test
    void testAddStoc_IntegrationV_Success() {

        when(mockStoc.getId()).thenReturn(1);
        when(mockStoc.getIngredient()).thenReturn("Apa");
        when(mockStoc.getCantitate()).thenReturn((double)10);
        when(mockStoc.getStocMinim()).thenReturn((double)2);
        
        when(stocRepo.save(mockStoc)).thenReturn(mockStoc);

        // Act & Assert
        assertDoesNotThrow(() -> stocService.add(mockStoc));

        // Verify
        verify(stocRepo, times(1)).save(mockStoc);
    }

    @Test
    void testAddStoc_IntegrationV_Fail() {
        when(mockStoc.getId()).thenReturn(-1);

        // Act & Assert - V real va prinde eroarea și va arunca excepție
        ValidationException ex = assertThrows(ValidationException.class, () -> stocService.add(mockStoc));
        assertTrue(ex.getMessage().contains("ID invalid"));

        // Verify
        verify(stocRepo, never()).save(any(Stoc.class));
    }
}

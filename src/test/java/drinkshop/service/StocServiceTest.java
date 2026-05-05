package drinkshop.service;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ValidationException;
import drinkshop.service.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StocServiceTest {

    @Mock
    private Repository<Integer, Stoc> stocRepo;

    @Mock
    private Validator<Stoc> stocValidator;

    @Mock
    private Stoc mockStoc;

    @InjectMocks
    private StocService stocService;

    @Test
    void testAddStoc_Success() {
        // Arrange
        when(stocRepo.save(mockStoc)).thenReturn(mockStoc); 

        // Act
        stocService.add(mockStoc);

        // Verify 
        verify(stocValidator, times(1)).validate(mockStoc);
        verify(stocRepo, times(1)).save(mockStoc);
        
        // Assert
        assertTrue(true);
    }

    @Test
    void testAddStoc_ValidationFails() {
        // Arrange 
        doThrow(new ValidationException("Stoc invalid")).when(stocValidator).validate(mockStoc);

        // Act & Assert 
        ValidationException exception = assertThrows(ValidationException.class, () -> stocService.add(mockStoc));
        assertEquals("Stoc invalid", exception.getMessage());

        // Verify
        verify(stocValidator, times(1)).validate(mockStoc);
        verify(stocRepo, never()).save(any(Stoc.class)); 
    }
}

package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceTest {

    private ProductService productService;
    private FileProductRepository repo;

    void init() {
        try {
            new java.io.PrintWriter("test_products.txt").close();
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }
        repo = new FileProductRepository("test_products.txt");
        productService = new ProductService(repo);
    }

    // ==================== ECP VALID CASES ====================
    @ParameterizedTest(name = "{index}: nume=''{0}'', pret={1} → saved") // Adnotare 2
    @CsvSource({
            // --- ECP VALID ---
            // TC_EC_1: EC1(nume valid) + EC3(pret=10, number, >0, <=1000)
            "Cherry Cola, 10.0",

            // --- BVA VALID ---
            // TC_BVA_1: pret = 1 (min, limita inferioara valida)
            "Cherry Cola, 1.0",

            // TC_BVA_3: pret = 10 (valoare normala in interval)
            "Cherry Cola, 10.0",

            // TC_BVA extra: pret = 1000 (max, limita superioara valida)
            "Cherry Cola, 1000.0"
    })
    @Order(1) // Adnotare 3
    @DisplayName("Cazuri VALIDE ECP+BVA → produs salvat") // Adnotare 4 (nu e interzisa)
    void addProduct_ValidCases_ShouldSave(String nume, double pret) {

        // ARRANGE
        init();
        TipBautura tipDummy = TipBautura.WATER_BASED;
        CategorieBautura catDummy = CategorieBautura.JUICE;
        String descriereDummy = "produs de test";

        // ACT
        Product result = productService.addProduct(
                nume, pret, tipDummy, catDummy, descriereDummy);

        // ASSERT
        assertNotNull(result);
        assertEquals(nume, result.getNume());
        assertEquals(pret, result.getPret(), 0.001);
    }

    // ==================== ECP + BVA INVALID CASES ====================
    // TC_EC_2: nume="" → Error message - empty name
    // TC_EC_3: pret="abc" → nu testam direct (e double), dar pret=0 e non-valid
    // TC_EC_4: pret=0 → Error message - price must be >0
    // TC_BVA_2: pret=0 (min-1) → Error message - price must be >=1
    // TC_BVA_4: pret=1001 (max+1) → Error message - price too large

    @ParameterizedTest(name = "{index}: nume=''{0}'', pret={1} → Exception: {2}")
    @CsvSource({
            // --- ECP NON-VALID ---
            // TC_EC_2: nume gol → Error message - empty name
            "'', 15.0, 'empty name'",

            // TC_EC_4: pret = 0 → Error - price must be >= 1
            "Limonada, 0.0, 'price must be >= 1'",

            // --- BVA NON-VALID ---
            // TC_BVA_2: pret = 0 (min-1) → Error message - price must be >= 1
            "Cherry Cola, 0.0, 'price must be >= 1'",

            // TC_BVA_4: pret = 1001 (max+1) → Error message - price too large
            "Cherry Cola, 1001.0, 'price too large'"
    })
    @Order(2)
    @DisplayName("Cazuri NON-VALIDE ECP+BVA → ValidationException cu mesaj")
    void addProduct_InvalidCases_ShouldThrow(String nume, double pret, String expectedMessage) {
        // ARRANGE
        init();

        TipBautura tipDummy = TipBautura.WATER_BASED;
        CategorieBautura catDummy = CategorieBautura.JUICE;
        String descriereDummy = "produs de test";
        // ACT
        ValidationException ex = assertThrows(ValidationException.class,
                () -> productService.addProduct(
                        nume, pret, tipDummy, catDummy, descriereDummy));
        // ASSERT
        assertTrue(ex.getMessage().contains(expectedMessage),
                "Mesajul de eroare '" + ex.getMessage() + "' nu contine '" + expectedMessage + "'");
    }

    // ==================== WHITE BOX TESTING (WBT) - Lab 3 ====================
    @Test
    @Order(3)
    @DisplayName("F02_P01: numePartial este null -> IllegalArgumentException")
    void testCautaProduse_P01_NullInput() {
        init();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            productService.cautaProduseDupaNume(null);
        });
        assertEquals("Numele cautat este invalid!", ex.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("F02_P02: numePartial este format din spatii goale -> IllegalArgumentException")
    void testCautaProduse_P02_EmptyInput() {
        init();
        assertThrows(IllegalArgumentException.class, () -> {
            productService.cautaProduseDupaNume("   ");
        });
    }

    @Test
    @Order(5)
    @DisplayName("F02_P03: Lista din repo este empty -> Nu intra în for, returnează lista goală")
    void testCautaProduse_P03_EmptyList() {
        init();
        List<Product> rezultate = productService.cautaProduseDupaNume("Cola");
        assertTrue(rezultate.isEmpty(), "Lista ar trebui să fie goală când baza de date e goală.");
    }

    @Test
    @Order(6)
    @DisplayName("F02_P04: Produsul are nume null in baza de date -> ignore")
    void testCautaProduse_P04_NullProductNameInDb() {
        init();
        Product corupt = new Product(1, null, 10.0, CategorieBautura.ALL, TipBautura.WATER_BASED, "Desc");
        productService.addProduct(corupt);

        List<Product> rezultate = productService.cautaProduseDupaNume("Cola");
        assertTrue(rezultate.isEmpty(), "Produsul nu este returnat din cauza numelui.");
    }

    @Test
    @Order(7)
    @DisplayName("F02_P05: Produsul este valid, dar numele NU contin textul cautat -> empty list")
    void testCautaProduse_P05_NameNotMatching() {
        init();
        Product p = new Product(1, "Fanta", 10.0, CategorieBautura.JUICE, TipBautura.WATER_BASED, "Desc");
        productService.addProduct(p);

        List<Product> rezultate = productService.cautaProduseDupaNume("Cola");
        assertTrue(rezultate.isEmpty(), "Nu returnam Fanta cand cautam Cola.");
    }

    @Test
    @Order(8)
    @DisplayName("F02_P06: Numele se potrivește, dar pretul este 0 -> Ignorat")
    void testCautaProduse_P06_PriceIsZero() {
        init();
        Product p = new Product(1, "Cherry Cola", 0.0, CategorieBautura.JUICE, TipBautura.WATER_BASED, "Desc");
        productService.addProduct(p);

        List<Product> rezultate = productService.cautaProduseDupaNume("Cola");
        assertTrue(rezultate.isEmpty(), "Produsul cu pret 0 nu e adaugat in rezultate.");
    }

    @Test
    @Order(9)
    @DisplayName("F02_P07: Totul este valid (Numele se potriveste, pret > 0) -> Produsul este adaugat")
    void testCautaProduse_P07_ValidMatch() {
        init();
        Product p = new Product(1, "Cherry Cola", 5.5, CategorieBautura.JUICE, TipBautura.WATER_BASED, "Desc");
        productService.addProduct(p);

        List<Product> rezultate = productService.cautaProduseDupaNume("cola");

        assertEquals(1, rezultate.size(), "Returneaza un produs.");
        assertEquals("Cherry Cola", rezultate.get(0).getNume());
    }
}
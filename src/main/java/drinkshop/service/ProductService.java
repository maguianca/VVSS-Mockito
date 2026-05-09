package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final Repository<Integer, Product> productRepo;
    private final ProductValidator productValidator = new ProductValidator();

    public ProductService(Repository<Integer, Product> productRepo) {
        this.productRepo = productRepo;
    }

    public void addProduct(Product p) {
        productRepo.save(p);
    }

    public Product addProduct(String nume, double pret, TipBautura tip,
                              CategorieBautura categorie, String descriere) {
        int nextId = productRepo.findAll().stream()
                .mapToInt(Product::getId)
                .max()
                .orElse(0) + 1;

        Product produsNou = new Product(nextId, nume, pret, categorie, tip, descriere);
        productValidator.validate(produsNou);
        productRepo.save(produsNou);
        return produsNou;
    }

    public void updateProduct(int id, String name, double price, CategorieBautura categorie, TipBautura tip) {
        Product updated = new Product(id, name, price, categorie, tip);
        productRepo.update(updated);
    }

    public void deleteProduct(int id) {
        productRepo.delete(id);
    }

    public List<Product> getAllProducts() {
//        Iterable<Product> it=productRepo.findAll();
//        ArrayList<Product> products=new ArrayList<>();
//        it.forEach(products::add);
//        return products;

//        return StreamSupport.stream(productRepo.findAll().spliterator(), false)
//                    .collect(Collectors.toList());
        return productRepo.findAll();
    }

    public Product findById(int id) {
        return productRepo.findOne(id);
    }

    public List<Product> filterByCategorie(CategorieBautura categorie) {
        if (categorie == CategorieBautura.ALL) return getAllProducts();
        return getAllProducts().stream()
                .filter(p -> p.getCategorie() == categorie)
                .collect(Collectors.toList());
    }

    public List<Product> filterByTip(TipBautura tip) {
        if (tip == TipBautura.ALL) return getAllProducts();
        return getAllProducts().stream()
                .filter(p -> p.getTip() == tip)
                .collect(Collectors.toList());
    }

    public List<Product> cautaProduseDupaNume(String numePartial) {
        List<Product> rezultate = new ArrayList<>();
        if (numePartial == null || numePartial.trim().isEmpty()) {
            throw new IllegalArgumentException("Numele cautat este invalid!");
        }
        for (Product p : getAllProducts()) {
            if (p.getNume() != null) {
                if (p.getNume().toLowerCase().contains(numePartial.toLowerCase()) && p.getPret() > 0) {
                    rezultate.add(p);
                }
            }
        }
        return rezultate;
    }
}
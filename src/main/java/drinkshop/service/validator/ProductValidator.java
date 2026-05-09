package drinkshop.service.validator;

import drinkshop.domain.Product;
import drinkshop.domain.CategorieBautura;
import drinkshop.domain.TipBautura;

public class ProductValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {

        String errors = "";

        if (product.getId() <= 0)
            errors += "ID invalid!\n";

        if (product.getNume() == null || product.getNume().isBlank())
            errors += "empty name\n";

        if (product.getPret() < 1.0)
            errors += "price must be >= 1\n";

        if (product.getPret() > 1000.0)
            errors += "price too large\n";

        if (product.getCategorie() == null || product.getCategorie() == CategorieBautura.ALL)
            errors += "Categoria este invalida!\n";

        if (product.getTip() == null || product.getTip() == TipBautura.ALL)
            errors += "Tipul bauturii este invalid!\n";

        if (product.getDescriere() == null || product.getDescriere().isBlank())
            errors += "Descrierea nu poate fi goala!\n";

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}

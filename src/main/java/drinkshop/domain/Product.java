package drinkshop.domain;

public class Product {

    private int id;
    private String nume;
    private double pret;
    private CategorieBautura categorie;
    private TipBautura tip;
    private String descriere;

    public Product(int id, String nume, double pret,
                   CategorieBautura categorie,
                   TipBautura tip) {
        this(id, nume, pret, categorie, tip, "");
    }

    public Product(int id, String nume, double pret,
                   CategorieBautura categorie,
                   TipBautura tip,
                   String descriere) {
        this.id = id;
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
        this.tip = tip;
        this.descriere = descriere;
    }

    public int getId() { return id; }
    public String getNume() { return nume; }
    public double getPret() { return pret; }
    public CategorieBautura getCategorie() { return categorie; }

    public void setCategorie(CategorieBautura categorie) {
        this.categorie = categorie;
    }

    public TipBautura getTip() { return tip; }

    public String getDescriere() { return descriere; }

    public void setTip(TipBautura tip) {
        this.tip = tip;
    }

    public void setDescriere(String descriere) { this.descriere = descriere; }

    public void setNume(String nume) { this.nume = nume; }
    public void setPret(double pret) { this.pret = pret; }

    @Override
    public String toString() {
        return nume + " (" + categorie + ", " + tip + ") - " + pret + " lei";
    }
}
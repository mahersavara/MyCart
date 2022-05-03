package hanu.a2_1901040122.models.Product;

public class Product {
    Integer Id;
    String Thumbnail;
    String name;
    Double Price;

    public Product(Integer id, String thumbnail, String name, Double price) {
        this.Id = id;
        this.Thumbnail = thumbnail;
        this.name = name;
        this.Price = price;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    @Override
    public String toString() {
        return "Product: " +
                "Id=" + Id +
                ", Thumbnail='" + Thumbnail + '\'' +
                ", name='" + name + '\'' +
                ", Price=" + Price +
                '.';
    }
}

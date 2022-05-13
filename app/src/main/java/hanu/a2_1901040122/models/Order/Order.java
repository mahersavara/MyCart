package hanu.a2_1901040122.models.Order;

public class Order {
    Integer Id;
    String Thumbnail;
    String name;
    Double price;
    Integer quantity;

    public Order(Integer id, String thumbnail, String name, Double price, Integer quantity) {
        Id = id;
        Thumbnail = thumbnail;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Order{" +
                "Id=" + Id +
                ", Thumbnail='" + Thumbnail + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

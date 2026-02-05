package com.rodrigobarbosa.menu.domain;

import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("menu_items")
public class MenuItem {
  @Id private String id;
  private String name;
  private BigDecimal price;
  private boolean available;

  public MenuItem() {}

  public MenuItem(String id, String name, BigDecimal price, boolean available) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.available = available;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }
}

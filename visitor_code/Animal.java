// Defined in Animal.java
public interface Animal {
  public String speak();
  public String interactWith(Animal other);
  public String accept(AnimalVisitor visitor);
}

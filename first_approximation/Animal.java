// Defined in Animal.java
public interface Animal {
  public String speak();

  // idea: add a version of interactWith which is specific to different kinds
  // of animals
  public String interactWith(Dog other);
}

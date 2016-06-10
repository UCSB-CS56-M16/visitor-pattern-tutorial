public class Dog implements Animal {
  public String speak() {
    return "Bark!";
  }

  public String interactWith(Dog other) {
    return "The first dog barks at the second dog";
  }
}

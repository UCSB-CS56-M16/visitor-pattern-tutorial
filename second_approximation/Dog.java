// Defined in Dog.java
public class Dog implements Animal {
  public String speak() {
    return "Bark!";
  }

  public String interactWith(Animal other) {
      if (other instanceof Dog) {
	  return "The first dog barks at the second dog";
      } else {
	  assert(false); // should not be possible
	  return "IMPOSSIBLE";
      }
  }
}

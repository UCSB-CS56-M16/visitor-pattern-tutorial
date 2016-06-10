// Defined in Dog.java
public class Dog implements Animal {
  public String speak() {
    return "Bark!";
  }

  public String interactWith(Animal other) {
    return other.beingInteractedWithDog(this);
  }

  public String beingInteractedWithDog(Dog dog) {
    return "The first dog barks at the second dog";
  }

  public String beingInteractedWithCat(Cat cat) {
    return "The cat runs away from the dog";
  }

  public String beingInteractedWithFish(Fish fish) {
    return "The fish swims oblivious to the dog";
  }
}	

// Defined in Cat.java
public class Cat implements Animal {
  public String speak() {
    return "Meow";
  }

  public String interactWith(Animal other) {
    return other.beingInteractedWithCat(this);
  }

  public String beingInteractedWithDog(Dog dog) {
    return "The dog chases after the cat.";
  }

  public String beingInteractedWithCat(Cat cat) {
    return "The cats stare at each other";
  }

  public String beingInteractedWithFish(Fish fish) {
    return "The fish swims away from the cat's direction";
  }
}

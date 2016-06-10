// Defined in Fish.java
public class Fish implements Animal {
  public String speak() {
    return "Blub";
  }

  public String interactWith(Animal other) {
    return other.beingInteractedWithFish(this);
  }
	
  public String beingInteractedWithDog(Dog dog) {
    return "The dog stares at the fish.";
  }

  public String beingInteractedWithCat(Cat cat) {
    return "The cat jumps towards the fish's bowl.";
  }
	
  public String beingInteractedWithFish(Fish fish) {
    return "The fish swim around each other.";
  }
}

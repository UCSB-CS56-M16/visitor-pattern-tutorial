// Defined in Dog.java
public class Dog implements Animal {
    public String speak() {
	return "Bark!";
    }
    public String interactWith(Animal other) {
	return other.accept(new AnimalVisitor() {
		public String visitDog(Dog dog) {
		    return "The first dog barks at the second dog";
		}
		public String visitCat(Cat cat) {
		    return "The dog chases after the cat.";
		}
		public String visitFish(Fish fish) {
		    return "The dog stares at the fish.";
		}
	    });
    }

    public String accept(AnimalVisitor visitor) {
	return visitor.visitDog(this);
    }
}


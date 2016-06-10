// Defined in Fish.java
public class Fish implements Animal {
    public String speak() {
	return "Blub";
    }
    public String interactWith(Animal other) {
	return other.accept(new AnimalVisitor() {
		public String visitDog(Dog dog) {
		    return "The fish swims oblivious to the dog";
		}
		public String visitCat(Cat cat) {
		    return "The fish swims away from the cat's direction";
		}
		public String visitFish(Fish fish) {
		    return "The fish swim around each other.";
		}
	    });
    }

    public String accept(AnimalVisitor visitor) {
	return visitor.visitFish(this);
    }
}


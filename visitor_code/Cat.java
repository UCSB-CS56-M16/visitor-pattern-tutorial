// Defined in Cat.java
public class Cat implements Animal {
    public String speak() {
	return "Meow";
    }
    public String interactWith(Animal other) {
	return other.accept(new AnimalVisitor() {
		public String visitDog(Dog dog) {
		    return "The cat runs away from the dog";
		}
		public String visitCat(Cat cat) {
		    return "The cats stare at each other";
		}
		public String visitFish(Fish fish) {
		    return "The cat jumps towards the fish's bowl.";
		}
	    });
    }

    public String accept(AnimalVisitor visitor) {
	return visitor.visitCat(this);
    }
}


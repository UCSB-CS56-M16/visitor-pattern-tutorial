// Defined in Main.java
public class Main {
    public static void main(String[] args) {
	// Go through all the animals
	Animal[] animals = { new Dog(), new Cat(), new Fish() };
	for (Animal first : animals) {
	    for (Animal second: animals) {
		System.out.println("---");
		System.out.println("First animal speaks: " + first.speak());
		System.out.println("Second animal speaks: " + second.speak());
		System.out.println("Interaction: " + first.interactWith(second));
	    }
	}	
    }
}

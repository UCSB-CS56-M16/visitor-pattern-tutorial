## Visitor Pattern Tutorial ##

This is a tutorial on the [Visitor Pattern](https://en.wikipedia.org/wiki/Visitor_pattern), which is commonly employed in parsers, interpreters, and compilers.

### Background ###

At this point, you are familiar with [single dispatch](https://en.wikipedia.org/wiki/Dynamic_dispatch#Single_and_multiple_dispatch).
That is, when you call a method on an object, the runtime type of the object determines exactly which method is called.
For example, consider the following code:

```java
// Defined in Animal.java
public interface Animal {
  public String speak();
}

// Defined in Dog.java
public class Dog implements Animal {
  public String speak() {
    return "Bark!";
  }
}

// Defined in Cat.java
public class Cat implements Animal {
  public String speak() {
    return "Meow";
  }
}

// Defined in Fish.java
public class Fish implements Animal {
  public String speak() {
    return "Blub";
  }
}

// Defined in Main.java
public class Main {
  public static void main(String[] args) {
    Animal animal = new Cat();
    System.out.println(animal.speak()); // prints "Meow"
    animal = new Dog();
    System.out.println(animal.speak()); // prints "Bark!"
	animal = new Fish();
	System.out.println(animal.speak()); // prints "Blub"
  }
}
```

In the `Main.java` code above, even though the type of the variable `animal` is `Animal`, we end up with differing behavior depending on exactly what type of `Animal` we refer to.


### Motivation ###

Let's take the same animal example from before, but now we want to add an `interactWith` method which will allow animals to interact with each other.
For example, a cat might try to eat the fish, and a dog might run after the cat, and so on.

As a first approximation, we might add a method like the following to the `Animal` interface:

```java
// Defined in Animal.java
public interface Animal {
  public String speak();

  // returns a string describing the interaction (e.g., "the dog runs after the cat")
  public String interactWith(Animal other);
}
```


This, however, quickly hits a problem when we try to implement the `interactWith` method downstream:


```java
// Defined in Dog.java
public class Dog implements Animal {
  public String speak() {
    return "Bark!";
  }

  public String interactWith(Animal other) {
    // Problem: what kind of animal are we interacting with?
    // We only know that we are interacting with some animal, not a
	// particular kind of animal (e.g., dog, fish, cat)

    return "The dog interacts with some other kind of animal";
  }
}
```

In order to properly implement `interactWith`, we need to know the type of animal we are interacting with.
Our `interactWith` definition from before only tells us we're interacting with *some* animal, not any particular kind of animal.

### First Approximation at a Solution ###

At first, a possible solution might seem to involve taking advantage of Java's [method overloading](https://en.wikipedia.org/wiki/Function_overloading), and define something like the following in the `Animal` interface:

```java
// Defined in Animal.java
public interface Animal {
  public String speak();

  // idea: add a version of interactWith which is specific to different kinds
  // of animals
  public String interactWith(Dog other);
  public String interactWith(Cat other);
  public String interactWith(Fish other);
}
```

This appears to "solve" the problem from before, as now we can define `Dog`:

```java
// Defined in Dog.java
public class Dog implements Animal {
  public String speak() {
    return "Bark!";
  }

  public String interactWith(Dog other) {
    return "The first dog barks at the second dog";
  }

  public String interactWith(Cat other) {
    return "The dog chases after the cat.";
  }

  public String interactWith(Fish other) {
    return "The dog stares at the fish.";
  }
}
```

The problem with the above "solution" (and why scare quotes have been used), is that method overloading in Java is limited to types which are known *at compile time*.
As such, the following program doesn't compile:

```java
// Defined in Main.java
public class Main {
  public static void main(String[] args) {
    Animal first = new Dog();
    Animal second = new Cat();
	System.out.println(first.interactWith(second)); // Type error!
  }
}
```

...with an error of the form:

```
error: incompatible types: Animal cannot be converted to Cat
```

With a slight tweak, we can get this program to compile and run as expected:

```java
// Defined in Main.java
public class Main {
  public static void main(String[] args) {
    Animal first = new Dog();
    // TWEAK: second is now explicitly a Cat, as opposed to an Animal
    Cat second = new Cat();

    // prints "The dog chases after the cat."
	System.out.println(first.interactWith(second));
  }
}
```

...but now this is a lot less flexible.
Fundamentally, this requires us to know ahead of time what kinds of animals we will interact with, which might not be possible.
That is, this assumed we knew the first animal would interact specifically with a `Cat`, as opposed to some arbitrary animal (say perhaps, a `Fish`).
As such, this solution doesn't really work as intended.


### Second Approximation at a Solution ###

Another option is to use runtime `instanceof` checks to determine this information.
For example, consider the following:

```java
// Defined in Animal.java
public interface Animal {
  public String speak();
  public String interactWith(Animal other);
}

// Defined in Dog.java
public class Dog implements Animal {
  public String speak() {
    return "Bark!";
  }

  public String interactWith(Animal other) {
    if (other instanceof Dog) {
      return "The first dog barks at the second dog";
    } else if (other instanceof Cat) {
	  return "The dog chases after the cat.";
    } else if (other instanceof Fish) {
      return "The dog stares at the fish.";
    } else {
	  assert(false); // should not be possible
	  return "IMPOSSIBLE";
	}
  }
}

// Definitions for Cat and Fish...

// Defined in Main.java
public class Main {
  public static void main(String[] args) {
    Animal first = new Dog();
    Animal second = new Cat();

    // prints "The dog chases after the cat."
	System.out.println(first.interactWith(second));
  }
}
```

This solution works as expected, and its more general than the first solution.
That is, we no longer need to know ahead of time what kinds of animals we are interacting with.
However, this is a **poor solution**.
To see why, let's say we want to add a `Bird` as a new kind of animal:

```java
// Defined in Bird.java
public class Bird implements Animal {
  public String spreak() {
    return "Chirp!";
  }

  public String interactWith(Animal other) {
    if (other instanceof Cat) {
      return "The bird flies away from the cat.";
    } else if (other instanceof Fish) {
      return "The bird stares perplexed at the fish";
    } else if (other instanceof Bird) {
      return "The birds peck at seeds together.";
    } else {
	  assert(false); // should not be possible
	  return "IMPOSSIBLE";
	}
  }
}
```

There is actually a bug in the above code, specific to the `interactWith` method.
Take a look at this code, and try to spot it youself.

See the problem?
The `interactWith` method for `Bird` is missing a case for `Dog`.
As such, if we had a bird interact with a dog, we'd end up asserting `false`, as this shouldn't be possible.

The problem isn't specific to `Bird` here, either.
We must add a case for `Bird` in `Dog`, `Cat`, and `Fish` as well.
The fact that we need to add these cases isn't the problem, as fundamentally we allow all possible kinds of pairwise interactions.
The problem is that it is *very* easy to forget to add a case, which will lead to the sort of bug above.
For this reason, `instanceof` is strongly discouraged, and the best practice is to avoid it entirely.


### Third Approximation to a Solution ###

Generally, the use of `instanceof` indicates a situation where single dispatch *could* somehow have been used, though it wasn't.
To see why, consider an alternative implementation of `Animal`'s `speak` method:

```java
public class AlternativeSpeak {
  public String speak(Animal animal) {
    if (animal instanceof Dog) {
      return "Bark!";
    } else if (animal instanceof Cat) {
      return "Meow";
    } else if (animal instanceof Fish) {
      return "Blub";
    } else {
      assert(false); // should not be possible
      return "IMPOSSIBLE";
    }
  }
}
```

Now instead of behaving as a method, the above code acts as a function that checks to see exactly which kind of animal is in play.
Instead of calling `animal.speak()`, we call `speak(animal)`.
Other than this difference, the code has the same behavior.
This demonstrates that we can emulate single dispatch with `instanceof`, and single dispatch can in fact be viewed as a way of dynamically using `instanceof` to figure out exactly which code to execute.
We can, in fact, go in reverse - that of taking code that uses `instanceof` and converting it to code that uses single dispatch.
This has two major advantages:

1. As mentioned before, the compiler checks to see that we handled every case.
   Missing a case here corresponds to not implementing an abstract method, which is considered a compile-time error in Java.
   This keeps us from having a nasty bug at runtime.
2. It is generally faster.
   To understand why, say we had 100 possible cases.
   With the `instanceof` approach above, worst-case scenario we would have to check all 100 cases.
   From a time complexity standpoint, this means that this approach is `O(N)`, where `N` is the number of cases.
   Thanks to some cleverness beyond the scope of this discussion, with single dispatch we don't need to check all cases to know which case to use; it is, in fact, `O(1)`.

With removing these uses of `instanceof`, observe that it is ultimately up to what kind of `Animal` we are dealing with to determine what `speak()` does.
That is, the particular instance of `Animal` itself dictates how `speak()` is handled.

Now look again at the solution using `instanceof` to implement `interactWith`:

```java
// Defined in Dog.java
public class Dog implements Animal {
  public String speak() {
    return "Bark!";
  }

  public String interactWith(Animal other) {
    if (other instanceof Dog) {
      return "The first dog barks at the second dog";
    } else if (other instanceof Cat) {
	  return "The dog chases after the cat.";
    } else if (other instanceof Fish) {
      return "The dog stares at the fish.";
    } else {
	  assert(false); // should not be possible
	  return "IMPOSSIBLE";
	}
  }
}
```

A similar pattern as in the previous example can be seen - it is up to the particular instance of `Animal` to determine how it must interact with a `Dog`.
The only difference in this case is that we were *already* using single dispatch to determine how to interact with the another animal.
As such, we need to use single dispatch here *twice*, in a manner than is referred to as [double dispatch](https://en.wikipedia.org/wiki/Double_dispatch).
As part of this process, we need to chain along the information that in the specific code above, a `Dog` was being interacted with.

Putting all of this information together, we can define a new interface for `Animal`, along with a new definition of our particular animals, like so:

```java
// Defined in Animal.java
public interface Animal {
  public String speak();

  public String interactWith(Animal other);

  public String beingInteractedWithDog(Dog dog);
  public String beingInteractedWithCat(Cat cat);
  public String beingInteractedWithFish(Fish fish);
}

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
```

All the interactions in this code work as intended, it does not use `instanceof`, and we still get an `interactWith` method that works with arbitrary animals with only runtime information.


### Single Dispatch versus Double Dispatch ###

The purpose of this section is to explain from a high-level why the code in the previous section works, from a somewhat different viewpoint.

Ultimately, what makes `interactWith` so much more painful relative to `speak` is because it depends on the runtime type of *two* different instances of `Animal`, namely, the two animals involved in the interaction.
The `speak` method was relatively easy because it depends on only *one* instance of `Animal`, namely, the animal that is speaking.
As such, `speak` only needs single dispatch.
Java was designed to make single dispatch relatively easy by having multiple classes implement the same method.
(That is, all the difference subclasses of `Animal` implement the `speak` method.)
However, Java does not provide a built-in way to dispatch on more than one thing at a time.
The previous example works around this limitation by performing *two* rounds of single dispatch, the first being on `interactWith`, and the second being on the variants of `beingInteractedWith`.

Specific to the example above, you may have noticed that the different implementations of `beingInteractedWith` do not actually use the parameters provided.
This is an artifact of the simplicity of the example, where all that was necessary to run was the knowledge of the type of animal we interacted with.
As things get more complex, the intention is that this parameter would be used.


### Generalizing Things: The Visitor Pattern ###

Building from the previous example, let's add additional functionality and allow for comparing animals to each other for the purposes of sorting.
In order to do this, we will add a `compareTo` method to `Animal`, like so:

```java
// Defined in Animal.java
public interface Animal {
  public String speak();

  public String interactWith(Animal other);

  public String beingInteractedWithDog(Dog dog);
  public String beingInteractedWithCat(Cat cat);
  public String beingInteractedWithFish(Fish fish);

  // -1 if I am before the parameter
  // 0 if I am equal to the parameter
  // 1 if I am greater than the parameter
  public int compareTo(Animal other);
}
```

(Note that you would normally do this by inheriting from Java's [`Comparable`](https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html) interface; this example avoids this just to keep things self-contained.)
For the purposes of this example, we will arbitrarily say that dogs are strictly greater than cats, and that cats are strictly greater than fish.
With this in mind, the `compareTo` method we just added has a similar problem as with the `interactWith` method: we must know the runtime types of *both* what we call `compareTo` on and of the parameter to `compareTo` in order to make the comparison.

We can solve this problem in a similar way as before, by adding on additional methods to `Animal` which delay the comparison of the parameter.
An example is below:

```java
// Defined in Animal.java
public interface Animal {
  public String speak();

  public String interactWith(Animal other);

  public String beingInteractedWithDog(Dog dog);
  public String beingInteractedWithCat(Cat cat);
  public String beingInteractedWithFish(Fish fish);

  // -1 if I am before the parameter
  // 0 if I am equal to the parameter
  // 1 if I am greater than the parameter
  public int compareTo(Animal other);

  public int beingComparedToDog(Dog dog);
  public int beingComparedToCat(Cat cat);
  public int beingComparedToFish(Fish fish);
}
```

If we keep going in this direction, this will eventually work.
However, things are starting to get ugly.
For one, our `Animal` interface now has a total of nine methods, even though it only has three capabilities (that of speaking, interacting, and comparing).
The methods for `beingInteractedWith` and `beingComparedTo` are there only to handle double dispatch, and these somewhat pollute an otherwise clean interface.
Moreover, `beingInteractedWith` and `beingComparedTo` are highly repetitive in nature, indicating that there is probably some common functionality that can be factored out into a separate class or interface.

There is, in fact, a third problem with the above design.
Let's say we want to add functionality to draw the different animals onto the screen.
With our current setup, we would have to put this functionality directly into `Animal` itself.
Drawing, however, is arguably a different concern which doesn't belong in `Animal`.
This is somewhat analogous to requiring that programmers understand how printing works in order to manipulate strings, just because, incidentally, we can print strings out.

All of these above problems can be addressed with the [Visitor Pattern](https://en.wikipedia.org/wiki/Visitor_pattern).
The core idea in this pattern is to encapsulate functionality related to the second dispatch (that is, the `beingInteractedWith` and `beingComparedTo` methods above) into an entirely separate class.
This separate class contaning the functionality is referred to as a `Visitor`.
The first dispatch (corresponding to `interactWith` and `compareTo` above) can then be uniformly handled via a generic `accept` method, which simply tells the `Visitor` exactly what sort of thing it is visiting.

We will incrementally work towards this.
First, let's start with a very basic `Animal` interface:

```java
// Defined in Animal.java
public interface Animal {
  public String speak();
  public String interactWith(Animal other);
}
```

From here, we can define a visitor for `Animal`:

```java
// Defined in AnimalVisitor.java
public interface AnimalVisitor {
  public String visitDog(Dog dog);
  public String visitCat(Cat cat);
  public String visitFish(Fish fish);
}
```

Observe that the above `AnimalVisitor` has functionality that mirrors what is done in `beingInteractedWith` and `beingComparedTo`.
Crucially, however, this functionality is now in a separate interface, so now we can define different `Animal` behaviors without actually modifying `Animal`.
Adding a behavior can be handled by defining new subclasses of `AnimalVisitor`.

In order to work with `AnimalVisitor`, the `Animal` interface needs an `accept` method, like so:

```java
// Defined in Animal.java
public interface Animal {
  public String speak();
  public String interactWith(Animal other);
  public String accept(AnimalVisitor visitor);
}
```

Let's now implement `Dog` incrementally.
The `speak` method stays the same, we won't spend any extra time on this.
The `accept` method is new, so let's see how to implement that first before touching `interactWith`:

```java
// Defined in Dog.java
public class Dog implements Animal {
  public String speak() {
    return "Bark!";
  }

  public String accept(AnimalVisitor visitor) {
    return visitor.visitDog(this);
  }
}
```

The `accept` method now looks a lot like our old `beingInteractedWith` and `beingComparedTo` methods.
Unlike these methods, however, `accept` now works for *any* behavior; it is not specific to interactions or comparisons, respectively.

This genericity of `accept`, however, comes at a bit of a cost, at least for our particular example.
To understand why, consider `beingInteractedWithDog`.
When this method is called, it means specifically we have a dog trying to interact with something else, where the something else is determined by the runtime type of the object that called `beingInteractedWithDog`.
From the definition of `accept`, we can plainly see that the only animal involved is the one that defines the `accept` method.
However, because `accept` takes an `AnimalVisitor`, a workaround is to put this information in the `AnimalVisitor` visitor itself.
To see exactly what this all means, we implement `Dog` with the `interactWith` method below:

```java
// Defined in Dog.java
public class Dog implements Animal {
  public String speak() {
    return "Bark!";
  }

  public String accept(AnimalVisitor visitor) {
    return visitor.visitDog(this);
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
}
```

Here an [anonymous class](https://docs.oracle.com/javase/tutorial/java/javaOO/anonymousclasses.html) was used to minimize the amount of code written, though it isn't absolutely necessary.
(Note that anonymous classes are just shorthand for creating an [inner class](https://docs.oracle.com/javase/tutorial/java/javaOO/innerclasses.html) without the need for a class name.)
As shown, we call the `accept` method of the `other` `Animal` in `Dog`'s `interactWith` method.
The `AnimalVisitor` passed implicitly assumes that we have a `Dog` interacting with something else.
The different implementations of `visit` (namely, `visitDog`, `visitCat`, and `visitFish`) effectively case split on the different possibilities of `other`.
For example, if the other animal is a `Dog`, then `visitDog` will be called, and so on.

For the sake of completeness, implementations of `Cat` and `Fish` are below.
The `Main` definition from before will work as-is for this example.

```java
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
```

### A Generic Visitor Pattern ###

An observant reader may have noticed that the visitor we defined does not actually generalize `compareTo`, as `compareTo` returns an `int` whereas our visitor returns a `String`.
This is only because the visitor definition given above assumed that only `String` would ever be returned, which is a stronger assumption than what we need to make.
Instead, we can parameterize the return type, like so:

```java
// Defined in AnimalVisitor.java
public interface AnimalVisitor<A> {
  public A visitDog(Dog dog);
  public A visitCat(Cat cat);
  public A visitFish(Fish fish);
}

// Defined in Animal.java
public interface Animal {
  public String speak();
  public String interactWith(Animal other);

  // introduces a type variable A
  // returns something of type A
  // takes an AnimalVisitor parameterized by type A
  public <A> A accept(AnimalVisitor<A> visitor);
}

// Defined in Dog.java
public class Dog implements Animal {
  public <A> A accept(AnimalVisitor<A> visitor) {
    return visitor.visitDog(this);
  }
  // more code follows...	
}
```

With this updated visitor, we can handle both `String` and `int` (the latter of which will need `Integer` in order to make it work with generics, but the concept is the same).


### Relationship to Parsers, Compilers, and Interpreters ###

As stated in the beginning, the visitor pattern is commonly employed in parsers, compilers, and interpreters.
To understand why, consider for a moment the [Java Virtual Machine (JVM)](https://en.wikipedia.org/wiki/Java_virtual_machine).
While we usually think of the JVM simply as an interpreter for Java bytecode, this dramatically oversimplifies the JVM.
Internally, the JVM actually hosts multiple interpreters for the same bytecode, as well as multiple compilers (it will dynamically convert Java bytecode to machine code via [Just-in-time (JIT) compilation](https://en.wikipedia.org/wiki/Just-in-time_compilation).
Ultimately, the reason for all this complexity is in the interest of better performance.
Understanding how all these pieces fit together is well beyond the scope of this course.
However, for a moment, imagine implementing multiple interpreters over the same abstract syntact tree.

Likely the simplest approach involves something like the following:

```java
public interface ASTNode {
  public Value interpret1();
  public Value interpret2();
  public Value interpret3();
}
```

That is, if we have three interpreters, we have three different `interpret` methods defined on our base class for AST nodes.
Each of our different kinds of AST nodes (e.g., `+`, `-`, `<`, and so on) inherits from `ASTNode`.

Given the complexity of Java, it is expected that these different `interpret` methods are going to be quite complex.
Additionally, these different interpreters may only be run at certain times.
As such, it does not make a lot of sense to group them together on `ASTNode`; these are truly separate interpreters with separate surrounding infrastructure, so putting these capabilities in `ASTNode` pollutes it.

Here is where the visitor pattern comes in.
Each interpreter can be viewed as a different kind of visitor of `ASTNode`, so all `ASTNode` needs to implement is an `accept` method.
The actual functionality for the different `interpret` methods can be placed in separate components which inherit from `ASTNode`'s visitor.

Relevant to this particular assignment is parsing.
We have asked you to define different kinds of tokens which inherit from a parent `Token` class.
During parsing, you'll need to know exactly which particular `Token` you have at any given moment.
Additionally, since parsing is a separate concern from tokenization, this parsing functionality *does not belong* in the `Token` class.
With all of this in mind, the visitor pattern is a natural fit: `Token` can define an `accept` method which takes a `TokenVisitor`, and then the parser can define a particular subclass of `TokenVisitor` which is relevant to parsing.

LEH
===

Generic reusable java equals, hashCode, toString methods implemented with reflection and an optional notion of identities. Seperates concept of identity and logical equality to permit comparing equality at the value level with optional consideration for primary key identity. Can be used for avoiding statically defining logical equals/hashCode/toString - however implementing your own equals/hashCode/toString methods are recommended in performance intensive contexts. 

Why do I need a different form of equality?
-------------------------------------------

Object doesn't by default implement logical equality/hashCode/toString:  
```
Object someObject1 = new Object();
Object someObject2 = new Object();
someObject1.equals(someObject2);                       // false
someObject1.hashCode() == someObject2.hashCode();      // false
someObject1.toString().equals(someObject2.toString()); // false
```
These methods behave as they do for performance reasons. To achieve logical results from these methods authors of classes are encouraged to implement these methods in every class and comply to a contract. Few developers implement these methods as intended, but couple code to those implementations making refactoring risky.

LEH provides logical alternative implementations of the java.lang.Object equals/hashCode/toString methods without requiring repetitive implementation or making behavioral changes to your code. 

The things you'll use
---------------------
```
leh.util.Entity
```  
A marker interface indicating an instance is eligible for evaluation by LEH. Objects not implementing this type (with the exception of Maps and Collections) are evaluated using their implementation of equals/hashCode/toString methods.
```
leh.util.annotations.Identity
```  
Indicates a compliment to equality. Fields annotated with ```@Identity``` are intended to indicate primary keys. By default fields annotated with this type are not evaluated in equals or hashCode. To include these fields annotate with the true value ```@Identity(true)``` to indicate the field does participate in equality/hashCode evaluation.
```
leh.util.LEH
```  
Reflectively access fields of Entity instances for equals/hashCode/toString determinations. Instances discovered in evaluation that do not implement Entity are merely evaluated per their implementation of equals/hashCode/toString.
```
leh.util.wrappers.LEHWrapper
```  
A proxy factory that wraps Objects to intercept invocations of equals/hashCode/toString and passes them to the corresponding LEH methods for evaluation.

Usage
-----

Take a new or existing class and implement leh.util.Entity. This indicates instances of that type are eligible for evaluation by leh.util.LEH. 

Get a reference to LEH singleton:
```LEH leh = LEH.getInstance();```  
To see if two Entity instances are logically equal: ```leh.isEquals(Object instance1, Object instance2)```  
To get an Entity instance's hashCode derived solely from its type and values: ```leh.getHashCode(Object instance)```  
To get an Entity instance's toString derived solely from its type and values: ```leh.getToString(Object instance)```  

Wrapper types are available for instances that do not implement Entity, however fields in the wrapped type must contain values implementing Entity or wrapped themselves or their equals/hashCode/toString implementations are used to determine values for equals/hashCode/toString. 

Wrapped instances dispatch to leh.util.LEH for equals/hashCode/toString Object method invocations to make the wrapper instance behave as though the wrapped instance honors logical equality regardless of whether the instance actually implements those methods or how:  

Get a reference to the Wrapper factory:
```LEHWrapper wrapper = LEHWrapper.getInstance();```  
"Wrap" the instances returning a new wrapper that dispatches equals/hashCode/toString to LEH instead of java.lang.Object.
```
someObject1 = wrapper.wrap(object1);
someObject2 = wrapper.wrap(new Object());
```
The wrapper does implement logical equality, hashCode, and toString:
```
someObject1.equals(someObject2);                       // true
someObject1.hashCode() == someObject2.hashCode();      // true
someObject1.toString().equals(someObject2.toString()); // true
```
Can be used to to operate with other existing code under an interpretation of equals that is configurable.  

Wrapped objects are especially useful in Hash containers like HashSet when equals or hashCode are unreliable or suit a stricter intention.


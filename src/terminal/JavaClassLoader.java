package terminal;

//load classes with default classloader
//imports
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import remote.*;

public class JavaClassLoader extends ClassLoader {
	//variables
	//constructor
	public JavaClassLoader() {

	}//end of constructor

	//methods
	//may need to create a method to get class that doesn't use the default class loader
	//instantiate a class and invoke a method
	public void invokeClassMethod(String className, String methodName, Connection conn, String commandWord, String commandOptions){
		try {
			//create a new JavaClassLoader
			//ClassLoader classLoader = this.getClass().getClassLoader();

			//load the target class using its binary name
			Class<?> loadedClass = loadClass(className);
			//System.out.println("Loaded class name: " + loadedClass.getName());
			
			//create a new instance from the loaded class
			Object myClassObject = loadedClass.newInstance();

			//getting the target method from the loaded class and invoking it using its name
			Method method = loadedClass.getMethod(methodName,Connection.class,String.class,String.class);
			//System.out.println("Invoked method name: " + method.getName());
			method.invoke(myClassObject, conn, commandWord, commandOptions);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//end of invokeClassMethod

	//instantiate a class and invoke a method but with only the command (no arguments)
	public void invokeClassMethod(String className, String methodName, Connection conn, String commandWord){
		try {
			//load the target class using its binary name
			Class<?> loadedClass = loadClass(className);
			//System.out.println("Loaded class name: " + loadedClass.getName());
			
			//create a new instance from the loaded class
			//Object myClassObject = loadedClass.getConstructor().newInstance();
			Object myClassObject = loadedClass.newInstance();

			//getting the target method from the loaded class and invoking it using its name
			Method method = loadedClass.getMethod(methodName,Connection.class,String.class);
			//System.out.println("Invoked method name: " + method.getName());
			method.invoke(myClassObject, conn, commandWord);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//end of invokeClassMethod
	//instantiate a class and invoke a method with no arguments
	public void invokeClassMethod(String className, String methodName, Connection conn){
		try {
			//load the target class using its binary name
			Class<?> loadedClass = loadClass(className);
			//System.out.println("Loaded class name: " + loadedClass.getName());
			
			//create a new instance from the loaded class
			Object myClassObject = loadedClass.newInstance();

			//getting the target method from the loaded class and invoking it using its name
			Method method = loadedClass.getMethod(methodName,Connection.class);
			//System.out.println("Invoked method name: " + method.getName());
			method.invoke(myClassObject, conn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//end of invokeClassMethod
}//end of JavaClassLoader

import java.util.Objects;

public class Pair<T1, T2>{
    private final T1 first;
    private final T2 second;
    
    /*
    public static void main(String[] args){
	Pair<Integer, Integer> p1 = new Pair<Integer, Integer>(5,4);
	Pair<Integer, Integer> p2 = new Pair<Integer, Integer>(5,4);
	Pair<Integer, Integer> p3 = new Pair<Integer, Integer>(5,3);
	System.out.println(p1.first());
	System.out.println(p1.equals(p2));
	System.out.println(p1.equals(p3));
    }
    */
    
    public Pair(T1 p1, T2 p2){
	this.first = p1;
	this.second = p2;
    }
    
    public boolean equals(Object o){
	if(!(o instanceof Pair)){
	    return false;
	}
	Pair<?, ?> p = (Pair<?, ?>) o;
	return Objects.equals(p.first(), this.first) && Objects.equals(p.second(), this.second);
    }

    public int hashCode() {
	int hashFirst = this.first != null ? this.first.hashCode() : 0;
	int hashSecond = this.second != null ? this.second.hashCode() : 0;

	return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }
    
    public T1 first(){
	return this.first;
    }

    public T2 second(){
	return this.second;
    }
    
    
    
}

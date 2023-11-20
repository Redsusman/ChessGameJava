/** */
public class Pair<T,E,D> {
    public T t;
    public E e;
    public D d;
    public Pair(T t, E e, D d) {
        this.t = t;
        this.e = e;
        this.d=d;
    }

    public T getT(){
        return t;
    }

    public E getE() {
        return e;
    }

    public D getD() {
        return d;
    }
}

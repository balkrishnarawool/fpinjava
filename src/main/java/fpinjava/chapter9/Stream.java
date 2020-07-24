package fpinjava.chapter9;

public abstract class Stream<T> {

    protected abstract T head();
    protected abstract Stream<T> tail();
    public abstract boolean isEmpty();

    private Stream() {}

}
package net.danygames2014.logisticspipes.util.tuple;

public class Pair3<T1, T2, T3> extends Pair<T1, T2>{
    protected final T3 value3;

    public Pair3(T1 value1, T2 value2, T3 value3) {
        super(value1, value2);
        this.value3 = value3;
    }

    public T3 getValue3(){
        return this.value3;
    }
}

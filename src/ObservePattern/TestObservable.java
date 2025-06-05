package src.ObservePattern;

import java.util.Observable;

public class TestObservable extends Observable {

    public TestObservable(){
        super();
        addObserver(new Test1Obsever());
        addObserver(new TestObserver());
        System.out.println("Source created");
    }
    public void mimicChange(){
        setChanged();
    }

}

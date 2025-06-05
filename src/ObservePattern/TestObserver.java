package src.ObservePattern;

import java.util.Observable;
import java.util.Observer;

public class TestObserver implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("I am the observer 1, now I received the change");
    }
}

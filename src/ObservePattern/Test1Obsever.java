package src.ObservePattern;

import java.util.Observable;
import java.util.Observer;

public class Test1Obsever implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("I am the observer 2, now I received the change too");
    }
}

package src.ObservePattern;

public class Main {
    public static void main(String[] args) {
        TestObservable testObservable=new TestObservable();
        testObservable.mimicChange();
        testObservable.notifyObservers();
        testObservable.notifyObservers();//第二次会失效，因为每次notifyobservers之后，都会调用clearChanged方法重置改变

    }
}


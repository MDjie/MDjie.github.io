package src.StatePattern;

public class TiredState implements MoodState{
    @Override
    public void work() {
        System.out.println("I am very tired now, the fucker still ask me to do this? I don't give it a shit, I am about to have a rest now!");
    }
}

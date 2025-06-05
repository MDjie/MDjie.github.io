package src.StatePattern;

public class HappyState implements MoodState{
    @Override
    public void work() {
        System.out.println("Alright! I can handle this effectively, cause I am string!!!");
    }
}

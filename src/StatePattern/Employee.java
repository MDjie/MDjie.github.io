package src.StatePattern;

public class Employee {
    Employee(MoodState mood){
        this.moodState=mood;
    }
    private MoodState moodState;
    public void setMoodState(MoodState mood){
        this.moodState=mood;
    }
    public void dowork(){
        moodState.work();
    }
}

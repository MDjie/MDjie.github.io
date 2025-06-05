package src.StatePattern;

public class Main {
    public static void main(String[] args) {
        Employee employee = new Employee(new AngryState());
        employee.dowork();
        employee.setMoodState(new HappyState());
        employee.dowork();
        employee.setMoodState(new TiredState());
        employee.dowork();
    }
}

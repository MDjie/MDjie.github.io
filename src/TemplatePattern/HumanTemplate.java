package src.TemplatePattern;

public abstract class HumanTemplate {
    private String head;
    private String hand;
    private String foot;
    private String body;
    public void buildHuman(){
        buildHead();
        buildBody();
        buildHand();
        buildFoot();

    };
    abstract void buildHead();
    abstract void buildBody();

    void buildHand(){
        System.out.println("Hand was built");
    }
    void buildFoot(){
        System.out.println("Foot was built");
    }
}

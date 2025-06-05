package src.TemplatePattern;

public class ManBuilder extends HumanTemplate{


    @Override
    void buildHead() {
        System.out.println("A man's head was built");
    }

    @Override
    void buildBody() {
        System.out.println("A man's body was built");
    }
}

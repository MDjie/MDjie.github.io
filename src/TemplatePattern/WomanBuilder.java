package src.TemplatePattern;

public class WomanBuilder extends HumanTemplate{
    @Override
    void buildHead() {
        System.out.println("A woman's head was built");
    }

    @Override
    void buildBody() {
        System.out.println("A woman's body was built");
    }
}

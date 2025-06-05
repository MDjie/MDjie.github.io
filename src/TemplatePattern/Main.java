package src.TemplatePattern;

public class Main
{
    public static void main(String[] args) {
        ManBuilder manBuilder=new ManBuilder();
        WomanBuilder womanBuilder= new WomanBuilder();
        manBuilder.buildHuman();
        womanBuilder.buildHuman();
    }
}

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Random{
    public static void main(String[] args) {
    List<String> names = new ArrayList<>();
    names.add("Jio");
    names.add("Marcille");
    names.add("Michael Russo");
    names.add("Jordi");

    Collections.shuffle(names);

    for(var name : names) {
        System.out.println(name);
    }
    }
}
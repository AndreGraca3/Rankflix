package pt.graca.menu;

import java.lang.reflect.Method;
import java.util.*;

abstract public class Menu {

    public Menu(Scanner scanner) {
        this.scanner = scanner;
        Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(MenuOption.class))
                .sorted(Comparator.comparing((Method method) ->
                                method.getAnnotation(MenuOption.class).priority())
                        .reversed()
                        .thenComparing(method -> method.getAnnotation(MenuOption.class).value()))
                .forEach(method -> menuOptions.put(menuOptions.size() + 1, method));
    }

    private final Scanner scanner;
    private final Map<Integer, Method> menuOptions = new HashMap<>();

    public void show() {
        System.out.println("Choose an option:");
        menuOptions.forEach((key, value) -> System.out.println(key + " - " + value.getAnnotation(MenuOption.class).value()));
        int option = Integer.parseInt(scanner.nextLine());
        Method method = menuOptions.get(option);
        try {
            method.invoke(this);
            System.out.println("✅ Done!");
        } catch (Exception e) {
            System.out.println("❌ An error occurred: " + e.getCause().getMessage());
        } finally {
            scanner.nextLine();
            System.out.println("-".repeat(50));
        }
    }

    protected String read(String s) {
        System.out.print(s);
        return scanner.nextLine();
    }
}

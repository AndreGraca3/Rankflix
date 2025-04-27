package pt.graca.menu;

import java.lang.reflect.Method;
import java.util.*;

abstract public class ConsoleMenu {

    public ConsoleMenu(Scanner scanner) {
        this.scanner = scanner;
        Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(ConsoleMenuOption.class))
                .sorted(Comparator.comparing((Method method) ->
                                method.getAnnotation(ConsoleMenuOption.class).priority())
                        .reversed()
                        .thenComparing(method -> method.getAnnotation(ConsoleMenuOption.class).value()))
                .forEach(method -> menuOptions.put(menuOptions.size() + 1, method));
    }

    protected final Scanner scanner;
    private final Map<Integer, Method> menuOptions = new HashMap<>();

    public void showForever() {
        while (true) {
            try {
                System.out.println("Choose an option:");
                menuOptions.forEach((key, value) -> System.out.println(key + " - " + value.getAnnotation(ConsoleMenuOption.class).value()));
                System.out.println("0 - Exit");

                System.out.println();
                System.out.print("Option: ");
                int option;
                try {
                    option = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                if (option == 0) break;
                Method method = menuOptions.get(option);
                if (method == null) throw new IllegalArgumentException("Invalid option");

                method.invoke(this);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("-".repeat(50));
            }
        }
    }

    protected String read(String s) {
        System.out.print(s);
        return scanner.nextLine();
    }
}

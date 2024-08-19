package Original.Task2.staticFactory.src;

public class ITStaffFactory {
    public static ITStaff createITManager() {
        return new ITManager();
    }

    public static ITStaff createDeveloper() {
        return new Developer();
    }

    public static ITStaff createTester() {
        return new Tester();
    }
}

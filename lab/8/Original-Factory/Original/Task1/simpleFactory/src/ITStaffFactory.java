package Original.Task1.simpleFactory.src;

public class ITStaffFactory {
    public ITStaff createITStaff(int type) {
        switch (type) {
            case 1:
                return new ITManager();
            case 2:
                return new Developer();
            case 3:
                return new Tester();
            default:
                return null;
        }

    }
}

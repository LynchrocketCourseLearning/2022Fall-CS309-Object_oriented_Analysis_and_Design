package Original.Task3.factoryMethod.src;

public class TesterFactory implements ITStaffFactory{
    @Override
    public ITStaff createITStaff() {
        return new Tester();
    }
}

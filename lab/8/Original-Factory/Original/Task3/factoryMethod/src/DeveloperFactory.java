package Original.Task3.factoryMethod.src;

public class DeveloperFactory implements ITStaffFactory{
    @Override
    public ITStaff createITStaff() {
        return new Developer();
    }
}

package Original.Task3.factoryMethod.src;

public class ITManagerFactory implements ITStaffFactory{
    @Override
    public ITStaff createITStaff() {
        return new ITManager();
    }
}

package Original.Task3.factoryMethod.src;

public class ArtDesignerFactory implements ITStaffFactory{
    @Override
    public ITStaff createITStaff() {
        return new ArtDesigner();
    }
}

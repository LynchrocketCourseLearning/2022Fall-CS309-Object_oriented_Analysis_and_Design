package task2;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter implements FileOperateInterfaceV2 {
    private FileOperateInterfaceV1 adaptee;
    private ManageStaffInterface adaptee2;

    public Adapter(FileOperateInterfaceV1 adaptee, ManageStaffInterface adaptee2) {
        this.adaptee = adaptee;
        this.adaptee2 = adaptee2;
    }

    @Override
    public List<StaffModel> readAllStaff() {
        return adaptee.readStaffFile();
    }

    @Override
    public void listAllStaff(List<StaffModel> list) {
        adaptee.printStaffFile(list);
    }

    @Override
    public void writeByName(List<StaffModel> list) {
        adaptee.writeStaffFile(list.stream().sorted(Comparator.comparing(StaffModel::getName)).collect(Collectors.toList()));
    }

    @Override
    public void writeByRoom(List<StaffModel> list) {
        adaptee.writeStaffFile(list.stream().sorted(Comparator.comparing(StaffModel::getRoom)).collect(Collectors.toList()));
    }

    @Override
    public void addNewStaff(List<StaffModel> list) {
        adaptee2.addingStaff(list);
    }

    @Override
    public void removeStaffByName(List<StaffModel> list) {
        adaptee2.removeStaff(list);
    }
}

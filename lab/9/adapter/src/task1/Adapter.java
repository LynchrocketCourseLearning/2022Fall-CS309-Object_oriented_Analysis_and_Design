package task1;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter implements FileOperateInterfaceV2 {
    private FileOperateInterfaceV1 adaptee;

    public Adapter(FileOperateInterfaceV1 adaptee) {
        this.adaptee = adaptee;
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
}

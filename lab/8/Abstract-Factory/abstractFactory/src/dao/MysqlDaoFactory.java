package dao;

public class MysqlDaoFactory implements DaoFactory{
    @Override
    public StaffDao createStaffDao() {
        return new MysqlStaffDao();
    }

    @Override
    public ComputerDao createComputerDao() {
        return new MysqlComputerDao();
    }
}

package dao;

import util.PropertiesReader;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class DaoFactoryImpl implements DaoFactory {
    private static DaoFactoryImpl instance = new DaoFactoryImpl();
    private Properties properties;

    private DaoFactoryImpl() {
        properties = PropertiesReader.readProperties("singleton/config.properties");
    }

    public static DaoFactoryImpl getInstance() {
        return instance;
    }

    public Object createObj(String interfaceName) {
        String implClzName = properties.getProperty(interfaceName);
        try {
            Class<?> implClz = Class.forName(implClzName);
            return implClz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StaffDao createStaffDao() {
        return (StaffDao) createObj("StaffDao");
    }

    @Override
    public ComputerDao createComputerDao() {
        return (ComputerDao) createObj("ComputerDao");
    }
}
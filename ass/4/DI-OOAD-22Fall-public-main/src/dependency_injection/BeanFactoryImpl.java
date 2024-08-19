package dependency_injection;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * TODO you should complete the class
 */
public class BeanFactoryImpl implements BeanFactory {
    private Properties injectProp;
    private Properties valueProp;

    @Override
    public void loadInjectProperties(File file) {
        injectProp = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            injectProp.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadValueProperties(File file) {
        valueProp = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            valueProp.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 一、确定实现类（implClazz）：
     * 1. 找到implClazzName
     * 2. Class.forName(implClazzName) 获取Class类型 implClazz
     * 二、确定构造方法
     * 1. 通过implClazz找所有的构造方法中，带有@Inject的构造方法
     *    （或default构造方法（递归的终止条件））
     *    getDeclaredConstructor().newInstance();
     * 三、构建对象
     * 1. 找构造方法里面所有的Parameter
     *       如果没有@Value，肯定是用户自定义类，递归调用newInstance(classtype)
     *       如果有@Value：
     *       boolean, int, String,
     *       boolean[], int[], String[],
     *       List<?>, Set<?>, Map<?, ?>: Boolean, Integer, String
     *       确定数据类型，通过配置文件创建实例 local-value.properties
     * 2. 对于每个Parameter，分别注入值，放在一个Object[]中
     * 3. 调用构造方法创建实例：
     *       Object[] object2 = {bObject, cObject, parameterObject1, parameterObject2};
     *       AA aObject2 = (AA) constructor.newInstance(objects2);
     * 四、在已有对象中注入属性：
     * 1. 找当前类里面所有的属性，getDeclaredFields()
     * 2. 在属性中找所有带有@Value注解的属性
     *       boolean, int, String,
     *       boolean[], int[], String[],
     *       List<?>, Set<?>, Map<?, ?>: Boolean, Integer, String
     *    给aObject中listField属性fieldObj值
     *       listField.setAccessible(true)
     *       listField.set(obj, value)
     *       listField.setAccessible(false)
     * 3. 在属性中找带有@Inject注解的属性
     *    递归 newInstance()
     * */

    private Class<?> getImplClazz(String clazzName) throws ClassNotFoundException {
        if (injectProp.containsKey(clazzName)) {
            return getImplClazz(injectProp.getProperty(clazzName));
        } else {
            return Class.forName(clazzName);
        }
    }

    private Object getInstance(Class<?> implClazz) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 获取构造器
        Constructor<?>[] implClazzDeclaredConstructors = implClazz.getDeclaredConstructors();
        Constructor<?> constructor = null;
        for (Constructor<?> c : implClazzDeclaredConstructors) {
            // 有@Inject注解的就用，否则默认构造器
            if (c.getAnnotation(Inject.class) != null) {
                constructor = c;
                break;
            }
        }
        ArrayList<Object> objects = new ArrayList<>();
        if (constructor != null) { // 找到自定义构造器，递归调用参数中的构造器
            Parameter[] parameters = constructor.getParameters();
            for (Parameter p : parameters) {
                if (p.getAnnotation(Value.class) == null) { // 用户自定义类型
                    // 自定义类型实例化
                    Class<?> paraImplClazz = getImplClazz(p.getType().getName());
                    Object paraInstance = getInstance(paraImplClazz);
                    objects.add(paraInstance);
                } else { // 有@Value
                    // 注意！！有值注入的要用local-value.properties
                    Value valueAnnotation = p.getAnnotation(Value.class);
                    String valueString = "";
                    if (valueProp.containsKey(valueAnnotation.value())) {
                        valueString = valueProp.getProperty(valueAnnotation.value());
                    } else {
                        valueString = valueAnnotation.value();
                    }
                    if (p.getType() != String.class) {
                        // 把左右括号删掉，非字符串
                        if (valueString.startsWith("(") || valueString.startsWith("[") || valueString.startsWith("{")) {
                            valueString = valueString.substring(1);
                        }
                        if (valueString.endsWith(")") || valueString.endsWith("]") || valueString.endsWith("}")) {
                            valueString = valueString.substring(0, valueString.length() - 1);
                        }
                    }
                    String[] strings = valueString.split(valueAnnotation.delimiter());
                    // 注意空串、空值判断
                    if (p.getType() == boolean.class) {
                        boolean flag = false;
                        for (String s : strings) {
                            if ("true".equalsIgnoreCase(s)) {
                                objects.add(true);
                                flag = true;
                                break;
                            } else if ("false".equalsIgnoreCase(s)) {
                                objects.add(false);
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            objects.add(false);
                        }
                    } else if (p.getType() == int.class) {
                        boolean flag = false;
                        for (String s : strings) {
                            try {
                                int sInt = Integer.parseInt(s);
                                objects.add(sInt);
                                flag = true;
                                break;
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        if (!flag) {
                            objects.add(0);
                        }
                    } else if (p.getType() == String.class) {
                        objects.add(strings[0]);
                    } else if (p.getType() == boolean[].class) {
                        ArrayList<Boolean> booleans = new ArrayList<>();
                        for (String s : strings) {
                            if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
                                booleans.add(Boolean.parseBoolean(s));
                            }
                        }
                        boolean[] actBooleans = new boolean[booleans.size()];
                        for (int i = 0; i < booleans.size(); i++) {
                            actBooleans[i] = booleans.get(i);
                        }
                        objects.add(actBooleans);
                    } else if (p.getType() == int[].class) {
                        ArrayList<Integer> ints = new ArrayList<>();
                        for (String s : strings) {
                            try {
                                ints.add(Integer.parseInt(s));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        int[] actInts = new int[ints.size()];
                        for (int i = 0; i < ints.size(); i++) {
                            actInts[i] = ints.get(i);
                        }
                        objects.add(actInts);
                    } else if (p.getType() == String[].class) {
                        if (!"".equals(valueString)) {
                            objects.add(strings);
                        } else {
                            objects.add(new String[0]);
                        }
                    } else if (p.getType() == List.class) {
                        Class<?> type = Class.forName(((ParameterizedType) p.getParameterizedType())
                                .getActualTypeArguments()[0].getTypeName());
                        List<Object> listObj = new ArrayList<>();
                        if (!"".equals(valueString)) {
                            if (type == Boolean.class) {
                                for (String s : strings) {
                                    if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
                                        listObj.add(Boolean.parseBoolean(s));
                                    }
                                }
                            } else if (type == Integer.class) {
                                for (String s : strings) {
                                    try {
                                        listObj.add(Integer.parseInt(s));
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            } else if (type == String.class) {
                                listObj.addAll(Arrays.asList(strings));
                            }
                        } else {
                            listObj = Collections.emptyList();
                        }
                        objects.add(listObj);
                    } else if (p.getType() == Set.class) {
                        Class<?> type = Class.forName(((ParameterizedType) p.getParameterizedType())
                                .getActualTypeArguments()[0].getTypeName());
                        Set<Object> setObj = new HashSet<>();
                        if (!"".equals(valueString)) {
                            if (type == Boolean.class) {
                                for (String s : strings) {
                                    if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
                                        setObj.add(Boolean.parseBoolean(s));
                                    }
                                }
                            } else if (type == Integer.class) {
                                for (String s : strings) {
                                    try {
                                        setObj.add(Integer.parseInt(s));
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            } else if (type == String.class) {
                                setObj.addAll(Arrays.asList(strings));
                            }
                        } else {
                            setObj = Collections.emptySet();
                        }
                        objects.add(setObj);
                    } else if (p.getType() == Map.class) {
                        Class<?> keyType = Class.forName(((ParameterizedType) p.getParameterizedType())
                                .getActualTypeArguments()[0].getTypeName());
                        Class<?> valueType = Class.forName(((ParameterizedType) p.getParameterizedType())
                                .getActualTypeArguments()[1].getTypeName());
                        Map<Object, Object> mapObj = new HashMap<>();
                        if (!"".equals(valueString)) {
                            if (keyType == Boolean.class) {
                                if (valueType == Boolean.class) {
                                    for (String s : strings) {
                                        String[] KeyValue = s.split(":");
                                        String key = KeyValue[0];
                                        String value = KeyValue[1];
                                        if (("true".equalsIgnoreCase(key) || "false".equalsIgnoreCase(key)) &&
                                                ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))) {
                                            mapObj.put(Boolean.parseBoolean(key), Boolean.parseBoolean(value));
                                        }
                                    }
                                } else if (valueType == Integer.class) {
                                    for (String s : strings) {
                                        String[] KeyValue = s.split(":");
                                        String key = KeyValue[0];
                                        String value = KeyValue[1];
                                        if ("true".equalsIgnoreCase(key) || "false".equalsIgnoreCase(key)) {
                                            try {
                                                mapObj.put(Boolean.parseBoolean(key), Integer.parseInt(value));
                                            } catch (NumberFormatException ignored) {
                                            }
                                        }
                                    }
                                } else if (valueType == String.class) {
                                    for (String s : strings) {
                                        String[] KeyValue = s.split(":");
                                        String key = KeyValue[0];
                                        String value = KeyValue[1];
                                        if ("true".equalsIgnoreCase(key) || "false".equalsIgnoreCase(key)) {
                                            mapObj.put(Boolean.parseBoolean(key), value);
                                        }
                                    }
                                }
                            } else if (keyType == Integer.class) {
                                if (valueType == Boolean.class) {
                                    for (String s : strings) {
                                        String[] KeyValue = s.split(":");
                                        String key = KeyValue[0];
                                        String value = KeyValue[1];
                                        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                                            try {
                                                mapObj.put(Integer.parseInt(key), Boolean.parseBoolean(value));
                                            } catch (NumberFormatException ignored) {
                                            }
                                        }
                                    }
                                } else if (valueType == Integer.class) {
                                    for (String s : strings) {
                                        String[] KeyValue = s.split(":");
                                        String key = KeyValue[0];
                                        String value = KeyValue[1];
                                        try {
                                            mapObj.put(Integer.parseInt(key), Integer.parseInt(value));
                                        } catch (NumberFormatException ignored) {
                                        }
                                    }
                                } else if (valueType == String.class) {
                                    for (String s : strings) {
                                        String[] KeyValue = s.split(":");
                                        String key = KeyValue[0];
                                        String value = KeyValue[1];
                                        try {
                                            mapObj.put(Integer.parseInt(key), value);
                                        } catch (NumberFormatException ignored) {
                                        }
                                    }
                                }
                            } else if (keyType == String.class) {
                                if (valueType == Boolean.class) {
                                    for (String s : strings) {
                                        String[] KeyValue = s.split(":");
                                        String key = KeyValue[0];
                                        String value = KeyValue[1];
                                        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                                            mapObj.put(key, Boolean.parseBoolean(value));
                                        }
                                    }
                                } else if (valueType == Integer.class) {
                                    for (String s : strings) {
                                        String[] KeyValue = s.split(":");
                                        String key = KeyValue[0];
                                        String value = KeyValue[1];
                                        try {
                                            mapObj.put(key, Integer.parseInt(value));
                                        } catch (NumberFormatException ignored) {
                                        }
                                    }
                                } else if (valueType == String.class) {
                                    for (String s : strings) {
                                        String[] KeyValue = s.split(":");
                                        String key = KeyValue[0];
                                        String value = KeyValue[1];
                                        try {
                                            mapObj.put(key, value);
                                        } catch (NumberFormatException ignored) {
                                        }
                                    }
                                }
                            }
                        } else {
                            mapObj = Collections.emptyMap();
                        }
                        objects.add(mapObj);
                    }
                }
            }
            Object instance = constructor.newInstance(objects.toArray());
            setFields(instance);
            return instance;
        } else { // 默认构造器
            Object instance = implClazz.getDeclaredConstructor().newInstance();
            setFields(instance);
            return instance;
        }
    }

    private void setFields(Object instance) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        // 注入属性的值
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Value.class) != null) { // 有@Value
                Value valueAnnotation = field.getAnnotation(Value.class);
                // 注意！！有值注入的要用local-value.properties
                String valueString = "";
                if (valueProp.containsKey(valueAnnotation.value())) {
                    valueString = valueProp.getProperty(valueAnnotation.value());
                } else {
                    valueString = valueAnnotation.value();
                }
                if (field.getType() != String.class) {
                    // 把左右括号删掉，非字符串
                    if (valueString.startsWith("(") || valueString.startsWith("[") || valueString.startsWith("{")) {
                        valueString = valueString.substring(1);
                    }
                    if (valueString.endsWith(")") || valueString.endsWith("]") || valueString.endsWith("}")) {
                        valueString = valueString.substring(0, valueString.length() - 1);
                    }
                }
                String[] strings = valueString.split(valueAnnotation.delimiter());
                // 注意空串、空值判断
                if (field.getType() == boolean.class) {
                    boolean flag = false;
                    for (String s : strings) {
                        if ("true".equalsIgnoreCase(s)) {
                            field.setAccessible(true);
                            field.set(instance, true);
                            field.setAccessible(false);
                            flag = true;
                            break;
                        } else if ("false".equalsIgnoreCase(s)) {
                            field.setAccessible(true);
                            field.set(instance, false);
                            field.setAccessible(false);
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        field.setAccessible(true);
                        field.set(instance, false);
                        field.setAccessible(false);
                    }
                } else if (field.getType() == int.class) {
                    boolean flag = false;
                    for (String s : strings) {
                        try {
                            int sInt = Integer.parseInt(s);
                            field.setAccessible(true);
                            field.set(instance, sInt);
                            field.setAccessible(false);
                            flag = true;
                            break;
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if (!flag) {
                        field.setAccessible(true);
                        field.set(instance, 0);
                        field.setAccessible(false);
                    }
                } else if (field.getType() == String.class) {
                    field.setAccessible(true);
                    field.set(instance, strings[0]);
                    field.setAccessible(false);
                } else if (field.getType() == boolean[].class) {
                    ArrayList<Boolean> booleans = new ArrayList<>();
                    for (String s : strings) {
                        if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
                            booleans.add(Boolean.parseBoolean(s));
                        }
                    }
                    boolean[] actBooleans = new boolean[booleans.size()];
                    for (int i = 0; i < booleans.size(); i++) {
                        actBooleans[i] = booleans.get(i);
                    }
                    field.setAccessible(true);
                    field.set(instance, actBooleans);
                    field.setAccessible(false);
                } else if (field.getType() == int[].class) {
                    ArrayList<Integer> ints = new ArrayList<>();
                    for (String s : strings) {
                        try {
                            ints.add(Integer.parseInt(s));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    int[] actInts = new int[ints.size()];
                    for (int i = 0; i < ints.size(); i++) {
                        actInts[i] = ints.get(i);
                    }
                    field.setAccessible(true);
                    field.set(instance, actInts);
                    field.setAccessible(false);
                } else if (field.getType() == String[].class) {
                    if (!"".equals(valueString)) {
                        field.setAccessible(true);
                        field.set(instance, strings);
                        field.setAccessible(false);
                    } else {
                        field.setAccessible(true);
                        field.set(instance, new String[0]);
                        field.setAccessible(false);
                    }
                } else if (field.getType() == List.class) {
                    Class<?> type = Class.forName(((ParameterizedType) field.getGenericType())
                            .getActualTypeArguments()[0].getTypeName());
                    List<Object> listObj = new ArrayList<>();
                    if (!"".equals(valueString)) {
                        if (type == Boolean.class) {
                            for (String s : strings) {
                                if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
                                    listObj.add(Boolean.parseBoolean(s));
                                }
                            }
                        } else if (type == Integer.class) {
                            for (String s : strings) {
                                try {
                                    listObj.add(Integer.parseInt(s));
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        } else if (type == String.class) {
                            listObj.addAll(Arrays.asList(strings));
                        }
                    } else {
                        listObj = Collections.emptyList();
                    }
                    field.setAccessible(true);
                    field.set(instance, listObj);
                    field.setAccessible(false);
                } else if (field.getType() == Set.class) {
                    Class<?> type = Class.forName(((ParameterizedType) field.getGenericType())
                            .getActualTypeArguments()[0].getTypeName());
                    Set<Object> setObj = new HashSet<>();
                    if (!"".equals(valueString)) {
                        if (type == Boolean.class) {
                            for (String s : strings) {
                                if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
                                    setObj.add(Boolean.parseBoolean(s));
                                }
                            }
                        } else if (type == Integer.class) {
                            for (String s : strings) {
                                try {
                                    setObj.add(Integer.parseInt(s));
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        } else if (type == String.class) {
                            setObj.addAll(Arrays.asList(strings));
                        }
                    } else {
                        setObj = Collections.emptySet();
                    }
                    field.setAccessible(true);
                    field.set(instance, setObj);
                    field.setAccessible(false);
                } else if (field.getType() == Map.class) {
                    Class<?> keyType = Class.forName(((ParameterizedType) field.getGenericType())
                            .getActualTypeArguments()[0].getTypeName());
                    Class<?> valueType = Class.forName(((ParameterizedType) field.getGenericType())
                            .getActualTypeArguments()[1].getTypeName());
                    Map<Object, Object> mapObj = new HashMap<>();
                    if (!"".equals(valueString)) {
                        if (keyType == Boolean.class) {
                            if (valueType == Boolean.class) {
                                for (String s : strings) {
                                    String[] KeyValue = s.split(":");
                                    String key = KeyValue[0];
                                    String value = KeyValue[1];
                                    if (("true".equalsIgnoreCase(key) || "false".equalsIgnoreCase(key)) &&
                                            ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))) {
                                        mapObj.put(Boolean.parseBoolean(key), Boolean.parseBoolean(value));
                                    }
                                }

                            } else if (valueType == Integer.class) {
                                for (String s : strings) {
                                    String[] KeyValue = s.split(":");
                                    String key = KeyValue[0];
                                    String value = KeyValue[1];
                                    if ("true".equalsIgnoreCase(key) || "false".equalsIgnoreCase(key)) {
                                        try {
                                            mapObj.put(Boolean.parseBoolean(key), Integer.parseInt(value));
                                        } catch (NumberFormatException ignored) {
                                        }
                                    }
                                }
                            } else if (valueType == String.class) {
                                for (String s : strings) {
                                    String[] KeyValue = s.split(":");
                                    String key = KeyValue[0];
                                    String value = KeyValue[1];
                                    if ("true".equalsIgnoreCase(key) || "false".equalsIgnoreCase(key)) {
                                        mapObj.put(Boolean.parseBoolean(key), value);
                                    }
                                }
                            }
                        } else if (keyType == Integer.class) {
                            if (valueType == Boolean.class) {
                                for (String s : strings) {
                                    String[] KeyValue = s.split(":");
                                    String key = KeyValue[0];
                                    String value = KeyValue[1];
                                    if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                                        try {
                                            mapObj.put(Integer.parseInt(key), Boolean.parseBoolean(value));
                                        } catch (NumberFormatException ignored) {
                                        }
                                    }
                                }
                            } else if (valueType == Integer.class) {
                                for (String s : strings) {
                                    String[] KeyValue = s.split(":");
                                    String key = KeyValue[0];
                                    String value = KeyValue[1];
                                    try {
                                        mapObj.put(Integer.parseInt(key), Integer.parseInt(value));
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            } else if (valueType == String.class) {
                                for (String s : strings) {
                                    String[] KeyValue = s.split(":");
                                    String key = KeyValue[0];
                                    String value = KeyValue[1];
                                    try {
                                        mapObj.put(Integer.parseInt(key), value);
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            }
                        } else if (keyType == String.class) {
                            if (valueType == Boolean.class) {
                                for (String s : strings) {
                                    String[] KeyValue = s.split(":");
                                    String key = KeyValue[0];
                                    String value = KeyValue[1];
                                    if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                                        mapObj.put(key, Boolean.parseBoolean(value));
                                    }
                                }
                            } else if (valueType == Integer.class) {
                                for (String s : strings) {
                                    String[] KeyValue = s.split(":");
                                    String key = KeyValue[0];
                                    String value = KeyValue[1];
                                    try {
                                        mapObj.put(key, Integer.parseInt(value));
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            } else if (valueType == String.class) {
                                for (String s : strings) {
                                    String[] KeyValue = s.split(":");
                                    String key = KeyValue[0];
                                    String value = KeyValue[1];
                                    try {
                                        mapObj.put(key, value);
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            }
                        }
                    } else {
                        mapObj = Collections.emptyMap();
                    }
                    field.setAccessible(true);
                    field.set(instance, mapObj);
                    field.setAccessible(false);
                }
            } else if (field.getAnnotation(Inject.class) != null) { // 对于自定义类型的属性值
                Class<?> injectImplClazz = getImplClazz(field.getType().getName());
                Object injectInstance = getInstance(injectImplClazz);
                field.setAccessible(true);
                field.set(instance, injectInstance);
                field.setAccessible(false);
            }
        }
    }

    @Override
    public <T> T createInstance(Class<T> clazz) {
        String clazzName = clazz.getName();
        try {
            // 递归查找到实现类
            Class<?> implClazz = getImplClazz(clazzName);
            Object instance = getInstance(implClazz);
            return (T) instance;
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}



import annotations.Inject;
import annotations.Value;

import java.lang.reflect.*;
import java.util.*;

/**
 * Teaching demo of DI
 */
class AA {
    @Value(value = "15")
    private int field;

    private boolean[] booleans;
    private int[] ints;

    private BB bb;
    private CC cc;

    @Value(value = "CS109,CS309,CS307,CS303")
    private List<String> list;

    private Map<String, Integer> map;

    static StringBuilder sb = new StringBuilder();


    public AA(BB bb, CC cc) {
        this.bb = bb;
        this.cc = cc;
    }

    @Inject
    public AA(BB bb, CC cc,
              @Value(value = "true,false,true,false") boolean[] booleans,
              @Value(value = "10086,10010,10000,baadfood") int[] ints,
              @Value(value = "CS309:100,CS303:99") Map<String, Integer> map) {
        this.bb = bb;
        this.cc = cc;
        this.booleans = booleans;
        this.ints = ints;
        this.map = map;
    }

    public AA(int field) {

    }

    public int getField() {
        return field;
    }

    public List<String> getList() {
        return list;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public static <K, V> String mapToString(Map<K, V> map) {
        sb.setLength(0);
        if (map == null) {
            return null;
        }
        sb.append("{");
        for (Map.Entry<K, V> entry : map.entrySet()) {
            sb.append(String.format("%s:%s", entry.getKey(), entry.getValue())).append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "AA{" +
                "field=" + field +
                ", booleans=" + Arrays.toString(booleans) +
                ", ints=" + Arrays.toString(ints) +
                ", map=" + mapToString(map) +
                ", bb=" + bb +
                ", cc=" + cc +
                '}';
    }
}

class BB {
}

class CC {
}


public class Demo {
    public static void main(String[] args) throws ClassNotFoundException {
        try {
            // Class<?> clazz: Class 类型是定义类的类型。任何一个类 类名.class 可以获取对应的Class类型
            // getDeclaredConstructor() 获取无参构造方法，newInstance() 构建无参实例
            BB bObject = BB.class.getDeclaredConstructor().newInstance();
            CC cObject = CC.class.getDeclaredConstructor().newInstance();

            // get all constructors from AA
            // declared: that return all in current class
            System.out.println("---- Test get all declared constructors ----");
            Constructor<?>[] constructorsA = AA.class.getDeclaredConstructors();
            Arrays.stream(constructorsA).forEach(System.out::println);


            // get all parameter types from constructor public AA(BB bb, CC cc)
            System.out.println();
            System.out.println("---- Test get all declared fields ----");
            Class<?>[] fieldTypes = AA.class.getDeclaredConstructor(BB.class, CC.class).getParameterTypes();
            Arrays.stream(fieldTypes).forEach(System.out::println);

            // Why need to convert Class[] type to Object[] type?
            Object[] objects = new Object[]{bObject, cObject};

            // how to instantiate AA by public AA(BB bb, CC cc) ?
            System.out.println();
            System.out.println("---- Test instantiate AA by public AA(BB bb, CC cc) ----");
            AA aObject = AA.class.getDeclaredConstructor(BB.class, CC.class).newInstance(objects);
            System.out.println(aObject);


            //how to get constructor only with @Inject
            Constructor<?> constructor = null;
            for (Constructor<?> c : AA.class.getDeclaredConstructors()) {
                if (c.getAnnotation(Inject.class) != null) {
                    constructor = c;
                    break;
                }
            }

            System.out.println();
            System.out.println("---- Test get Parameter Types ----");
            assert constructor != null;
            Arrays.stream(constructor.getParameterTypes()).forEach(System.out::println);


            //get all parameter from constructor public AA(BB bb, CC cc)
            //You need to understand the difference between getParameterTypes() with getParameters()
            System.out.println();
            System.out.println("---- Test difference between Parameter and Types ----");
            Parameter[] parameters = constructor.getParameters();


            Object parameterObject1 = null;
            Object parameterObject2 = null;
            Object parameterObject3 = null;
            for (Parameter p : parameters) {
                if (p.getAnnotation(Value.class) != null) {
                    System.out.println("The name of parameter:" + p.getName());
                    System.out.println("The type of parameter:" + p.getType().getName());
                    Value valueAnnotation = p.getAnnotation(Value.class);
                    System.out.println("value = " + valueAnnotation.value());
                    System.out.println("delimiter = " + valueAnnotation.delimiter());

                    // how to inject a boolean[] parameter
                    if (p.getType() == boolean[].class) {
                        String[] strings = valueAnnotation.value().split(valueAnnotation.delimiter());
                        boolean[] booleans = new boolean[strings.length];
                        for (int i = 0; i < strings.length; i++) {
                            booleans[i] = Boolean.parseBoolean(strings[i]);
                        }
                        parameterObject1 = booleans;
                    }
                    ///////////////////////////
                    if (p.getType() == int[].class) {
                        String[] strings = valueAnnotation.value().split(valueAnnotation.delimiter());
                        ArrayList<Integer> ints = new ArrayList<>();
                        for (int i = 0; i < strings.length; i++) {
                            try {
                                ints.add(Integer.parseInt(strings[i]));
                            } catch (NumberFormatException ignored) {

                            }
                        }
                        int[] actInts = new int[ints.size()];
                        for (int i = 0; i < ints.size(); i++) {
                            actInts[i] = ints.get(i);
                        }
                        parameterObject2 = actInts;
                    }
                    //////////////////////////
                    // how to inject a Map<String,Integer> parameter
                    if (p.getType() == Map.class) {
                        // find parameter type of Map
                        Class<?> keyType = Class.forName(((ParameterizedType) p.getParameterizedType())
                                .getActualTypeArguments()[0].getTypeName());
                        Class<?> valueType = Class.forName(((ParameterizedType) p.getParameterizedType())
                                .getActualTypeArguments()[1].getTypeName());
                        System.out.println(keyType + ":" + valueType);
                        String[] elements = valueAnnotation.value().split(valueAnnotation.delimiter());
                        Map<Object, Object> mapObj = new HashMap<>();
                        for (String element : elements) {
                            String key = element.split(":")[0];
                            String value = element.split(":")[1];
                            if (keyType == String.class && valueType == Integer.class) {
                                mapObj.put(key, Integer.parseInt(value));
                            }
                            System.out.printf("%s:%s%n", key, mapObj.get(key));
                        }
                        parameterObject3 = mapObj;
                    }
                }
            }


            // public AA(BB bb, CC cc, @Value(value = "true,false,true,false") boolean[] booleans)
            System.out.println();
            System.out.println("---- Test instantiate AA by public AA(BB bb, CC cc,  boolean[] booleans, Map<>) ----");
            Object[] objects2 = {bObject, cObject, parameterObject1, parameterObject2, parameterObject3};
            AA aObject2 = (AA) constructor.newInstance(objects2);
            System.out.println(aObject2);

            //How to inject value into an int field?
            System.out.println();
            System.out.println("---- Test inject int field----");
            Field intField = aObject.getClass().getDeclaredField("field");
            if (intField.getAnnotation(Value.class) != null) {
                Value valueAnnotation = intField.getAnnotation(Value.class);
                if (intField.getType() == int.class) {
                    intField.setAccessible(true);
                    intField.set(aObject, Integer.parseInt(valueAnnotation.value()));
                    intField.setAccessible(false);
                }
            }
            System.out.println(aObject.getField());


            //How to inject value into a List<String> field?
            System.out.println();
            System.out.println("---- Test inject List<?> field ----");
            Field listField = aObject.getClass().getDeclaredField("list");
            if (listField.getAnnotation(Value.class) != null) {
                Value valueAnnotation = listField.getAnnotation(Value.class);
                if (listField.getType() == List.class) {
                    //find Element type of List
                    Class<?> elementType = Class.forName(((ParameterizedType) listField.getGenericType())
                            .getActualTypeArguments()[0].getTypeName());
                    System.out.println("elementType = " + elementType);
                    String[] element = valueAnnotation.value().split(valueAnnotation.delimiter());
                    List<Object> elementList = null;
                    if (elementType == String.class) {
                        elementList = new ArrayList<>(Arrays.asList(element));
                    }
                    Object fieldObj = elementList;
                    listField.setAccessible(true);
                    listField.set(aObject, fieldObj);
                    listField.setAccessible(false);
                }
            }
            aObject.getList().forEach(System.out::println);


            //Now you can feel relax finishing the fourth assignment!

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}

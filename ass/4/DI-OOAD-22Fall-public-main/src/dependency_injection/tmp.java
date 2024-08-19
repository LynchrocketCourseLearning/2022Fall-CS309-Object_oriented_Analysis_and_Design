package dependency_injection;

public class tmp {
    public static void main(String[] args) {
        String a = "";
        try {
//            int b = Integer.parseInt(a);
            String[] c = a.split(",");
            for (String cc : c) {
                System.out.println(cc);
            }
            System.out.println(c.length);
            System.out.println(c[0].equals(""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package Original.Task3.factoryMethod.src;

public class ArtDesigner extends personInfo implements ITStaff {
    private int level;

    public ArtDesigner() {
        super("ArtDesigner", "123456");
        this.setStartingSalary(7000);
        this.level = (int) (Math.random() * 5 + 1);
    }

    @Override
    public String working() {
        return "Art";
    }

    @Override
    public int getSalary() {
        return super.getStartingSalary() + level * 1500;
    }

    public String toString() {
        return String.format("%-12sname: %-15s, salary: %5d", working(), this.getUserID() + " " + this.getName(), this.getSalary());
    }


}

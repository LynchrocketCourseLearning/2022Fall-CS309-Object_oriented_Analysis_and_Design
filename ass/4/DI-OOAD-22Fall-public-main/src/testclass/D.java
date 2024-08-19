package testclass;


import dependency_injection.Value;

public class D {
    @Value("d.val")
    private int val;

    public int getVal() {
        return val;
    }
}

// 在构建完D实例后，要给其属性val注入一个值
// 在local-value中找这个值： val = 10;

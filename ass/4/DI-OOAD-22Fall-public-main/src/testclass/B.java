package testclass;


import dependency_injection.Inject;

public class B {

    private C cDep;

    public C getCDep() {
        return cDep;
    }

    public D getDDep() {
        return dDep;
    }

    private D dDep;

    @Inject
    public B(C cDep, D dDep) {
        this.cDep = cDep;
        this.dDep = dDep;
    }
}

// B b = new B();
// B.class.getDeclaredConstructor().newInstance();
// B.class.getDeclaredConstructor(C.class, D.class).newInstance(objectC, objectD);
// 在构建B实例之前先要构建C与D的实例
package net.atired.executiveorders.accessors;

public interface LivingEntityAccessor {
    void setExecuteTime(int numb);
    int getExecuteTime();
    void setThunderedTime(int numb);
    int getThunderedTime();
    void setstruckDown(boolean struck);
    boolean isStruckDown();
    void setNoclipped(boolean noclipped);
    boolean isNoclipped();
    void setHollowing(boolean hollowing);
    boolean isHollowing();
}

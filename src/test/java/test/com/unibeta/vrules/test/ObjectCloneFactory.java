package test.com.unibeta.vrules.test;

public class ObjectCloneFactory implements Cloneable{

    private Object object;

    public ObjectCloneFactory() {

    }

    public ObjectCloneFactory(Object obj) {

        object = obj;
    }

    public Object cloneObject() {

        ObjectCloneFactory o = null;
        try {
            o = (ObjectCloneFactory) super.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return o.object;
    }

    public void set(Object obj) {

        this.object = obj;
    }

    public Object get() {

        return cloneObject();
    }
}

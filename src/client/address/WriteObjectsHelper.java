package client.address;

import javafx.beans.property.*;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * This class is used to automate the process of serializing objects
 * @author Elloco
 * solution was found here: https://stackoverflow.com/questions/18791566/notserializableexception-on-simplelistproperty
 */
public class WriteObjectsHelper
{

    /**
     * Write a string property to the output stream
     * @param s The objectOutputStream being used
     * @param strProp The string property
     * @throws IOException
     */
    public static void writeStringProp(ObjectOutputStream s, StringProperty strProp) throws IOException
    {
        s.writeUTF(strProp.getValueSafe());
    }


    /**
     * Write a list property to the output stream
     * @param s the ObjectOutputStream being used
     * @param lstProp the string property
     * @throws IOException
     */
    public static void writeListProp(ObjectOutputStream s, ListProperty lstProp) throws IOException {
        if(lstProp==null || lstProp.getValue()==null) {
            s.writeInt(0);
            return;
        }
        s.writeInt(lstProp.size());
        for(Object elt:lstProp.getValue()) s.writeObject(elt);
    }

    /**
     * Receive a set of properties whose types are automatically picked and written
     * to the output stream
     * @param s Objectoutput stream being written to
     * @param properties the collection of properties given
     * @throws IOException
     */
    public static void writeAllProp(ObjectOutputStream s, Property... properties) throws IOException {
        s.defaultWriteObject();
        for(Property prop:properties) {
            if(prop instanceof IntegerProperty) s.writeInt(((IntegerProperty) prop).intValue());
            else if(prop instanceof LongProperty) s.writeLong(((LongProperty) prop).longValue());
            else if(prop instanceof StringProperty) s.writeUTF(((StringProperty)prop).getValueSafe());
            else if(prop instanceof BooleanProperty) s.writeBoolean(((BooleanProperty)prop).get());
            else if(prop instanceof ListProperty) writeListProp(s,(ListProperty)prop);
            else if(prop instanceof ObjectProperty) s.writeObject(((ObjectProperty) prop).get());
            else throw new RuntimeException("Type d'objet incompatible : " + prop.toString());
        }
    }
}

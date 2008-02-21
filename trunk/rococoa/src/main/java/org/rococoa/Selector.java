package org.rococoa;

import com.sun.jna.IntegerType;

@SuppressWarnings("nls")
public class Selector extends IntegerType {
    
    private  String name;

    public Selector() {
        this("undefined selector", 0);
    };
    
    public Selector(String name, long value) {
        super(4, value);
        this.name = name;
    }
    
    // used for setting name once we have got one from OC
    Selector initName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return String.format("[Selector %s]", name); 
    }
    
}

package org.rococoa;

@SuppressWarnings("nls")
public class VarArgsUnpacker {

    private static final String SEPERATOR = ", ";
    private static final Object[] NULLARGS = new Object[0];
    private final Object[] args;

    public VarArgsUnpacker(Object... args) {
        this.args = args != null ? args : NULLARGS;        
    }
    
    @Override
    public String toString() {
        StringBuilder  result = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            result.append(String.valueOf(args[i])).append(SEPERATOR);
        }
        if (result.length() > 0)
            result.setLength(result.length() - SEPERATOR.length());
        return result.toString();
    }

}

package org.rococoa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public abstract class RococoaTestCase extends TestCase {
    
    static {initializeLogging();};

    protected static Logger logging = LoggerFactory.getLogger("Rococoa");

    public static void initializeLogging() {
        try {
            InputStream is = null;
            try {
                is = RococoaTestCase.class.getResourceAsStream("/test-logging.properties");
                if (is == null)
                    throw new FileNotFoundException("Cannot find test-logging.properties");
                LogManager.getLogManager().readConfiguration(is);
            } finally {
                if (is != null)
                    is.close();
            }
        } catch (IOException x) {
            throw new RuntimeException("Could not initialize logging", x);
            
        }
    }
    
    protected ID pool;
    
    @Override
    public void runBare() throws Throwable {
        pool = Foundation.createPool();
        try {
            super.runBare();
        } finally {
            Foundation.releasePool(pool);
        }
    }

}

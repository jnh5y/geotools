package org.geotools.coverage.io.grib;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;


import ucar.nc2.grib.GribCollection;
import ucar.nc2.util.DiskCache2;

/**
 * Helper class used for setting a GRIB cache if defined with the JAVA argument -DGRIB_CACHE_DIR
 * 
 * @author Nicola Lagomarsini GeoSolutions S.A.S.
 *
 */
public class GribUtilities {

    /** The LOGGER for this class. */
    private static final Logger LOGGER = Logger.getLogger(GribUtilities.class.toString());
    
    /** String associated to the grib cache directory property */
    private static final String GRIB_CACHE_DIR = "GRIB_CACHE_DIR";

    /**
     * Static initialization of the GRIB cache directory if set as JAVA argument
     */
    static {

        final Object cacheDir = System.getProperty(GRIB_CACHE_DIR);
        if (cacheDir != null) {
            String dir = (String) cacheDir;
            final File file = new File(dir);
            if (isValid(file, GRIB_CACHE_DIR)) {
                DiskCache2 cache = new DiskCache2();
                cache.setRootDirectory(dir);
                cache.setAlwaysUseCache(true);
                GribCollection.setDiskCache2(cache);
            }
        }
    }

    /**
     * Method for checking if the input file is an existing writable directory.
     *
     * @param file
     * @param property
     * @return
     */
    public static boolean isValid(File file, String property) {
        String dir = file.getAbsolutePath();
        if (!file.exists()) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("The specified " + property + " property doesn't refer "
                        + "to an existing folder. Please check the path: " + dir);
            }
            return false;
        } else if (!file.isDirectory()) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("The specified " + property + " property doesn't refer "
                        + "to a directory. Please check the path: " + dir);
            }
            return false;
        } else if (!file.canWrite()) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("The specified " + property + " property refers to "
                        + "a directory which can't be write. Please check the path and"
                        + " the permissions for: " + dir);
            }
            return false;
        }
        return true;
    }
}

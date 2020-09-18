package server.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class used to manage the properties of the project which can be modified to change the params
 * used in the whole application
 *
 */
public class ProjectProperties {

    private static ProjectProperties instance;

    private ProjectProperties() {
    }

    public static ProjectProperties getInstance() {
        if (instance == null) {
            instance = new ProjectProperties();
        }
        return instance;
    }

    private Properties projectProperties;

    private Properties getProperties() {
        if (projectProperties == null) {
            try {
                projectProperties = new Properties();
                projectProperties.load(ProjectProperties.class.getResourceAsStream("/config.properties"));
            } catch (IOException ex) {
                org.apache.log4j.Logger.getLogger(this.getClass()).error(ex, ex);
            }
        }
        return projectProperties;
    }

    /**
     * Get a property from the property file
     *
     * @param propertyName the property name
     * @return the corresponding property
     */
    public String getProperty(String propertyName) {
        return this.getProperties().getProperty(propertyName);
    }
}

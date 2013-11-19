package nz.org.nesi.appmanage.model;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import grisu.jcommons.interfaces.GrinformationManagerDozer;
import grisu.jcommons.view.html.VelocityUtils;
import grisu.model.info.dto.Application;
import grisu.model.info.dto.Queue;
import grisu.model.info.dto.Version;
import nz.org.nesi.appmanage.Utils;
import nz.org.nesi.appmanage.exceptions.AppFileException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Project: Applications
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 15/11/13
 * Time: 8:25 AM
 */
public class Documentation {

    public final static GrinformationManagerDozer gm = new GrinformationManagerDozer("git://github.com/nesi/nesi-grid-info.git/nesi/nesi.groovy");

    public static final String README_FILE_NAME = "Readme.md";
    public static final String SUMMARY_TEMPLATE_FILE_NAME = "Summary_template.md";
    public static final String APP_PROPERTIES_NAME = "app.properties";
    public static final String SUMMARY_FILE_NAME = "Home.md";
    public static final String APP_SUBFOLDER = "apps";
    public static final String DOC_FOLDER = "doc";
    public static final String USAGE_FILE_NAME = "Usage.md";
    public static final String DESCRIPTION_FILE_NAME = "Description.md";
    public static final String APPLICATION_PAGE_TEMPLATE_FILE_NAME = "Application_page_template.md";

    private final File appRoot;
    private final File appFolder;
    private final File docRoot;

    private final String appName;
    private final File propsFile;
    private final String docPageContent;

    private final Map<String, Object> properties = Maps.newLinkedHashMap();

    private final Jobs jobs;

    public Documentation(File appFolder, File appRoot) {
        if (appRoot == null || !appRoot.exists() || !appRoot.isDirectory()) {
            throw new AppFileException("Application root '"+appRoot.getAbsolutePath()+"' not valid");
        }
        if (appFolder == null || !appFolder.isDirectory() || appFolder.getName().startsWith(".")
                || "scripts".equals(appFolder.getName())|| "logs".equals(appFolder.getName())) {
            throw new AppFileException("Application folder '"+appFolder.getAbsolutePath()+"' not valid");
        }
        this.appRoot = appRoot;
        this.appFolder = appFolder;
        this.docRoot = new File(appFolder, DOC_FOLDER);
        this.appName = Utils.getApplication(appFolder, appRoot);

        properties.put("application", this.appName);

        propsFile = new File(this.docRoot, APP_PROPERTIES_NAME);
        if (propsFile.exists()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(propsFile));
                for ( String key : props.stringPropertyNames() ) {
                properties.put(key, props.get(key));
            }
            } catch (Exception e) {
                throw new AppFileException("Can't load application properties from '"+propsFile.getAbsolutePath()+"'", e);
            }
            for ( String key : props.stringPropertyNames() ) {
                properties.put(key, props.get(key));
            }

        }

        File usageFile = new File(docRoot, USAGE_FILE_NAME);
        if ( usageFile.exists() ) {
            try {
                properties.put("usage", FileUtils.readFileToString(usageFile));
            } catch (IOException e) {
                throw new AppFileException("Can't read file "+usageFile.getAbsolutePath());
            }
        } else {
            properties.put("usage", null);
        }

        File descFile = new File(docRoot, DESCRIPTION_FILE_NAME);
        if ( descFile.exists() ) {
            try {
                properties.put("description", FileUtils.readFileToString(descFile));
            } catch (IOException e) {
                throw new AppFileException("Can't read file "+descFile.getAbsolutePath());
            }
        } else {
            properties.put("description", null);
        }

        List<Application> allapps = gm.getAllApplicationsOnGrid();

        Queue q = gm.getQueue("pan:gram.uoa.nesi.org.nz");
        List<Version> versions = gm.getVersionsOfApplicationOnSubmissionLocation(appName, q.toString());
        List<String> v = Lists.newLinkedList();
        for ( Version ver : versions ) {
            if (!Version.ANY_VERSION.equals(ver))
                v.add(ver.getVersion());
        }
        properties.put("versions", v);

        jobs = new Jobs(appFolder, appRoot);
        if ( jobs.getJobs().size() > 0 ) {
            properties.put("jobs", jobs);
        } else {
            properties.put("jobs", null);
        }

        docPageContent = VelocityUtils.render(APPLICATION_PAGE_TEMPLATE_FILE_NAME, properties);

    }

    public String createExampleJobPage(Job job) {


        return null;
    }

    public File getApplicationRoot() {
        return this.appRoot;
    }

    public File getApplicationFolder() {
        return this.appFolder;
    }

    public File getDocumentationFolder() {
        return this.docRoot;
    }

    public String getApplicationName() {
        return this.appName;
    }

    public File getDocFile(String relativeFilename) {
        File temp = new File(docRoot, relativeFilename);

        if ( temp.exists() ) {
            return temp;
        } else {
            return null;
        }
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Object getProperty(String key) {
        return getProperties().get(key);
    }

    public String getDocPageContent() {
        return docPageContent;
    }

    public Jobs getJobs() {
      return jobs;
    }
}

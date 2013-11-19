package nz.org.nesi.appmanage

import grisu.jcommons.interfaces.GrinformationManagerDozer
import grisu.jcommons.view.html.VelocityUtils
import grisu.model.info.dto.Application
import grisu.model.info.dto.Queue
import grisu.model.info.dto.Version
import org.apache.commons.io.FileUtils
/**
 * Project: grisu
 *
 * Written by: Markus Binsteiner
 * Date: 6/06/13
 * Time: 10:53 AM
 */
class DocTemplateCreator {

    public static final String README_FILE_NAME = "Readme.md"
    public static final String APP_PROPERTIES_NAME = "app.properties"
    public static final String MAIN_FILE_NAME = "Home.md"
    public static final String APP_SUBFOLDER = "apps"

    GrinformationManagerDozer gm = new GrinformationManagerDozer("/home/markus/src/config/nesi-grid-info/nesi/nesi.groovy")


    public static void main (String[] args) {

        // basic housekeeping
//        LoginManager.initGrisuClient("create-doc-confluence");

        DocTemplateCreator dtc = new DocTemplateCreator()
        dtc.outputFolder = new File("/home/markus/doc/applications.wiki")
        dtc.applications = ["Abaqus", "Java"]
        dtc.appRoot = new File("/home/markus/src/config/applications")

        println dtc.createPageString()
    }

    File outputFolder
    List<String> applications
    File appRoot

    public void validate() {
        if (!new File(getOutputFolder()).exists()) {
            new File(getOutputFolder()).mkdirs()
        }
    }

    public void printMessage(String msg, boolean isError=false) {
        println msg
    }


    public String createPageString() {
        //        String homepage = "#Nesi applications repository\n\n"
        String homepage = ""

        if (getApplications()) {
            for (String app : getApplications()) {
                def appFolder = Utils.getApplicationFolder(getAppRoot(), app)
                String appSubPage = createApplicationPage(appFolder)

                homepage += createHomepageEntry(appFolder, appSubPage)
            }
        } else {

            getAppRoot().listFiles().sort { it.name }.each { it ->

                if (!it.isDirectory()) {
                    return
                }
                printMessage("Checking application: " + it.getName())

                String appSubpage = createApplicationPage(it)

                homepage += createHomepageEntry(it, appSubpage)


            }

        }

        return homepage

    }

    public void execute() {

        def homepage = createPageString()

        println homepage

        if (getOutputFolder()) {
            File main = new File(getOutputFolder(), MAIN_FILE_NAME)
            FileUtils.deleteQuietly(main)
            FileUtils.write(main, homepage)
        }
    }


    private String createApplicationPage(File appFolder) {

        String appName = Utils.getApplication(appFolder, getAppRoot())

        if (!appFolder || !appFolder.exists() || !appFolder.isDirectory()) {
            printMessage("Folder: " + appFolder + " not available", true)
            return
        }

        File readme_template = new File(appFolder, README_FILE_NAME + ".vm")

        def properties = [:]
        properties.put('xxxx', 'yyyy')
        properties.put('aaaa', 'zzzz')

        String page = VelocityUtils.render(readme_template, properties)

        if (getOutputFolder()) {

            File applicationsSubdir = new File(getOutputFolder(), APP_SUBFOLDER)
            if (!applicationsSubdir.exists()) {
                applicationsSubdir.mkdirs()
            }

            File pageFile = new File(applicationsSubdir, appName + ".md")
            FileUtils.write(pageFile, page)

            return appName
        } else {
            return null

        }
    }

    private String createHomepageEntry(File appFolder, String appSubPage) {

        String appName = Utils.getApplication(appFolder, getAppRoot())

        if (!appFolder || !appFolder.exists() || !appFolder.isDirectory()) {
            printMessage("Folder: " + appFolder + " not available", true)
            return
        }
        File readme = new File(appFolder, README_FILE_NAME)

        List<Application> allapps = gm.getAllApplicationsOnGrid();

        Queue q = gm.getQueue("pan:gram.uoa.nesi.org.nz")


        List<Version> versions = gm.getVersionsOfApplicationOnSubmissionLocation(appName, q.toString())
        def v = []
        versions.each {
            if (!Version.ANY_VERSION.equals(it))
                v.add(it.getVersion())
        }

        def properties = [:]
        File propsFile = new File(appFolder, APP_PROPERTIES_NAME)
        if (propsFile.exists()) {
            Properties props = new Properties()
            props.load(propsFile.newDataInputStream())
            properties.putAll(props)
        }

        properties.put("appSubPage", appSubPage)
        properties.put("application", appName)
        properties.put("versions", v)


        String readme_content = VelocityUtils.render(README_FILE_NAME, properties)
        return readme_content


    }


}

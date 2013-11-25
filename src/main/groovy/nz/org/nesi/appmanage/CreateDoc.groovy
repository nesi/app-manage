package nz.org.nesi.appmanage

import com.google.common.collect.Maps
import grisu.frontend.control.login.LoginManager
import grisu.jcommons.interfaces.GrinformationManagerDozer
import grisu.jcommons.view.html.VelocityUtils
import nz.org.nesi.appmanage.exceptions.AppFileException
import nz.org.nesi.appmanage.model.Documentation
import org.apache.commons.io.FileUtils
/**
 * Project: grisu
 *
 * Written by: Markus Binsteiner
 * Date: 6/06/13
 * Time: 10:53 AM
 */
class CreateDoc extends CreateDocumentationCliParameters {


    GrinformationManagerDozer gm = new GrinformationManagerDozer("git://github.com/nesi/nesi-grid-info.git/nesi/nesi.groovy")

    public static void main(String[] args) {
        createDoc()

        System.exit(0)
    }

    def appsToProcess = [:]

    public CreateDoc() {

    }

    public static void createDoc() {

        def args = ["-v", "-a",
                "/home/markus/src/config/applications", "create-doc", "--applications",
                "Abaqus,Java", "--output-dir", "/home/markus/doc/applications.wiki"];

        // basic housekeeping
        LoginManager.initGrisuClient("create-doc-confluence");

        // helps to parse commandline arguments, if you don't want to create
        // your own parameter class, just use DefaultCliParameters
        MainCliParameters params = new MainCliParameters();
        ExportModule expParams = new ExportModule();
        ImportModule impParams = new ImportModule();
        CreateDocumentationCliParameters createDocParams = new CreateDoc();

        // create the client
        AppManage client = null;
        try {
            client = new AppManage(params, expParams, impParams, createDocParams, args as String[]);
        } catch (Exception e) {
            System.err.println("Could not start app-manage: "
                    + e.getLocalizedMessage());
            return;
        }


    }

    public void validate() {
        if (!new File(getOutputFolder()).exists()) {
            new File(getOutputFolder()).mkdirs()
        }
    }

    public String createPageString() {


        def properties = [:]

        Map<String, Map<String, Object>> applications = Maps.newTreeMap()
        appsToProcess.each { appName, doc ->
            applications.put(appName, doc)
        }

        applications = applications.sort { a, b ->
            a.key.compareToIgnoreCase b.key
        }

        properties.put("applications", applications)

        String content = VelocityUtils.render((String)getSummaryTemplate(), properties)

        return content

    }

    public void execute() {

        if (getApplications()) {
            for (String app : getApplications()) {
                def appFolder = Utils.getApplicationFolder(getAppRoot(), app)
                try {
                    Documentation temp = new Documentation(appFolder, getAppRoot())
                    appsToProcess.put(temp.getApplicationName(), temp)
                } catch (AppFileException afe) {
                    printMessage("Ignoring folder " + appFolder + ": " + afe.getLocalizedMessage(), true)
                }

            }
        } else {
            getAppRoot().listFiles().sort { it.name }.each { appFolder ->
                try {
                    Documentation temp = new Documentation(appFolder, getAppRoot())
                    appsToProcess.put(temp.getApplicationName(), temp)
                } catch (AppFileException afe) {
                    printMessage("Ignoring folder " + appFolder + ": " + afe.getLocalizedMessage(), true)
                }

            }
        }

        if (getOutputFolder()) {

            for ( String app : appsToProcess.keySet() ) {
                Documentation doc = appsToProcess.get(app)
                String content = doc.getDocPageContent()
                File mdFile = new File(getOutputFolder(), Documentation.APP_SUBFOLDER+File.separator+doc.getApplicationName()+".md")
                FileUtils.deleteQuietly(mdFile)
                FileUtils.write(mdFile, content)
                doc.getProperties().put("appSubPage", doc.getApplicationName())
            }

            def homepage = createPageString()

            println homepage

            File main = new File(getOutputFolder(), Documentation.SUMMARY_FILE_NAME)
            FileUtils.deleteQuietly(main)
            FileUtils.write(main, homepage)

        } else {
            def homepage = createPageString()
            println homepage
        }
    }


}

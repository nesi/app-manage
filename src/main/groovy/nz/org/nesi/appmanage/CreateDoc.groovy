package nz.org.nesi.appmanage
import com.google.common.collect.Maps
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

    def appsToProcess = [:]

    public CreateDoc() {

    }

    public void validate() {
        if (!new File(getOutputFolder()).exists()) {
            new File(getOutputFolder()).mkdirs()
        }
    }

    public String createPageString() {


        def properties = [:]

        def ignore = getIgnore()

        if ( ignore ) {
            def temp = new File(ignore)
            if ( temp.exists() ) {
                ignore = FileUtils.readLines(temp)
            } else {
                ignore = ignore.split(",")
            }
        } else {
            ignore = []
        }

        ignore.collect() { it ->
            it = it.toLowerCase().trim()
        }


        Map<String, Map<String, Object>> applications = Maps.newTreeMap()
        appsToProcess.each { appName, doc ->
            if ( ! ignore.contains(appName) ) {
                if ( doc.getVersions().size() == 0 ) {
                    return;
                }
                applications.put(appName, doc)
                if ( isCreateStub() ) {
                    doc.createMssingFiles()
                }
            }
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

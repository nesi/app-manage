package nz.org.nesi.appmanage
import com.google.common.collect.Maps
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
class CheckDoc extends nz.org.nesi.appmanage.CheckDocsCliParameters {


    def appsToProcess = [:]

    public CheckDoc() {

    }


    public void validate() {

    }

    public List<File> checkDoc() {


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

        def createdFiles = []
        Map<String, Map<String, Object>> applications = Maps.newTreeMap()
        appsToProcess.each { appName, doc ->
            if ( ! ignore.contains(appName) ) {
                applications.put(appName, doc)
                if ( isCreateDoc() ) {
                    createdFiles.addAll(doc.createMissingFiles())
                }
            }
        }

        return createdFiles

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

        String result = checkDoc().join(", ")

        println result
    }


}

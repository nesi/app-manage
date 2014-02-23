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
class CreateAppList extends CreateAppListCliParameters {

    def appsToProcess = [:]

    public CreateAppList() {

    }

    public void validate() {
        if (getOutputFile() != null && !new File(getOutputFile()).exists()) {
            new File(getOutputFile()).mkdirs()
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
                if ( doc.getTags().contains(Documentation.IGNORE_APP_TAG) ) {
                    return;
                }
                applications.put(appName, doc)

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

        List<String> tags = null;
        if ( getTags() ) {
            tags = getTags().split(",")
        }


        if (getApplications()) {
            for (String app : getApplications()) {
                def appFolder = Utils.getApplicationFolder(getAppRoot(), app)
                try {
                    Documentation temp = new Documentation(appFolder, getAppRoot())
                    if ( ! tags || temp.getTags().intersect(tags) ) {
                        appsToProcess.put(temp.getApplicationName(), temp)
                    }
                } catch (AppFileException afe) {
                    printMessage("Ignoring folder " + appFolder + ": " + afe.getLocalizedMessage(), true)
                }

            }
        } else {
            getAppRoot().listFiles().sort { it.name }.each { appFolder ->
                try {
                    Documentation temp = new Documentation(appFolder, getAppRoot())
                    if ( ! tags || temp.getTags().intersect(tags) ) {
                        appsToProcess.put(temp.getApplicationName(), temp)
                    }
                } catch (AppFileException afe) {
                    printMessage("Ignoring folder " + appFolder + ": " + afe.getLocalizedMessage(), true)
                }

            }
        }

            //for ( String app : appsToProcess.keySet() ) {
            //    Documentation doc = appsToProcess.get(app)
//                String content = doc.getDocPageContent()
//                doc.getProperties().put("appSubPage", doc.getApplicationName())
//            }

        def page = createPageString()

        if ( getOutputFile() ) {
            FileUtils.deleteQuietly(new File(getOutputFile()))
            FileUtils.write(new File(getOutputFile()), page)
        } else {
            println page
        }

    }


}

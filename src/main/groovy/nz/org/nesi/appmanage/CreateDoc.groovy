package nz.org.nesi.appmanage

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
        if (getOutputFolder() != null && !new File(getOutputFolder()).exists()) {
            new File(getOutputFolder()).mkdirs()
        }
    }

    public void execute() {

        if (getApplications()) {
            for (String app : getApplications()) {
                def appFolder = Utils.getApplicationFolder(getAppRoot(), app)
                try {
                    Documentation temp = new Documentation(appFolder, getAppRoot(), getApplicationPageTemplate())
                    appsToProcess.put(temp.getApplicationName(), temp)
                } catch (AppFileException afe) {
                    printMessage("Ignoring folder " + appFolder + ": " + afe.getLocalizedMessage(), true)
                }

            }
        } else {
            getAppRoot().listFiles().sort { it.name }.each { appFolder ->
                try {
                    Documentation temp = new Documentation(appFolder, getAppRoot(), getApplicationPageTemplate())
                    appsToProcess.put(temp.getApplicationName(), temp)
                } catch (AppFileException afe) {
                    printMessage("Ignoring folder " + appFolder + ": " + afe.getLocalizedMessage(), true)
                }

            }
        }


        for (String app : appsToProcess.keySet()) {
            Documentation doc = appsToProcess.get(app)
            String content = doc.getDocPageContent()
            if (getOutputFolder()) {
                File mdFile = new File(getOutputFolder(), doc.getApplicationName() + ".md")
                FileUtils.deleteQuietly(mdFile)
                FileUtils.write(mdFile, content)
                //doc.getProperties().put("appSubPage", doc.getApplicationName())
            } else {
                System.out.println(content);
                System.out.println("");
            }


        }
    }
}
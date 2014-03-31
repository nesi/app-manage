package nz.org.nesi.appmanage
import com.google.common.collect.Sets
import grisu.control.ServiceInterface
import grisu.jcommons.constants.Constants
import groovy.io.FileType
import nz.org.nesi.appmanage.model.Documentation
import org.apache.commons.io.FileUtils
/**
 * Project: grisu
 *
 * Written by: Markus Binsteiner
 * Date: 6/06/13
 * Time: 10:53 AM
 */
class ImportModule extends ImportModulesCliParameters {


    public void validate() {

        if (!new File(getInput()).exists()) {
            throw new RuntimeException("Import folder '" + getInput() + "' does not exist");
        }

    }

    public void execute() {

        def allmodules = Sets.newTreeSet()

        new File(getInput()).traverse(type: FileType.FILES) { it ->
            if ( it.getName().endsWith("~") ) {
                return
            }
            allmodules << it
        }

        printMessage("Copying modules...")

        allmodules.each { file ->

            def app
            try {
                app = Utils.getApplication(file, getInput())
            } catch (Exception e) {
                printMessage("Can't parse module file: '" + file + "': " + e.getLocalizedMessage()+". Ignored.", true)
                return
            }

            File appDir = Utils.getApplicationFolder(getAppRoot(), app)

            File moduleDir = new File(appDir, AppManage.MODULE_DIR_NAME + File.separator + getToken())

            moduleDir.mkdirs()
            File newFile = new File(moduleDir, file.getName())

            if (newFile.exists()) {

                if (Utils.bothFilesEqual(file, newFile)) {
                    printMessage("Ignoring module: " + app + "/" + file.getName())
                } else {
                    printMessage("Replacing module: " + app + "/" + file.getName())
                }
            } else {
                printMessage("Copying module: " + app + "/" + file.getName())
            }


            FileUtils.copyFile(file, newFile)

            newFile.setExecutable(true, false)
            newFile.setReadable(true, false)
            newFile.setWritable(true, true)

            if ( getTags() ) {
                Documentation doc = new Documentation(appDir, getAppRoot())

                doc.ensureTags(getTags().split(",") as Set);
            }

        }

        if (isUpdateGrisu()) {
            println "Updating info..."
            ServiceInterface si = client.getServiceInterface();
            si.admin(Constants.REFRESH_GRID_INFO, null);
        }

    }


}

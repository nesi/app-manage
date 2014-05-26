package nz.org.nesi.appmanage
import com.google.common.collect.Sets
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
            if ( it.getName().equalsIgnoreCase("default") ) {
                return
            }
            allmodules << it
        }

        printMessage("Copying modules...")

        def docMap = [:]

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

            Documentation doc = new Documentation(appDir, getAppRoot())
            docMap[app] = doc

            if ( getTags() ) {

                doc.ensureTags(getTags().split(",") as Set);
            }

        }

        if (isSync()) {

            File targetRoot = new File(getInput())

            // deleting modules for empty application directories
            targetRoot.eachFile (FileType.DIRECTORIES, { dir ->
                if ( ! docMap.get(dir.getName()) ) {
//                    println "EMPTY: " + dir
                    def potentialAppDir = new File(dir.getName(), getAppRoot())
                    if (potentialAppDir.exists()) {
                        docMap[dir.getName()] = new Documentation(new File(dir.getName(), getAppRoot()), getAppRoot())
                    }

                }
            }
            )

            docMap.each{ app, doc ->

                def versions = doc.getVersions()

//                println "app "+app
//                println "doc "+doc.getApplicationFolder()
//                println "versions "+versions

                File moduleDir = new File(targetRoot, app)

                def valid_versions = []

                moduleDir.eachFile(FileType.FILES) { file ->
                    valid_versions << file.getName()
                }

                //println "target "+moduleDir+" "+valid_versions

                for (String version : versions) {
                    if (! valid_versions.contains(version)) {
                        def msg = 'deleting module in application repository: '+app+"/"+version
                        printMessage(msg, false, true)
                        boolean deleted = doc.deleteModule(version)
                    }
                }


            }



        }

    }


}

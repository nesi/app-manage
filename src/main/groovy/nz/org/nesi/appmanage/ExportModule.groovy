package nz.org.nesi.appmanage

import grisu.jcommons.constants.Constants
import groovy.io.FileType
import org.apache.commons.io.FileUtils

/**
 * Project: grisu
 *
 * Written by: Markus Binsteiner
 * Date: 6/06/13
 * Time: 10:53 AM
 */
class ExportModule extends ExportModulesCliParameters {


    public static List<File> getAllModuleFiles(File appRoot, String token) {
        def allmodules = []
        appRoot.traverse(type: FileType.FILES) { it ->
            if (it.getAbsolutePath().toLowerCase().contains(File.separator + AppManage.MODULE_DIR_NAME + File.separator)) {
                allmodules << it
            }
        }

        def modules = allmodules.findAll { it ->
            it.getAbsolutePath().toLowerCase().contains(File.separator+token.toLowerCase())
        }
        return modules
    }

    public void execute() {

        def modules = getAllModuleFiles(appRoot, token)
        println "Copying modules..."

        modules.each { file ->
            def app = Utils.getApplication(file, appRoot)

            File appDir = new File(getOutput(), app);
            appDir.mkdirs()
            File newFile = new File(appDir, file.getName())

            if (verbose) {
                if (newFile.exists()) {
                    println "Replacing module: " + app + "/" + file.getName()
                } else {
                    println "Copying module: " + app + "/" + file.getName()
                }

            }
            FileUtils.copyFile(file, newFile)

            newFile.setExecutable(true, false)
            newFile.setReadable(true, false)
            newFile.setWritable(true, true)

        }


    }

    public void validate() {
        if (!new File(getOutput()).exists()) {
            new File(getOutput()).mkdirs()
        }
    }


}

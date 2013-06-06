package nz.org.nesi.appmanage

import grisu.control.ServiceInterface
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
class ExportModule {

    private final MainCliParameters mainParams
    private final ExportModulesCliParameters exportParams

    private final File appRoot
    private final String token
    private final File outputDir
    private final boolean updateInfo
    private final AppManage client
    private final boolean verbose

    public ExportModule(AppManage client) {
        this.client = client
        this.mainParams = client.mainParams
        this.exportParams = client.exportModulesCliParameters
        this.appRoot = client.mainParams.getApplicationsRootFile()
        this.token = client.exportModulesCliParameters.getToken()
        this.outputDir = new File(client.exportModulesCliParameters.getOutput())

        if ( ! this.outputDir.exists() ) {
            this.outputDir.mkdirs()
        }
        this.updateInfo = client.exportModulesCliParameters.updateGrisu
        this.verbose = client.mainParams.isVerbose()
    }

    public void execute() {

        def allmodules = []
        appRoot.traverse(type: FileType.FILES) { it ->
            if (it.getAbsolutePath().toLowerCase().contains(File.separator + 'modules' + File.separator)) {
                allmodules << it
            }
        }

        def modules = allmodules.findAll { it ->
            it.getAbsolutePath().toLowerCase().contains(token)
        }

        println "Copying modules..."

        modules.each { file ->
            def app = Utils.getApplication(file, appRoot)

            File appDir = new File(outputDir, app);
            appDir.mkdirs()
            File newFile = new File(appDir, file.getName())

            if ( verbose ) {
                if ( newFile.exists() ) {
                    println "Replacing module: "+app+"/"+file.getName()
                } else {
                    println "Copying module: "+app+"/"+file.getName()
                }

            }
            FileUtils.copyFile(file, newFile)

            newFile.setExecutable(true, false)
            newFile.setReadable(true, false)
            newFile.setWritable(true, true)

        }

        if ( updateInfo ) {
            println "Updating info..."
            ServiceInterface si = client.getServiceInterface();
            si.admin(Constants.REFRESH_GRID_INFO, null);
        }

    }


}

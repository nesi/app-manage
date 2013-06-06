package nz.org.nesi.appmanage

/**
 * Project: grisu
 *
 * Written by: Markus Binsteiner
 * Date: 6/06/13
 * Time: 11:44 AM
 */
class Utils {

    static String getApplication(def path, def appRoot) {

        if ( path instanceof File ) {
            path = path.getAbsolutePath()
        }

        if ( appRoot instanceof File ) {
            appRoot = appRoot.getAbsolutePath()
        }

        if ( ! path.startsWith(appRoot) ) {
            throw new RuntimeException("Not a valid path: "+path)
        }

        String relPath = path - appRoot
        if ( relPath.startsWith(File.separator) ) {
            relPath = relPath.substring(1)
        }

        int i = relPath.indexOf(File.separator)
        relPath = relPath.substring(0, i)

        return relPath

    }
}

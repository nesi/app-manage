package nz.org.nesi.appmanage

import com.google.common.base.Charsets
import com.google.common.io.Files
import difflib.DiffUtils
import difflib.Patch
import groovy.io.FileType

/**
 * Project: grisu
 *
 * Written by: Markus Binsteiner
 * Date: 6/06/13
 * Time: 11:44 AM
 */
class Utils {

    public static boolean bothFilesEqual(File file1, File file2) {

        List<String> file1Content = Files.readLines(file1, Charsets.UTF_8)
        List<String> file2Content = Files.readLines(file2, Charsets.UTF_8)

         Patch patch = DiffUtils.diff(file1Content, file2Content)

        if (patch.getDeltas().size() == 0 ) {
            return true;
        } else {
            return false;
        }
    }

    static File getApplicationFolder(def appRoot, def app) {

        if ( appRoot instanceof String ) {
            appRoot = new File(appRoot)
        }

        appRoot.traverse(type: FileType.DIRECTORIES) { it ->
            if ( it.getName().equalsIgnoreCase(app) ) {
                return it
            }
        }

        return new File(appRoot, app)

    }

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

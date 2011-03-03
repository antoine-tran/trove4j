package gnu.trove;

/**
 * Simple class meant as a possible main class (via manifest) to report the
 * implementation version of the trove4j jar.
 *
 * This may be useful to ask feedback WITH build version information
 *
 * The Main-Class entry in the manifest.mf should be set during the build as well
 * as the Implementation-Version manifest attribute should be set as well.
 *
 * Created by IntelliJ IDEA.
 * User: Johan Parent
 * Date: 3/03/11
 * Time: 22:10
 * To change this template use File | Settings | File Templates.
 */
public class Version {
    public static void main(String[] args) {
        String version = Version.class.getPackage().getImplementationVersion();
        //
        if (version != null) {
            System.out.println("trove4j version " + version);
        } else {
            System.out.println("Sorry no Implementation-Version manifest attribute available");
        }
    }
}

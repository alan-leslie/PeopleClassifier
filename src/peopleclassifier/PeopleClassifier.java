
package peopleclassifier;

import com.alag.ci.blog.classify.weka.impl.WEKABlogClassifier;
import com.alag.ci.blog.classify.weka.impl.WEKABlogClassifier.Algorithm;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author al
 */
public class PeopleClassifier {

    /**
     * @param args the command line arguments
     */
    public static void main(String [] args) {
        try {
            WEKABlogClassifier c = new WEKABlogClassifier();
            c.classify(Algorithm.DECISION_TREE);
            c.retest();
    //        c.classify(Algorithm.RBF);
        } catch (Exception ex) {
            Logger.getLogger(PeopleClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


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
            WEKABlogClassifier c = new WEKABlogClassifier("/home/al/wiki_scots/crawl-small/processed/");
            c.classify(Algorithm.DECISION_TREE); //NAIVE_BAYES); //
            c.classifyAll("/home/al/wiki_scots/crawl-1328562253594/processed/");
        } catch (Exception ex) {
            Logger.getLogger(PeopleClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

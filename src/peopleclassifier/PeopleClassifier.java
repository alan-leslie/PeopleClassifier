
package peopleclassifier;

import com.alag.ci.blog.classify.weka.impl.WEKABlogClassifier;
import com.alag.ci.blog.classify.weka.impl.WEKABlogClassifier.Algorithm;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
            c.classify(Algorithm.NAIVE_BAYES); //DECISION_TREE); //NAIVE_BAYES); //
            Set<String> peepsSet = new TreeSet<String>();
            List<String> thePeeps = c.classifyAll("/home/al/wiki_scots/crawl-1328562253594/processed/");
            thePeeps.addAll(c.classifyAll("/home/al/wiki_scots/crawl-1328697440847/processed/"));
            thePeeps.addAll(c.classifyAll("/home/al/wiki_scots/crawl-1328709945152/processed/"));
            thePeeps.addAll(c.classifyAll("/home/al/wiki_scots/crawl-1328711416349/processed/"));
            thePeeps.addAll(c.classifyAll("/home/al/wiki_scots/crawl-1328713337542/processed/"));
            peepsSet.addAll(thePeeps);
            PeopleFile.writeData("peeps_classify.txt", peepsSet);          
        } catch (Exception ex) {
            Logger.getLogger(PeopleClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

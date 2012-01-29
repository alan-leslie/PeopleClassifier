package peopleclassifier;

//import com.alag.ci.blog.search.RetrievedDataEntry;
import java.io.IOException;
import java.util.*;

//import com.alag.ci.cluster.*;
import com.alag.ci.tagcloud.TagCloud;
import com.alag.ci.textanalysis.*;
import com.alag.ci.textanalysis.lucene.impl.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author al
 */
public class PageTextDataSetCreatorImpl implements DataSetCreator {

    private TagCache tagCache = null;
    private final String dataDir;
    private List<RetrievedDataEntry> theData;

    public PageTextDataSetCreatorImpl(String dataDir,
            List<RetrievedDataEntry> theData) {
        this.tagCache = new TagCacheImpl();
        this.dataDir = dataDir;
        this.theData = theData;
    }

    @Override
    public List<TextDataItem> createLearningData()
            throws Exception {
        if(theData == null){
            theData = getData();
        }
        
        return getTagMagnitudeVectors(theData);
    }

    public Collection<Tag> getAllTags() {
        return this.tagCache.getAllTags();
    }

    private List<RetrievedDataEntry> getData() {
        List<RetrievedDataEntry> retVal = new ArrayList<RetrievedDataEntry>();
        File dir = new File(dataDir);

        // todo - need this to get summaries
        String[] children = dir.list();
        if (children == null) {
            // Either dir does not exist or is not a directory
        } else {
            for (int i = 0; i < children.length; i++) {
                // Get filename of file or directory
                String fileName = children[i];
                String subDirName = dataDir + fileName + "/";
                String txtSubDirName = subDirName + "summary/";
                File subDir = new File(txtSubDirName);

                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".txt");
                    }
                };
                
                String[] thePages = subDir.list(filter);
                if (thePages == null) {
                    // Either subdir does not exist or is not a directory
                } else {
                    for (int j = 0; j < thePages.length; j++) {
                        String thePage = thePages[j];
                        try {
                            String txtFileNameWOExt = thePage.substring(0, (thePage.length() - 4));
                            RetrievedDataEntry newPage = new CrawlerPage(subDirName, txtFileNameWOExt, true);
                            retVal.add(newPage);                            
                        } catch (IOException ex) {
                            Logger.getLogger(PageTextDataSetCreatorImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }

        return retVal;
    }

    private List<TextDataItem> getTagMagnitudeVectors(
            List<RetrievedDataEntry> theData) throws IOException {
        List<TextDataItem> result = new ArrayList<TextDataItem>();
        InverseDocFreqEstimator freqEstimator =
                new InverseDocFreqEstimatorImpl(theData.size());
        TextAnalyzer textAnalyzer = new LuceneTextAnalyzer(
                this.tagCache, freqEstimator);

        for (RetrievedDataEntry thePage : theData) {
            String text = thePage.getText();
            TagMagnitudeVector tmv = textAnalyzer.createTagMagnitudeVector(text);
            for (TagMagnitude tm : tmv.getTagMagnitudes()) {
                freqEstimator.addCount(tm.getTag());
            }
        }
        
        System.out.println("No of tags is:" + Integer.toString(freqEstimator.noOfTags()));
        System.out.println("Frequencies");
//        freqEstimator.outputFrequencies();
        System.out.println("");
        freqEstimator.prune(20, 70);
        
        for (RetrievedDataEntry thePage : theData) {
            String text = thePage.getText();
            TagMagnitudeVector tmv = textAnalyzer.createTagMagnitudeVector(text);
                        System.out.println(tmv);

            result.add(getPageTextAnalysisDataItem((RetrievedDataEntry)thePage, tmv));
        }
        
        return result;
    }

    private DataEntryDataItem getPageTextAnalysisDataItem(RetrievedDataEntry thePage,
            TagMagnitudeVector tmv) {
        return new DataEntryDataItem(thePage, tmv);
    }

//    public static void main(String[] args) throws Exception {
//        PageTextDataSetCreatorImpl pt = new PageTextDataSetCreatorImpl("/home/al/lasers/crawl_small/processed/", null);
//        List<TextDataItem> beList = pt.createLearningData();
//        
//        TagMagnitudeVector tmCombined = null;
//
//        for (TextDataItem tm : beList) {
//            if(tmCombined == null){
//                tmCombined = tm.getTagMagnitudeVector();
//            } else {
//                tmCombined = tmCombined.add(tm.getTagMagnitudeVector());
//            }
//            
//
//         }
//        
//            System.out.println(tmCombined);
//            System.out.println("Tag mag vector size:" + Integer.toString(tmCombined.getTagMagnitudes().size()));
//        
//        TagCloud tagCloud = LuceneTextAnalyzer.createTagCloud(tmCombined);
//        String html = LuceneTextAnalyzer.visualizeTagCloud(tagCloud);
////        for (TextDataItem tm : beList) {
////            System.out.println(tm.getTagMagnitudeVector());
////        }
//    }
}

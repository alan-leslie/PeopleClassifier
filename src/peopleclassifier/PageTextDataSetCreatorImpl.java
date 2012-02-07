package peopleclassifier;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alag.ci.textanalysis.TagCache;
import com.alag.ci.textanalysis.InverseDocFreqEstimator;
import com.alag.ci.textanalysis.Tag;
import com.alag.ci.textanalysis.TagMagnitude;
import com.alag.ci.textanalysis.TagMagnitudeVector;
import com.alag.ci.textanalysis.TextAnalyzer;
import com.alag.ci.textanalysis.lucene.impl.TagCacheImpl;
import com.alag.ci.textanalysis.lucene.impl.InverseDocFreqEstimatorImpl;

/**
 *
 * @author al
 */
public class PageTextDataSetCreatorImpl implements DataSetCreator {

    private TagCache tagCache = null;
    private final String dataDir;
    private List<RetrievedDataEntry> theData;
    private InverseDocFreqEstimator freqEstimator = null;

    public PageTextDataSetCreatorImpl(String dataDir,
            List<RetrievedDataEntry> theData) {
        this.tagCache = new TagCacheImpl();
        this.dataDir = dataDir;
        this.theData = theData;
    }

    @Override
    public List<TextDataItem> createLearningData()
            throws Exception {
        if (theData == null) {
            theData = getData();
        }

        return getTagMagnitudeVectors(theData);
    }

    public Collection<Tag> getAllTags() {
        return this.tagCache.getAllTags();
    }

    public Collection<Tag> getRelevantTags() {
        Collection<Tag> retVal = new ArrayList<Tag>();

        Collection<Tag> theTags = tagCache.getAllTags();
        for (Tag theTag : theTags) {
            double thePctFreq = freqEstimator.estimateDocFreqPercent(theTag);
            System.out.println("Tag: " + theTag + " = " + Double.toString(thePctFreq));

            if (thePctFreq > 10.0
                    && thePctFreq < 60.0) {
                retVal.add(theTag);
            }
        }

        int noOfRelevant = retVal.size();
        return retVal;
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
        freqEstimator =
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
        System.out.println("No of tags is:" + Integer.toString(getRelevantTags().size()));
//        System.out.println("Frequencies");
//        freqEstimator.outputFrequencies();
        System.out.println("");

        for (RetrievedDataEntry thePage : theData) {
            String text = thePage.getText();
            TagMagnitudeVector tmv = textAnalyzer.createTagMagnitudeVector(text);
            System.out.println(tmv);

            result.add(getPageTextAnalysisDataItem((RetrievedDataEntry) thePage, tmv));
        }

        return result;
    }

    private DataEntryDataItem getPageTextAnalysisDataItem(RetrievedDataEntry thePage,
            TagMagnitudeVector tmv) {
        return new DataEntryDataItem(thePage, tmv);
    }
    // todo rcreate test include
    // unicode to ascii conversion e.g. %29 = )? in http://en.wikipedia.org/wiki/Colin_Campbell_%28Swedish_East_India_Company%29
    // &#160; should convert to space
    // get a tag that is 'is a scottish' or 'was a scottish'
    // maybe get a tag that covers a period e.g. (1 November 1686 – 9 May 1757) 
//    public static void main(String[] args) throws Exception {
//        PageTextDataSetCreatorImpl pt = new PageTextDataSetCreatorImpl("/home/al/wiki_scots/crawl-small/processed/", null);
//        List<TextDataItem> beList = pt.createLearningData();
////        
////        TagMagnitudeVector tmCombined = null;
////
////        for (TextDataItem tm : beList) {
////            if(tmCombined == null){
////                tmCombined = tm.getTagMagnitudeVector();
////            } else {
////                tmCombined = tmCombined.add(tm.getTagMagnitudeVector());
////            }
////            
////
////         }
////        
////            System.out.println(tmCombined);
////            System.out.println("Tag mag vector size:" + Integer.toString(tmCombined.getTagMagnitudes().size()));
////        
////        TagCloud tagCloud = LuceneTextAnalyzer.createTagCloud(tmCombined);
////        String html = LuceneTextAnalyzer.visualizeTagCloud(tagCloud);
//////        for (TextDataItem tm : beList) {
//////            System.out.println(tm.getTagMagnitudeVector());
//////        }
//    }
}

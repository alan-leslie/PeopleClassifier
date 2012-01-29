package peopleclassifier;

import java.io.IOException;
import java.util.*;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.FastVector;
import weka.core.Attribute;

//import com.alag.ci.blog.search.BlogQueryResult;
//import com.alag.ci.cluster.TextDataItem;
import com.alag.ci.textanalysis.*;
import com.alag.ci.textanalysis.Tag;
import com.alag.ci.textanalysis.lucene.impl.TagImpl;
import java.net.URL;
import weka.core.SparseInstance;

public class WEKAPredictiveBlogDataSetCreatorImpl extends PageTextDataSetCreatorImpl {

    private List<TextDataItem> dataEntries = null;
    FastVector allAttributes = null;
    int numAttributes = 0;
    Collection<Tag> allTags = null;

    public WEKAPredictiveBlogDataSetCreatorImpl(String dataDir,
            List<RetrievedDataEntry> theData) throws Exception {
        super(dataDir, theData);
        allAttributes = null;
        numAttributes = 0;
        allTags = null;

        if (theData == null) {
            this.dataEntries = super.createLearningData();
            for (TextDataItem dataItem : this.dataEntries) {
                URL url = new URL(dataItem.getData().getUrl());
                String fullPath = url.getPath();
                String[] pathComponents = fullPath.split("/");
                String theFileName = "";

                if(pathComponents.length > 0){
                    theFileName = pathComponents[pathComponents.length - 1];
                }

                if(!(url.getRef() != null ||
                        theFileName.indexOf(".php") >= 0 ||
                        theFileName.indexOf("Category:")== 0 ||
                        theFileName.indexOf("Template_talk:")== 0 ||
                        theFileName.indexOf("Template:")== 0 ||
                        theFileName.indexOf("File:")== 0 ||
                        theFileName.indexOf("Wikipedia:")== 0 ||
                        theFileName.indexOf("Special:")== 0 ||
                        theFileName.indexOf("Portal:")== 0
                        )){
                    TagMagnitudeVector tagMagnitudeVector = dataItem.getTagMagnitudeVector();
                    List<TagMagnitude> tagMagnitudes = tagMagnitudeVector.getTagMagnitudes();

                    if(dataItem.getData().getUrl().equalsIgnoreCase("http://en.wikipedia.org/wiki/Charles_Wyville_Thomson") ||
                        dataItem.getData().getUrl().equalsIgnoreCase("http://en.wikipedia.org/wiki/Colin_Campbell_(Swedish_East_India_Company)") ||
                        dataItem.getData().getUrl().equalsIgnoreCase("http://en.wikipedia.org/wiki/John_Watson_Gordon") ||
                        dataItem.getData().getUrl().equalsIgnoreCase("http://en.wikipedia.org/wiki/Andrew_Gilchrist") ||
                        dataItem.getData().getUrl().equalsIgnoreCase("http://en.wikipedia.org/wiki/Dugald_Clark") ||
                        dataItem.getData().getUrl().equalsIgnoreCase("http://en.wikipedia.org/wiki/Henry_Ogg_Forbes")){
                        dataItem.setIsPerson(true);
                    } else {
                        Tag theDobTag = new TagImpl("date of birth", "date of birth");
                        Tag theBornTag = new TagImpl("born", "born");
                        Tag theBioTag = new TagImpl("biographical", "biograph");

                        if(tagMagnitudeVector.getTagMagnitudeMap().containsKey(theBornTag)||
                                tagMagnitudeVector.getTagMagnitudeMap().containsKey(theBioTag) ||
                                tagMagnitudeVector.getTagMagnitudeMap().containsKey(theDobTag)){
                            dataItem.setIsPerson(true);
                        }
                    }
                }
            }
        }

        for (TextDataItem dataItem : this.dataEntries) {
            boolean isPerson = dataItem.isPerson();
            if (isPerson) {
                System.out.println(dataItem.getData().getUrl() + " is a Person");
            }
        }

        for (TextDataItem dataItem : this.dataEntries) {
            boolean isPerson = dataItem.isPerson();
            if (!isPerson) {
                System.out.println(dataItem.getData().getUrl() + " is NOT a Person");
            }
        }
    }

//    private List<TextDataItem> createLearningDataSet(boolean isContinuous) throws Exception {
//        String [] positiveTags = { "collective intelligence", "data mining", "web 2.0"};
//        String [] negativeTags = { "child intelligence", "AJAX"};
//        return createLearningDataSet(positiveTags, negativeTags, isContinuous);
//    }
//    private List<TextDataItem> createLearningDataSet(String [] positiveTags, String [] negativeTags,
//            boolean isContinuous) throws Exception {
//        List<TextDataItem> data = new ArrayList<TextDataItem>();
//        // todo surely this is just taken from subclass
////        for (String tag: positiveTags) {
////            data.addAll(getBlogData(tag, true));
////        }
////        for (String tag: negativeTags) {
////            data.addAll(getBlogData(tag, false));
////        }
//        return data;
//    }
//    private List<TextDataItem> getBlogData(String tag, boolean isRelevant)
//        throws Exception {
//        BlogQueryResult bqrCI = getBlogsFromTechnorati(tag);
//        return  getBlogTagMagnitudeVectors(bqrCI,isRelevant);
//    }
//    protected List<TextDataItem> getDataTagMagnitudeVectors() throws IOException {
//        List<TextDataItem> tdiList = super.getBlogTagMagnitudeVectors(blogQueryResult);
//        for (TextDataItem dataItem: tdiList) {
//            dataItem.setCiRelated(isRelevant);
//        }
//        return tdiList;
//    }
    public Instances createLearningDataSet(String datasetName, boolean isContinuous) throws Exception {
        allTags = this.getAllTags();
        allAttributes = createAttributes(isContinuous);
        Instances trainingDataSet = new Instances(datasetName,
                allAttributes, dataEntries.size());
        numAttributes = allAttributes.size();
        System.out.println("Number attributes =" + numAttributes);

        for (TextDataItem dataItem : dataEntries) {
            Instance instance = createNewInstance(trainingDataSet,
                    dataItem, isContinuous);
            trainingDataSet.add(instance);
        }
        
        System.out.println(trainingDataSet);
        return trainingDataSet;
    }

    // precon allTags is set up
    // postcon return value has same num as allTags plus one
    protected FastVector createAttributes(boolean isContinuous) {
//        Collection<Tag> allTags = this.getAllTags();
        FastVector allAttributes = new FastVector(allTags.size());
        for (Tag tag : allTags) {
            Attribute tagAttribute = createAttribute(tag.getDisplayText(), isContinuous);
            allAttributes.addElement(tagAttribute);
        }
        Attribute classificationAttribute = createAttribute("ClassificationAttribute", isContinuous);
        allAttributes.addElement(classificationAttribute);
        return allAttributes;
    }

    private Attribute createAttribute(String attributeName, boolean isContinuous) {
        if (isContinuous) {
            return createContinuousAttribute(attributeName);
        }
        return createBinaryNominalAttribute(attributeName);
    }

    private Attribute createBinaryNominalAttribute(String attributeName) {
        FastVector attNominalValues = new FastVector(2);
        attNominalValues.addElement("true");
        attNominalValues.addElement("false");
        return new Attribute(attributeName, attNominalValues);
    }

    private Attribute createContinuousAttribute(String attributeName) {
        return new Attribute(attributeName);
    }
    
    protected Instance reCreateInstance(Instances trainingDataSet, Collection<Tag> allTags,
            TextDataItem dataItem, boolean isContinuous) {
        Instance instance = new SparseInstance(numAttributes);
        instance.setDataset(trainingDataSet);
        int index = 0;
               
        TagMagnitudeVector tmv = dataItem.getTagMagnitudeVector();       
        Map<Tag, TagMagnitude> tmvMap = tmv.getTagMagnitudeMap();
        
        for (Tag tag : allTags) {
            TagMagnitude tm = tmvMap.get(tag);
            if (tm != null) {
                setInstanceValue(instance, index++, tm.getMagnitude(), isContinuous);
            } else {
                setInstanceValue(instance, index++, 0., isContinuous);
            }
        }
//        BlogAnalysisDataItem blog = (BlogAnalysisDataItem) dataItem;
        if (dataItem.isPerson()) {
            setInstanceValue(instance,index, 1., isContinuous);
        } else {
            setInstanceValue(instance,index, 0., isContinuous);
        }
        return instance;
    }

    // precon allTags is set up
    protected Instance createNewInstance(Instances trainingDataSet, 
            TextDataItem dataItem, boolean isContinuous) {
        Instance instance = new SparseInstance(numAttributes);
        instance.setDataset(trainingDataSet);
        int index = 0;
               
        TagMagnitudeVector tmv = dataItem.getTagMagnitudeVector();       
        Map<Tag, TagMagnitude> tmvMap = tmv.getTagMagnitudeMap();
        
        for (Tag tag : allTags) {
            TagMagnitude tm = tmvMap.get(tag);
            if (tm != null) {
                setInstanceValue(instance, index++, tm.getMagnitude(), isContinuous);
            } else {
                setInstanceValue(instance, index++, 0., isContinuous);
            }
        }
//        BlogAnalysisDataItem blog = (BlogAnalysisDataItem) dataItem;
        if (dataItem.isPerson()) {
            setInstanceValue(instance,index, 1., isContinuous);
        } else {
            setInstanceValue(instance,index, 0., isContinuous);
        }
        return instance;
    }

    private void setInstanceValue(Instance instance, int index, double magnitude, boolean isContinuous) {
        if (isContinuous) {
            instance.setValue(index, magnitude);
        } else {
            if (magnitude > 0.) {
                instance.setValue(index, "true");
            } else {
                instance.setValue(index, "false");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        WEKAPredictiveBlogDataSetCreatorImpl dataSetCreator =
                new WEKAPredictiveBlogDataSetCreatorImpl("/home/al/wiki_scots/crawl-small/processed/", null);

//        WEKAPredictiveBlogDataSetCreatorImpl dataCreator = new WEKAPredictiveBlogDataSetCreatorImpl();
        Instances discreteDataSet = dataSetCreator.createLearningDataSet("nominalBlogData", false);
        System.out.println(discreteDataSet.toSummaryString());
//        Instances continuousDataSet = dataCreator.createLearningDataSet("continuousBlogData",true);
//        System.out.println(continuousDataSet);
    }
}

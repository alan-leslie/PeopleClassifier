/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peopleclassifier;

//import com.alag.ci.blog.search.RetrievedDataEntry;
//import com.alag.ci.cluster.TextDataItem;
import com.alag.ci.textanalysis.TagMagnitude;
import com.alag.ci.textanalysis.TagMagnitudeVector;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author al
 */
public class DataEntryDataItem implements TextDataItem {
    private RetrievedDataEntry thePage = null;
    private TagMagnitudeVector tagMagnitudeVector = null;
    private Integer clusterId;
    private boolean isPersonRelated = false;
    
    DataEntryDataItem(RetrievedDataEntry newPage,
            TagMagnitudeVector tagMagnitudeVector) {
        this.thePage = newPage;
        this.tagMagnitudeVector = tagMagnitudeVector;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataEntryDataItem other = (DataEntryDataItem) obj;
        if (this.thePage != other.thePage && (this.thePage == null || !this.thePage.equals(other.thePage))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.clusterId != null ? this.clusterId.hashCode() : 0);
        return hash;
    }
  
    public boolean isPerson() {
        return isPersonRelated;
    }

    @Override
    public void setIsPerson(boolean isPerson) {
        this.isPersonRelated = isPerson;
    }

    @Override
    public RetrievedDataEntry getData() {
        return this.thePage;
    }
    
    RetrievedDataEntry getPage() {
        return thePage;
    }

    @Override
    public TagMagnitudeVector getTagMagnitudeVector() {
        return tagMagnitudeVector;
    }
    
    public double distance(TagMagnitudeVector other) {
        return this.getTagMagnitudeVector().dotProduct(other);
    }

    @Override
    public Integer getClusterId() {
        return clusterId;
    }

    @Override
    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public Map<String, String> getAttributeMap() {
        Map<String, String> theAttributes = new HashMap<String, String>();
        theAttributes.put("Title", thePage.getTitle());
        theAttributes.put("URL", thePage.getUrl());
        theAttributes.put("Text", thePage.getText());
        return theAttributes;
    }

    @Override
    public String[] getTags(int noOfTags) {
        List<TagMagnitude> tagMagnitudes = tagMagnitudeVector.getTagMagnitudes();
        Collections.sort(tagMagnitudes);
      
        int tagArraySize = noOfTags;
        if(noOfTags < 1){
            tagArraySize = tagMagnitudes.size();
        }

        String retVal[] = new String[tagArraySize];
        StringBuilder opBuilder = new StringBuilder();
        
        for(int i = 0; i < tagArraySize && i < tagMagnitudes.size(); ++i){
            String theStemmedText = tagMagnitudes.get(i).getTag().getStemmedText();
            retVal[i] = theStemmedText;
            opBuilder.append(theStemmedText);
            opBuilder.append(",");         
        }
        
        System.out.println(opBuilder.toString());
        
        // anything left over is set to null
        
        return retVal;
    }
}


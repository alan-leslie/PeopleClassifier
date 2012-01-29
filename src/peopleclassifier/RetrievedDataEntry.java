package peopleclassifier;

import java.util.Date;

public interface RetrievedDataEntry {
    public String getName();
    public String getUrl();
    public String getTitle();
    public String getExcerpt();    
    public String getText();
    public String getAuthor();
    public Date getLastUpdateTime();
    public Date getCreationTime();
}

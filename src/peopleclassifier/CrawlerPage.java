/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peopleclassifier;

import com.alag.ci.TextFile;
//import com.alag.ci.blog.search.RetrievedDataEntry;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author al
 */
public class CrawlerPage implements RetrievedDataEntry {
    private String dirName;
    private String fileName;
    private String theText = "";
    private String theURL = "";
    private String theTitle = "";
    private static final String propDir = "properties/";
    private static final String txtDir = "txt/";
    private static final String summaryDir = "summary/";
    private static final String propExt = ".properties";
    private static final String txtExt = ".txt";
    
    public CrawlerPage(String dirName, 
            String fileName,
            boolean isSummary) throws IOException {
        this.dirName = dirName;
        this.fileName = fileName;
        
        StringBuilder theTextBuilder = new StringBuilder();
        String subDirName = txtDir;
        if(isSummary){
            subDirName = summaryDir;
        }
        String fullTxtFileName = this.dirName + subDirName + this.fileName + txtExt;
        theText = TextFile.getFileData(fullTxtFileName);            

        // todo this should use a property file read
        try{
            String fullPropFileName = this.dirName + propDir + this.fileName + propExt;
            BufferedReader in = new BufferedReader(new FileReader(fullPropFileName));
            String str;
            while ((str = in.readLine()) != null) {
                if(str.indexOf("title:") == 0){
                    theTitle = str.substring(6);
                }
                               
                if(str.indexOf("url:") == 0){
                    theURL = str.substring(4);
                }
            }
            in.close();          
        } catch(IOException exc) {
            throw exc;           
        } finally {
            // clean up and close files
        }        
    }
    
    @Override
    public String getText(){
        return theText;
    }
    
    @Override
    public String getTitle(){
        return theTitle;
    }
    
    @Override
    public String getUrl(){
        return theURL;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CrawlerPage other = (CrawlerPage) obj;
        if ((this.theURL == null) ? (other.theURL != null) : !this.theURL.equals(other.theURL)) {
            return false;
        }
 
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.theURL != null ? this.theURL.hashCode() : 0);
        return hash;
    } 
    
    public static void main(String [] args) throws Exception {
        CrawlerPage thePage = new CrawlerPage("/home/al/lasers/crawl-1317050427563/processed/3/", "1", true);

        System.out.println(thePage.getTitle());      
        System.out.println(thePage.getUrl());  
    }

    @Override
    public String getName() {
        return getTitle();
    }

    @Override
    public String getExcerpt() {
        return getText();
    }

    @Override
    public String getAuthor() {
        return "";
    }

    @Override
    public Date getLastUpdateTime() {
        Date retVal = new Date();
        return retVal;
    }

    @Override
    public Date getCreationTime() {
        Date retVal = new Date();
        return retVal;
    }
}



import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

public class ObjectPersistenceCRUDExample {
    
    static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
        
    public static void main(String[] args) throws IOException {
        testCRUDOperations();  
        System.out.println("Example complete!");
    }

    // @DynamoDBTable(tableName="ProductCatalog")
    @DynamoDBTable(tableName="TwitterLog")
    public static class CatalogItem {
        // private Integer id;
        private String id;
        private String lat;
        private String lon;
        private String text;
        private String time;
//        private Set<String> bookAuthors;
        
        CatalogItem(String id, String lat, String lon, String text, String time){
        	this.id = id;
        	this.lat = lat;
        	this.lon = lon;
        	this.time = time;
        	this.text = text;
        }
      
        @DynamoDBHashKey(attributeName="id")
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        @DynamoDBAttribute(attributeName="text")
        public String gettext() { return text; }    
        public void settext(String text) { this.text = text; }
        
        @DynamoDBAttribute(attributeName="lat")
        public String getlat() { return lat; }    
        public void setlat(String lat) { this.lat = lat;}

        @DynamoDBAttribute(attributeName="lon")
        public String getlon() { return lon; }    
        public void setlon(String lon) { this.lon = lon;}

        @DynamoDBAttribute(attributeName="time")
        public String gettime() { return time; }    
        public void settime(String time) { this.time = time;}
        
        // @DynamoDBAttribute(attributeName = "Authors")
        // public Set<String> getBookAuthors() { return bookAuthors; }    
        // public void setBookAuthors(Set<String> bookAuthors) { this.bookAuthors = bookAuthors; }
        // @Override
        public String toString() {
            return "Tweet [time=" + time + ", lat=" + lat + ", lon=" + lon + ", id=" + id + ", text=" + text + "]";            
        }
    }
        
    private static void testCRUDOperations() {

//        CatalogItem item = new CatalogItem();
//        item.setId(601);
//        item.settext("ssd");
//        item.setlat("611-11111111999");
//        item.setBookAuthors(new HashSet<String>(Arrays.asList("Author1", "Author2")));
    	
    	CatalogItem item = new CatalogItem("000", "110.23", "324.32", "this is a sample tweet","12:20");
        
        // Save the item (book).
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        mapper.save(item);
        
        // Retrieve the item.
        CatalogItem itemRetrieved = mapper.load(CatalogItem.class, "000"); //Fetch id
        System.out.println("Item retrieved:");
        System.out.println(itemRetrieved);

        // Update the item.
        itemRetrieved.setlat("222");
//        itemRetrieved.setBookAuthors(new HashSet<String>(Arrays.asList("Author1", "Author3")));
        mapper.save(itemRetrieved);
        System.out.println("Item updated:");
        System.out.println(itemRetrieved);
        
        // Retrieve the updated item.
        DynamoDBMapperConfig config = new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT);
        CatalogItem updatedItem = mapper.load(CatalogItem.class, 601, config);
        System.out.println("Retrieved the previously updated item:");
        System.out.println(updatedItem);
        
        // Delete the item.
//        mapper.delete(updatedItem);
        
        // Try to retrieve deleted item.
        CatalogItem deletedItem = mapper.load(CatalogItem.class, updatedItem.getId(), config);
        if (deletedItem == null) {
            System.out.println("Done - Sample item is deleted.");
        }
    }
}
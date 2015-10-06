import java.text.SimpleDateFormat;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


public class CollectTweets {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
   	 ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey("Qrtfa7tRVdMnJVboGR9mtD84s")
          .setOAuthConsumerSecret("5aOmn7azudhFhvvp75NjpjN4JLyZjZwYuELWX6BnjCcSHIlGuV")
          .setOAuthAccessToken("1670776861-POLp0kLFwsKfkW57CkYqrY3v6g3WIjMqAWTXhYH")
          .setOAuthAccessTokenSecret("ZWHy6zIEpOaiwjqEmwqRm0mzkzt0HUmpddbH4PawC6Oy4");

        /*
         * tableName: the table to be created for recording tweets.
         * */
        String tableName = "Tweets";
     
        /*Create table
         * */
        /*
        try {
			DynamoFunction.CreateTable(tableName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}*/
 
        
       DynamoDB dynamoDB = new DynamoDB ((AmazonDynamoDB) new 
       		AmazonDynamoDBClient(new ProfileCredentialsProvider()));
       final String twitterTableName = tableName;
       final Table table = dynamoDB.getTable(twitterTableName);
       
       TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
       StatusListener listener = new StatusListener() {
           @Override
           public void onStatus(Status status) {
           	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
               if(status.getGeoLocation() != null){
               try {
                   System.out.println("Adding data to " + twitterTableName);
                   String dataString = dateFormatter.format(status.getCreatedAt());
                   System.out.println(dataString);
                   Item item = new Item()
                       .withPrimaryKey("fid", "1")
                       .withString("twid", String.valueOf(status.getId()))
                       .withString("text", status.getText())
                       .withString("lat", String.valueOf(status.getGeoLocation().getLatitude()))
                       .withString("lon", String.valueOf(status.getGeoLocation().getLongitude()))
                       .withString("time", dataString);
                       // can add more Attributes from here
                   	table.putItem(item);

               } catch (Exception e) {
                   System.err.println("Failed to create item in " + twitterTableName);
                   System.err.println(e.getMessage());
               }
               System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText() + ' ' + status.getGeoLocation().getLongitude() + ' ' + status.getGeoLocation().getLatitude());
           }
           }
           
			@Override
           public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
               //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
           }

           @Override
           public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
               //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
           }

           @Override
           public void onScrubGeo(long userId, long upToStatusId) {
               //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
           }

           @Override
           public void onStallWarning(StallWarning warning) {
               //System.out.println("Got stall warning:" + warning);
           }

           @Override
           public void onException(Exception ex) {
               ex.printStackTrace();
           }
       };
       
       twitterStream.addListener(listener);
       FilterQuery filtre = new FilterQuery();
       String[] keywords = {"Apple", "Facebook", "Amazon", "Samsung"};
       filtre.track(keywords);
       twitterStream.filter(filtre);

       //twitterStream.sample();
	}

}

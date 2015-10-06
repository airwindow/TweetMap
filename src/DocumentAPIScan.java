
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;

public class DocumentAPIScan {

    static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(new ProfileCredentialsProvider()));
    static String tableName = "Tweets";

    public static void main(String[] args) throws Exception {

        findTweetsWithATopic();
    }


    private static void findTweetsWithATopic() {
        
        Table table = dynamoDB.getTable(tableName);
        String topic = "hello";
        
        Condition scanFilterCondition = new Condition()
            .withComparisonOperator(ComparisonOperator.CONTAINS)
            .withAttributeValueList(new AttributeValue().withS(topic));

        Map<String, Condition> expressionAttributeValues = new HashMap<String, Condition>();
        expressionAttributeValues.put("lat", scanFilterCondition);

        ItemCollection<ScanOutcome> items = table.scan(
            withScanFilter(expressionAttributeValues), //FilterExpression
//            "id, text, lat, lon", //ProjectionExpression
            null, null); //ExpressionAttributeNames - not used in this example 
            // expressionAttributeValues);
        
        // System.out.println("Scan of " + tableName + " for items with a price less than 100.");
        System.out.println("Scan of " + tableName + " for tweets with a topic " + topic + ".");
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toJSONPretty());
        }    
    }


	private static String withScanFilter(
			Map<String, Condition> expressionAttributeValues) {
		// TODO Auto-generated method stub
		return null;
	}
    
}
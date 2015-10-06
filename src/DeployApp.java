import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentTier;
import com.amazonaws.services.elasticbeanstalk.model.S3Location;
import com.amazonaws.services.elasticbeanstalk.model.UpdateEnvironmentRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class DeployApp {
	
	static AmazonEC2 ec2;
	static AmazonS3 s3;
	
	public static void main(String[] args) { 
		deployApp("/Users/yangjingwei/Desktop/Final.war");
	}
	
	public static void deployApp(String filePath) {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Please make sure your criential file is right!",
					e);
		}
		ec2 = new AmazonEC2Client(credentials);
		s3 = new AmazonS3Client(credentials);
		ec2.setEndpoint("ec2.us-east-1.amazonaws.com");
		AWSElasticBeanstalk beanstalk = new AWSElasticBeanstalkClient(credentials);
		beanstalk.setEndpoint("elasticbeanstalk.us-east-1.amazonaws.com");
		

		String applicationName = "Final";
		String applicationDescription = "Deploy TweetMap on Amazon AWS";
		
		
		/*
		 * select the ElasticBeanStalk
		 * */
		s3.putObject("elasticbeanstalk-us-east-1-263661336656", applicationName + ".war", new File(filePath));
		
		
		/*
		 * The basic info for the application
		 * */
		CreateApplicationRequest createApplicationRequest = new CreateApplicationRequest();
		createApplicationRequest.withApplicationName(applicationName);
		createApplicationRequest.withDescription(applicationDescription);
		beanstalk.createApplication(createApplicationRequest);
		
		/*
		 * Configure the App's version info. 
		 * */
		S3Location sourceBundle = new S3Location("elasticbeanstalk-us-east-1-263661336656", applicationName + ".war");
		CreateApplicationVersionRequest createApplicationVersionRequest = new CreateApplicationVersionRequest();
		createApplicationVersionRequest.withApplicationName(applicationName)
									   .withVersionLabel(applicationName)
									   .withAutoCreateApplication(true)
									   .withSourceBundle(sourceBundle);
		beanstalk.createApplicationVersion(createApplicationVersionRequest);
		
		/*
		 * Configure the ELB
		 * */
		Collection<ConfigurationOptionSetting> settings = new ArrayList<ConfigurationOptionSetting>();
		settings.add(new ConfigurationOptionSetting("aws:autoscaling:asg", "MaxSize", "1"));
		settings.add(new ConfigurationOptionSetting("aws:elb:loadbalancer", "CrossZone", "true"));
		settings.add(new ConfigurationOptionSetting("aws:autoscaling:launchconfiguration", "InstanceType", "t2.micro"));
		settings.add(new ConfigurationOptionSetting("aws:elb:policies", "ConnectionDrainingEnabled", "true"));
		settings.add(new ConfigurationOptionSetting("aws:elb:policies", "ConnectionDrainingTimeout", "20"));

		/*
		 * Configure the environment for the ELB
		 * */ 
		CreateEnvironmentRequest createEnvironmentRequest = new CreateEnvironmentRequest();
		createEnvironmentRequest.withTier(new EnvironmentTier().withName("WebServer").withType("Standard"))
								.withApplicationName(applicationName)
								.withEnvironmentName(applicationName.toLowerCase())
								.withOptionSettings(settings)
								.withCNAMEPrefix(applicationName.toLowerCase() + "-test")
								.withVersionLabel(applicationName)
								.withSolutionStackName("64bit Amazon Linux 2014.09 v1.2.0 running Tomcat 8 Java 8");
		beanstalk.createEnvironment(createEnvironmentRequest);
		
		/*
		 * Deploy the application onto the AWS
		 * */
		UpdateEnvironmentRequest updateEnvironmentRequest = new UpdateEnvironmentRequest();
		updateEnvironmentRequest.withEnvironmentName(applicationName.toLowerCase()).withVersionLabel(applicationName);
		beanstalk.updateEnvironment(updateEnvironmentRequest);
		
		System.out.println("The application is successfully deployed!");
		
	}
}

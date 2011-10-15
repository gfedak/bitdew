package xtremweb.serv.dt.amazonS3;
/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 * 
 * Copyright 2008 James Murty
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */


import java.io.IOException;
import java.util.Properties;
import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.conf.ConfigurationProperties;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

/**
 * Utilities used by all Sample code, collected in one place for convenience.
 * 
 * @author James Murty
 */
public class AmazonS3Utils {
    
    /**
     * Amazon S3 user key property
     */
    public static final String AWS_ACCESS_KEY_PROPERTY_NAME = "xtremweb.serv.dr.s3.key";
    
    /**
     * Amazon S3 user secret property
     */
    public static final String AWS_SECRET_KEY_PROPERTY_NAME = "xtremweb.serv.dr.s3.secret";
    
    /**
     * Amazon S3 bucketName property
     */
    public static final String BUCKET_PROPERTY_NAME = "xtremweb.serv.dr.s3.bucketName";
    
    /**
     * Amazon S3 objectKey property
     */
    public static final String OBJECT_KEY_PROPERTY_NAME="xtremweb.serv.dr.s3.objectKey";
    
    /**
     * Properties 
     */
    private Properties testProperties;
    
    /**
     * Class constructor
     */
    public AmazonS3Utils () { 
	try {
	    testProperties = ConfigurationProperties.getProperties();
	} catch (ConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} 
    }
    
    /**
     * Loads AWS Credentials from the file properties.json that must be available in the  
     * classpath, and must contain properties "xtremweb.serv.dr.s3.secret" "xtremweb.serv.dr.s3.key"
     * "xtremweb.serv.dr.s3.bucketName" "xtremweb.serv.dr.s3.objectKey"
     * @return
     * the AWS credentials loaded from the samples properties file.
     */
    public AWSCredentials loadAWSCredentials() throws IOException {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
            testProperties.getProperty(AWS_ACCESS_KEY_PROPERTY_NAME),
            testProperties.getProperty(AWS_SECRET_KEY_PROPERTY_NAME));  
        return awsCredentials;        
    }
    
    /**
     * get Bucket name
     * @return the bucket name
     * @throws IOException
     */
    public String loadS3Bucket()  throws IOException {
       String testbucket = testProperties.getProperty(BUCKET_PROPERTY_NAME);
       return testbucket;        
    }
    
    /**
     * Get object key
     * @return the object key
     */
    public String loadObjectKey() {
	String objectkey = testProperties.getProperty(OBJECT_KEY_PROPERTY_NAME);
	return objectkey;
    }

}

package com.maurice.DocumentManagement.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.maurice.DocumentManagement.utils.Utilities.getFirstLevelObjectKeys;

@Service
public class S3Operations {

    @Value("${project.bucket}")
    private String bucketName;

    private S3Client s3;

    public S3Operations(){
        Region region = Region.US_EAST_1;
        s3 = S3Client.builder()
                .region(region)
                .build();
    }

    /**
     @param key It represents the key S3 object name
     @param pathname It represents the local file system's path of the file to be uploaded
     */
    public void putObject(String key, String pathname){
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        File file = new File(pathname);
        s3.putObject(request, Path.of(file.toURI()));
        System.out.println("File successfully uploaded");
    }

    /**
    * @param key It's the S3 object key to be retrieved
     * @param pathname It represents the local directory to temporally store the requested file
     */
    public void getObject(String key, String pathname) throws IOException {        // In addition, we can implicitly create directories by defining a file containing a path with folders that do not yet exist
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> responseResponseBytes = s3.getObjectAsBytes(objectRequest);
        byte[] data = responseResponseBytes.asByteArray();
        System.out.println("Size: "+((data.length / (float)1024))+" kilobytes");
        // Write the data to a local file.
        File myFile = new File(pathname);
        OutputStream os = new FileOutputStream(myFile);
        os.write(data);
        System.out.println("Successfully obtained bytes from an S3 object");
        os.close();
    }

    public void deleteObject(String key){
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3.deleteObject(deleteObjectRequest);
        System.out.println("File successfully removed");
    }

    public List<String> getObjectKeys(String prefix){
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix) // this pattern is used to filter objects that are located in the said folder
                .build();
        ListObjectsV2Response listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
        List<S3Object> contents = listObjectsV2Response.contents();

        return contents.stream().map(S3Object::key).collect(Collectors.toList());
    }
    public Set<String> getFirstLevelS3ObjectKeys(String prefix){
        Set<String> result = new HashSet<>();
        List<String> list = this.getObjectKeys(prefix);
        getFirstLevelObjectKeys(prefix, list, result);
        System.out.println("Number of objects in the bucket: " + (long) result.size());
        return result;
    }



    /**
    @param objectKey It's the object to be updated
     @param newName It's just the new name of the file to be updated
     */
    public void updateObject(String objectKey, String newName){

        String[] slices = objectKey.split("/");
        String lastSlice = slices[slices.length - 1];

        String fileType = "." + lastSlice.split("\\.")[1];

        String folderKey = objectKey.substring(0, objectKey.length() - lastSlice.length());
        System.out.println(folderKey);
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(objectKey)
                .destinationBucket(bucketName)
                .destinationKey(folderKey + newName + fileType)
                .build();

        s3.copyObject(copyObjectRequest);

        this.deleteObject(objectKey);
    }

    public void getBuckets(){
        ListBucketsResponse list = s3.listBuckets();
        List<Bucket> buckets = list.buckets();
        buckets.forEach(Bucket::name);
    }

}

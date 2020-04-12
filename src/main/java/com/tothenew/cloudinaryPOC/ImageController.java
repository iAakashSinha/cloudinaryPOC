package com.tothenew.cloudinaryPOC;

import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageController {

  @Autowired
  @Qualifier("com.cloudinary.cloud_name")
  String mCloudName;

  @Autowired
  @Qualifier("com.cloudinary.api_key")
  String mApiKey;

  @Autowired
  @Qualifier("com.cloudinary.api_secret")
  String mApiSecret;


  @PostMapping(value="/image")
  public ResponseEntity< Map > post(
      @RequestParam(value="upload", required=true) MultipartFile aFile) throws IOException {

    Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", mCloudName,
        "api_key", mApiKey,
        "api_secret", mApiSecret));

    File f= Files.createTempFile("temp", aFile.getOriginalFilename()).toFile();
    aFile.transferTo(f);
    Map response=cloudinary.uploader().upload(f, ObjectUtils.emptyMap());

//    cloudinary.uploader().upload(aFile,
//        ObjectUtils.asMap("public_id", "sample_woman"));

    return new ResponseEntity<>(response,HttpStatus.OK);
  }


  @GetMapping(value="/image")
  public ResponseEntity<List<String>> get(
      @RequestParam(value="name", required=false) String aName){

    Cloudinary cloudinary=new Cloudinary("cloudinary://"+mApiKey+":"+mApiSecret+"@"+mCloudName);
    List<String> retval=new ArrayList<String>();
    try
    {
      Map response=cloudinary.api().resource("", ObjectUtils.asMap("type", "upload"));
      JSONObject json=new JSONObject(response);
      JSONArray ja=json.getJSONArray("resources");
      for(int i=0; i<ja.length(); i++)
      {
        JSONObject j=ja.getJSONObject(i);
        retval.add(j.getString("url"));
      }

      return new ResponseEntity< List<String> >(retval, HttpStatus.OK);
    }
    catch (Exception e)
    {
      return new ResponseEntity< List<String> >(HttpStatus.BAD_REQUEST);
    }

  }
}

package com.notionreplica.udbs.controller;

import com.notionreplica.udbs.entities.Properties;
import com.notionreplica.udbs.entities.PropertyType;
import com.notionreplica.udbs.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/{userId}/workspace/{workspaceId}/notes")
public class PropertyController {

    @Autowired
    PropertyService propertyService;


    @GetMapping("/property/{propertyID}")
    public ResponseEntity<Map<String,Object>> getProperty(@PathVariable("propertyID") String propertyID) throws Exception {
        Properties property = propertyService.getProperty(propertyID);
        Map<String, Object> response = new HashMap<>();
        response.put("Property", property);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createProperty")
    public ResponseEntity<Map<String,Object>> createProperty(@RequestBody Map<String,Object> propertyData) throws Exception {
        String propertyType = (String) propertyData.get("propertyType");
        String title = (String) propertyData.get("title");
        Properties property = propertyService.createProperty(PropertyType.valueOf(propertyType.toUpperCase()), title);
        Map<String, Object> response = new HashMap<>();
        response.put("Property", property);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/property/{propertyID}")
    public ResponseEntity<String> deleteProperty(@PathVariable("propertyID") String propertyID) throws Exception {
        propertyService.deleteProperty(propertyID);
        return ResponseEntity.ok("Property " + propertyID + " deleted");
    }

    @PutMapping("/property/{propertyID}")
    public ResponseEntity<Map<String,Object>> updateProperty(@PathVariable("propertyID") String propertyID,
                                                             @RequestBody Map<String,Object> propertyData) throws Exception {

        PropertyType propertyType = (PropertyType) propertyData.get("propertyType");
        String title = (String) propertyData.get("title");
        Properties property = propertyService.updateProperty(propertyID, propertyType, title);
        Map<String, Object> response = new HashMap<>();
        response.put("Property", property);
        return ResponseEntity.ok(response);
    }
}
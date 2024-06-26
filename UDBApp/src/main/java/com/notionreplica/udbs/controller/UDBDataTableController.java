package com.notionreplica.udbs.controller;

import com.notionreplica.udbs.entities.UDBDataTable;
import com.notionreplica.udbs.services.UDBDataTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@RestController
@RequestMapping("/udb/{userId}/workspace/{workspaceId}/notes")
public class UDBDataTableController {
    @Autowired
    private UDBDataTableService udbDataTableService;

    Logger log = LoggerFactory.getLogger(UDBDataTableController.class);
    @GetMapping("/udbTable/{UDBid}")
    public ResponseEntity<Map<String,Object>> getUDBDataTable(@PathVariable("UDBid") String id) throws Exception {
        UDBDataTable udbDataTable = udbDataTableService.getUDBDataTable(id);
        Map<String, Object> response = new HashMap<>();
        response.put("UDB Data Table", udbDataTable);
        log.info("user accessed udb table with id :" + id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/udbTable/{UDBid}/udbPages")
    public ResponseEntity<Map<String,Object>> getUDBPagesUDBTable(@PathVariable("UDBid") String id) throws Exception {
        LinkedHashSet pages = udbDataTableService.getAllUDBPagesInTable(id);
        Map<String, Object> response = new HashMap<>();
        response.put("UDB Pages", pages);
        log.info("user accessed udb pages in table with id :" + id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createUdbTable")
    public ResponseEntity<Map<String,Object>> createUDBDataTable(@RequestBody Map<String,String> reqBody) throws Exception {
        UDBDataTable udbDataTable = udbDataTableService.createUDBDataTable(reqBody.get("title"));
        Map<String, Object> response = new HashMap<>();
        response.put("UDB Data Table", udbDataTable);
        log.info("user created udb table with id :" + udbDataTable.getUdbDataTableID()+ " and title :" + reqBody.get("title"));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateUdbTable/{UDBid}")
    public ResponseEntity<Map<String,Object>> updateUDBDataTable(@PathVariable("UDBid") String id, @RequestBody Map<String,String> reqBody) throws Exception {
        Map<String, Object> response = new HashMap<>();
        try{
            UDBDataTable udbDataTable = udbDataTableService.getUDBDataTable(id);
            if (reqBody.containsKey("title") && reqBody.get("title") != null) {
                udbDataTable = udbDataTableService.updateUDBDataTable(id, reqBody.get("title"));
            }
            if (reqBody.containsKey("propertyID") && reqBody.get("propertyID") != null) {
                udbDataTable= (UDBDataTable) udbDataTableService.addPropertyToUDBDataTable(id, reqBody.get("propertyID"));
            }
            if(reqBody.containsKey("udbPagesID") && reqBody.get("udbPagesID") != null){
                udbDataTable = (UDBDataTable) udbDataTableService.addUDBPageToUDBDataTable(id, reqBody.get("udbPagesID"));
            }

            response.put("UDB Data Table", udbDataTable);
            log.info("user updated udb table with id :" + id);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            System.out.println("7are2a 7are2a");
            return ResponseEntity.ok(response);
        }

    }

    @PutMapping("/updateUdbTable/{UDBid}/property/{propertyID}")
    public ResponseEntity<String> removePropertyFromTable(@PathVariable("UDBid") String id, @PathVariable("propertyID") String propertyID) throws Exception {
        udbDataTableService.removePropertyFromUDBDataTable(id, propertyID);
        log.info("user removed property from udb table with id :" + id);
        return ResponseEntity.ok("Removed Property from UDB Table");
    }
    @PutMapping("/updateUdbTable/{UDBid}/udbPage/{udbPageID}")
    public ResponseEntity<String> removeUDBPageFromTable(@PathVariable("UDBid") String id, @PathVariable("udbPageID") String udbPageID) throws Exception{
        udbDataTableService.removeUDBPageFromUDBDataTable(id,udbPageID);
        log.info("user removed udb page from udb table with id :" + id);
        return ResponseEntity.ok("Removed UDB Page from UDB Table");
    }

    @DeleteMapping("/{UDBid}/updateUdbTable/property/{propertyID}")
    public ResponseEntity<String> deleteAProperty(@PathVariable("UDBid") String id, @PathVariable("propertyID") String propertyID) throws Exception {
        udbDataTableService.removePropertyFromUDBDataTable(id, propertyID);
        log.info("user deleted property from udb table with id :" + id);
        return ResponseEntity.ok("Deleted UDB Data Table");
    }


    @DeleteMapping("/deleteUdbTable/{UDBid}")
    public ResponseEntity<String> deleteUDBDataTable(@PathVariable("UDBid") String id) throws Exception {
        udbDataTableService.deleteUDBDataTable(id);
        log.info("user deleted udb table with id :" + id);
        return ResponseEntity.ok("Deleted UDB Data Table");
    }

}

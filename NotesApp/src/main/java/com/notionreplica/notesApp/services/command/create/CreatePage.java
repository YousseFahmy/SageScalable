package com.notionreplica.notesApp.services.command.create;

import com.notionreplica.notesApp.entities.AccessModifier;
import com.notionreplica.notesApp.entities.Page;
import com.notionreplica.notesApp.entities.Workspace;
import com.notionreplica.notesApp.exceptions.WorkspaceNotFoundException;
import com.notionreplica.notesApp.repositories.PageRepo;
import com.notionreplica.notesApp.repositories.WorkspaceRepo;
import com.notionreplica.notesApp.services.command.CommandInterface;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
public class CreatePage implements CommandInterface {
    PageRepo pageRepo;
    WorkspaceRepo workRepo;
    String workspaceId;
    AccessModifier accessModifier;
    String parentId;
    @Override
    public Object execute() throws Exception {
        Optional<Workspace> userWorkspaceExists = workRepo.findById(workspaceId);
        if (userWorkspaceExists.isPresent()) {
            Workspace userWorkspace = userWorkspaceExists.get();
            Page newPage = new Page(userWorkspace.getWorkSpaceId());
            pageRepo.save(newPage);
            if(ObjectId.isValid(parentId)){
                Optional<Page> parentPage=pageRepo.findById(parentId);
                if(parentPage.isPresent()){
                    Page parentPages = parentPage.get();
                    Set<String> newSubPages = parentPages.getSubPagesIds();
                    newSubPages.add(newPage.getPageId());
                    parentPages.setSubPagesIds(newSubPages);
                    pageRepo.save(parentPages);
                }
            }
            userWorkspace.getAccessModifiers().put(newPage.getPageId(),accessModifier);
            workRepo.save(userWorkspace);
            return newPage;
        } else throw new WorkspaceNotFoundException("The worksspace"+ workspaceId +" doesn't exist");
    }
}

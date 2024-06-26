package org.example.groups_microservice.Service;

import jakarta.transaction.Transactional;
import org.example.groups_microservice.DTO.EventDTO;
import org.example.groups_microservice.DTO.GroupDTO;
import org.example.groups_microservice.DTO.GroupMemberDTO;
import org.example.groups_microservice.DTO.UserRole;
import org.example.groups_microservice.Entity.EventEntity;
import org.example.groups_microservice.Entity.GroupMemberEntity;
import org.example.groups_microservice.Entity.GroupEntity;
import org.example.groups_microservice.Exceptions.*;
import org.example.groups_microservice.Repository.GroupRepository;
import org.example.groups_microservice.Service.GroupPubSub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.groups_microservice.Service.GroupPubSub.publishGroupByMember;


/**
 * This class is a service for the Group entity.
 * It is used to interact with the repository.
 * It provides methods to store, retrieve, update and delete group objects.
 */

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final EventService eventService;
    private final GroupMemberService groupMemberService;
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    public GroupService(GroupRepository groupRepository, EventService eventService, GroupMemberService groupMemberService) {
        this.groupRepository = groupRepository;
        this.eventService = eventService;
        this.groupMemberService = groupMemberService;
    }

    /**
     * createGroup method is used to create a new group.
     *
     * @param groupDTO - the group object to be created
     * @return the created group entity
     * @throws GroupAlreadyExistsException if the group already exists or is invalid
     */
    @Transactional
    public GroupEntity createGroup(GroupDTO groupDTO) throws GroupAlreadyExistsException {
        logger.info("Creating group with name: {}", groupDTO.getGroupName());
        groupRepository.findByGroupName(groupDTO.getGroupName())
                .ifPresent(s -> {
                    try {
                        throw new GroupAlreadyExistsException("Group already exists with name: " + groupDTO.getGroupName());
                    } catch (GroupAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }
                });

        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setGroupName(groupDTO.getGroupName());
        groupEntity.setIsPrivate(groupDTO.getIsPrivate());


        //Link GroupMembers to the Group
        for (GroupMemberDTO groupMemberDTO : groupDTO.getGroupMembers()) {
            LinkGroupMemberToGroup(groupEntity, groupMemberDTO);
        }
        groupEntity.setMemberCount(groupEntity.getGroupMembers().size());

        //Link Event to the Group
        for (EventDTO eventDTO : groupDTO.getEvents()) {
            linkEventToGroup(groupEntity, eventDTO);
        }
        return groupRepository.save(groupEntity);
    }

    /**
     * linkEventToGroup method is used to link an event to a group.
     * @param groupEntity - the group entity
     * @param eventDTO - the event DTO
     */
    private void linkEventToGroup(GroupEntity groupEntity, EventDTO eventDTO) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setGlobalEventID(eventDTO.getGlobalEventID());
        eventEntity.setGroup(groupEntity);
        groupEntity.getEvents().add(eventEntity);
    }
    /**
     * LinkGroupMemberToGroup method is used to link a group member to a group.
     *
     * @param groupEntity - the group entity
     * @param groupMemberDTO - the group member DTO
     */
    private void LinkGroupMemberToGroup(GroupEntity groupEntity, GroupMemberDTO groupMemberDTO) {
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity();
        groupMemberEntity.setRole(groupMemberDTO.getRole());
        groupMemberEntity.setGroup(groupEntity);
        groupMemberEntity.setGroupMemberID(groupMemberDTO.getGroupMemberID());
        groupMemberEntity.setUserID(groupMemberDTO.getUserID());
        groupMemberEntity.setUserName(groupMemberDTO.getUserName());
        groupEntity.getGroupMembers().add(groupMemberEntity);
        groupEntity.setMemberCount(groupEntity.getGroupMembers().size());

    }
    /**
     * convertToGroupEntity method is used to convert a group DTO to a group entity.
     *
     * @param dto - the group DTO to be converted
     * @return the group entity
     */
    private EventEntity convertToEventEntity(EventDTO dto, GroupEntity groupEntity) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setGlobalEventID(dto.getGlobalEventID());
        eventEntity.setEventID(dto.getEventID());
        eventEntity.setGroup(groupEntity);
        return eventEntity;
    }
    /**
     * convertToGroupMemberEntity method is used to convert a group member DTO to a group member entity.
     *
     * @param dto - the group member DTO
     * @return the group member entity
     */

    private GroupMemberEntity convertToGroupMemberEntity(GroupMemberDTO dto, GroupEntity groupEntity) {

        GroupMemberEntity groupMemberEntity = new GroupMemberEntity();
        groupMemberEntity.setRole(dto.getRole());
        groupMemberEntity.setGroup(groupEntity);
        groupMemberEntity.setUserID(dto.getUserID());
        groupMemberEntity.setGroupMemberID(dto.getGroupMemberID());
        groupMemberEntity.setUserName(dto.getUserName());
        return groupMemberEntity;
    }

    /**
     * getGroup method is used to retrieve a group by its ID.
     *
     * @param groupID - the ID of the group to be retrieved
     * @return the group entity
     * @throws GroupNotFoundException if the group does not exist
     */
    @Transactional
    public GroupEntity getGroup(Integer groupID) throws GroupNotFoundException {
        logger.info("Getting group with ID: {}", groupID);
        return groupRepository.findGroupEntityByGroupID(groupID)
                .orElseThrow(() -> new GroupNotFoundException("Group does not exist with ID: " + groupID));
    }

    /**
     * getGroups method is used to retrieve all groups.
     * @return a list of group entities
     */
    @Transactional
    public List<GroupEntity> getGroups() {
        logger.info("Getting all groups");
        return groupRepository.findAll();
    }

    /**
     * updateGroup method is used to update a group.
     *
     * @param groupID - the ID of the group to be updated
     * @param groupDTO - the group object with updated values
     * @return the updated group entity
     * @throws GroupNotFoundException if the group does not exist or is invalid
     */
    @Transactional
    public GroupEntity updateGroup(int groupID, GroupDTO groupDTO,String userID, String username) throws GroupNotFoundException, NotAuthorizationException{
        logger.info("Updating group with ID: {}", groupID);
        GroupEntity groupEntity = groupRepository.findGroupEntityByGroupID(groupID)
                .orElseThrow(() -> new GroupNotFoundException("Group does not exist with ID: " + groupID));

        // Check if the user is authorized to update the group through the group member role and user ID
        GroupMemberEntity groupMemberEntity =  groupEntity.getGroupMembers().stream()
                .filter(groupMember -> groupMember.getUserID().equals(userID))
                .filter(groupMember -> groupMember.getRole().equals(UserRole.ADMIN))
                .findFirst()
                .orElseThrow(() -> new NotAuthorizationException("User:"+ username + "ID:"+ userID + " is not authorized to update the group"));

        // Update simple fields
        groupEntity.setGroupName(groupDTO.getGroupName());
        groupEntity.setIsPrivate(groupDTO.getIsPrivate());



        // Update group members carefully
        groupMemberService.updateGroupMembers(groupEntity, groupDTO.getGroupMembers());
        groupEntity.setMemberCount(groupDTO.getGroupMembers().size());


        // Update events carefully
        eventService.updateEvents(groupEntity, groupDTO.getEvents());


        return groupRepository.save(groupEntity);
    }


    /**
     * deleteGroup method is used to delete a group by its ID.
     *
     * @param groupID - the ID of the group to be deleted
     * @throws GroupNotFoundException if the group does not exist
     */
    @Transactional
    public void deleteGroup(Integer groupID) throws GroupNotFoundException, EventNotFoundException, MemberNotFoundException {
        logger.info("Deleting group with ID: {}", groupID);
        GroupEntity groupEntity = groupRepository.findGroupEntityByGroupID(groupID)
             .orElseThrow(() -> new GroupNotFoundException("Group does not exist with ID: " + groupID));
        publishGroupByMember(groupEntity, "DELETE");
        groupRepository.delete(groupEntity);

    }

    public List<GroupEntity> getGroupsByUserID(String userID) {
        logger.info("Getting all groups by user ID: {}", userID);
        return groupRepository.findAllByGroupMembersUserID(userID);
    }
}

package org.ucalgary.events_service.Service;

import org.springframework.stereotype.Service;
import org.ucalgary.events_service.DTO.EventDTO;
import org.ucalgary.events_service.Entity.AddressEntity;
import org.ucalgary.events_service.Repository.AddressRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Service class for managing AddressEntity objects.
 * This class provides methods to create, update, and delete address entities.
 */
@Service
public class AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Creates a new address entity based on the provided event DTO.
     * @param event The event DTO containing address information.
     * @return The newly created address entity.
     * @throws IllegalArgumentException if the address is invalid.
     */
    @Transactional
    public AddressEntity createAddress(EventDTO event) throws IllegalArgumentException {
        checkAddress(event);
        AddressEntity newAddress = new AddressEntity(event.getAddressId(),
                event.getLocation().getStreet(),
                event.getLocation().getCity(),
                event.getLocation().getProvince(),
                event.getLocation().getPostalCode(),
                event.getLocation().getCountry());
        AddressEntity savedAddress = addressRepository.save(newAddress);
        logger.info("Created new address with ID: {}", savedAddress.getAddressId());
        return savedAddress;
    }


    /**
     * Updates an existing address entity based on the provided event DTO.
     * If the address does not exist, it will be created.
     * @param event The updated event DTO.
     * @return The updated or newly created address entity.
     * @throws IllegalArgumentException if the address is invalid.
     */
    public AddressEntity updateAddress(EventDTO event) throws IllegalArgumentException {
        checkAddress(event);
        AddressEntity updatedAddress = new AddressEntity(event.getAddressId(),
                event.getLocation().getStreet(),
                event.getLocation().getCity(),
                event.getLocation().getProvince(),
                event.getLocation().getPostalCode(),
                event.getLocation().getCountry());
        AddressEntity savedAddress = addressRepository.save(updatedAddress);
        logger.info("Updated address with ID: {}", savedAddress.getAddressId());
        return savedAddress;
    }

    /**
     * Deletes an address entity based on the provided address ID.
     * @param addressID The ID of the address entity to delete.
     * @throws EntityNotFoundException if the address does not exist.
     */
    @Transactional
    public void deleteAddress(int addressID) throws EntityNotFoundException {
        if (addressRepository.existsById(addressID)) {
            addressRepository.deleteById(addressID);
            logger.info("Deleted address with ID: {}", addressID);
        } else {
            logger.error("Address not found with ID: {}", addressID);
            throw new EntityNotFoundException("Address not found with id: " + addressID);
        }
    }

    /**
     * Checks if the address is valid.
     * @param event The event DTO containing address information.
     * @throws IllegalArgumentException if the address is invalid.
     */
    private void checkAddress(EventDTO event) throws IllegalArgumentException {
        logger.info("Checking validity of address for event with ID: {}", event.getEventId());

        if(event.getLocation() == null){
            throw new IllegalArgumentException("You have to have an address");
        }
        if (event.getLocation().getStreet() == null || event.getLocation().getStreet().isEmpty()) {
            throw new IllegalArgumentException("Street is required");
        }
        if (event.getLocation().getCity() == null || event.getLocation().getCity().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (event.getLocation().getProvince() == null || event.getLocation().getProvince().isEmpty()) {
            throw new IllegalArgumentException("Province is required");
        }
        if (event.getLocation().getPostalCode() == null || event.getLocation().getPostalCode().isEmpty()) {
            throw new IllegalArgumentException("Postal code is required");
        }
        if (event.getLocation().getCountry() == null || event.getLocation().getCountry().isEmpty()) {
            throw new IllegalArgumentException("Country is required");
        }
        logger.info("Address is valid for event with ID: {}", event.getEventId());
    }
}

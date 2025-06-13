package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.RestaurantDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;
import it.unibo.cs.asm.acmeat.model.Restaurant;

import java.util.List;

public interface RestaurantService {
    /**
     * Retrieves a restaurant by its ID.
     *
     * @param id the ID of the restaurant
     * @return the restaurant with the specified ID
     */
    Restaurant getRestaurantById(int id);

    /**
     * Retrieves a list of restaurants by city ID.
     *
     * @param cityId the ID of the city
     * @return a list of RestaurantDTO objects representing the restaurants in the specified city
     */
    List<RestaurantDTO> getRestaurantsByCityId(int cityId);

    /**
     * Retrieves the menu of a restaurant by its ID.
     *
     * @param restaurantId the ID of the restaurant
     * @return a list of MenuDTO objects representing the menu items
     */
    List<MenuDTO> getMenuByRestaurantId(int restaurantId);

    /**
     * Retrieves active time slots for a restaurant by its ID.
     *
     * @param restaurantId the ID of the restaurant
     * @return a list of TimeSlotDTO objects representing the active time slots
     */
    List<TimeSlotDTO> getActiveTimeSlotsByRestaurantId(int restaurantId);

    /**
     * Retrieves all time slots for a restaurant by its ID.
     *
     * @param restaurantId the ID of the restaurant
     * @return a list of TimeSlotDTO objects representing all time slots
     */
    List<TimeSlotDTO> getTimeSlotsByRestaurantId(int restaurantId);

    /**
     * Adds a new menu item to a restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @param name         the name of the menu item
     * @param price        the price of the menu item
     * @return a MenuDTO object representing the added menu item
     */
    MenuDTO addMenuToRestaurant(int restaurantId, String name, double price);

    /**
     * Updates an existing menu item in a restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @param menuId       the ID of the menu item to update
     * @param name         the new name of the menu item
     * @param price        the new price of the menu item
     * @return a MenuDTO object representing the updated menu item
     */
    MenuDTO updateMenu(int restaurantId, int menuId, String name, double price);

    /**
     * Deletes a menu item from a restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @param menuId       the ID of the menu item to delete
     */
    void deleteMenu(int restaurantId, int menuId);

    /**
     * Updates the active status of a time slot in a restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @param timeSlotId   the ID of the time slot to update
     * @param active       the new active status of the time slot
     * @return a TimeSlotDTO object representing the updated time slot
     */
    TimeSlotDTO updateTimeSlot(int restaurantId, int timeSlotId, boolean active);
}

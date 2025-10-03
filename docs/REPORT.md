````markdown
# ACMEat project report

**Project carried out by**: Orazio Andrea Capone | Matteo Cardellini | Francesco Goretti

## Table of Contents
1. [Domain description](#1-domain-description)  
2. [Choreography modelling](#2-choreography-modelling)  
2.1. [Main choreography – Ordering process](#21-main-choreography--ordering-process)  
2.2. [Secondary choreography – Updating restaurant information](#22-secondary-choreography--updating-restaurant-information)  
3. [BPMN modelling](#3-bpmn-modelling)  
4. [SOA diagram](#4-soa-diagram)  

---

## 1. Domain description

The company **ACMEat** offers customers a service that allows them to select a menu from one of a set of partner restaurants and have it delivered to their home.

To use the service, the customer must first select a municipality among those where the service is active. Based on this selection, ACMEat displays a list of partner restaurants operating in that municipality and the menus they offer. The customer can then specify the restaurant and the menu they are interested in, as well as a delivery time slot (these are 15-minute slots between 12:00 and 14:00 and between 19:00 and 21:00).

Next comes a payment phase, which is handled through a third-party banking institution to which the customer is redirected. After the payment, the bank issues a token to the customer, who forwards it to ACMEat; ACMEat then uses the token to verify with the bank that the payment has been successfully completed. At this point the order becomes active.

Customers can still cancel the order, but not later than one hour before the scheduled delivery time. In that case ACMEat requests the bank to cancel/refund the payment.

ACMEat maintains the list of partner restaurants in the municipalities where it operates, including their opening days and hours. If a restaurant is not available on a day it would normally be open, it is the restaurant’s responsibility to inform ACMEat of the unavailability by 10:00 AM. Any menu changes for a given day must also be communicated by that time (if no update is provided, menus from the previous day are assumed to be available).

Restaurants are also contacted for each order to verify that they can actually fulfill the customer’s request. If they cannot, order acceptance stops before the payment phase.

For deliveries, ACMEat relies on multiple external shipping companies: for each delivery, ACMEat contacts all companies whose headquarters are within 10 kilometers of the relevant municipality, providing the pickup address (restaurant), the customer’s delivery address and the expected delivery time. The companies must respond within 15 seconds indicating their availability and the requested price. ACMEat selects, among the companies that replied in time and are available, the one offering the lowest price.

If no delivery company is available, the order is canceled before the payment phase.

---

## 2. Choreography modelling

### 2.1. Main choreography – Ordering process

#### Participants

- **Customer**
- **ACMEat**
- **Restaurant**
- **Bank**
- **ShippingCompany**

#### Choreography

```text
retrieveCities: ACMEat -> customer ;
selectCity: customer -> ACMEat ;
retrieveRestaurants: ACMEat -> customer ;
selectRestaurant: customer -> ACMEat ;
retrieveMenusAndTimeSlots: ACMEat -> customer ;
selectMenu: customer -> ACMEat ;
selectTimeSlot: customer -> ACMEat ;
enterDeliveryAddress: customer -> ACMEat ;
checkRestaurantAvailability: ACMEat -> restaurant ;
(
    confirmRestaurantAvailability: restaurant -> ACMEat ;
    confirmedOrder : ACMEat -> customer;
    (
        requestShippingCompany: ACMEat -> shippingCompany ;
        sendShippingCost: shippingCompany -> ACMEat;
        (
            confirmShippingCompany: ACMEat -> shippingCompany ;
            initilizePayment: ACMEat -> bank ;
            sendPaymentRedirect: bank -> ACMEat ;
            redirectToBank: ACMEat -> customer ;
            makePayment: customer -> bank ;
            sendPaymentToken: bank -> customer ;
            sendPaymentTokenToAcme: customer -> ACMEat ;
            verifyPayment: ACMEat -> bank ;
            (
                acceptPayment: bank -> ACMEat ;
                activateOrder: ACMEat -> customer ;
                orderNotCancelled: ACMEat -> restaurant;
                (
                    requestOrderCancellation: customer -> ACMEat ;
                    cancelOrderInRestaurant: ACMEat -> restaurant ;
                    cancelOrderInShippingCompany: ACMEat -> shippingCompany ;
                    paymentRefund: ACMEat -> bank ;
                    cancelOrder: ACMEat -> customer
                )
                +
                (
                    orderNotCancelled: customer -> ACMEat ;
                    orderNotCancelled: ACMEat -> restaurant;
                    orderDelivered: shippingCompany -> ACMEat ;
                    confirmPayment: ACMEat -> bank ;
                    orderCompleted: ACMEat -> customer
                )
            )
            +
            (
                rejectPayment: bank -> ACMEat ;
                cancelOrderInRestaurant: ACMEat -> restaurant ;
                cancelOrderInShippingCompany: ACMEat -> shippingCompany ;
                cancelOrder: ACMEat -> customer
            )
        )
        +
        (
            rejectShippingCompany: ACMEat -> shippingCompany
        )
    )*
    +
    (
        cancelOrder: ACMEat -> customer ;
        cancelOrderInRestaurant: ACMEat -> restaurant
    )
)
+
(
    rejectRestaurantAvailability: restaurant -> ACMEat ;
    cancelOrder: ACMEat -> customer
)
```

#### Projection on roles
```text
proj(C, customer) = 
    retrieveCities@ACMEat ;
    selectCity^@ACMEat ;
    retrieveRestaurants@ACMEat ;
    selectRestaurant^@ACMEat ;
    retrieveMenusAndTimeSlots@ACMEat ;
    selectMenu^@ACMEat ;
    selectTimeSlot^@ACMEat ;
    enterDeliveryAddress^@ACMEat ;
    (
        (
            confirmedOrder@ACMEat ;
            redirectToBank@ACMEat ;
            makePayment^@bank ;
            sendPaymentToken@bank;
            sendPaymentTokenToAcme^@ACMEat ;
            (
                activateOrder@ACMEat ;
                (
                    requestOrderCancellation^@ACMEat ;
                    cancelOrder@ACMEat
                )
                +
                (
                    orderNotCancelled^@ACMEat ;
                    orderCompleted@ACMEat
                )
            )
            +
            (
                cancelOrder@ACMEat
            )
        )
        +
        (
            cancelOrder@ACMEat
        )
    )
    +
    (
        cancelOrder@ACMEat
    )
```

```text
proj(C, ACMEat) =
    retrieveCities^@customer ;
    selectCity@customer ;
    retrieveRestaurants^@customer ;
    selectRestaurant@customer ;
    retrieveMenusAndTimeSlots^@customer ;
    selectMenu@customer ;
    selectTimeSlot@customer ;
    enterDeliveryAddress@customer ;
    checkRestaurantAvailability^@restaurant ;
    (
        confirmRestaurantAvailability@restaurant ;
        (
            confirmedOrder^@customer;
            requestShippingCompany^@shippingCompany ;
            sendShippingCost@shippingCompany;
            (
                confirmShippingCompany^@shippingCompany ;
                initilizePayment^@bank ;
                sendPaymentRedirect@bank ;
                redirectToBank^@customer ;
                sendPaymentTokenToAcme@customer ;
                verifyPayment^@bank ;
                (
                    acceptPayment@bank ;
                    activateOrder^@customer ;
                    orderNotCancelled^@restaurant;
                    (
                        requestOrderCancellation@customer ;
                        cancelOrderInRestaurant^@restaurant ;
                        cancelOrderInShippingCompany^@shippingCompany ;
                        paymentRefund^@bank ;
                        cancelOrder^@customer
                    )
                    +
                    (
                        orderNotCancelled@customer ;
                        orderNotCancelled^@restaurant;
                        orderDelivered@shippingCompany ;
                        confirmPayment^@bank ;
                        orderCompleted^@customer
                    )
                )
                +
                (
                    rejectPayment@bank ;
                    cancelOrderInRestaurant^@restaurant ;
                    cancelOrderInShippingCompany^@shippingCompany ;
                    cancelOrder^@customer
                )
            )
            +
            (
                rejectShippingCompany^@shippingCompany
            )
        )*
        +
        (
            cancelOrder^@customer;
            cancelOrderInRestaurant^@restaurant
        )
    )
    +
    (
        rejectRestaurantAvailability@restaurant ;
        cancelOrder^@customer
    )
```

```text
proj(C, restaurant) =
    checkRestaurantAvailability@ACMEat ;
    (
        confirmRestaurantAvailability^@ACMEat ;
        (
            (
                (
                    orderNotCancelled@ACMEat ;
                    (
                        cancelOrderInRestaurant@ACMEat
                    )
                    +
                    (
                        orderNotCancelled@ACMEat
                    )
                )
                +
                (
                    cancelOrderInRestaurant@ACMEat
                )
            )
            +
            1
        )*
        +
        (
            cancelOrderInRestaurant@ACMEat
        )
    )
    +
    (
        rejectRestaurantAvailability^@ACMEat
    )
```

```text
proj(C, shippingCompany) =
    requestShippingCompany@ACMEat ;
    sendShippingCost^@ACMEat
    (
        confirmShippingCompany@ACMEat ;
        (
            (
                cancelOrderInShippingCompany@ACMEat
            )
            +
            (
                orderDelivered^@ACMEat
            )
        )
        +
        (
            cancelOrderInShippingCompany@ACMEat
        )
    )
    +
    (
        rejectShippingCompany@ACMEat
    )
```

```text
proj(C, bank) =
    initilizePayment@ACMEat ;
    sendPaymentRedirect^@ACMEat ;
    makePayment@customer ;
    sendPaymentToken^@customer ;
    verifyPayment@ACMEat ;
    (
        acceptPayment^@ACMEat ;
        (
            paymentRefund@ACMEat
        )
        +
        (
            confirmPayment@ACMEat
        )
    )
    +
    (
        rejectPayment^@ACMEat
    )
```

### 2.2. Secondary choreography – Updating restaurant information

#### Participants
- **Restaurant**
- **ACMEat**

#### Choreography

```text
requestCurrentInfo: restaurant -> ACMEat ;
(
    sendCurrentInfo: ACMEat -> restaurant ;
    sendUpdatedInfo: restaurant -> ACMEat ;
    confirmUpdate: ACMEat -> restaurant
)
+
(
    rejectUpdate: ACMEat -> restaurant
)
```

#### Projection on roles
```text
proj(C, restaurant) =
    requestCurrentInfo^@ACMEat ;
    (
        sendCurrentInfo@ACMEat ;
        sendUpdatedInfo^@ACMEat ;
        confirmUpdate@ACMEat
    )
    +
    (
        rejectUpdate@ACMEat
    )
```

```text
proj(C, ACMEat) =
    requestCurrentInfo@restaurant ;
    (
        sendCurrentInfo^@restaurant ;
        sendUpdatedInfo@restaurant ;
        confirmUpdate^@restaurant
    )
    +
    (
        rejectUpdate^@restaurant
    )
```
---

## 3. BPMN modelling
The process was modelled using BPMN 2.0 with a collaborative notation. Each participant in the system (customer, ACMEat, restaurant, shipping company, bank) is represented by a separate pool, highlighting the exchanged messages and the different decision flows.

![BPMN Diagram](./img/bpmn_image.png)

> *The original BPMN file is available here: [`acmeat.bpmn`](../srcs/ACMEat/backend/src/main/resources/acmeat.bpmn)*

---

## 4. SOA diagram
The following SOA diagram, modelled in the TinySOA style, represents the capabilities provided by the ACMEat system and the corresponding exposed interfaces, as well as the dependencies on external services involved in the process (bank, restaurants, shipping companies, GIS).

![SOA Diagram](./img/tiny_soa-acmeat.png)

> *The original Papyrus project is available here: [`TinySOA_ACMEat`](./TinySOA-ACMEat)*

---

````

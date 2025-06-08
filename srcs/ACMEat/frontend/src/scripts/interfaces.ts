export const  getFetchConfig : RequestInit= {
    mode: 'cors',
}

export interface City{
    id? : number,
    name? : string,
    correlationKey?:string
}

export interface Restaurant{
    id? : number,
    name? : string,
    correlationKey?:string
}

export interface Menu{
    id? : number,
    name? : string,
    price? : string,
    quantity? : number,
    correlationKey?:string
}

export interface TimeSlot{
    id? : number,
    startTime? : string,
    endTime? : string,
    correlationKey?:string
}

export interface Order{
    id? : number,
    restaurantId? : number,
    restaurantName?:string,
    items? : any[],
    timeSlotId? : number,
    deliveryAddress? : string,
    correlationKey?:string
}


export interface citiesResponse{
    cities? : City[],
    correlationKey?:string
}

export interface restaurantsRsponse{
    restaurants? : Restaurant[],
    correlationKey?:string
}

export interface restaurantDetailsResponse{
    menus?: Menu[]
    timeSlots? : TimeSlot[],
    correlationKey?:string
}

export interface orderCreationResponse{
    order?: Order,
    price?:string,
    deliveryAddress? : string,
    deliveryTime? : string
}
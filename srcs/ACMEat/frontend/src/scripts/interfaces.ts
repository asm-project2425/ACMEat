export const  getFetchConfig : RequestInit= {
    mode: 'cors',
}

export interface City{
    id? : number,
    name? : string
}

export interface Restaurant{
    id? : number,
    name? : string
}

export interface Menu{
    id? : number,
    name? : string,
    price? : string,
    quantity? : number
}

export interface Order{
    id? : number,
    restaurantId? : number,
    items? : any[],
    timeSlotId? : number,
    deliveryAddress? : string
}

export interface citiesResponse{
    cities? : City[]
}

export interface restaurantsRsponse{
    restaurants? : Restaurant[]
}

export interface restaurantDetailsResponse{
    menus?: Menu[]
}
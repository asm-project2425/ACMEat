import type { Order, orderStatus } from "./interfaces";

const PUBLIC_SELF_HOST = import.meta.env.PUBLIC_SELF_HOST
const PUBLIC_ORDER_DETAILS : string = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_ORDER_DETAILS;
const PUBLIC_ORDER_STATUS : string = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_ORDER_STATUS;
export async function GetOrderDetails(orderId: string) : Promise<Order> {
    const url = PUBLIC_ORDER_DETAILS.replace("{orderId}", orderId)
    const res = await fetch(url);
    
    if(res.ok){
        const jres = await res.json();
        //console.log(jres);
        return jres;
    }

    console.log(res);
    throw new Error("GetOrderDetails");
}

export async function GetOrderStatus(orderId : string) : Promise<orderStatus> {
    const url = PUBLIC_ORDER_STATUS.replace("{orderId}", orderId)
    const res = await fetch(url);
    const jres: orderStatus = await res.json();
    //console.log("Order status",jres);
    return jres;
}

export const sleep_ms = ms => new Promise(r => setTimeout(r, ms));
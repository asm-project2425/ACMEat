import type { Order } from "./interfaces";

const PUBLIC_SELF_HOST = import.meta.env.PUBLIC_SELF_HOST
const PUBLIC_ORDER_DETAILS : string = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_ORDER_DETAILS;
export async function GetOrderDetails(orderId: string) : Promise<Order> {
    const url = PUBLIC_ORDER_DETAILS.replace("{orderId}", orderId)
    const res = await fetch(url);
    
    if(res.ok){
        const jres = await res.json();
        console.log(jres);
        return jres;
    }

    console.log(res);
    throw new Error("GetOrderDetails");
}
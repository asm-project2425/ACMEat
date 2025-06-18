import type { Order, orderStatus, paymentToken } from "./interfaces";
import { GetOrderDetails } from "./order_utils";

const PUBLIC_SELF_HOST = import.meta.env.PUBLIC_SELF_HOST
const PUBLIC_VERIFY_PAYMENT = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_VERIFY_PAYMENT;
const PUBLIC_ORDER_STATUS : string = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_ORDER_STATUS;
const PUBLIC_ORDER_DETAILS : string = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_ORDER_DETAILS;
let orderId:string;
const mess_label = document.getElementById("mess_label") as HTMLLabelElement;

const order_id = document.getElementById("order_id") as HTMLLabelElement;
const restaurant = document.getElementById("restaurant") as HTMLLabelElement;
const address = document.getElementById("address") as HTMLLabelElement;
const time = document.getElementById("time") as HTMLLabelElement;
const price = document.getElementById("price") as HTMLLabelElement;
const request_label = document.getElementById("request_label") as HTMLLabelElement;
const shipping_price = document.getElementById("shipping_price") as HTMLLabelElement;

async function Verify_Payment(details : paymentToken) {
    const res = await fetch(PUBLIC_VERIFY_PAYMENT, {
        method : "post",
        body : JSON.stringify(details),
        headers :{
            "Content-Type":"application/json"
        }
    });
    console.log(res);

    if(res.ok){
        const orderStatus: orderStatus = await GetOrderStatus(orderId);

        if(orderStatus.status == "PAYMENT_REQUESTED"){
            mess_label.textContent = `PAYMENT_REQUESTED attendendo ACMEat...`;
            return await Verify_Payment(details);
        }else if(orderStatus.status == "PAID"){
            mess_label.className = "";
            mess_label.textContent = `Pagamento verificato ✅`;
            return true;
        }
    }
    return res;

}

async function GetOrderStatus(orderId : string) : Promise<orderStatus> {
    const url = PUBLIC_ORDER_STATUS.replace("{orderId}", orderId)
    const res = await fetch(url);
    const jres: orderStatus = await res.json();
    console.log("Order status",jres);
    return jres;
}



function UpdateOrderDetails(order: Order){
    order_id.textContent = `${order.id}`;
    restaurant.textContent = order.restaurantName;
}




async function main() {
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");
    orderId = params.get("orderId");
    console.log("Token:",token, orderId);

    if(!token){
        alert("token non trovato nell'url");
        throw new Error("Token non trovato");
    }else if(!orderId){
        alert("orderId non trovato nell'url");
        throw new Error("orderId non trovato");
    }

    const stat =await Verify_Payment({orderId: parseInt(orderId), paymentToken: token});
    if(stat !== true){
        console.log(stat);
        throw new Error("Errore nel pagamento");
    }

    const ordine : Order = await GetOrderDetails(orderId);
    UpdateOrderDetails(ordine);
}

main();
import type { Order, orderStatus, paymentToken } from "./interfaces";
import { GetOrderDetails, GetOrderStatus, sleep_ms } from "./order_utils";

const PUBLIC_SELF_HOST = import.meta.env.PUBLIC_SELF_HOST
const PUBLIC_VERIFY_PAYMENT = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_VERIFY_PAYMENT;
const PUBLIC_CANCEL_ORDER : string = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_CANCEL_ORDER;
let orderId:string;
const mess_label = document.getElementById("mess_label") as HTMLLabelElement;
//const request_label = document.getElementById("request_label") as HTMLLabelElement;

const order_id = document.getElementById("order_id") as HTMLLabelElement;
const restaurant = document.getElementById("restaurant") as HTMLLabelElement;
const address = document.getElementById("address") as HTMLLabelElement;
const time = document.getElementById("time") as HTMLLabelElement;
const price = document.getElementById("price") as HTMLLabelElement;
const shipping_price = document.getElementById("shipping_price") as HTMLLabelElement;

const cancel_btn = document.getElementById("cancel_btn") as HTMLButtonElement;

async function Verify_Payment(details : paymentToken) {
    await sleep_ms(1000);
    const res = await fetch(PUBLIC_VERIFY_PAYMENT, {
        method : "post",
        body : JSON.stringify(details),
        headers :{
            "Content-Type":"application/json"
        }
    });
    console.log(res);

    if(res.ok){
        await sleep_ms(1000);
        Loop(details);
        return true;
    }
    return res;

}





function UpdateOrderDetails(order: Order){
    order_id.textContent = `Id ordine : ${order.id}`;
    restaurant.textContent = `Ristorante : ${order.restaurantName}`;
    address.textContent = `Indirizzo di consegna : ${order.deliveryAddress}`;
    time.textContent = `Orario previsto : ${order.deliveryTime}`;
    price.textContent = `Prezzo : ${order.price}`;
    shipping_price.textContent = `Prezzo spedizione : ${order.shippingPrice}`;
}


async function Loop(details :paymentToken) {
    while(true){
        await sleep_ms(1000);
        
        const orderStatus: orderStatus = await GetOrderStatus(`${details.orderId}`);
        console.log("Order status", orderStatus.status);
        const ordine : Order = await GetOrderDetails(orderId);
        UpdateOrderDetails(ordine);
        
        if(orderStatus.status == "PAYMENT_REQUESTED"){
            mess_label.textContent = `PAYMENT_REQUESTED attendendo ACMEat...`;
        }else if(orderStatus.status == "PAID"){
            cancel_btn.disabled = false;
            mess_label.className = "";
            mess_label.textContent = `Pagamento verificato ✅`;
        }
        else if(orderStatus.status == "DELIVERED"){
            mess_label.className = "";
            mess_label.textContent = `Ordine consegnato 📦✅`;
            cancel_btn.disabled = true;
            cancel_btn.parentElement.removeChild(cancel_btn);
            return;
        }else if(orderStatus.status == "CANCELLED"){
            mess_label.className = "";
            mess_label.textContent = `Ordine annullato ❌`;
            cancel_btn.disabled = true;
            cancel_btn.parentElement.removeChild(cancel_btn);
            return;
        }else if(orderStatus.status == "CANCELLATION_REJECTED"){
            mess_label.className = "";
            mess_label.textContent = `Pagamento verificato ✅\nNon puoi annullare l'ordine se manca meno di un ora`;
            cancel_btn.disabled = true;
            cancel_btn.parentElement.removeChild(cancel_btn);
            return;
        }

        
    }
}

async function On_Cancel_btn() {
    cancel_btn.disabled = true;
    const res = await fetch(`${PUBLIC_CANCEL_ORDER}?orderId=${orderId}`, {
        method : "POST",
        body : JSON.stringify({orderId : orderId}),
        headers :{
            "Content-Type":"application/json"
        }
    })

    
    if(res.ok){
        cancel_btn.parentElement.removeChild(cancel_btn);
        //alert("Ordine annullato");
        return;
    }
    
    console.log(res);
    alert("Problema durante la cancellazione dell'ordine\n");
}

async function main() {
    cancel_btn.onclick = On_Cancel_btn;
    cancel_btn.disabled = true;
    const params = new URLSearchParams(window.location.search);
    let token = params.get("token");
    orderId = params.get("orderId");
    //console.log("Token:",token, orderId);

    if(!token){
        token = "a"
        //alert("token non trovato nell'url");
        //throw new Error("Token non trovato");
    }else if(!orderId){
        alert("orderId non trovato nell'url");
        throw new Error("orderId non trovato");
    }

    const stat =await Verify_Payment({orderId: parseInt(orderId), paymentToken: token});
    if(stat !== true){
        console.log(stat);
        const ordine : Order = await GetOrderDetails(orderId);
        UpdateOrderDetails(ordine);
        throw new Error("Errore nel pagamento");
    }

    //const ordine : Order = await GetOrderDetails(orderId);
    //UpdateOrderDetails(ordine);
}

main();
import type { Order, orderCreationResponse, orderStatus } from "./interfaces";
import { GetOrderDetails, GetOrderStatus, sleep_ms } from "./order_utils";
const PUBLIC_SELF_HOST = import.meta.env.PUBLIC_SELF_HOST
const PUBLIC_RECIVE_SHIPPING_COST = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_RECIVE_SHIPPING_COST;
const PUBLIC_PAYMENT_REDIRECT = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_PAYMENT_REDIRECT;

const div = document.getElementById("order_accepted") as HTMLDivElement;
const order_id = document.getElementById("order_id") as HTMLLabelElement;
const restaurant = document.getElementById("restaurant") as HTMLLabelElement;
const address = document.getElementById("address") as HTMLLabelElement;
const time = document.getElementById("time") as HTMLLabelElement;
const price = document.getElementById("price") as HTMLLabelElement;
const request_label = document.getElementById("request_label") as HTMLLabelElement;
const shipping_price = document.getElementById("shipping_price") as HTMLLabelElement;


async function On_order_accepted(order: orderCreationResponse, correlationKey:string, orderId : string = undefined) {
    if(!order){
        order = {order : await GetOrderDetails(orderId)};
    }

    console.log(`%c Ordine accettato`, `color: green`);
    console.log(order);
    div.className = "";

    order_id.textContent = `Id ordine : ${order.order.id}`;
    restaurant.textContent = `Ristorante : ${order.order.restaurantName}`;
    address.textContent = `Indirizzo di consegna : ${order.order.deliveryAddress}`;
    time.textContent = `Orario previsto : ${order.order.deliveryTime}`;
    price.textContent = `Prezzo : ${order.order.price}`;

    const s_cost:string = await GetShippingCost(1, order.order.id, correlationKey+"+1");


    //const s_cost = "0.0";
    shipping_price.textContent = `Prezzo di spedizione ${s_cost}`;
    let url = await GetPayment(1, order.order.id);
    url = url.replaceAll("https", "http");
    url = url.replaceAll("bank-frontend", "localhost:8002");
    //console.log(s_cost, order.order.price);
    //console.log(parseFloat(order.order.price), parseFloat(s_cost), parseFloat(order.order.price) + parseFloat(s_cost))
    const total :string=  (parseFloat(order.order.price) + parseFloat(s_cost)).toFixed(2);
    url += `&orderId=${order.order.id}&total=${total}`;
    await sleep_ms(2000);
    window.location.href = url;
}

async function GetShippingCost(count:number, orderID:number, correlationKey) :Promise<string> {
    request_label.textContent = `In attesa del costo di spedizione. Tentativo ${count}`;
    try {
        const order : Order = await GetOrderDetails(`${orderID}`);
        if(order && order.shippingPrice?.length > 0){
            return order.shippingPrice;
        }else{
            //console.log(order);
            throw new Error("Errore durante GetShippingCost ");
        }
        
    } catch (error) {
        //console.log(error);
        if(count > 100)
            throw new Error("GetShippingCost failed");

        if(count >10){
            const orderStatus: orderStatus = await GetOrderStatus(`${orderID}`);
            if(orderStatus.status == "CANCELLED"){

                window.location.href = `/payment_confirm?orderId=${orderID}`;
            }
            console.log(orderStatus);
        }

        await sleep_ms(1000);
        return (await GetShippingCost(count+1, orderID, correlationKey));
    }
    //return "Errore durante la richiesta del costo di spedizione";
}

async function GetPayment(count:number, orderID) : Promise<string> {
    try {
        request_label.textContent = `In attesa dei dati di pagamento. Tentativo ${count}`;
        const res = await fetch(`${PUBLIC_PAYMENT_REDIRECT}?orderId=${orderID}`);
        const jres = await res.json();
        console.log(jres);

        if(res.ok){
            return jres.redirectUrl;
        }else{
            throw new Error("GetPayment error "+res.statusText);
        }
        
    } catch (error) {
        console.log(error);
        await sleep_ms(1000);
        return (await GetPayment(count+1, orderID));
    }
    
}


async function main() {
    //@ts-ignore
    window.On_order_accepted = On_order_accepted;

    
}


main();
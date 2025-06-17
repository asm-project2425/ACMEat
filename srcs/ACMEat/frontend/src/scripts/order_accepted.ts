import type { orderCreationResponse } from "./interfaces";
const PUBLIC_SELF_HOST = import.meta.env.PUBLIC_SELF_HOST
const PUBLIC_PAYMENT_REDIRECT = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_PAYMENT_REDIRECT;

const div = document.getElementById("order_accepted") as HTMLDivElement;
const order_id = document.getElementById("order_id") as HTMLLabelElement;
const restaurant = document.getElementById("restaurant") as HTMLLabelElement;
const address = document.getElementById("address") as HTMLLabelElement;
const time = document.getElementById("time") as HTMLLabelElement;
const price = document.getElementById("price") as HTMLLabelElement;
const request_label = document.getElementById("request_label") as HTMLLabelElement;


async function On_order_accepted(order: orderCreationResponse) {
    console.log(`%c Ordine accettato`, `color: green`);
    console.log(order);
    div.className = "";

    order_id.textContent = `Id ordine : ${order.order.id}`;
    restaurant.textContent = `Ristorante : ${order.order.restaurantName}`;
    address.textContent = `Indirizzo di consegna : ${order.order.deliveryAddress}`;
    time.textContent = `Orario previsto : ${order.order.deliveryTime}`;
    price.textContent = `Prezzo : ${order.order.price}`;

    GetPayment(1, order.order.id);
}

async function GetPayment(count:number, orderID) {
    try {
        request_label.textContent = `In attesa dei dati di pagamento. Tentativo ${count}`;
        const res = await fetch(`${PUBLIC_PAYMENT_REDIRECT}?orderId=${orderID}`);
        const jres = await res.json();
        console.log(jres);
        
    } catch (error) {
        console.log(error);
        GetPayment(count+1, orderID);
    }
    
}


async function main() {
    //@ts-ignore
    window.On_order_accepted = On_order_accepted;
}


main();
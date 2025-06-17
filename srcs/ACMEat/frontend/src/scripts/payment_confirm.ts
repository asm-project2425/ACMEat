import type { orderStatus, paymentToken } from "./interfaces";

const PUBLIC_SELF_HOST = import.meta.env.PUBLIC_SELF_HOST
const PUBLIC_VERIFY_PAYMENT = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_VERIFY_PAYMENT;
const PUBLIC_ORDER_STATUS : string = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_ORDER_STATUS;
let orderId:string;
const mess_label = document.getElementById("mess_label") as HTMLLabelElement;

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

        if(orderStatus.status == "PAID"){
            mess_label.className = "";
            mess_label.textContent = `Pagamento verificato ✅`;
        }
    }

}

async function GetOrderStatus(orderId : string) : Promise<orderStatus> {
    const url = PUBLIC_ORDER_STATUS.replace("{orderId}", orderId)
    const res = await fetch(url);
    const jres: orderStatus = await res.json();
    console.log("Order status",jres);
    return jres;
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

    await Verify_Payment({orderId: parseInt(orderId), paymentToken: token});
}

main();
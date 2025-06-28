import type { Menu, restaurantDetailsResponse } from "./interfaces";
import { Append_Menu_Card, Append_Time_Card, Get_Restaurant_informations, InitValues } from "./restaurants_utils";

const PUBLIC_SELF_HOST = import.meta.env.PUBLIC_SELF_HOST;
const PUBLIC_ADD_MENU = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_ADD_MENU;
const PUBLIC_CONFIRM_CHANGES = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_CONFIRM_CHANGES;
const main_card = document.getElementById("main_card") as HTMLDivElement;
const menu_div = document.getElementById("menu_div") as HTMLDivElement;
const menu_table = document.getElementById("menu_table") as HTMLTableElement;
const add_menu_btn = document.getElementById("add_menu_btn") as HTMLButtonElement;
const time_table = document.getElementById("time_table") as HTMLTableElement;
const commit_btn = document.getElementById("commit_btn") as HTMLButtonElement;

let correlationKey:string;
function Init(restaurant : restaurantDetailsResponse){
    for(const m of restaurant.menus){
        Append_Menu_Card(menu_table, m);
    }

    for(const t of restaurant.timeSlots){
        Append_Time_Card(time_table, t)
    }
}

async function On_Add_Menu_btn(restaurantId : string) {
    add_menu_btn.disabled = true;
    const menu : Menu = {id:undefined, name: "New Name", price: "0.00"};
    const url = PUBLIC_ADD_MENU.replace("{restaurantId}", restaurantId);
    const res = await fetch(url, {
        method : "POST",
        body : JSON.stringify(menu),
        headers :{
            "Content-Type":"application/json"
        }
    })

    if(res.ok){
        const jres : Menu = await res.json();
        //console.log(jres);
        Append_Menu_Card(menu_table, jres);
    }

    add_menu_btn.disabled = false;
}

async function On_Commit_btn(id:string) {
    const res = await fetch(`${PUBLIC_CONFIRM_CHANGES}?correlationKey=${correlationKey}`,{
        method : "POST",
        body : JSON.stringify({}),
        headers :{
            "Content-Type":"application/json"
        }
    });
    if(res.ok){
        alert("Cambiamenti confermati");
        return;
    }

    console.error(res);
}

async function main() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");
    add_menu_btn.onclick = On_Add_Menu_btn.bind(this, id);
    commit_btn.onclick = On_Commit_btn.bind(this, id);

    try {
        const rest : restaurantDetailsResponse= await Get_Restaurant_informations(id);
        correlationKey = rest.correlationKey;
        console.log("Modificando ristorante",1);
        console.log(rest);

        InitValues(id, correlationKey);
        Init(rest);
    } catch (error) {
        console.log(error);
        main_card.innerHTML="";
        main_card.textContent="Puoi modificare le informazioni solo tra le 21 e le 10";
    }
    
}

main();
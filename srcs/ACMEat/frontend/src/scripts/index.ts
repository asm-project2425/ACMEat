import { getFetchConfig, type citiesResponse, type Menu, type Order, type restaurantDetailsResponse, type restaurantsRsponse, type TimeSlot } from "./interfaces";

const PUBLIC_SELF_HOST = import.meta.env.PUBLIC_SELF_HOST
const PUBLIC_RETRIVE_CITIES = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_RETRIVE_CITIES;
const PUBLIC_RETRIVE_RESTAURANTS = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_RETRIVE_RESTAURANTS;
const PUBLIC_RETRIVE_RESTAURANT_DETAILS = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_RETRIVE_RESTAURANT_DETAILS;
const PUBLIC_CREATE_ORDER = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_CREATE_ORDER;

let comune :HTMLDivElement = document.getElementById("comune") as HTMLDivElement;;
let locale :HTMLDivElement = document.getElementById("locale") as HTMLDivElement;
let menu :HTMLDivElement = document.getElementById("menu") as HTMLDivElement;
let orario :HTMLDivElement = document.getElementById("orario") as HTMLDivElement;
let indirizzo : HTMLButtonElement= document.getElementById("indirizzo") as HTMLButtonElement;
let ordina : HTMLButtonElement= document.getElementById("ordina") as HTMLButtonElement;

let comune_select : HTMLSelectElement;
let locale_select : HTMLSelectElement;
let menu_select : HTMLSelectElement;
let orario_select : HTMLSelectElement;
let indirizzo_input : HTMLInputElement;


async function GetCities() {
    const res = await fetch(PUBLIC_RETRIVE_CITIES);
    console.log(await res.json());
    
}

function CreateOption(label:string, value:string = undefined) : HTMLOptionElement{
    const option : HTMLOptionElement = document.createElement("option");
    option.text = label;
    option.value = value || label;

    return option;
}

function ClearOptions(select:HTMLSelectElement){

    for(let i=select.options.length; i>0; i--){
        select.options.remove(i);
    }
}

async function Set_Comuni(){
    const res= await fetch(PUBLIC_RETRIVE_CITIES);
    const cRes : citiesResponse = await res.json();
    const comuni = cRes.cities;


    if(!comuni || comuni.length<1){
        alert(`Nessun comune trovato`);
    }

    for(const c of comuni){
        comune_select.options.add(CreateOption(c.name, c.id.toString()));
    }

    comune.style.visibility = "visible";
}

async function On_Comune_Change() {
    const value = comune_select.value;
    const res = await fetch(PUBLIC_RETRIVE_RESTAURANTS+`?cityId=${value}`)
    const rRes : restaurantsRsponse = await res.json();
    const locali = rRes.restaurants;

    if(!locali || locali.length<1){
        alert(`Nessun locale trovato a ${value}`);
        return;
    }

    ClearOptions(locale_select);
    ClearOptions(menu_select);
    ClearOptions(orario_select);

    for(const l of locali){
        locale_select.options.add(CreateOption(l.name, l.id.toString()));
    }

    locale.style.visibility = "visible";

}

async function On_Locale_Change() {
    const value = locale_select.value;
    const res = await fetch(PUBLIC_RETRIVE_RESTAURANT_DETAILS+`/${value}`);
    const jres : restaurantDetailsResponse = await res.json();

    const menus:Menu[] = jres.menus;
    const orari : TimeSlot[] = jres.timeSlots;

    if(!menus || menus.length<1){
        alert(`Nessun menù trovato per ${value}`);
        return;
    }

    ClearOptions(menu_select);
    ClearOptions(orario_select);

    for(const m of menus){
        menu_select.options.add(CreateOption(`${m.name} ${m.price}€`, m.id.toString()));
    }

    for(const o of orari){
        orario_select.options.add(CreateOption(`${o.startTime} ${o.endTime}`, o.id.toString()));
    }


    menu.style.visibility = "visible";
}

async function On_Menu_Change(){

    orario.style.visibility = "visible";
}

async function On_Orario_Change() {
    const value = orario_select.value;

    ordina.style.visibility = "visible";
}

async function On_Ordina() {
    const resId : number = Number.parseInt(locale_select.value); 
    const menuId : number = Number.parseInt(menu_select.value);
    const timeSlotId : number = Number.parseInt(orario_select.value);
    const indirizzo : string = indirizzo_input.value;

    if(resId==undefined|| menuId==undefined|| timeSlotId==undefined){
        alert("Seleziona tutti i campi!");
        return;
    }

    const ordine : Order={
        restaurantId : resId,
        items : [{
            menuId : menuId,
            quantity : 1
        }],
        timeSlotId: timeSlotId,
        deliveryAddress : indirizzo

    } 

    const res = await fetch(PUBLIC_CREATE_ORDER,{
        method : "post",
        body : JSON.stringify(ordine)
    });

    console.log(await res.json());
    alert(JSON.stringify(await res.json(), null, 2))
}



async function main() {
    console.log("hi");

    comune_select = comune.getElementsByTagName("select")[0] as HTMLSelectElement;
    locale_select = locale.getElementsByTagName("select")[0] as HTMLSelectElement;
    menu_select = menu.getElementsByTagName("select")[0] as HTMLSelectElement;
    orario_select = orario.getElementsByTagName("select")[0] as HTMLSelectElement;
    indirizzo_input = indirizzo.getElementsByTagName("input")[0] as HTMLInputElement;
    
    comune_select.onchange = On_Comune_Change;
    locale_select.onchange = On_Locale_Change;
    menu_select.onchange = On_Menu_Change;
    orario_select.onchange = On_Orario_Change;
    ordina.onclick = On_Ordina;

    
    comune.style.visibility = "collapse"
    locale.style.visibility = "collapse";
    menu.style.visibility = "collapse";
    orario.style.visibility = "collapse";
    ordina.style.visibility = "collapse";
    Set_Comuni();
}



main();
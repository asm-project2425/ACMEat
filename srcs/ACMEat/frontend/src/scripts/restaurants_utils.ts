import type { Menu, restaurantDetailsResponse, TimeSlot } from "./interfaces";

const PUBLIC_SELF_HOST = import.meta.env.PUBLIC_SELF_HOST;
const PUBLIC_RESTAURANTS_INFORMATION = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_RESTAURANTS_INFORMATION;
const PUBLIC_UPDATE_MENU = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_UPDATE_MENU;
const PUBLIC_DELETE_MENU = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_DELETE_MENU;
const PUBLIC_UPDATE_TIME_SLOT = PUBLIC_SELF_HOST + import.meta.env.PUBLIC_UPDATE_TIME_SLOT;
let restaurantId : string;
let correlationKey: string;
const menus_status = document.getElementById("menus_status") as HTMLTableCellElement;

export async function Get_Restaurant_informations(restaurantId : string) : Promise<restaurantDetailsResponse>{
    let url = PUBLIC_RESTAURANTS_INFORMATION;
    url = url.replace("{restaurantId}", restaurantId)

    const res = await fetch(url);
    if(res.ok){
        return await res.json();
    }

    console.error(res);
    throw new Error("Get_Restaurant_informations");
}



async function On_Menu_Enter(row : HTMLTableRowElement){
    menus_status.textContent = `⚠️`;
    const cells = row.childNodes as NodeListOf<HTMLTableCellElement>;
    cells[3].textContent = `⏳`;

    const id = cells[0].textContent;
    const name= cells[1].textContent;
    const price = parseFloat(cells[2].textContent);
    cells[2].textContent = price.toFixed(2);

    let url = PUBLIC_UPDATE_MENU.replace("{restaurantId}", restaurantId).replace("{menuId}", id);
    const res = await fetch(url, {
        method : "PUT",
        body : JSON.stringify({name: name, price: price}),
        headers :{
            "Content-Type":"application/json"
        }
    });

    if(res.ok){
        cells[3].textContent = `💾`;
    }else{
        cells[3].textContent = `⚠️`;
    }

}

function On_Menu_Edited(row : HTMLTableRowElement){
    const cells = row.childNodes as NodeListOf<HTMLTableCellElement>;
    cells[3].textContent = `🖋️`;
}


function On_LabelKeydown(this: HTMLTableCellElement, ev : KeyboardEvent){
    //console.log(this);
    //console.log(ev);

    On_Menu_Edited(this.parentElement as HTMLTableRowElement);

    if(ev.key === "Enter"){
        //console.log("Enter", this, ev);
        ev.preventDefault();
        this.blur();
        On_Menu_Enter(this.parentElement as HTMLTableRowElement);
    }
}

async function On_Delete_menu(this: HTMLTableRowElement, id: string) {
    const url = PUBLIC_DELETE_MENU.replace("{restaurantId}", restaurantId).replace("{menuId}", id);
    const res = await fetch(url, {method:"DELETE"});
    if(res.ok){
        this.parentElement.removeChild(this);
        return;
    }

    alert(await res.text());
}

export function InitValues(rId : string, cK: string){
    restaurantId = rId;
    correlationKey =cK;
    //console.log(restaurantId, correlationKey);
}


export function Append_Menu_Card(parent: HTMLTableElement, menu : Menu) {
    const row =document.createElement("tr");
    const id = document.createElement("td");
    const name = document.createElement("td");
    const price = document.createElement("td");
    const status = document.createElement("td");
    const del = document.createElement("td");
    name.contentEditable = "true";
    price.contentEditable = "true";
    
    id.textContent = menu.id?.toString();
    name.textContent = menu.name;
    price.textContent = menu.price;
    del.textContent = `🗑️`;

    name.onkeydown = On_LabelKeydown.bind(name);
    price.onkeydown = On_LabelKeydown.bind(price);
    del.onclick = On_Delete_menu.bind(row, menu.id.toString());

    row.appendChild(id);
    row.appendChild(name);
    row.appendChild(price);
    row.appendChild(status);
    row.appendChild(del);
    parent.appendChild(row);
}

async function On_Slot_checked(id:string) {
    const url = PUBLIC_UPDATE_TIME_SLOT.replace("{restaurantId}", restaurantId).replace("{timeSlotId}", id);
    const res = await fetch(`${url}?active=${this.checked}`, {method:"PUT"});

    if(res.ok){
        //console.log(res);
        return;
    }
    console.error(res);
}


export function Append_Time_Card(parent: HTMLTableElement, slot : TimeSlot){
    //console.log(slot)
    const row =document.createElement("tr");
    const id = document.createElement("td");
    const start = document.createElement("td");
    const end = document.createElement("td");
    const status = document.createElement("td");

    id.textContent = slot.id.toString();
    start.textContent = slot.startTime;
    end.textContent = slot.endTime;

    const check = document.createElement("input");
    check.type = "checkbox";
    check.checked = slot.active;
    check.onchange = On_Slot_checked.bind(check, slot.id.toString());
    status.appendChild(check);

    row.appendChild(id);
    row.appendChild(start);
    row.appendChild(end);
    row.appendChild(status);
    parent.appendChild(row);
}
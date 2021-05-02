address = null
user_edit = false

$(()=>{
    $(".window.user .actions .logout").click(()=>logout())
    $(".window.user .actions .delete").click(()=>deleteAccount())
    $(".window.user .addresses .new").click(()=>openAddress(null))
    $(".window.newAddress > form").submit(e => e.preventDefault())
    $(".window.newAddress .submit").click(()=>createChangeAddress())
    $(".window.newAddress .cancel").click(()=>$(".window.newAddress").css("display", "none"))

    setCitySelect($(".window.newAddress .postalcode input"), $(".window.newAddress .city select"))
    setCitySelect($(".window.newShop .postalcode input"), $(".window.newShop .city select"))

    $(".window.user .infos .edit").click(()=>editUser())

    $(".window.user .shops .new").click(()=>{
        $(".window.newShop input").val("")
        $(".window.newShop").css("display", "grid")
        newShopAct()
    })
    $(".window.newShop .cancel").click(()=>{
        $(".window.newShop").css("display", "none")
    })
    $(".window.newShop form").submit(e=>e.preventDefault())
    $(".window.newShop .category select").change(()=>{
        if($(".window.newShop .dropdown select").val() === "new"){
            let category = prompt("Entrer une nouvelle categorie")
            if(category === "" || category === null)
                return;

            $.post(api + "store/type/new", {
                user_token: token,
                name: category
            }, data=>{
                if(data.error){
                    if(data.error === "Type already exist")
                        errorPopup("Cette catégorie existe déjà")
                    else
                        errorPopup(data.error)
                }else{
                    newShopAct(()=>{
                        $(".window.newShop .category select").val(data.id)
                    })
                }
            }, "json")


        }
    })
    $(".window.newShop .submit").click(()=>{
        clearInputError()

        let name = $(".window.newShop .name input").val()
        let email = $(".window.newShop .email input").val()
        let tel = $(".window.newShop .tel input").val()
        let category = $(".window.newShop .category select").val()
        let number = $(".window.newShop .number input").val()
        let way = $(".window.newShop .way input").val()
        let city = $(".window.newShop .city select").val()
        let postalcode = $(".window.newShop .postalcode input").val()

        if(name === "")
            inputError("name")
        else if(city === "")
            inputError("city")
        else{
            $.post(api + "store/new", {
                user_token: token,
                name: name,
                email: email,
                tel: tel,
                storeType_id: category,
                number: number,
                way: way,
                city: city,
                postalcode: postalcode
            }, data=>{
                if(data.error)
                    errorPopup(data.error)
                else {
                    $(".window.newShop").css("display", "none")
                    userAct()
                }
            }, "json")
        }
    })
})

function newShopAct(fun) {
    let select = $(".window.newShop .category select")
    $(".window.newShop .category select *").off()
    select.html("")
    $.post(api + "store/type/getAll", {user_token: token}, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            for(let i=0; i<data.length; i++){
                let type = data[i]

                let option = $("<option value="+type.id+">"+type.name+"</option>")

                select.append(option)
            }
            select.append($("<option value='new'>Nouveau</option>"))
            if(fun)
                fun()
        }
    }, "json")
}

function editUser(){
    let btn = $(".window.user .infos .edit")
    let inputs = $(".window.user .infos input")

    if(user_edit){

        let name = $(".window.user .infos .name input").val()
        let surname = $(".window.user .infos .surname input").val()
        let tel = $(".window.user .infos .tel input").val()

        if(name === "")
            inputError("name")
        else if(surname === "")
            inputError("surname")
        else{
            $.post(api + "user/change", {
                user_token: token,
                new_name: name,
                new_surname: surname,
                new_tel: tel
            }, data=>{
                if(data.error)
                    errorPopup(data.error)
            }, "json")
        }

        btn.html("Modifier")
    }else{
        btn.html("Enregistrer")
    }
    inputs.prop("disabled", user_edit)
    user_edit = !user_edit
}

function createChangeAddress(){
    let number = $(".window.newAddress .number input").val()
    let way = $(".window.newAddress .way input").val()
    let city = $(".window.newAddress .city select").val()
    let postalcode = $(".window.newAddress .postalcode input").val()
    let state = $(".window.newAddress .state input").val()

    if(address === null){
        $.post(api + "address/new", {
            user_token: token,
            number: number,
            way: way,
            city: city,
            postalcode: postalcode,
            state: state
        }, data=>{
            if(data.error)
                errorPopup(data.error)
            else{
                $(".window.newAddress").css("display", "none")
                userAct()
            }
        }, "json")
    }else{
        $.post(api + "address/modify", {
            user_token: token,
            address_id: address.id,
            number: number,
            way: way,
            city: city,
            postalcode: postalcode,
            state: state
        }, data=>{
            if(data.error){
                errorPopup(data.error)
            }else{
                $(".window.newAddress").css("display", "none")
                userAct()
            }
        }, "json")
    }
}
function userAct(){
    $(".window.user .name input").val(user.name)
    $(".window.user .surname input").val(user.surname)
    $(".window.user .tel input").val(user.tel)
    $(".window.user .email").html(user.mail)
    $(".window.user .creation").html(dateToStringFormat(user.created))

    $.post(api + "store/get/byUser", {
        user_token: token
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else
            fillStores(data)
    }, "json")

    $.post(api + "order/getAll", {
        user_token: token
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else
            fillOrders(data)
    }, "json")
    actAddresses()
}
function actAddresses(){
    $.post(api + "user/get/allAddresses", {
        user_token: token
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else {
            fillAddresses(data)
        }
    }, "json")
}
function fillOrders(orders){
    let list = $(".window.user .orders .list")

    list.html("")

    for(let i=0; i<orders.length; i++){
        let order = orders[i]

        let tr = $("<tr></tr>")
        tr.append($("<td></td>").html(order.id))
        tr.append($("<td></td>").html(dateToStringFormat(order.created)))
        tr.append($("<td></td>").addClass("clickable").html(order.store.name).click(()=>openStore(order.store)))
        tr.append($("<td></td>").html(order.store.address.city))
        tr.append($("<td></td>").html(order.subProducts.length))
        tr.append($("<td></td>").html(order.priceTTC.toFixed(2) + " €"))
        tr.append($("<td></td>").html(order.state))


        list.append(tr)
    }
}
function fillStores(stores){
    let list = $(".window.user .shops .list")
    $(".window.user .shops .list *").off()
    list.html("")

    for(let i=0; i<stores.length; i++){
        let store = stores[i]

        let tr = $("<tr></tr>")

        tr.append($("<td></td>").addClass("name").html(store.name))

        if(store.address) {
            tr.append($("<td></td>").addClass("city").html(store.address.city))
        }
        else{
            tr.append($("<td></td>").addClass("city").html("NULL"))
        }

        let btns = $("<td></td>").addClass("actions")

        let edit = $("<img src='files/img/edit.svg' alt='edit'>").addClass("clickable edit")
        let remove = $("<img src='files/img/delete.svg' alt='delete'>").addClass("clickable remove")

        edit.click(()=>{
            openWindow("shopadmin")
            shopAdminAct(store)
        })
        remove.click(()=>removeShop(store))

        btns.append(edit)
        btns.append(remove)

        tr.append(btns)

        list.append(tr)
    }
}

function removeShop(shop){
    let response = confirm("Êtes vous sur de vouloir supprimer " + shop.name + " ?");
    if(response === true){
        $.post(api + "store/delete", {
            user_token: token,
            id: shop.id
        }, data=>{
            if(data.error)
                errorPopup(data.error)
            else
                userAct()
        }, "json")
    }
}

function deleteAddress(address) {
    $.post(api + "address/delete", {
        user_token: token,
        address_id: address.id
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        userAct()
    }, "json")
}

function fillAddresses(addresses){
    let list = $(".window.user .addresses .list")
    let select = $(".window.chooseAddress select")
    $(".window.user .addresses .list *").off()
    list.html("")
    select.html("")

    for(let i=0; i<addresses.length; i++){
        let address = addresses[i]

        let tr = $("<tr></tr>")

        tr.append($("<td></td>").addClass("number").html(address.number))
        tr.append($("<td></td>").addClass("way").html(address.way))
        tr.append($("<td></td>").addClass("city").html(address.city))
        tr.append($("<td></td>").addClass("postalcode").html(address.postalcode))
        tr.append($("<td></td>").addClass("state").html(address.state))

        let tdbtn = $("<td></td>").addClass("actions")
        let edit = $("<img src='files/img/edit.svg' alt='edit'/>").addClass("clickable edit")
        let remove = $("<img src='files/img/delete.svg' alt='delete'/>").addClass("clickable delete")

        edit.click(()=>openAddress(address))
        remove.click(()=>deleteAddress(address))

        tdbtn.append(edit)
        tdbtn.append(remove)

        tr.append(tdbtn)

        list.append(tr)


        let option = $("<option value='"+address.id+"'></option>").html(getAddress(address))
        select.append(option)
    }
}
function deleteAccount(){
    if(confirm("Êtes-vous sûr de vouloir supprimer ce compte ?"))
        $.post(api + "user/delete", {
            user_token: token
        }, data=>{
            if(data.error)
                errorPopup(data.error)
            else {
                logout()
            }
        }, "json")
}
function openAddress(address_){
    address = address_
    if(address_ === null){
        $(".window.newAddress .number input").val("")
        $(".window.newAddress .way input").val("")
        $(".window.newAddress .city select").val("")
        $(".window.newAddress .postalcode input").val("")
        $(".window.newAddress .state input").val("")

    }else{
        $(".window.newAddress .number input").val(address.number)
        $(".window.newAddress .way input").val(address.way)
        $(".window.newAddress .postalcode input").val(address.postalcode)
        setCity(address.postalcode.toString(), address.city, $(".window.newAddress .city select"))
        $(".window.newAddress .state input").val(address.state)
    }
    $(".window.newAddress").css("display", "block")
}
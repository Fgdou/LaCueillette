address = null

$(()=>{
    $(".window.user .logout").click(()=>logout())
    $(".window.user .newAddress > form").submit(e => e.preventDefault())
    $(".window.user .newAddress .submit").click(()=>createChangeAddress())
    $(".window.user .addresses .new").click(()=>openAddress(null))
    $(".window.user .newAddress .cancel").click(()=>$(".window.user .newAddress").css("display", "none"))
})

function createChangeAddress(){
    let number = $(".window.user .newAddress .number input").val()
    let way = $(".window.user .newAddress .way input").val()
    let city = $(".window.user .newAddress .city input").val()
    let postalcode = $(".window.user .newAddress .postalcode input").val()
    let state = $(".window.user .newAddress .state input").val()

    if(address === null){
        $.post(api + "/address/new", {
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
                $(".window.user .newAddress").css("display", "none")
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
    $(".window.user .creation").html(dateToString(user.created))

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

function deleteAddress(address) {
    //TODO
}

function fillAddresses(addresses){
    let list = $(".window.user .addresses .list")
    list.html("")

    for(let i=0; i<addresses.length; i++){
        let address = addresses[i]

        let tr = $("<tr></tr>")
        tr.addClass("id", address.id)

        console.log(address)

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
    }
}
function openAddress(address_){
    address = address_
    if(address_ === null){
        $(".window.user .newAddress .number input").val("")
        $(".window.user .newAddress .way input").val("")
        $(".window.user .newAddress .city input").val("")
        $(".window.user .newAddress .postalcode input").val("")
        $(".window.user .newAddress .state input").val("")

    }else{
        $(".window.user .newAddress .number input").val(address.number)
        $(".window.user .newAddress .way input").val(address.way)
        $(".window.user .newAddress .city input").val(address.city)
        $(".window.user .newAddress .postalcode input").val(address.postalcode)
        $(".window.user .newAddress .state input").val(address.state)
    }
    $(".window.user .newAddress").css("display", "block")
}